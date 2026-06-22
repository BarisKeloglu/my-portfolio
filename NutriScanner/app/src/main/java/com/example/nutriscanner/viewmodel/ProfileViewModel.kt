package com.example.nutriscanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.nutriscanner.data.repository.ProductRepository
import com.example.nutriscanner.data.repository.UserPreferencesRepository
import com.example.nutriscanner.data.room.ProductEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val productRepository: ProductRepository
) : ViewModel() {


    val username: StateFlow<String> = userPreferencesRepository.usernameFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val email: StateFlow<String> = userPreferencesRepository.emailFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")


    val favorites: StateFlow<List<ProductEntity>> = productRepository.favorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recents: StateFlow<List<ProductEntity>> = productRepository.recents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveProfile(newUsername: String, newEmail: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveUserProfile(newUsername, newEmail)
        }
    }
}

class ProfileViewModelFactory(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val productRepository: ProductRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(userPreferencesRepository, productRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
