package com.myreceipt.domain.analyzer

/**
 * Parser for extracting structured data from OCR text. Uses regex patterns to identify merchants,
 * dates, and amounts.
 */
object ReceiptParser {

    data class ParseResult(val merchantName: String?, val date: String?, val totalAmount: String?)

    // Common date patterns
    private val datePatterns =
            listOf(
                    Regex("""\d{2}/\d{2}/\d{4}"""),
                    Regex("""\d{2}-\d{2}-\d{4}"""),
                    Regex("""\d{2}\.\d{2}\.\d{4}"""),
                    Regex("""\d{4}/\d{2}/\d{2}"""),
                    Regex("""\d{4}-\d{2}-\d{2}"""),
                    Regex(
                            """\d{1,2}\s+(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*\s+\d{4}""",
                            RegexOption.IGNORE_CASE
                    ),
                    Regex(
                            """(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*\s+\d{1,2},?\s+\d{4}""",
                            RegexOption.IGNORE_CASE
                    )
            )

    // Amount patterns (various formats including MAD)
    private val amountPatterns =
            listOf(
                    Regex(
                            """(?:TOTAL|MONTANT|المجموع|الإجمالي)[\s:]*(\d+[.,]\d{2})\s*(?:MAD|DH)?""",
                            RegexOption.IGNORE_CASE
                    ),
                    Regex("""(\d+[.,]\d{2})\s*(?:MAD|DH)"""),
                    Regex("""(?:MAD|DH)\s*(\d+[.,]\d{2})"""),
                    Regex("""TOTAL[\s:]*(\d+[.,]\d{2})""", RegexOption.IGNORE_CASE),
                    Regex("""(\d{1,3}(?:[.,]\d{3})*[.,]\d{2})""")
            )

    // Known Moroccan store patterns
    private val storePatterns =
            listOf(
                    Regex("""(?:MARJANE|Marjane|marjane)"""),
                    Regex("""(?:CARREFOUR|Carrefour|carrefour)"""),
                    Regex("""(?:ACIMA|Acima|acima)"""),
                    Regex("""(?:BIM|Bim|bim)"""),
                    Regex("""(?:LABEL['']VIE|Label['']Vie)""", RegexOption.IGNORE_CASE),
                    Regex("""(?:ATACADAO|Atacadao)""", RegexOption.IGNORE_CASE),
                    Regex("""(?:ASWAK ASSALAM|Aswak Assalam)""", RegexOption.IGNORE_CASE),
                    Regex("""(?:HANOUTY|Hanouty)""", RegexOption.IGNORE_CASE)
            )

    fun parse(text: String): ParseResult {
        val merchantName = extractMerchantName(text)
        val date = extractDate(text)
        val totalAmount = extractAmount(text)

        return ParseResult(merchantName = merchantName, date = date, totalAmount = totalAmount)
    }

    private fun extractMerchantName(text: String): String? {
        // First try known store patterns
        for (pattern in storePatterns) {
            val match = pattern.find(text)
            if (match != null) {
                return match.value.uppercase()
            }
        }

        // Fallback: use first non-empty line that looks like a store name
        val lines = text.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
        for (line in lines.take(5)) {
            // Skip lines that look like dates, amounts, or addresses
            if (line.matches(Regex(""".*\d{2}[/.-]\d{2}[/.-]\d{4}.*"""))) continue
            if (line.matches(Regex(""".*\d+[.,]\d{2}.*"""))) continue
            if (line.length < 3 || line.length > 40) continue
            if (line.contains("@") || line.contains("www") || line.contains("http")) continue

            // This might be the store name
            return line.take(30)
        }

        return null
    }

    private fun extractDate(text: String): String? {
        for (pattern in datePatterns) {
            val match = pattern.find(text)
            if (match != null) {
                return match.value
            }
        }
        return null
    }

    private fun extractAmount(text: String): String? {
        var bestAmount: Double? = null
        var bestAmountString: String? = null

        for (pattern in amountPatterns) {
            val matches = pattern.findAll(text)
            for (match in matches) {
                val amountStr =
                        if (match.groupValues.size > 1) {
                            match.groupValues[1]
                        } else {
                            match.value
                        }

                val normalized = amountStr.replace(",", ".").replace(Regex("""[^\d.]"""), "")

                val amount = normalized.toDoubleOrNull()
                if (amount != null && (bestAmount == null || amount > bestAmount)) {
                    // Reasonable amount range check
                    if (amount in 1.0..100000.0) {
                        bestAmount = amount
                        bestAmountString = amountStr
                    }
                }
            }
        }

        return bestAmountString
    }
}
