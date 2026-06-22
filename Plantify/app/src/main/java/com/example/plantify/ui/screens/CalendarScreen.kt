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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.plantify.data.model.Plant
import com.example.plantify.data.model.PlantStatus
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun CalendarScreen(
    plants: List<Plant>,
    onWaterPlant: (String) -> Unit,
    onToggleCareNote: (String, String) -> Unit = { _, _ -> },
    onBack: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }

    val turkishMonths = listOf(
        "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
        "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
    )


    fun doesPlantNeedWaterOnDate(plant: Plant, date: LocalDate): Boolean {
        return try {
            val lastWateredInstant = Instant.parse(plant.lastWateredDate)
            val lastWateredLocalDate = LocalDate.ofInstant(lastWateredInstant, ZoneId.systemDefault())
            
            if (date.isBefore(lastWateredLocalDate)) return false
            if (date == lastWateredLocalDate) return false
            
            val daysBetween = ChronoUnit.DAYS.between(lastWateredLocalDate, date)
            daysBetween % plant.wateringIntervalDays == 0L
        } catch (e: Exception) {
            false
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 120.dp)
    ) {

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Takvim",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0F172A),
                    letterSpacing = (-1).sp
                )

                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Geri Dön",
                        tint = Color(0xFF94A3B8)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }


        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val monthName = try {
                            turkishMonths[currentYearMonth.monthValue - 1]
                        } catch (e: Exception) {
                            currentYearMonth.month.name
                        }
                        Text(
                            text = "$monthName ${currentYearMonth.year}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0F172A)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    currentYearMonth = YearMonth.now()
                                    selectedDate = LocalDate.now()
                                },
                                modifier = Modifier.padding(end = 4.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                val today = LocalDate.now()
                                val monthLabel = try { turkishMonths[today.monthValue - 1] } catch(e: Exception) { today.month.name }
                                Text(
                                    text = "BUGÜN (${today.dayOfMonth} $monthLabel)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF2E7D32)
                                )
                            }


                            IconButton(
                                onClick = { currentYearMonth = currentYearMonth.minusMonths(1) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFF1F5F9))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = "Önceki Ay",
                                    tint = Color(0xFF475569),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            

                            IconButton(
                                onClick = { currentYearMonth = currentYearMonth.plusMonths(1) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFF1F5F9))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Sonraki Ay",
                                    tint = Color(0xFF475569),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))


                    val daysLabels = listOf("Pt", "Sa", "Ça", "Pe", "Cu", "Ct", "Pz")
                    Row(modifier = Modifier.fillMaxWidth()) {
                        daysLabels.forEach { label ->
                            Text(
                                text = label,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFCBD5E1) // Soft matching text slate
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))


                    val lengthOfMonth = currentYearMonth.lengthOfMonth()
                    val firstDayOfWeek = currentYearMonth.atDay(1).dayOfWeek.value // 1 = Monday to 7 = Sunday
                    val emptyCellsBefore = firstDayOfWeek - 1

                    val cells = remember(currentYearMonth) {
                        val list = mutableListOf<LocalDate?>()
                        for (i in 0 until emptyCellsBefore) {
                            list.add(null)
                        }
                        for (day in 1..lengthOfMonth) {
                            list.add(currentYearMonth.atDay(day))
                        }
                        while (list.size % 7 != 0) {
                            list.add(null)
                        }
                        list
                    }

                    val rows = cells.chunked(7)
                    for (row in rows) {
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            for (date in row) {
                                if (date != null) {
                                    val isToday = date == LocalDate.now()
                                    val isSelected = date == selectedDate
                                    
                                    val plantsScheduled = plants.filter { doesPlantNeedWaterOnDate(it, date) }
                                    val hasWateringTask = plantsScheduled.isNotEmpty()

                                    val careNotesToday = plants.flatMap { p -> 
                                        p.careNotes.filter { it.date == date.toString() && !it.isCompleted }.map { p to it } 
                                    }
                                    val hasCareNoteTask = careNotesToday.isNotEmpty()

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .padding(2.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                when {
                                                    isSelected -> Color(0xFF2E7D32)
                                                    isToday -> Color(0xFFE8F5E9)
                                                    else -> Color.Transparent
                                                }
                                            )
                                            .clickable {
                                                selectedDate = date
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = date.dayOfMonth.toString(),
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected || isToday) FontWeight.Black else FontWeight.Bold,
                                                color = when {
                                                    isSelected -> Color.White
                                                    isToday -> Color(0xFF2E7D32)
                                                    else -> Color(0xFF475569)
                                                }
                                            )
                                            
                                            if (hasWateringTask || hasCareNoteTask) {
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    if (hasWateringTask) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(4.dp)
                                                                .clip(CircleShape)
                                                                .background(
                                                                    if (isSelected) Color.White else Color(0xFF0284C7)
                                                                )
                                                        )
                                                    }
                                                    if (hasCareNoteTask) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(4.dp)
                                                                .clip(CircleShape)
                                                                .background(
                                                                    if (isSelected) Color.White else Color(0xFFF97316)
                                                                )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }


        item {
            val selectedDateText = try {
                val monthLabel = turkishMonths[selectedDate.monthValue - 1]
                "${selectedDate.dayOfMonth} $monthLabel ${selectedDate.year}"
            } catch (e: Exception) {
                selectedDate.toString()
            }

            Text(
                text = "$selectedDateText Görevleri",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A),
                letterSpacing = (-0.5).sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }


        val selectedDatePlants = plants.filter { doesPlantNeedWaterOnDate(it, selectedDate) }


        val selectedDateNotes = plants.flatMap { p -> 
            p.careNotes.filter { it.date == selectedDate.toString() }.map { p to it }
        }

        if (selectedDatePlants.isEmpty() && selectedDateNotes.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Görev Yok",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Harika! Bugün yapılacak görev veya hatırlatıcı yok.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            if (selectedDatePlants.isNotEmpty()) {
                item {
                    Text(
                        text = "Rutin Sulama Görevleri",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF64748B),
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                    )
                }
                items(selectedDatePlants) { plant ->
                    TaskCalendarRow(
                        plant = plant,
                        onWaterClick = {
                            onWaterPlant(plant.id)
                        }
                    )
                }
            }

            if (selectedDateNotes.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Özel Bakım Hatırlatıcıları",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF64748B),
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                items(selectedDateNotes) { (plant, note) ->
                    TaskCareNoteRow(
                        plantName = plant.name,
                        content = note.content,
                        isCompleted = note.isCompleted,
                        onToggle = {
                            onToggleCareNote(plant.id, note.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskCareNoteRow(
    plantName: String,
    content: String,
    isCompleted: Boolean,
    onToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF1F5F9).copy(alpha = 0.7f))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {

                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(if (isCompleted) Color(0xFF2E7D32) else Color.Transparent)
                        .border(2.dp, if (isCompleted) Color(0xFF2E7D32) else Color(0xFFCBD5E1), CircleShape)
                        .clickable { onToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
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
                        text = content,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) Color(0xFF94A3B8) else Color(0xFF0F172A),
                        textDecoration = if (isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else androidx.compose.ui.text.style.TextDecoration.None
                    )
                    Text(
                        text = "Bitki: $plantName",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

@Composable
fun TaskCalendarRow(
    plant: Plant,
    onWaterClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF2E7D32).copy(alpha = 0.04f))
            .border(1.dp, Color(0xFF2E7D32).copy(alpha = 0.08f), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(plant.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = plant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF1F5F9))
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = plant.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${plant.room} • ${plant.wateringIntervalDays} günde bir",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }


            Button(
                onClick = onWaterClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                modifier = Modifier.height(38.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "Sulandı",
                        modifier = Modifier.size(14.dp),
                        tint = Color.White
                    )
                    Text(
                        text = "SULADIM",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}
