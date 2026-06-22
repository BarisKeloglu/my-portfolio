package com.example.plantify.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.plantify.data.api.PlantifyApis
import com.example.plantify.data.model.Plant
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PlantDetailScreen(
    plant: Plant,
    onBack: () -> Unit,
    onWaterClick: () -> Unit,
    onAddCareNote: (String, String) -> Unit = { _, _ -> },
    onToggleCareNote: (String) -> Unit = { _ -> },
    onDeleteCareNote: (String) -> Unit = { _ -> },
    onDeletePlant: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var aiInsights by remember { mutableStateOf<String?>(null) }
    var loadingInsights by remember { mutableStateOf(false) }


    LaunchedEffect(plant.id) {
        loadingInsights = true
        aiInsights = PlantifyApis.getPlantInsights(plant.name, plant.type)
        loadingInsights = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                ) {
                    AsyncImage(
                        model = plant.imageUrl,
                        contentDescription = plant.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )


                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.4f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.75f)
                                    ),
                                    startY = 0f,
                                    endY = Float.POSITIVE_INFINITY
                                )
                            )
                    )


                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(top = 28.dp, start = 20.dp)
                            .size(52.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .align(Alignment.TopStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Geri Dön",
                            tint = Color.White
                        )
                    }


                    IconButton(
                        onClick = onDeletePlant,
                        modifier = Modifier
                            .padding(top = 28.dp, end = 20.dp)
                            .size(52.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.Red.copy(alpha = 0.2f))
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Sil",
                            tint = Color.Red
                        )
                    }


                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(24.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF2E7D32))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "Room",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = plant.room.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = plant.name,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            lineHeight = 42.sp,
                            letterSpacing = (-1).sp
                        )

                        Text(
                            text = plant.type,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }


            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DetailStatWidget(
                            icon = Icons.Default.WbSunny, 
                            label = "Işık", 
                            value = plant.sunRequirement.value,
                            modifier = Modifier.weight(1f)
                        )
                        DetailStatWidget(
                            icon = Icons.Default.Layers, 
                            label = "Toprak", 
                            value = plant.soilType,
                            modifier = Modifier.weight(1f)
                        )
                        DetailStatWidget(
                            icon = Icons.Default.CalendarToday, 
                            label = "Periyot", 
                            value = "${plant.wateringIntervalDays} Gün",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))


                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(32.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, Color(0xFF2E7D32).copy(alpha = 0.1f), RoundedCornerShape(32.dp))
                            .padding(24.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF2E7D32))
                                            .padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = "AI Sparks",
                                            tint = Color.White
                                        )
                                    }
                                    
                                    Text(
                                        text = "AI Bitki Analizi",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF0F172A)
                                    )
                                }


                                if (!loadingInsights && aiInsights != null) {
                                    IconButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                loadingInsights = true
                                                aiInsights = PlantifyApis.getPlantInsights(plant.name, plant.type)
                                                loadingInsights = false
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "Dene",
                                            tint = Color(0xFF94A3B8)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))


                            if (loadingInsights) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(
                                        color = Color(0xFF2E7D32),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Botanik zekası analiz ediyor...",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            } else {
                                Text(
                                    text = aiInsights ?: "Yapay zeka analizine şu an ulaşılamıyor.",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF475569),
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))


                    Text(
                        text = "Bakım Hatırlatıcıları",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(12.dp))


                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9).copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "YENİ MANUEL HATIRLATICI NOTU",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF64748B),
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            var noteContent by remember { mutableStateOf("") }


                            var selectedDaysOffset by remember { mutableStateOf(0) } 
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    0 to "Bugün",
                                    1 to "Yarın",
                                    3 to "3 Gün",
                                    7 to "1 Hafta"
                                ).forEach { (offset, label) ->
                                    val isPicked = selectedDaysOffset == offset
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isPicked) Color(0xFF2E7D32) else Color.White)
                                            .border(1.dp, if (isPicked) Color(0xFF2E7D32) else Color(0xFFCBD5E1).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                            .clickable { selectedDaysOffset = offset }
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isPicked) Color.White else Color(0xFF475569)
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = noteContent,
                                    onValueChange = { noteContent = it },
                                    placeholder = { Text("Örn: Sıvı gübre verilecek", fontSize = 13.sp) },
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF2E7D32),
                                        unfocusedBorderColor = Color(0xFFE2E8F0),
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White
                                    )
                                )
                                
                                Button(
                                    onClick = {
                                        if (noteContent.isNotBlank()) {
                                            val targetDate = java.time.LocalDate.now().plusDays(selectedDaysOffset.toLong()).toString()
                                            onAddCareNote(targetDate, noteContent.trim())
                                            noteContent = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                                    modifier = Modifier.height(50.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Ekle",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val turkishMonths = listOf(
                        "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
                        "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
                    )


                    if (plant.careNotes.isEmpty()) {
                        Text(
                            text = "Planlanmış özel bakım hatırlatıcısı yok.",
                            fontSize = 13.sp,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFF94A3B8),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        plant.careNotes.forEach { note ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {

                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .background(if (note.isCompleted) Color(0xFF2E7D32) else Color.Transparent)
                                                .border(2.dp, if (note.isCompleted) Color(0xFF2E7D32) else Color(0xFFCBD5E1), CircleShape)
                                                .clickable { onToggleCareNote(note.id) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (note.isCompleted) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Check",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                        
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = note.content,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (note.isCompleted) Color(0xFF94A3B8) else Color(0xFF0F172A),
                                                textDecoration = if (note.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else androidx.compose.ui.text.style.TextDecoration.None //
                                                    //
                                                //
                                            )
                                            
                                            val formattedNoteDate = try {
                                                val localDate = java.time.LocalDate.parse(note.date)
                                                val day = localDate.dayOfMonth
                                                val monthName = turkishMonths[localDate.monthValue - 1]
                                                "$day $monthName"
                                            } catch (e: Exception) {
                                                note.date
                                            }
                                            
                                            Text(
                                                text = "Planlanan Tarih: $formattedNoteDate",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF94A3B8)
                                            )
                                        }
                                    }
                                    
                                    IconButton(
                                        onClick = { onDeleteCareNote(note.id) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Sil",
                                            tint = Color(0xFFEF4444).copy(alpha = 0.8f),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))


                    Text(
                        text = "Bakım Günlüğü",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A)
                    )
                }
            }

            // History item list mapping
            if (plant.history.isEmpty()) {
                item {
                    Text(
                        text = "Henüz sulama kaydı bulunmuyor.",
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }
            } else {
                items(plant.history) { historyItem ->
                    HistoryItemCard(dateString = historyItem.date)
                }
            }
        }

        // Standard Absolute Action Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Button(
                onClick = onWaterClick,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Opacity,
                        contentDescription = "Water Now",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Sulandı İşaretle",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun DetailStatWidget(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFF8FAFC))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color(0xFF2E7D32)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = label.uppercase(),
                fontSize = 8.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF94A3B8),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A)
            )
        }
    }
}

@Composable
fun HistoryItemCard(dateString: String) {
    val formattedDate = remember(dateString) {
        try {
            val instant = Instant.parse(dateString)
            val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("tr"))
            formatter.format(instant)
        } catch (e: Exception) {
            dateString
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF2E7D32).copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success tick",
                    tint = Color(0xFF2E7D32)
                )
            }

            Column {
                Text(
                    text = "Sulama Yapıldı",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = formattedDate,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}
