package com.example.nutriscanner.data.api

import com.example.nutriscanner.data.model.ProductResponse
import com.example.nutriscanner.data.model.ProductSearchResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenFoodFactsApi {

    @GET("api/v0/product/{barcode}.json")
    suspend fun getProduct(@Path("barcode") barcode: String): ProductResponse

    @GET("api/v0/search")
    suspend fun searchProducts(
        @Query("categories_tags") category: String,
        @Query("nutrition_grades_tags") grade: String,
        @Query("countries_tags") country: String = "en:turkey",
        @Query("page_size") pageSize: Int = 50

    ): ProductSearchResponse
}

object RetrofitInstance {
    val api: OpenFoodFactsApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenFoodFactsApi::class.java)
    }
}