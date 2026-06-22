package com.example.nutriscanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nutriscanner.ui.screens.HomeScreen
import com.example.nutriscanner.ui.screens.ProductDetailScreen
import com.example.nutriscanner.ui.screens.ScannerScreen
import com.example.nutriscanner.ui.theme.NutriscannerTheme
import com.example.nutriscanner.viewmodel.MainViewModel
import com.example.nutriscanner.viewmodel.MainViewModelFactory
import com.example.nutriscanner.viewmodel.ProfileViewModel
import com.example.nutriscanner.viewmodel.ProfileViewModelFactory
import com.example.nutriscanner.viewmodel.ProductState
import com.example.nutriscanner.data.room.AppDatabase
import com.example.nutriscanner.data.repository.ProductRepository
import com.example.nutriscanner.data.repository.UserPreferencesRepository
import com.example.nutriscanner.ui.screens.ProfileScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = AppDatabase.getDatabase(this)
        val productRepository = ProductRepository(database.productDao())
        val userPreferencesRepository = UserPreferencesRepository(this)
        
        setContent {
            NutriscannerTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val mainViewModel: MainViewModel = viewModel(
                        factory = MainViewModelFactory(productRepository)
                    )
                    val profileViewModel: ProfileViewModel = viewModel(
                        factory =   ProfileViewModelFactory(userPreferencesRepository, productRepository)
                    )
                    
                    AppNavigation(
                        viewModel = mainViewModel, 
                        profileViewModel = profileViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    viewModel: MainViewModel, 
    profileViewModel: ProfileViewModel
) {
    val navController = rememberNavController()
    val state by viewModel.productState.collectAsStateWithLifecycle()


    LaunchedEffect(state) {
        if (state is ProductState.Success) {
            // Eğer zaten result ekranında değilsek git
            if (navController.currentDestination?.route != "result") {
                navController.navigate("result")
            }
        }
    }

    NavHost(navController = navController, startDestination = "home") {


        composable("home") {

            LaunchedEffect(Unit) {
                viewModel.resetState()
            }

            HomeScreen(
                onScanClick = { navController.navigate("scanner") },
                onSearchClick = { barcode ->

                    viewModel.fetchProduct(barcode)
                },
                onProfileClick = { navController.navigate("profile") }
            )


            if (state is ProductState.Loading) {
                LoadingOverlay()
            }
            if (state is ProductState.Error) {
                ErrorOverlay(message = (state as ProductState.Error).message, onDismiss = { viewModel.resetState() })
            }
        }


        composable("scanner") {
            Box(modifier = Modifier.fillMaxSize()) {
                ScannerScreen(
                    onBarcodeDetected = { barcode ->
                        viewModel.fetchProduct(barcode)
                    },
                    onClose = { navController.popBackStack() }
                )

                if (state is ProductState.Loading) {
                    LoadingOverlay()
                }
                if (state is ProductState.Error) {
                    ErrorOverlay(message = (state as ProductState.Error).message, onDismiss = { viewModel.resetState() })
                }
            }
        }

        composable("profile") {
            ProfileScreen(
                viewModel = profileViewModel,
                onBack = { navController.popBackStack() },
                onProductClick = { code ->

                    viewModel.fetchProduct(code)

                }
            )
        }


        composable("result") {
            if (state is ProductState.Success) {
                val product = (state as ProductState.Success).product
                ProductDetailScreen(
                    product = product,
                    onBack = {
                        viewModel.resetState()

                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color.White)
    }
}

@Composable
fun ErrorOverlay(message: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Color.White,
            modifier = Modifier.padding(20.dp)
        )
    }
}