package com.receiptreader.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.receiptreader.data.local.AppDatabase
import com.receiptreader.data.local.CategorySpending
import com.receiptreader.data.local.Receipt
import com.receiptreader.data.preferences.ThemePreferences
import com.receiptreader.data.repository.ReceiptRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ReceiptViewModel - Manages UI state and data operations.
 * 
 * PRIVACY BY DESIGN:
 * All data operations are local-only. No network calls are made.
 */
class ReceiptViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val repository = ReceiptRepository(database.receiptDao())
    private val themePreferences = ThemePreferences(application)
    
    // =========================================================================
    // STATE
    // =========================================================================
    
    // Theme state
    val isDarkMode: StateFlow<Boolean> = themePreferences.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    
    // Receipt list
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    @OptIn(ExperimentalCoroutinesApi::class)
    val receipts: StateFlow<List<Receipt>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                repository.getAllReceipts()
            } else {
                repository.searchReceipts(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Dashboard stats
    val totalSpending: StateFlow<Double> = repository.getTotalSpending()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    
    val monthlySpending: StateFlow<Double> = repository.getMonthlySpending()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    
    val receiptCount: StateFlow<Int> = repository.getReceiptCountFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    
    val categorySpending: StateFlow<List<CategorySpending>> = repository.getSpendingByCategory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Scan result (for showing preview dialog)
    private val _pendingScanResult = MutableStateFlow<PendingScanResult?>(null)
    val pendingScanResult: StateFlow<PendingScanResult?> = _pendingScanResult.asStateFlow()
    
    // Selected receipt for detail dialog
    private val _selectedReceipt = MutableStateFlow<Receipt?>(null)
    val selectedReceipt: StateFlow<Receipt?> = _selectedReceipt.asStateFlow()
    
    // =========================================================================
    // ACTIONS
    // =========================================================================
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            themePreferences.setDarkMode(enabled)
        }
    }
    
    /**
     * Called when a receipt is captured from the camera.
     * Stores the data temporarily for the preview dialog.
     */
    fun onReceiptCaptured(
        storeName: String?,
        date: String?,
        amount: Double?,
        rawText: String
    ) {
        _pendingScanResult.value = PendingScanResult(
            storeName = storeName,
            date = date,
            amount = amount,
            rawText = rawText
        )
    }
    
    /**
     * Clear pending scan result (dismiss preview dialog).
     */
    fun dismissScanPreview() {
        _pendingScanResult.value = null
    }
    
    /**
     * Save the scanned receipt to database.
     */
    fun saveReceipt(
        storeName: String,
        date: String,
        amount: Double,
        category: String,
        rawText: String
    ) {
        viewModelScope.launch {
            val receipt = Receipt(
                storeName = storeName.ifEmpty { null },
                date = date.ifEmpty { null },
                totalAmount = amount,
                currency = "MAD",
                category = category,
                rawText = rawText
            )
            repository.insertReceipt(receipt)
            _pendingScanResult.value = null
        }
    }
    
    /**
     * Select a receipt to show detail dialog.
     */
    fun selectReceipt(receipt: Receipt) {
        _selectedReceipt.value = receipt
    }
    
    /**
     * Dismiss receipt detail dialog.
     */
    fun dismissReceiptDetail() {
        _selectedReceipt.value = null
    }
    
    /**
     * Delete a receipt.
     */
    fun deleteReceipt(receipt: Receipt) {
        viewModelScope.launch {
            repository.deleteReceipt(receipt)
            if (_selectedReceipt.value?.id == receipt.id) {
                _selectedReceipt.value = null
            }
        }
    }
    
    /**
     * Delete all receipts.
     */
    fun deleteAllReceipts() {
        viewModelScope.launch {
            repository.deleteAllReceipts()
        }
    }
}

/**
 * Holds scan result data temporarily before saving.
 */
data class PendingScanResult(
    val storeName: String?,
    val date: String?,
    val amount: Double?,
    val rawText: String
)
