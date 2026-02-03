package com.receiptreader.data.repository

import com.receiptreader.data.local.CategorySpending
import com.receiptreader.data.local.Receipt
import com.receiptreader.data.local.ReceiptDao
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

/**
 * Repository for Receipt data operations.
 * 
 * PRIVACY BY DESIGN - DATA ACCESS LAYER:
 * This repository provides abstraction over the local database.
 * All operations are local-only with no network transmission capability.
 */
class ReceiptRepository(private val receiptDao: ReceiptDao) {
    
    /**
     * Observe all receipts as a Flow.
     */
    fun getAllReceipts(): Flow<List<Receipt>> = receiptDao.getAllReceipts()
    
    /**
     * Search receipts by store name or category.
     */
    fun searchReceipts(query: String): Flow<List<Receipt>> = receiptDao.searchReceipts(query)
    
    /**
     * Get receipts by category.
     */
    fun getReceiptsByCategory(category: String): Flow<List<Receipt>> = 
        receiptDao.getReceiptsByCategory(category)
    
    /**
     * Get total spending.
     */
    fun getTotalSpending(): Flow<Double> = receiptDao.getTotalSpending()
    
    /**
     * Get monthly spending.
     */
    fun getMonthlySpending(): Flow<Double> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return receiptDao.getMonthlySpending(calendar.timeInMillis)
    }
    
    /**
     * Get spending by category for chart.
     */
    fun getSpendingByCategory(): Flow<List<CategorySpending>> = 
        receiptDao.getSpendingByCategory()
    
    /**
     * Get receipt count.
     */
    fun getReceiptCountFlow(): Flow<Int> = receiptDao.getReceiptCountFlow()
    
    /**
     * Insert a new receipt.
     */
    suspend fun insertReceipt(receipt: Receipt): Long = receiptDao.insertReceipt(receipt)
    
    /**
     * Update a receipt.
     */
    suspend fun updateReceipt(receipt: Receipt) = receiptDao.updateReceipt(receipt)
    
    /**
     * Get a receipt by ID.
     */
    suspend fun getReceiptById(id: Long): Receipt? = receiptDao.getReceiptById(id)
    
    /**
     * Delete a receipt.
     */
    suspend fun deleteReceipt(receipt: Receipt) = receiptDao.deleteReceipt(receipt)
    
    /**
     * Delete all receipts.
     */
    suspend fun deleteAllReceipts() = receiptDao.deleteAllReceipts()
}
