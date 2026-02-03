package com.receiptreader.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Receipt Entity - Represents a scanned receipt stored in the local database.
 * 
 * PRIVACY BY DESIGN - DATA STORAGE:
 * This entity is stored exclusively in the device's private internal storage
 * using Room (SQLite). The data is:
 * - Sandboxed: Only this app can access it
 * - Not synced: No cloud backup (disabled in manifest)
 * - Not exported: No content provider exposing this data
 * - Local-only: Cannot be transmitted due to lack of INTERNET permission
 * 
 * @property id Auto-generated primary key
 * @property storeName Extracted merchant/store name (may be null if not detected)
 * @property date Extracted date from receipt (may be null if not detected)
 * @property totalAmount Extracted total amount (numeric value)
 * @property currency Currency code (DH, MAD, EUR, USD, etc.)
 * @property category User-selected category for spending tracking
 * @property rawText The complete OCR text extracted from the receipt image
 * @property imagePath Local path to the saved receipt image (optional)
 * @property timestamp Unix timestamp when the receipt was scanned
 */
@Entity(tableName = "receipts")
data class Receipt(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val storeName: String?,
    
    val date: String?,
    
    val totalAmount: Double?,
    
    val currency: String = "MAD",
    
    val category: String = "Other",
    
    val rawText: String,
    
    val imagePath: String? = null,
    
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Spending categories with associated colors.
 * Used for categorizing receipts and visualization in charts.
 */
enum class ReceiptCategory(val displayName: String, val colorHex: Long) {
    GROCERIES("Groceries", 0xFF4CAF50),      // Green
    DINING("Dining", 0xFFFF9800),            // Orange
    SHOPPING("Shopping", 0xFF2196F3),        // Blue
    TRANSPORTATION("Transportation", 0xFF9C27B0), // Purple
    HEALTHCARE("Healthcare", 0xFFFFEB3B),    // Yellow
    ENTERTAINMENT("Entertainment", 0xFF00BCD4), // Cyan
    UTILITIES("Utilities", 0xFF607D8B),      // Grey
    OTHER("Other", 0xFF795548);              // Brown
    
    companion object {
        fun fromString(value: String): ReceiptCategory {
            return entries.find { it.displayName.equals(value, ignoreCase = true) } ?: OTHER
        }
        
        fun allCategories(): List<String> = entries.map { it.displayName }
    }
}
