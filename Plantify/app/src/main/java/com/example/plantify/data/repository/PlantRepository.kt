package com.example.plantify.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.plantify.data.model.Plant
import com.example.plantify.data.model.SunRequirement
import com.example.plantify.data.model.WateringHistory
import com.example.plantify.data.model.CareNote
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

object PlantRepository {
    val plants = mutableStateListOf<Plant>()
    private var sharedPreferences: SharedPreferences? = null
    private val gson = Gson()
    private const val PREFS_NAME = "Plantify_prefs"
    private const val KEY_PLANTS = "plants_list"

    val isDarkMode = mutableStateOf(false)
    val sunNotificationsEnabled = mutableStateOf(false)

    fun initialize(context: Context) {
        if (sharedPreferences != null) return
        sharedPreferences = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isDarkMode.value = sharedPreferences?.getBoolean("dark_mode", false) ?: false
        sunNotificationsEnabled.value = sharedPreferences?.getBoolean("sun_notifications", false) ?: false
        loadPlants()
    }

    fun toggleDarkMode(value: Boolean) {
        isDarkMode.value = value
        sharedPreferences?.edit()?.putBoolean("dark_mode", value)?.apply()
    }

    fun toggleSunNotifications(value: Boolean) {
        sunNotificationsEnabled.value = value
        sharedPreferences?.edit()?.putBoolean("sun_notifications", value)?.apply()
    }

    private fun loadPlants() {
        val prefs = sharedPreferences ?: return
        val json = prefs.getString(KEY_PLANTS, null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<Plant>>() {}.type
                val loadedPlants: List<Plant> = gson.fromJson(json, type)
                plants.clear()
                plants.addAll(loadedPlants)
            } catch (e: Exception) {
                e.printStackTrace()
                setupInitialMockPlants()
            }
        } else {
            setupInitialMockPlants()
        }
    }

    private fun savePlants() {
        val prefs = sharedPreferences ?: return
        try {
            val json = gson.toJson(plants.toList())
            prefs.edit().putString(KEY_PLANTS, json).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupInitialMockPlants() {
        val now = Instant.now()
        val todayStr = java.time.LocalDate.now().toString()
        val tomorrowStr = java.time.LocalDate.now().plusDays(1).toString()
        val dayAfterStr = java.time.LocalDate.now().plusDays(2).toString()
        
        plants.clear()
        plants.add(
            Plant(
                id = "1",
                name = "Salon Sarmaşığı",
                type = "Epipremnum aureum",
                room = "Salon",
                wateringIntervalDays = 7,
                lastWateredDate = now.minus(6, ChronoUnit.DAYS).toString(),
                sunRequirement = SunRequirement.ORTA,
                soilType = "Torflu karışım",
                imageUrl = "https://images.unsplash.com/photo-1596547609652-9cf5d8d76921?auto=format&fit=crop&q=80&w=800",
                history = listOf(
                    WateringHistory(now.minus(6, ChronoUnit.DAYS).toString()),
                    WateringHistory(now.minus(13, ChronoUnit.DAYS).toString())
                ),
                careNotes = listOf(
                    CareNote(
                        id = "c1",
                        date = todayStr,
                        content = "Saksı yönünü güneşe doğru çevir.",
                        isCompleted = false
                    ),
                    CareNote(
                        id = "c2",
                        date = dayAfterStr,
                        content = "Sıvı gübre takviyesi yap.",
                        isCompleted = false
                    )
                )
            )
        )
        plants.add(
            Plant(
                id = "2",
                name = "Paşa Kılıcı",
                type = "Sansevieria",
                room = "Yatak Odası",
                wateringIntervalDays = 14,
                lastWateredDate = now.minus(12, ChronoUnit.DAYS).toString(),
                sunRequirement = SunRequirement.DUSUK,
                soilType = "Kaktüs toprağı",
                imageUrl = "https://picsum.photos/seed/snakeplant/400/400",
                history = listOf(
                    WateringHistory(now.minus(12, ChronoUnit.DAYS).toString())
                ),
                careNotes = listOf(
                    CareNote(
                        id = "c3",
                        date = tomorrowStr,
                        content = "Yaprakların tozunu nemli bezle al.",
                        isCompleted = false
                    )
                )
            )
        )
        plants.add(
            Plant(
                id = "3",
                name = "Barış Çiçeği",
                type = "Spathiphyllum",
                room = "Mutfak",
                wateringIntervalDays = 3,
                lastWateredDate = now.minus(4, ChronoUnit.DAYS).toString(),
                sunRequirement = SunRequirement.YUKSEK,
                soilType = "Nem tutan toprak",
                imageUrl = "https://picsum.photos/seed/peace/400/400",
                history = listOf(
                    WateringHistory(now.minus(4, ChronoUnit.DAYS).toString())
                ),
                careNotes = emptyList()
            )
        )
        savePlants()
    }

    fun addPlant(
        name: String,
        type: String,
        room: String,
        wateringIntervalDays: Int,
        sunRequirement: SunRequirement,
        soilType: String,
        imageUrl: String?
    ) {
        val finalImage = if (imageUrl.isNullOrEmpty()) {
            "https://picsum.photos/seed/${name}/600/400"
        } else {
            imageUrl
        }
        val newPlant = Plant(
            id = UUID.randomUUID().toString(),
            name = name,
            type = type,
            room = room,
            wateringIntervalDays = wateringIntervalDays,
            lastWateredDate = Instant.now().toString(),
            sunRequirement = sunRequirement,
            soilType = soilType,
            imageUrl = finalImage,
            history = listOf(WateringHistory(Instant.now().toString()))
        )
        plants.add(0, newPlant)
        savePlants()
    }

    fun waterPlant(plantId: String) {
        val index = plants.indexOfFirst { it.id == plantId }
        if (index != -1) {
            val originalPlant = plants[index]
            val nowStr = Instant.now().toString()
            val updatedHistory = listOf(WateringHistory(nowStr)) + originalPlant.history
            

            plants[index] = originalPlant.copy(
                lastWateredDate = nowStr,
                history = updatedHistory
            )
            savePlants()
        }
    }

    fun addCareNote(plantId: String, date: String, content: String) {
        val index = plants.indexOfFirst { it.id == plantId }
        if (index != -1) {
            val originalPlant = plants[index]
            val newNote = CareNote(
                id = UUID.randomUUID().toString(),
                date = date,
                content = content,
                isCompleted = false
            )
            val updatedNotes = originalPlant.careNotes + newNote
            plants[index] = originalPlant.copy(careNotes = updatedNotes)
            savePlants()
        }
    }

    fun toggleCareNote(plantId: String, noteId: String) {
        val index = plants.indexOfFirst { it.id == plantId }
        if (index != -1) {
            val originalPlant = plants[index]
            val updatedNotes = originalPlant.careNotes.map {
                if (it.id == noteId) {
                    it.copy(isCompleted = !it.isCompleted)
                } else {
                    it
                }
            }
            plants[index] = originalPlant.copy(careNotes = updatedNotes)
            savePlants()
        }
    }

    fun deleteCareNote(plantId: String, noteId: String) {
        val index = plants.indexOfFirst { it.id == plantId }
        if (index != -1) {
            val originalPlant = plants[index]
            val updatedNotes = originalPlant.careNotes.filter { it.id != noteId }
            plants[index] = originalPlant.copy(careNotes = updatedNotes)
            savePlants()
        }
    }

    fun deletePlant(plantId: String) {
        val index = plants.indexOfFirst { it.id == plantId }
        if (index != -1) {
            plants.removeAt(index)
            savePlants()
        }
    }
}
