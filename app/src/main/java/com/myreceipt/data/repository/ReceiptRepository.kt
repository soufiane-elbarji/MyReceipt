package com.myreceipt.data.repository

import com.myreceipt.data.local.CategorySpending
import com.myreceipt.data.local.Receipt
import com.myreceipt.data.local.ReceiptDao
import java.util.Calendar
import kotlinx.coroutines.flow.Flow

/** Repository for receipt data operations. Abstracts data sources from the rest of the app. */
class ReceiptRepository(private val receiptDao: ReceiptDao) {

    fun getAllReceipts(): Flow<List<Receipt>> = receiptDao.getAllReceipts()

    fun searchReceipts(query: String): Flow<List<Receipt>> = receiptDao.searchReceipts(query)

    suspend fun insertReceipt(receipt: Receipt): Long = receiptDao.insertReceipt(receipt)

    suspend fun updateReceipt(receipt: Receipt) = receiptDao.updateReceipt(receipt)

    suspend fun deleteReceipt(receipt: Receipt) = receiptDao.deleteReceipt(receipt)

    suspend fun getReceiptById(id: Long): Receipt? = receiptDao.getReceiptById(id)

    fun getTotalSpending(): Flow<Double> = receiptDao.getTotalSpending()

    fun getReceiptCount(): Flow<Int> = receiptDao.getReceiptCount()

    fun getCategorySpending(): Flow<List<CategorySpending>> = receiptDao.getCategorySpending()

    fun getMonthlySpending(): Flow<Double> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        val endOfMonth = calendar.timeInMillis

        return receiptDao.getMonthlySpending(startOfMonth, endOfMonth)
    }

    fun getSpendingThisWeek(): Flow<Double> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return receiptDao.getSpendingSince(calendar.timeInMillis)
    }

    suspend fun deleteAllReceipts() = receiptDao.deleteAllReceipts()
}
