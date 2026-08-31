package com.souqrodina.accounting.data.repository

import com.souqrodina.accounting.data.local.datastore.PreferencesManager
import com.souqrodina.accounting.data.local.db.AppDatabase
import com.souqrodina.accounting.data.local.db.entities.ContactEntity
import com.souqrodina.accounting.data.local.db.entities.ProductEntity
import com.souqrodina.accounting.data.local.db.entities.TransactionEntity
import com.souqrodina.accounting.data.model.Currency
import com.souqrodina.accounting.data.model.Language
import com.souqrodina.accounting.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class AccountingRepository(
    private val database: AppDatabase,
    private val preferences: PreferencesManager
) {
    val productsFlow: Flow<List<ProductEntity>> = database.productDao().getAllProductsFlow()
    val contactsFlow: Flow<List<ContactEntity>> = database.contactDao().getAllContactsFlow()
    
    val businessProfileFlow = preferences.businessProfileFlow
    val capitalFlow = preferences.capitalFlow
    val seasonFlow = preferences.seasonFlow
    val currencyFlow = preferences.currencyFlow
    val languageFlow = preferences.languageFlow
    val lastBackupTimestampFlow = preferences.lastBackupTimestampFlow

    fun getTransactionsForSeasonFlow(season: Int): Flow<List<TransactionEntity>> {
        return database.transactionDao().getTransactionsForSeasonFlow(season)
    }

    suspend fun executeSale(
        product: ProductEntity,
        units: Int,
        customerName: String?,
        currentSeason: Int
    ): Boolean {
        if (product.quantity < units) return false
        
        // 1. Deduct stock in Room
        val updated = database.productDao().deductStock(product.id, units)
        if (updated > 0) {
            val totalRevenue = product.salePrice * units
            val now = Date()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
            
            val tx = TransactionEntity(
                id = "tx_${System.currentTimeMillis()}",
                type = TransactionType.SALE,
                amount = totalRevenue,
                title = "Sale of $units ${product.nameEn}",
                titleAr = "بيع $units ${product.nameAr}",
                category = product.category,
                date = dateFormat.format(now),
                time = timeFormat.format(now),
                itemId = product.id,
                units = units,
                customerName = customerName,
                invoiceNumber = "INV-${System.currentTimeMillis().toString().takeLast(6)}",
                seasonNumber = currentSeason
            )
            database.transactionDao().insertTransaction(tx)
            return true
        }
        return false
    }

    suspend fun voidSale(transaction: TransactionEntity) {
        if (transaction.type == TransactionType.SALE && !transaction.isVoided) {
            database.transactionDao().voidTransaction(transaction.id)
            if (transaction.itemId != null && transaction.units != null) {
                database.productDao().restock(transaction.itemId, transaction.units)
            }
        }
    }

    suspend fun addExpense(
        title: String,
        amount: Double,
        category: String,
        currentSeason: Int
    ) {
        val now = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
        val tx = TransactionEntity(
            id = "exp_${System.currentTimeMillis()}",
            type = TransactionType.EXPENSE,
            amount = amount,
            title = title,
            titleAr = title,
            category = category,
            date = dateFormat.format(now),
            time = timeFormat.format(now),
            seasonNumber = currentSeason
        )
        database.transactionDao().insertTransaction(tx)
    }

    suspend fun updateProduct(product: ProductEntity) {
        database.productDao().insertOrUpdate(product)
    }

    suspend fun deleteProduct(product: ProductEntity) {
        database.productDao().deleteProduct(product)
    }

    suspend fun addContact(contact: ContactEntity) {
        database.contactDao().insertOrUpdate(contact)
    }

    suspend fun deleteContact(contact: ContactEntity) {
        database.contactDao().deleteContact(contact)
    }

    suspend fun setCurrency(currency: Currency) = preferences.setCurrency(currency)
    suspend fun setLanguage(language: Language) = preferences.setLanguage(language)
    suspend fun updatePinCode(pin: String) = preferences.updatePinCode(pin)
    suspend fun updateCapital(amount: Double) = preferences.updateCapital(amount)
    suspend fun updateBusinessProfile(name: String, phone: String, logoUri: String) = 
        preferences.updateBusinessProfile(name, phone, logoUri)

    suspend fun resetSeason(currentSeason: Int) {
        preferences.incrementSeason()
    }
}