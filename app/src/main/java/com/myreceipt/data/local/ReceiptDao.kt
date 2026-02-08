package com.myreceipt.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/** Data Access Object for Receipt entities. Provides all database operations for receipts. */
@Dao
interface ReceiptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(receipt: Receipt): Long

    @Update suspend fun updateReceipt(receipt: Receipt)

    @Delete suspend fun deleteReceipt(receipt: Receipt)

    @Query("SELECT * FROM receipts ORDER BY timestamp DESC")
    fun getAllReceipts(): Flow<List<Receipt>>

    @Query("SELECT * FROM receipts WHERE id = :id") suspend fun getReceiptById(id: Long): Receipt?

    @Query(
            "SELECT * FROM receipts WHERE storeName LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY timestamp DESC"
    )
    fun searchReceipts(query: String): Flow<List<Receipt>>

    @Query("SELECT COALESCE(SUM(totalAmount), 0.0) FROM receipts")
    fun getTotalSpending(): Flow<Double>

    @Query(
            "SELECT COALESCE(SUM(totalAmount), 0.0) FROM receipts WHERE timestamp >= :startTimestamp"
    )
    fun getSpendingSince(startTimestamp: Long): Flow<Double>

    @Query("SELECT COUNT(*) FROM receipts") fun getReceiptCount(): Flow<Int>

    @Query(
            "SELECT category, SUM(totalAmount) as total FROM receipts GROUP BY category ORDER BY total DESC"
    )
    fun getCategorySpending(): Flow<List<CategorySpending>>

    @Query(
            "SELECT category, SUM(totalAmount) as total FROM receipts WHERE timestamp >= :startTimestamp GROUP BY category ORDER BY total DESC"
    )
    fun getCategorySpendingSince(startTimestamp: Long): Flow<List<CategorySpending>>

    @Query("DELETE FROM receipts") suspend fun deleteAllReceipts()

    @Query("SELECT * FROM receipts WHERE timestamp >= :startTimestamp ORDER BY timestamp DESC")
    fun getReceiptsSince(startTimestamp: Long): Flow<List<Receipt>>

    // Monthly spending query
    @Query(
            """
        SELECT COALESCE(SUM(totalAmount), 0.0) FROM receipts 
        WHERE timestamp >= :startOfMonth AND timestamp < :endOfMonth
    """
    )
    fun getMonthlySpending(startOfMonth: Long, endOfMonth: Long): Flow<Double>
}
