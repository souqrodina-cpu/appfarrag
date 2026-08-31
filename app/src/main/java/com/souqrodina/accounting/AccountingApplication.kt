package com.souqrodina.accounting

import android.app.Application
import com.souqrodina.accounting.data.local.db.AppDatabase
import com.souqrodina.accounting.data.local.datastore.PreferencesManager
import com.souqrodina.accounting.data.repository.AccountingRepository

class AccountingApplication : Application() {

    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val preferencesManager: PreferencesManager by lazy { PreferencesManager(this) }
    val repository: AccountingRepository by lazy { 
        AccountingRepository(database, preferencesManager) 
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: AccountingApplication
            private set
    }
}