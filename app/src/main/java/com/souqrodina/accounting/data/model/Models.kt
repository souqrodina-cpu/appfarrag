package com.souqrodina.accounting.data.model

import kotlinx.serialization.Serializable

enum class Language(val code: String, val displayName: String) {
    AR("ar", "العربية"),
    EN("en", "English")
}

enum class Currency(val code: String, val symbol: String, val symbolAr: String) {
    EGP("EGP", "EGP", "ج.م"),
    SAR("SAR", "SAR", "ر.س"),
    USD("USD", "$", "$")
}

enum class TabType {
    DASHBOARD,
    INVENTORY,
    DIRECTORIES,
    SALES,
    SETTINGS
}

enum class TransactionType {
    SALE,
    EXPENSE,
    CAPITAL_ADDITION,
    CAPITAL_WITHDRAWAL,
    REFUND
}

enum class ContactType {
    CUSTOMER,
    SUPPLIER
}

@Serializable
data class BusinessProfile(
    val name: String = "سوق روضينة",
    val phone: String = "+20 100 000 0000",
    val logoUri: String = "",
    val pinCode: String = "1234",
    val isPinProtected: Boolean = true
)

@Serializable
data class FinancialSummary(
    val totalSales: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val netProfit: Double = 0.0,
    val capitalInvested: Double = 50000.0,
    val seasonNumber: Int = 1,
    val lastBackupTimestamp: Long? = null
)