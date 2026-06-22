package com.example.plantify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.plantify.data.repository.PlantRepository
import com.example.plantify.ui.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlantRepository.initialize(applicationContext)
        setContent {
            MaterialTheme {
                PlantifyApp()
            }
        }
    }
}

enum class ActiveTab {
    DASHBOARD,
    CALENDAR,
    SETTINGS,
    ADD,
    DETAIL
}

@Composable
fun PlantifyApp() {
    var activeTab by remember { mutableStateOf(ActiveTab.DASHBOARD) }
    var selectedPlantId by remember { mutableStateOf<String?>(null) }
    

    val plants = PlantRepository.plants

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val today = java.time.LocalDate.now()
        val todayStr = today.toString()
        

        val wateringDuePlants = plants.filter { plant ->
            try {
                val lastWateredInstant = java.time.Instant.parse(plant.lastWateredDate)
                val lastWateredLocalDate = java.time.LocalDate.ofInstant(lastWateredInstant, java.time.ZoneId.systemDefault())
                if (!today.isBefore(lastWateredLocalDate) && today != lastWateredLocalDate) {
                    val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(lastWateredLocalDate, today)
                    daysBetween % plant.wateringIntervalDays == 0L
                } else {
                    false
                }
            } catch (e: Exception) {
                false
            }
        }
        

        val careNotesToday = plants.flatMap { plant ->
            plant.careNotes.filter { it.date == todayStr && !it.isCompleted }.map { plant to it }
        }
        

        wateringDuePlants.forEach { plant ->
            com.example.plantify.ui.util.NotificationHelper.sendNotification(
                context = context,
                title = "Sulama Zamanı: ${plant.name}",
                message = "Bugün ${plant.name} bitkinizin rutin sulama zamanı geldi (${plant.room})."
            )
        }
        
        careNotesToday.forEach { (plant, note) ->
            com.example.plantify.ui.util.NotificationHelper.sendNotification(
                context = context,
                title = "Bakım Hatırlatıcısı: ${plant.name}",
                message = "${plant.name} için bugünkü not: ${note.content}"
            )
        }
    }

    val selectedPlant = remember(selectedPlantId, plants.size) {
        plants.find { it.id == selectedPlantId }
    }

    val isDark = PlantRepository.isDarkMode.value
    val backgroundColor = if (isDark) Color(0xFF121614) else Color(0xFFF8FAFC)
    val navBarColor = if (isDark) Color(0xFF1E2521) else Color.White

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {

        AnimatedContent(
            targetState = activeTab,
            transitionSpec = {
                fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
            },
            label = "ScreenTransition"
        ) { targetScreen ->
            when (targetScreen) {
                ActiveTab.DASHBOARD -> {
                    DashboardScreen(
                        plants = plants,
                        onWaterPlant = { plantId ->
                            PlantRepository.waterPlant(plantId)
                        },
                        onPlantSelect = { plantId ->
                            selectedPlantId = plantId
                            activeTab = ActiveTab.DETAIL
                        },
                        onAddPlantClick = {
                            activeTab = ActiveTab.ADD
                        }
                    )
                }
                ActiveTab.ADD -> {
                    AddPlantScreen(
                        onSave = { name, scientific, room, interval, sun, soil, imagePath ->
                            PlantRepository.addPlant(
                                name = name,
                                type = scientific,
                                room = room,
                                wateringIntervalDays = interval,
                                sunRequirement = sun,
                                soilType = soil,
                                imageUrl = imagePath
                            )
                            activeTab = ActiveTab.DASHBOARD
                        },
                        onCancel = {
                            activeTab = ActiveTab.DASHBOARD
                        }
                    )
                }
                ActiveTab.DETAIL -> {
                    val currentPlant = plants.find { it.id == selectedPlantId }
                    if (currentPlant != null) {
                        PlantDetailScreen(
                            plant = currentPlant,
                            onBack = {
                                activeTab = ActiveTab.DASHBOARD
                            },
                            onWaterClick = {
                                PlantRepository.waterPlant(currentPlant.id)
                            },
                            onAddCareNote = { date, content ->
                                PlantRepository.addCareNote(currentPlant.id, date, content)
                            },
                            onToggleCareNote = { noteId ->
                                PlantRepository.toggleCareNote(currentPlant.id, noteId)
                            },
                            onDeleteCareNote = { noteId ->
                                PlantRepository.deleteCareNote(currentPlant.id, noteId)
                            },
                            onDeletePlant = {
                                PlantRepository.deletePlant(currentPlant.id)
                                activeTab = ActiveTab.DASHBOARD
                            }
                        )
                    } else {
                        activeTab = ActiveTab.DASHBOARD
                    }
                }
                ActiveTab.CALENDAR -> {
                    CalendarScreen(
                        plants = plants,
                        onWaterPlant = { plantId ->
                            PlantRepository.waterPlant(plantId)
                        },
                        onToggleCareNote = { plantId, noteId ->
                            PlantRepository.toggleCareNote(plantId, noteId)
                        },
                        onBack = {
                            activeTab = ActiveTab.DASHBOARD
                        }
                    )
                }
                ActiveTab.SETTINGS -> {
                    SettingsScreen(
                        onBack = {
                            activeTab = ActiveTab.DASHBOARD
                        }
                    )
                }
            }
        }


        val showNavBar = activeTab != ActiveTab.ADD && activeTab != ActiveTab.DETAIL
        if (showNavBar) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(navBarColor)
                    .padding(bottom = 24.dp, top = 12.dp, start = 16.dp, end = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavBarItem(
                        active = activeTab == ActiveTab.DASHBOARD,
                        icon = Icons.Default.Home,
                        label = "Home",
                        onClick = { activeTab = ActiveTab.DASHBOARD }
                    )
                    
                    NavBarItem(
                        active = activeTab == ActiveTab.CALENDAR,
                        icon = Icons.Default.CalendarToday,
                        label = "Takvim",
                        onClick = { activeTab = ActiveTab.CALENDAR }
                    )
                    
                    NavBarItem(
                        active = activeTab == ActiveTab.SETTINGS,
                        icon = Icons.Default.Settings,
                        label = "Ayarlar",
                        onClick = { activeTab = ActiveTab.SETTINGS }
                    )
                }
            }
        }
    }
}

@Composable
fun NavBarItem(
    active: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    val isDark = PlantRepository.isDarkMode.value
    val activeColor = Color(0xFF2E7D32)
    val inactiveColor = if (isDark) Color(0xFF64748B) else Color(0xFFCBD5E1)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (active) activeColor else inactiveColor,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label.uppercase(),
                fontSize = 8.sp,
                fontWeight = FontWeight.Black,
                color = if (active) activeColor else inactiveColor,
                letterSpacing = 1.sp
            )
        }
    }
}
