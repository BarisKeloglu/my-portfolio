package com.example.nutriscanner.data.model

data class ProductResponse(
    val code: String?,
    val product: Product?
)

data class Product(
    val code: String?,
    val product_name: String?,
    val brands: String?,
    val image_front_url: String?,
    val ingredients_text: String?,
    val nutrition_grades: String?,
    val categories_tags: List<String>?,
    val categories_hierarchy: List<String>?,
    val countries_tags: List<String>?,
    val brands_tags: List<String>?,
    val nutriments: Nutriments?
)

data class Nutriments(
    val energy_100g: Double?,
    val sugars_100g: Double?,
    val fat_100g: Double?,
    val proteins_100g: Double?
)

data class ProductSearchResponse(
    val count: Int?,
    val page: Int?,
    val products: List<Product>?
)