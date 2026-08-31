package com.souqrodina.accounting.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.souqrodina.accounting.data.local.db.entities.ContactEntity
import com.souqrodina.accounting.data.local.db.entities.ProductEntity
import com.souqrodina.accounting.data.local.db.entities.TransactionEntity
import com.souqrodina.accounting.data.model.*
import com.souqrodina.accounting.data.repository.AccountingRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AccountingUiState(
    val business: BusinessProfile = BusinessProfile(),
    val currency: Currency = Currency.EGP,
    val language: Language = Language.AR,
    val currentTab: TabType = TabType.DASHBOARD,
    val products: List<ProductEntity> = emptyList(),
    val transactions: List<TransactionEntity> = emptyList(),
    val contacts: List<ContactEntity> = emptyList(),
    val capitalInvested: Double = 50000.0,
    val seasonNumber: Int = 1,
    val isCapitalUnlocked: Boolean = false,
    val searchQuery: String = "",
    val lastBackupTimestamp: Long? = null,
    val toastMessage: String? = null
) {
    val totalSales: Double
        get() = transactions.filter { it.type == TransactionType.SALE && !it.isVoided }.sumOf { it.amount }

    val totalExpenses: Double
        get() = transactions.filter { it.type == TransactionType.EXPENSE && !it.isVoided }.sumOf { it.amount }

    val netProfit: Double
        get() = totalSales - totalExpenses
}

class AccountingViewModel(private val repository: AccountingRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountingUiState())
    val uiState: StateFlow<AccountingUiState> = _uiState.asStateFlow()

    init {
        observeRepository()
    }

    private fun observeRepository() {
        viewModelScope.launch {
            repository.businessProfileFlow.collect { profile ->
                _uiState.update { it.copy(business = profile) }
            }
        }
        viewModelScope.launch {
            repository.currencyFlow.collect { curr ->
                _uiState.update { it.copy(currency = curr) }
            }
        }
        viewModelScope.launch {
            repository.languageFlow.collect { lang ->
                _uiState.update { it.copy(language = lang) }
            }
        }
        viewModelScope.launch {
            repository.capitalFlow.collect { cap ->
                _uiState.update { it.copy(capitalInvested = cap) }
            }
        }
        viewModelScope.launch {
            repository.productsFlow.collect { list ->
                _uiState.update { it.copy(products = list) }
            }
        }
        viewModelScope.launch {
            repository.contactsFlow.collect { list ->
                _uiState.update { it.copy(contacts = list) }
            }
        }
        viewModelScope.launch {
            repository.seasonFlow.collect { season ->
                _uiState.update { it.copy(seasonNumber = season) }
                repository.getTransactionsForSeasonFlow(season).collect { txs ->
                    _uiState.update { it.copy(transactions = txs) }
                }
            }
        }
    }

    fun setTab(tab: TabType) = _uiState.update { it.copy(currentTab = tab) }
    fun setSearchQuery(query: String) = _uiState.update { it.copy(searchQuery = query) }
    
    fun unlockCapital(pin: String): Boolean {
        if (pin == _uiState.value.business.pinCode) {
            _uiState.update { it.copy(isCapitalUnlocked = true) }
            return true
        }
        return false
    }
    fun lockCapital() = _uiState.update { it.copy(isCapitalUnlocked = false) }

    fun executeSale(product: ProductEntity, units: Int, customerName: String?) {
        viewModelScope.launch {
            val success = repository.executeSale(product, units, customerName, _uiState.value.seasonNumber)
            if (success) {
                showToast("Sale completed successfully!")
            } else {
                showToast("Insufficient stock for sale!")
            }
        }
    }

    fun voidTransaction(tx: TransactionEntity) {
        viewModelScope.launch {
            repository.voidSale(tx)
            showToast("Sale voided and inventory restored")
        }
    }

    fun addExpense(title: String, amount: Double, category: String) {
        viewModelScope.launch {
            repository.addExpense(title, amount, category, _uiState.value.seasonNumber)
            showToast("Expense recorded successfully")
        }
    }

    fun addProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.updateProduct(product)
            showToast("Product saved to inventory")
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            showToast("Product removed")
        }
    }

    fun addContact(contact: ContactEntity) {
        viewModelScope.launch {
            repository.addContact(contact)
            showToast("Contact saved to directory")
        }
    }

    fun deleteContact(contact: ContactEntity) {
        viewModelScope.launch {
            repository.deleteContact(contact)
            showToast("Contact deleted")
        }
    }

    fun setCurrency(currency: Currency) = viewModelScope.launch { repository.setCurrency(currency) }
    fun setLanguage(language: Language) = viewModelScope.launch { repository.setLanguage(language) }
    fun updatePin(pin: String) = viewModelScope.launch { repository.updatePinCode(pin) }
    fun updateCapital(amount: Double) = viewModelScope.launch { repository.updateCapital(amount) }
    fun resetSeason() = viewModelScope.launch { repository.resetSeason(_uiState.value.seasonNumber) }

    private fun showToast(msg: String) {
        _uiState.update { it.copy(toastMessage = msg) }
    }
    fun clearToast() = _uiState.update { it.copy(toastMessage = null) }
}

class AccountingViewModelFactory(private val repository: AccountingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AccountingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}