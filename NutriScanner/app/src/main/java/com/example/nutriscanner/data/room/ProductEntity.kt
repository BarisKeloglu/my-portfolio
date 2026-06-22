package com.example.nutriscanner.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val code: String,
    val productName: String?,
    val brands: String?,
    val imageUrl: String?,
    val nutritionGrade: String?,
    val isFavorite: Boolean = false,
    val lastScannedAt: Long? = null
)
