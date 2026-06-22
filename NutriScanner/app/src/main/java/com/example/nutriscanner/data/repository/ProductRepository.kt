package com.example.nutriscanner.data.repository

import com.example.nutriscanner.data.model.Product
import com.example.nutriscanner.data.room.ProductDao
import com.example.nutriscanner.data.room.ProductEntity
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {

    val favorites: Flow<List<ProductEntity>> = productDao.getFavorites()
    val recents: Flow<List<ProductEntity>> = productDao.getRecents()

    fun getProductFlow(code: String): Flow<ProductEntity?> = productDao.getProductFlow(code)

    suspend fun addToRecents(product: Product) {
        val code = product.code ?: return
        val existing = productDao.getProduct(code)
        
        val entity = existing?.copy(
            lastScannedAt = System.currentTimeMillis()
        ) ?: ProductEntity(
            code = code,
            productName = product.product_name,
            brands = product.brands,
            imageUrl = product.image_front_url,
            nutritionGrade = product.nutrition_grades,
            isFavorite = false,
            lastScannedAt = System.currentTimeMillis()
        )
        productDao.insertProduct(entity)
    }

    suspend fun toggleFavorite(product: Product) {
        val code = product.code ?: return
        val existing = productDao.getProduct(code)
        
        val newFavoriteStatus = existing?.isFavorite?.not() ?: true
        
        val entity = existing?.copy(
            isFavorite = newFavoriteStatus
        ) ?: ProductEntity(
            code = code,
            productName = product.product_name,
            brands = product.brands,
            imageUrl = product.image_front_url,
            nutritionGrade = product.nutrition_grades,
            isFavorite = newFavoriteStatus,
            lastScannedAt = null
        )
        productDao.insertProduct(entity)
    }
    

}
