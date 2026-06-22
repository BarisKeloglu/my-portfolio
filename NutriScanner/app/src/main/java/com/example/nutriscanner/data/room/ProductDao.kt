package com.example.nutriscanner.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE isFavorite = 1")
    fun getFavorites(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE lastScannedAt IS NOT NULL ORDER BY lastScannedAt DESC")
    fun getRecents(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE code = :code")
    suspend fun getProduct(code: String): ProductEntity?

    @Query("SELECT * FROM products WHERE code = :code")
    fun getProductFlow(code: String): Flow<ProductEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)
    
    @Query("DELETE FROM products WHERE code = :code")
    suspend fun deleteProduct(code: String)
}
