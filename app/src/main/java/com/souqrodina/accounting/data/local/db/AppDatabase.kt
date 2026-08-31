package com.souqrodina.accounting.data.local.db

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.souqrodina.accounting.data.local.db.dao.*
import com.souqrodina.accounting.data.local.db.entities.*
import com.souqrodina.accounting.data.model.ContactType
import com.souqrodina.accounting.data.model.TransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProductEntity::class,
        TransactionEntity::class,
        ContactEntity::class,
        SeasonEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun transactionDao(): TransactionDao
    abstract fun contactDao(): ContactDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "souq_rodina_accounting.db"
                )
                .addCallback(SeedDataCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class SeedDataCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedInitialData(database)
                    }
                }
            }

            private suspend fun seedInitialData(database: AppDatabase) {
                val productDao = database.productDao()
                val contactDao = database.contactDao()
                val transactionDao = database.transactionDao()

                // Initial Products
                val initialProducts = listOf(
                    ProductEntity("p1", "فستان حرير مطرز", "Embroidered Silk Dress", "SKU-101", "فساتين", 450.0, 750.0, 18, 5, "shirt"),
                    ProductEntity("p2", "عباءة خليجية كلاسيك", "Classic Abaya", "SKU-102", "عباءات", 600.0, 950.0, 12, 3, "sparkles"),
                    ProductEntity("p3", "طقم كاجوال شتوي", "Winter Casual Set", "SKU-103", "ملابس", 350.0, 580.0, 25, 5, "layers"),
                    ProductEntity("p4", "حقيبة يد جلد طبيعي", "Genuine Leather Handbag", "SKU-201", "إكسسوارات", 280.0, 490.0, 8, 4, "shopping-bag"),
                    ProductEntity("p5", "شال حرير طبيعي", "Natural Silk Scarf", "SKU-202", "إكسسوارات", 120.0, 220.0, 4, 6, "wind"),
                    ProductEntity("p6", "حذاء نسائي كعب متوسط", "Classic Heels", "SKU-301", "أحذية", 320.0, 540.0, 0, 5, "footprints")
                )
                productDao.insertAll(initialProducts)

                // Initial Contacts
                val initialContacts = listOf(
                    ContactEntity("c1", "سارة أحمد", "+20 101 234 5678", ContactType.CUSTOMER, 1200.0, 4500.0),
                    ContactEntity("c2", "منى محمود", "+20 102 345 6789", ContactType.CUSTOMER, 0.0, 3200.0),
                    ContactEntity("c3", "نور الهدى", "+20 103 456 7890", ContactType.CUSTOMER, 450.0, 1850.0),
                    ContactEntity("s1", "مصنع الأناقة للأقمشة", "+20 109 876 5432", ContactType.SUPPLIER, 15000.0, 85000.0),
                    ContactEntity("s2", "مؤسسة الحرير الذهبي", "+20 108 765 4321", ContactType.SUPPLIER, 8500.0, 42000.0)
                )
                contactDao.insertAll(initialContacts)

                // Initial Transactions
                val initialTx = listOf(
                    TransactionEntity("tx1", TransactionType.SALE, 1500.0, "بيع 2 فستان حرير مطرز", "بيع 2 فستان حرير مطرز", "فساتين", "2026-08-30", "14:30", "p1", 2, "سارة أحمد", "INV-2026-001"),
                    TransactionEntity("tx2", TransactionType.SALE, 950.0, "بيع 1 عباءة خليجية", "بيع 1 عباءة خليجية", "عباءات", "2026-08-30", "13:15", "p2", 1, "منى محمود", "INV-2026-002"),
                    TransactionEntity("tx3", TransactionType.EXPENSE, 450.0, "فاتورة كهرباء ومرافق", "فاتورة كهرباء ومرافق", "مرافق", "2026-08-30", "11:00", null, null, null, null)
                )
                transactionDao.insertAll(initialTx)
            }
        }
    }
}