package com.souqrodina.accounting.data.local.db.dao

import androidx.room.*
import com.souqrodina.accounting.data.local.db.entities.ContactEntity
import com.souqrodina.accounting.data.local.db.entities.ProductEntity
import com.souqrodina.accounting.data.local.db.entities.TransactionEntity
import com.souqrodina.accounting.data.model.ContactType
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY nameAr ASC")
    fun getAllProductsFlow(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: String): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Query("UPDATE products SET quantity = quantity - :units WHERE id = :productId AND quantity >= :units")
    suspend fun deductStock(productId: String, units: Int): Int

    @Query("UPDATE products SET quantity = quantity + :units WHERE id = :productId")
    suspend fun restock(productId: String, units: Int)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE seasonNumber = :season ORDER BY id DESC")
    fun getTransactionsForSeasonFlow(season: Int): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<TransactionEntity>)

    @Query("UPDATE transactions SET isVoided = 1 WHERE id = :id")
    suspend fun voidTransaction(id: String)

    @Query("DELETE FROM transactions WHERE seasonNumber = :season")
    suspend fun clearSeasonTransactions(season: Int)
}

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAllContactsFlow(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE type = :type ORDER BY name ASC")
    fun getContactsByTypeFlow(type: ContactType): Flow<List<ContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(contact: ContactEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contacts: List<ContactEntity>)

    @Delete
    suspend fun deleteContact(contact: ContactEntity)
}