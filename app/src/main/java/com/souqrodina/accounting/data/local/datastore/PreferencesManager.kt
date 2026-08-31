package com.souqrodina.accounting.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.souqrodina.accounting.data.model.BusinessProfile
import com.souqrodina.accounting.data.model.Currency
import com.souqrodina.accounting.data.model.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "souq_rodina_prefs")

class PreferencesManager(private val context: Context) {

    private object Keys {
        val BUSINESS_NAME = stringPreferencesKey("business_name")
        val BUSINESS_PHONE = stringPreferencesKey("business_phone")
        val LOGO_URI = stringPreferencesKey("logo_uri")
        val PIN_CODE = stringPreferencesKey("pin_code")
        val IS_PIN_ENABLED = booleanPreferencesKey("is_pin_enabled")
        val CAPITAL_INVESTED = doublePreferencesKey("capital_invested")
        val SEASON_NUMBER = intPreferencesKey("season_number")
        val SELECTED_CURRENCY = stringPreferencesKey("selected_currency")
        val SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
        val LAST_BACKUP_TIMESTAMP = longPreferencesKey("last_backup_timestamp")
    }

    val businessProfileFlow: Flow<BusinessProfile> = context.dataStore.data.map { prefs ->
        BusinessProfile(
            name = prefs[Keys.BUSINESS_NAME] ?: "سوق روضينة",
            phone = prefs[Keys.BUSINESS_PHONE] ?: "+20 100 000 0000",
            logoUri = prefs[Keys.LOGO_URI] ?: "",
            pinCode = prefs[Keys.PIN_CODE] ?: "1234",
            isPinProtected = prefs[Keys.IS_PIN_ENABLED] ?: true
        )
    }

    val capitalFlow: Flow<Double> = context.dataStore.data.map { prefs ->
        prefs[Keys.CAPITAL_INVESTED] ?: 50000.0
    }

    val seasonFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[Keys.SEASON_NUMBER] ?: 1
    }

    val currencyFlow: Flow<Currency> = context.dataStore.data.map { prefs ->
        val code = prefs[Keys.SELECTED_CURRENCY] ?: "EGP"
        try { Currency.valueOf(code) } catch (e: Exception) { Currency.EGP }
    }

    val languageFlow: Flow<Language> = context.dataStore.data.map { prefs ->
        val code = prefs[Keys.SELECTED_LANGUAGE] ?: "AR"
        try { Language.valueOf(code) } catch (e: Exception) { Language.AR }
    }

    val lastBackupTimestampFlow: Flow<Long?> = context.dataStore.data.map { prefs ->
        prefs[Keys.LAST_BACKUP_TIMESTAMP]
    }

    suspend fun updateBusinessProfile(name: String, phone: String, logoUri: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.BUSINESS_NAME] = name
            prefs[Keys.BUSINESS_PHONE] = phone
            prefs[Keys.LOGO_URI] = logoUri
        }
    }

    suspend fun updatePinCode(newPin: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.PIN_CODE] = newPin
        }
    }

    suspend fun updateCapital(amount: Double) {
        context.dataStore.edit { prefs ->
            prefs[Keys.CAPITAL_INVESTED] = amount
        }
    }

    suspend fun setCurrency(currency: Currency) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SELECTED_CURRENCY] = currency.name
        }
    }

    suspend fun setLanguage(language: Language) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SELECTED_LANGUAGE] = language.name
        }
    }

    suspend fun incrementSeason() {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.SEASON_NUMBER] ?: 1
            prefs[Keys.SEASON_NUMBER] = current + 1
        }
    }

    suspend fun setLastBackupTimestamp(timestamp: Long) {
        context.dataStore.edit { prefs ->
            prefs[Keys.LAST_BACKUP_TIMESTAMP] = timestamp
        }
    }
}