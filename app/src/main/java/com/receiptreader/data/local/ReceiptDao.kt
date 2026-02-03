package com.receiptreader.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Receipt operations.
 * 
 * PRIVACY BY DESIGN - DATA ACCESS:
 * All database operations are local-only. The Flow-based observation
 * pattern ensures reactive UI updates without any network calls.
 */
@Dao
interface ReceiptDao {
    
    /**
     * Insert a new receipt into the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(receipt: Receipt): Long
    
    /**
     * Update an existing receipt.
     */
    @Update
    suspend fun updateReceipt(receipt: Receipt)
    
    /**
     * Observe all receipts as a Flow, ordered by timestamp (newest first).
     */
    @Query("SELECT * FROM receipts ORDER BY timestamp DESC")
    fun getAllReceipts(): Flow<List<Receipt>>
    
    /**
     * Search receipts by store name or category.
     */
    @Query("SELECT * FROM receipts WHERE storeName LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchReceipts(query: String): Flow<List<Receipt>>
    
    /**
     * Get receipts by category.
     */
    @Query("SELECT * FROM receipts WHERE category = :category ORDER BY timestamp DESC")
    fun getReceiptsByCategory(category: String): Flow<List<Receipt>>
    
    /**
     * Get a single receipt by ID.
     */
    @Query("SELECT * FROM receipts WHERE id = :id")
    suspend fun getReceiptById(id: Long): Receipt?
    
    /**
     * Delete a specific receipt.
     * PRIVACY: Supports GDPR "right to erasure".
     */
    @Delete
    suspend fun deleteReceipt(receipt: Receipt)
    
    /**
     * Delete all receipts.
     */
    @Query("DELETE FROM receipts")
    suspend fun deleteAllReceipts()
    
    /**
     * Get total count of receipts.
     */
    @Query("SELECT COUNT(*) FROM receipts")
    suspend fun getReceiptCount(): Int
    
    /**
     * Get total count as Flow for reactive UI.
     */
    @Query("SELECT COUNT(*) FROM receipts")
    fun getReceiptCountFlow(): Flow<Int>
    
    /**
     * Get total spending amount.
     */
    @Query("SELECT COALESCE(SUM(totalAmount), 0.0) FROM receipts")
    fun getTotalSpending(): Flow<Double>
    
    /**
     * Get spending for current month.
     */
    @Query("SELECT COALESCE(SUM(totalAmount), 0.0) FROM receipts WHERE timestamp >= :startOfMonth")
    fun getMonthlySpending(startOfMonth: Long): Flow<Double>
    
    /**
     * Get spending by category for chart.
     */
    @Query("SELECT category, COALESCE(SUM(totalAmount), 0.0) as total FROM receipts GROUP BY category")
    fun getSpendingByCategory(): Flow<List<CategorySpending>>
}

/**
 * Data class for category spending aggregation.
 */
data class CategorySpending(
    val category: String,
    val total: Double
)
