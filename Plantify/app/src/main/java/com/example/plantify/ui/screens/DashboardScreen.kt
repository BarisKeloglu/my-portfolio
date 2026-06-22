package com.example.plantify.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.plantify.data.model.Plant
import com.example.plantify.data.model.PlantStatus

@Composable
fun DashboardScreen(
    plants: List<Plant>,
    onWaterPlant: (String) -> Unit,
    onPlantSelect: (String) -> Unit,
    onAddPlantClick: () -> Unit
) {

    val needsWateringCount = plants.filter { 
        it.getStatus() != PlantStatus.SULANMIS 
    }.size

    val isDark = com.example.plantify.data.repository.PlantRepository.isDarkMode.value
    val backgroundColor = if (isDark) Color(0xFF121614) else Color(0xFFF8FAFC)
    val cardColor = if (isDark) Color(0xFF1E2521) else Color.White
    val primaryTextColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F172A)
    val secondaryTextColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 120.dp)
        ) {

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Plantify",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = primaryTextColor,
                            letterSpacing = (-1).sp
                        )
                        Text(
                            text = "Senin Yeşil Dünyan",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = secondaryTextColor
                        )
                    }
                    

                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(cardColor)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "Sprout Logo",
                            modifier = Modifier.size(28.dp),
                            tint = Color(0xFF2E7D32)
                        )
                    }
                }
            }


            item {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF2E7D32), Color(0xFF1B5E20))
                            )
                        )
                        .padding(28.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "GÜNLÜK BAKIM",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White.copy(alpha = 0.6f),
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (needsWateringCount > 0) {
                                    "Bugün $needsWateringCount bitki su bekliyor"
                                } else {
                                    "Harika! Tüm bitkilerin su almış görünüyor."
                                },
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = (-0.5).sp,
                                lineHeight = 26.sp
                            )
                        }
                        

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = "Water Stat",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }


            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "Bitkilerin",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = primaryTextColor,
                        letterSpacing = (-0.5).sp
                    )
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF2E7D32).copy(alpha = 0.12f))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${plants.size} Toplam",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }


            if (plants.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Henüz bitki eklenmemiş. Sağ alttaki + ile başlayın!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = secondaryTextColor
                        )
                    }
                }
            } else {
                items(plants, key = { it.id }) { plant ->
                    PlantCardItem(
                        plant = plant,
                        onWaterClick = { onWaterPlant(plant.id) },
                        onCardClick = { onPlantSelect(plant.id) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }


        FloatingActionButton(
            onClick = onAddPlantClick,
            shape = RoundedCornerShape(24.dp),
            containerColor = Color(0xFF2E7D32),
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 96.dp, end = 24.dp)
                .size(64.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Plant",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun PlantCardItem(
    plant: Plant,
    onWaterClick: () -> Unit,
    onCardClick: () -> Unit
) {
    val status = plant.getStatus()
    val hydration = plant.getHydrationProgress()

    val isDark = com.example.plantify.data.repository.PlantRepository.isDarkMode.value
    val cardColor = if (isDark) Color(0xFF1E2521) else Color.White
    val primaryTextColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F172A)
    val secondaryTextColor = if (isDark) Color(0xFF94A3B8) else Color(0xFFCBD5E1)
    val progressTrackColor = if (isDark) Color(0xFF171C19) else Color(0xFFF1F5F9)
    val waterBtnDisabledBg = if (isDark) Color(0xFF171C19) else Color(0xFFF8FAFC)

    val statusColor = when (status) {
        PlantStatus.KRITIK -> Color(0xFFEF4444)
        PlantStatus.SULANMALI -> Color(0xFFF59E0B)
        PlantStatus.SULANMIS -> Color(0xFF2E7D32)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Plant image with status dot marker
            Box(modifier = Modifier.size(72.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(plant.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = plant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isDark) Color(0xFF171C19) else Color(0xFFE2E8F0))
                )


                if (status != PlantStatus.SULANMIS) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(cardColor)
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color(0xFFF59E0B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Alert",
                                tint = Color.White,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))


            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = plant.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = primaryTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    letterSpacing = (-0.5).sp
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Location",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = plant.room.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = secondaryTextColor,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))


                LinearProgressIndicator(
                    progress = hydration,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = statusColor,
                    trackColor = progressTrackColor
                )
            }


            IconButton(
                onClick = onWaterClick,
                enabled = status != PlantStatus.SULANMIS,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (status == PlantStatus.SULANMIS) {
                            waterBtnDisabledBg
                        } else {
                            Color(0xFF2E7D32)
                        }
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Opacity,
                    contentDescription = "Water Now",
                    tint = if (status == PlantStatus.SULANMIS) {
                        Color(0xFF2E7D32).copy(alpha = 0.2f)
                    } else {
                        Color.White
                    },
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
