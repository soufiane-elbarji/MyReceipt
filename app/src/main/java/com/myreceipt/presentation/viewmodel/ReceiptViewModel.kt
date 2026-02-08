package com.myreceipt.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.myreceipt.data.local.AppDatabase
import com.myreceipt.data.local.CategorySpending
import com.myreceipt.data.local.Receipt
import com.myreceipt.data.preferences.ThemePreferences
import com.myreceipt.data.repository.ReceiptRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for the MyReceipt app. Manages receipt data, spending analytics, and theme preferences.
 */
class ReceiptViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = ReceiptRepository(database.receiptDao())
    private val themePreferences = ThemePreferences(application)

    // Search query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Receipts - filtered by search
    val receipts: StateFlow<List<Receipt>> =
            _searchQuery
                    .flatMapLatest { query ->
                        if (query.isEmpty()) {
                            repository.getAllReceipts()
                        } else {
                            repository.searchReceipts(query)
                        }
                    }
                    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Analytics
    val totalSpending: StateFlow<Double> =
            repository
                    .getTotalSpending()
                    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val monthlySpending: StateFlow<Double> =
            repository
                    .getMonthlySpending()
                    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val receiptCount: StateFlow<Int> =
            repository
                    .getReceiptCount()
                    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val categorySpending: StateFlow<List<CategorySpending>> =
            repository
                    .getCategorySpending()
                    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Theme
    val isDarkMode: StateFlow<Boolean> =
            themePreferences.isDarkMode.stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    true
            )

    // Pending scan result for preview dialog
    private val _pendingScanResult = MutableStateFlow<PendingScanResult?>(null)
    val pendingScanResult: StateFlow<PendingScanResult?> = _pendingScanResult.asStateFlow()

    // Selected receipt for detail view
    private val _selectedReceipt = MutableStateFlow<Receipt?>(null)
    val selectedReceipt: StateFlow<Receipt?> = _selectedReceipt.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setPendingScanResult(storeName: String?, date: String?, amount: Double?, rawText: String) {
        _pendingScanResult.value = PendingScanResult(storeName, date, amount, rawText)
    }

    fun clearPendingScanResult() {
        _pendingScanResult.value = null
    }

    fun setSelectedReceipt(receipt: Receipt?) {
        _selectedReceipt.value = receipt
    }

    fun saveReceipt(
            storeName: String,
            date: String,
            amount: Double,
            category: String,
            rawText: String
    ) {
        viewModelScope.launch {
            val receipt =
                    Receipt(
                            storeName = storeName.ifEmpty { null },
                            date = date.ifEmpty { null },
                            totalAmount = amount,
                            category = category,
                            rawText = rawText
                    )
            repository.insertReceipt(receipt)
            clearPendingScanResult()
        }
    }

    fun deleteReceipt(receipt: Receipt) {
        viewModelScope.launch {
            repository.deleteReceipt(receipt)
            _selectedReceipt.value = null
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch { themePreferences.setDarkMode(!isDarkMode.value) }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { themePreferences.setDarkMode(enabled) }
    }
}

/** Temporary holder for scanned receipt data before saving. */
data class PendingScanResult(
        val storeName: String?,
        val date: String?,
        val amount: Double?,
        val rawText: String
)
