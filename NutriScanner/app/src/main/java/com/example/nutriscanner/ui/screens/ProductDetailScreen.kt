package com.example.nutriscanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.nutriscanner.data.model.Product
import com.example.nutriscanner.viewmodel.MainViewModel

@Composable
fun ProductDetailScreen(
    product: Product,
    onBack: () -> Unit,
    viewModel: MainViewModel
) {
    val scrollState = rememberScrollState()
    val isAlternativesLoading by viewModel.alternativesLoading.collectAsStateWithLifecycle()
    val alternatives by viewModel.alternatives.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isCurrentProductFavorite.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F8F8))) {


        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(top = 48.dp, start = 24.dp)
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White)
                .zIndex(2f)
        ) {
            Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Geri", tint = Color.Black, modifier = Modifier.size(20.dp))
        }


        IconButton(
            onClick = { viewModel.toggleFavorite() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 24.dp)
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White)
                .zIndex(2f)
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Favoriye Ekle",
                tint = if (isFavorite) Color.Red else Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = product.image_front_url,
                    contentDescription = product.product_name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(250.dp)
                )

                if (product.nutrition_grades != null) {
                    Surface(
                        color = getNutriColor(product.nutrition_grades),
                        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 32.dp)
                    ) {
                        Text(
                            text = "NUTRI-SCORE ${product.nutrition_grades.uppercase()}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-24).dp)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(Color(0xFFF8F8F8))
                    .padding(24.dp)
            ) {
                Text(
                    text = product.product_name ?: "Bilinmeyen Ürün",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = "${product.brands ?: ""} • ${product.nutriments?.energy_100g?.toInt()} kcal",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NutrientBox("Kalori", "${product.nutriments?.energy_100g?.toInt() ?: 0}", Color(0xFFFFF3E0), Color(0xFFFF9800))
                    NutrientBox("Şeker", "${product.nutriments?.sugars_100g ?: 0}g", Color(0xFFE3F2FD), Color(0xFF2196F3))
                    NutrientBox("Yağ", "${product.nutriments?.fat_100g ?: 0}g", Color(0xFFFFF8E1), Color(0xFFFFC107))
                    NutrientBox("Protein", "${product.nutriments?.proteins_100g ?: 0}g", Color(0xFFE8F5E9), Color(0xFF4CAF50))
                }

                Spacer(modifier = Modifier.height(24.dp))


                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.BarChart, contentDescription = null, tint = Color(0xFF2196F3))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("İçindekiler", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = product.ingredients_text ?: "İçerik bilgisi mevcut değil.",
                            color = Color.DarkGray,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))


                Text(
                    text = "Alternatif Ürünler",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (isAlternativesLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF2196F3))
                    }
                } else if (alternatives.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(alternatives) { alt ->
                            AlternativeItem(alt) {
                                viewModel.updateCurrentProduct(alt)
                            }
                        }
                    }
                } else {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(Color.White, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Bu ürün için daha sağlıklı bir\nalternatif (Türkiye) bulunamadı.",
                            textAlign = TextAlign.Center,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.BarChart, contentDescription = null, tint = Color.LightGray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Barkod: ${product.code ?: "---"}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

@Composable
fun NutrientBox(title: String, value: String, bgColor: Color, textColor: Color) {
    Column(
        modifier = Modifier
            .width(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

fun getNutriColor(grade: String?): Color {
    return when(grade?.uppercase()) {
        "A" -> Color(0xFF038141)
        "B" -> Color(0xFF85BB2F)
        "C" -> Color(0xFFFECB02)
        "D" -> Color(0xFFEE8100)
        "E" -> Color(0xFFE63E11)
        else -> Color.Gray
    }
}

@Composable
fun AlternativeItem(product: Product, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = product.image_front_url,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            if (product.nutrition_grades != null) {
                Surface(
                    color = getNutriColor(product.nutrition_grades),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Text(
                        text = product.nutrition_grades.uppercase(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = product.product_name ?: "İsimsiz",
            fontSize = 12.sp,
            color = Color.Black,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        )
    }
}