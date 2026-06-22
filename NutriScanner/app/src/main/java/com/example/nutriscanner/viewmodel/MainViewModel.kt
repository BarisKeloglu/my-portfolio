package com.example.nutriscanner.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.nutriscanner.data.api.RetrofitInstance
import com.example.nutriscanner.data.model.Product
import com.example.nutriscanner.data.repository.ProductRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val productRepository: ProductRepository? = null
) : ViewModel() {

    private val _productState = MutableStateFlow<ProductState>(ProductState.Idle)
    val productState = _productState.asStateFlow()

    private val _isCurrentProductFavorite = MutableStateFlow(false)
    val isCurrentProductFavorite = _isCurrentProductFavorite.asStateFlow()

    private var favoritesJob: Job? = null

    private val _alternatives = MutableStateFlow<List<Product>>(emptyList())
    val alternatives = _alternatives.asStateFlow()

    private val _alternativesLoading = MutableStateFlow(false)
    val alternativesLoading = _alternativesLoading.asStateFlow()

    fun fetchProduct(barcode: String) {
        viewModelScope.launch {
            _productState.value = ProductState.Loading
            _alternatives.value = emptyList()
            try {
                val response = RetrofitInstance.api.getProduct(barcode)
                if (response.product != null) {
                    _productState.value = ProductState.Success(response.product)
                    productRepository?.addToRecents(response.product)
                    checkIfFavorite(response.product.code)


                    fetchAlternatives(response.product)
                } else {
                    _productState.value = ProductState.Error("Ürün bulunamadı.")
                }
            } catch (e: Exception) {
                _productState.value = ProductState.Error("Hata: ${e.localizedMessage}")
            }
        }
    }

    private fun checkIfFavorite(code: String?) {
        favoritesJob?.cancel()
        _isCurrentProductFavorite.value = false
        if (code == null || productRepository == null) return
        favoritesJob = viewModelScope.launch {
            productRepository.getProductFlow(code).collect { entity ->
                _isCurrentProductFavorite.value = entity?.isFavorite == true
            }
        }
    }

    fun toggleFavorite() {
        val currentState = _productState.value
        if (currentState is ProductState.Success && productRepository != null) {
            viewModelScope.launch {
                productRepository.toggleFavorite(currentState.product)
            }
        }
    }



    private fun fetchAlternatives(product: Product) {
        viewModelScope.launch {
            _alternativesLoading.value = true
            try {
                val currentGrade = product.nutrition_grades?.lowercase()

                if (currentGrade == null) {
                    _alternativesLoading.value = false
                    return@launch
                }

                val rawCategories = product.categories_hierarchy ?: product.categories_tags ?: emptyList()
                val specificCategories = rawCategories.takeLast(2).reversed()

                if (specificCategories.isEmpty()) {
                    _alternativesLoading.value = false
                    return@launch
                }

                val allGrades = listOf("a", "b", "c", "d", "e")
                val currentIndex = allGrades.indexOf(currentGrade)

                val targetGrades = if (currentIndex != -1) {
                    allGrades.take(currentIndex + 1)
                } else {
                    listOf("a", "b")
                }

                val deferredJobs = specificCategories.flatMap { category ->
                    targetGrades.map { grade ->
                        async {
                            try {
                                val response = RetrofitInstance.api.searchProducts(
                                    category = category,
                                    grade = grade,
                                    country = "en:turkey",
                                    pageSize = 20
                                )
                                response.products ?: emptyList()
                            } catch (e: Exception) {
                                emptyList()
                            }
                        }
                    }
                }

                val allResults = deferredJobs.awaitAll().flatten()

                val finalAlternatives = allResults
                    .asSequence()
                    .filter { alt ->
                        alt.code != product.code &&
                                !alt.product_name.isNullOrBlank()
                    }
                    .distinctBy { it.code }
                    .sortedBy { alt ->

                        alt.nutrition_grades?.lowercase() ?: "z"
                    }
                    .take(10)
                    .toList()

                _alternatives.value = finalAlternatives

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _alternativesLoading.value = false
            }
        }
    }

    fun updateCurrentProduct(product: Product) {
        _productState.value = ProductState.Success(product)
        checkIfFavorite(product.code)
        fetchAlternatives(product)
    }

    fun resetState() {
        _productState.value = ProductState.Idle
        _alternatives.value = emptyList()
        _isCurrentProductFavorite.value = false
    }
}

class MainViewModelFactory(private val repository: ProductRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

sealed class ProductState {
    object Idle : ProductState()
    object Loading : ProductState()
    data class Success(val product: Product) : ProductState()
    data class Error(val message: String) : ProductState()
}