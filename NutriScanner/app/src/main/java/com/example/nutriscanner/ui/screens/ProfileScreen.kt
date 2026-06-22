package com.example.nutriscanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.nutriscanner.R
import com.example.nutriscanner.data.room.ProductEntity
import com.example.nutriscanner.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel? = null,
    onProductClick: (String) -> Unit = {}
) {
    val username by viewModel?.username?.collectAsStateWithLifecycle() ?: remember { mutableStateOf("") }
    val email by viewModel?.email?.collectAsStateWithLifecycle() ?: remember { mutableStateOf("") }
    val favorites by viewModel?.favorites?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(emptyList()) }
    val recents by viewModel?.recents?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(emptyList()) }

    var isEditing by remember { mutableStateOf(false) }
    var editUsername by remember { mutableStateOf("") }
    var editEmail by remember { mutableStateOf("") }

    var selectedFilter by remember { mutableStateOf("favorites") }

    val mostCommonGradeData by remember {
        derivedStateOf {
            recents
                .mapNotNull { it.nutritionGrade }
                .groupingBy { it }
                .eachCount()
                .maxByOrNull { it.value }
        }
    }

    val mostCommonGrade = mostCommonGradeData?.key
    val mostCommonGradeCount = mostCommonGradeData?.value ?: 0

    LaunchedEffect(username, email) {
        if (!isEditing) {
            editUsername = username.ifBlank { "Kullanıcı" }
            editEmail = email.ifBlank { "kullanici@example.com" }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(top = 48.dp, start = 16.dp)
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White)
                .zIndex(2f)
        ) {
            Icon(
                Icons.Default.ArrowBackIosNew,
                contentDescription = "Geri",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 80.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE3F2FD)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.person),
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                                tint = Color.Unspecified
                            )
                        }

                        IconButton(
                            onClick = {
                                if (isEditing) {
                                    viewModel?.saveProfile(editUsername, editEmail)
                                    isEditing = false
                                } else {
                                    isEditing = true
                                    editUsername = username
                                    editEmail = email
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 8.dp, y = 8.dp)
                                .size(36.dp)
                                .background(Color.White, CircleShape)
                                .zIndex(1f)
                        ) {
                            Icon(
                                imageVector = if (isEditing) Icons.Default.Save else Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = if (isEditing) Color(0xFF4CAF50) else Color(0xFF2196F3),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isEditing) {
                        OutlinedTextField(
                            value = editUsername,
                            onValueChange = { editUsername = it },
                            label = { Text("Kullanıcı Adı") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(0.8f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editEmail,
                            onValueChange = { editEmail = it },
                            label = { Text("E-posta") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(0.8f)
                        )
                    } else {
                        Text(
                            text = username.ifBlank { "Misafir Kullanıcı" },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = email.ifBlank { "Giriş yapılmadı" },
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.History,
                        value = "${recents.size}",
                        label = "Taranan",
                        color = Color(0xFFFFF3E0),
                        iconColor = Color(0xFFFF9800)
                    )

                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Favorite,
                        value = "${favorites.size}",
                        label = "Favori",
                        color = Color(0xFFFFEBEE),
                        iconColor = Color(0xFFF44336)
                    )

                    if (mostCommonGrade != null) {
                        NutritionGradeCard(
                            grade = mostCommonGrade,
                            count = mostCommonGradeCount,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterTabButton(
                        text = "Favorilerim",
                        isSelected = selectedFilter == "favorites",
                        onClick = { selectedFilter = "favorites" }
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    FilterTabButton(
                        text = "Son Tarananlar",
                        isSelected = selectedFilter == "recents",
                        onClick = { selectedFilter = "recents" }
                    )
                }
            }

            if (selectedFilter == "favorites") {
                item {
                    Text(
                        text = "Favorilerim",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                    )
                }

                if (favorites.isEmpty()) {
                    item {
                        Text(
                            text = "Henüz favoriniz yok",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    items(favorites) { product ->
                        ProductRow(product) { onProductClick(it) }
                    }
                }
            }

            if (selectedFilter == "recents") {
                item {
                    Text(
                        text = "Son Tarananlar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp)
                    )
                }

                if (recents.isEmpty()) {
                    item {
                        Text(
                            text = "Henüz taranmış ürün yok",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    items(recents) { product ->
                        ProductRow(product) { onProductClick(it) }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

@Composable
fun NutritionGradeCard(
    grade: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(getNutriColor(grade)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = grade.uppercase(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))


            Text(
                text = count.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "En Çok Taranan",
                fontSize = 11.sp,
                color = Color.Gray,
                maxLines = 1
            )

        }
    }
}

@Composable
fun FilterTabButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .clickable { onClick() }
            .background(
                if (isSelected) Color(0xFF2196F3) else Color.Transparent
            )
            .border(
                width = 1.5.dp,
                color = if (isSelected) Color(0xFF2196F3) else Color(0xFFDDDDDD),
                shape = RoundedCornerShape(50)
            ),
        color = if (isSelected) Color(0xFF2196F3) else Color.White
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = if (isSelected) Color.White else Color.Gray
        )
    }
}

@Composable
fun ProductRow(product: ProductEntity, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        onClick = {
            onClick(product.code)
        },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.productName ?: "İsimsiz Ürün",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1
                )
                Text(
                    text = product.brands ?: "",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }

            if (product.nutritionGrade != null) {
                Surface(
                    color = getNutriColor(product.nutritionGrade),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = product.nutritionGrade.uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 12.sp
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    color: Color,
    iconColor: Color
) {
    Card(
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = label,
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}

