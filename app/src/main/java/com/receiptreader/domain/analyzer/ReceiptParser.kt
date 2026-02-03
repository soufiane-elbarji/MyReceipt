package com.receiptreader.domain.analyzer

/**
 * Regex-based parser for extracting structured data from OCR text.
 * 
 * PRIVACY BY DESIGN - LOCAL PROCESSING:
 * All parsing logic runs entirely on-device using standard Kotlin regex.
 * No data is sent to any external service for "smart" parsing.
 * 
 * This parser uses heuristics and regex patterns to extract:
 * - Date (multiple formats)
 * - Total amount (with various currency symbols)
 * - Merchant name (first prominent line)
 */
object ReceiptParser {
    
    // =========================================================================
    // DATE PATTERNS
    // =========================================================================
    // Supports multiple date formats commonly found on receipts:
    // - DD/MM/YYYY, DD-MM-YYYY, DD.MM.YYYY
    // - YYYY-MM-DD, YYYY/MM/DD
    // - MM/DD/YYYY (US format)
    
    private val datePatterns = listOf(
        // DD/MM/YYYY or DD-MM-YYYY or DD.MM.YYYY
        Regex("""(\d{1,2}[/\-\.]\d{1,2}[/\-\.]\d{2,4})"""),
        // YYYY-MM-DD or YYYY/MM/DD
        Regex("""(\d{4}[/\-]\d{1,2}[/\-]\d{1,2})"""),
        // Written dates like "15 Jan 2024" or "January 15, 2024"
        Regex("""(\d{1,2}\s+(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*\.?\s+\d{2,4})""", RegexOption.IGNORE_CASE),
        Regex("""((?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*\.?\s+\d{1,2},?\s+\d{2,4})""", RegexOption.IGNORE_CASE)
    )
    
    // =========================================================================
    // TOTAL AMOUNT PATTERNS
    // =========================================================================
    // Looks for keywords indicating the total, followed by currency amounts.
    // Supports: DH, MAD (Moroccan Dirham), $, €, £
    
    private val totalKeywords = listOf(
        "total", "totale", "total ttc", "ttc", "somme", "montant",
        "amount", "grand total", "net à payer", "net a payer",
        "à payer", "a payer", "total due", "balance due"
    )
    
    private val currencyPatterns = listOf(
        // Amount followed by currency: 123.45 DH, 123,45 MAD, 123.45€
        Regex("""(\d{1,}[.,]\d{2})\s*(DH|MAD|€|\$|£|EUR|USD)""", RegexOption.IGNORE_CASE),
        // Currency followed by amount: $123.45, €123,45
        Regex("""(€|\$|£)\s*(\d{1,}[.,]\d{2})"""),
        // Just numbers after total keyword: 123.45 or 123,45
        Regex("""(\d{1,}[.,]\d{2})""")
    )
    
    /**
     * Result of parsing a receipt's OCR text.
     */
    data class ParseResult(
        val merchantName: String?,
        val date: String?,
        val totalAmount: String?
    )
    
    /**
     * Parse OCR text to extract receipt information.
     * 
     * @param ocrText The raw text from ML Kit text recognition
     * @return ParseResult containing extracted fields (may be null if not found)
     */
    fun parse(ocrText: String): ParseResult {
        val lines = ocrText.split("\n")
            .map { it.trim() }
            .filter { it.isNotBlank() }
        
        return ParseResult(
            merchantName = extractMerchantName(lines),
            date = extractDate(ocrText),
            totalAmount = extractTotalAmount(ocrText, lines)
        )
    }
    
    /**
     * Extract the merchant name using heuristics.
     * 
     * HEURISTIC: The merchant name is typically:
     * 1. One of the first few lines
     * 2. Usually in uppercase or prominent text
     * 3. Not a date, address, or transaction detail
     */
    private fun extractMerchantName(lines: List<String>): String? {
        // Look at the first 5 lines for potential merchant names
        val candidateLines = lines.take(5)
        
        for (line in candidateLines) {
            // Skip lines that look like dates
            if (datePatterns.any { it.containsMatchIn(line) }) continue
            
            // Skip lines that look like addresses (contain numbers with letters)
            if (line.matches(Regex(""".*\d+\s+.*(?:rue|avenue|street|st|av|blvd|road|rd).*""", RegexOption.IGNORE_CASE))) continue
            
            // Skip very short lines or lines with mostly numbers
            if (line.length < 3) continue
            val digitCount = line.count { it.isDigit() }
            if (digitCount > line.length / 2) continue
            
            // Skip common receipt headers
            val skipKeywords = listOf("receipt", "reçu", "ticket", "facture", "invoice", "tel:", "phone:", "fax:")
            if (skipKeywords.any { line.contains(it, ignoreCase = true) }) continue
            
            // This line looks like a merchant name
            return line.take(50) // Limit length
        }
        
        // Fallback: return first non-empty line
        return lines.firstOrNull()?.take(50)
    }
    
    /**
     * Extract date from the OCR text.
     * Tries multiple date patterns and returns the first match.
     */
    private fun extractDate(text: String): String? {
        for (pattern in datePatterns) {
            val match = pattern.find(text)
            if (match != null) {
                return match.groupValues[1]
            }
        }
        return null
    }
    
    /**
     * Extract the total amount from the OCR text.
     * 
     * STRATEGY:
     * 1. Look for lines containing total keywords
     * 2. Extract currency amount from those lines
     * 3. If multiple totals found, prefer the last one (usually the grand total)
     */
    private fun extractTotalAmount(text: String, lines: List<String>): String? {
        val textLower = text.lowercase()
        
        // Find lines containing total keywords
        var lastTotalAmount: String? = null
        
        for (line in lines) {
            val lineLower = line.lowercase()
            
            // Check if this line contains a total keyword
            val hasKeyword = totalKeywords.any { keyword ->
                lineLower.contains(keyword)
            }
            
            if (hasKeyword) {
                // Try to extract amount from this line
                for (pattern in currencyPatterns) {
                    val match = pattern.find(line)
                    if (match != null) {
                        lastTotalAmount = match.value
                        break
                    }
                }
            }
        }
        
        // If no total keyword found, try to find the largest amount
        // (often the total is the largest number on the receipt)
        if (lastTotalAmount == null) {
            lastTotalAmount = findLargestAmount(text)
        }
        
        return lastTotalAmount
    }
    
    /**
     * Find the largest monetary amount in the text.
     * Used as fallback when no total keyword is found.
     */
    private fun findLargestAmount(text: String): String? {
        val amountPattern = Regex("""(\d{1,}[.,]\d{2})""")
        val amounts = amountPattern.findAll(text)
            .map { match ->
                val normalized = match.value.replace(",", ".")
                normalized.toDoubleOrNull() to match.value
            }
            .filter { it.first != null }
            .toList()
        
        return amounts.maxByOrNull { it.first!! }?.second
    }
}
