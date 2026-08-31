package com.souqrodina.accounting.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.souqrodina.accounting.data.model.ContactType
import com.souqrodina.accounting.data.model.TransactionType

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val sku: String,
    val category: String,
    val purchasePrice: Double,
    val salePrice: Double,
    val quantity: Int,
    val minStockAlert: Int = 5,
    val iconName: String = "box"
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val type: TransactionType,
    val amount: Double,
    val title: String,
    val titleAr: String,
    val category: String,
    val date: String,
    val time: String,
    val itemId: String? = null,
    val units: Int? = null,
    val customerName: String? = null,
    val invoiceNumber: String? = null,
    val isVoided: Boolean = false,
    val seasonNumber: Int = 1
)

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val phone: String,
    val type: ContactType,
    val balance: Double = 0.0,
    val totalPurchases: Double = 0.0
)

@Entity(tableName = "seasons")
data class SeasonEntity(
    @PrimaryKey(autoGenerate = true)
    val seasonId: Int = 0,
    val seasonNumber: Int,
    val startDate: String,
    val endDate: String?,
    val finalSales: Double,
    val finalExpenses: Double,
    val finalNetProfit: Double
)