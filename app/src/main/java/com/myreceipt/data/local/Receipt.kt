package com.myreceipt.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Receipt entity for the database. Stores all extracted and manual receipt data. */
@Entity(tableName = "receipts")
data class Receipt(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val storeName: String?,
        val date: String?,
        val totalAmount: Double?,
        val currency: String = "MAD",
        val category: String = "Other",
        val rawText: String,
        val imagePath: String? = null,
        val timestamp: Long = System.currentTimeMillis()
)

/** Category spending aggregation for analytics. */
data class CategorySpending(val category: String, val total: Double)

/** Receipt categories with display names. */
enum class ReceiptCategory(val displayName: String) {
    GROCERIES("Groceries"),
    DINING("Dining"),
    SHOPPING("Shopping"),
    TRANSPORTATION("Transportation"),
    HEALTHCARE("Healthcare"),
    ENTERTAINMENT("Entertainment"),
    UTILITIES("Utilities"),
    OTHER("Other");

    companion object {
        fun allCategories(): List<String> = entries.map { it.displayName }

        fun fromDisplayName(name: String): ReceiptCategory {
            return entries.find { it.displayName.equals(name, ignoreCase = true) } ?: OTHER
        }
    }
}
