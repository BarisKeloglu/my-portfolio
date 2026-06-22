package com.example.plantify.data.model

import java.time.Instant
import java.time.temporal.ChronoUnit

enum class SunRequirement(val value: String) {
    DUSUK("Düşük"),
    ORTA("Orta"),
    YUKSEK("Yüksek")
}

enum class PlantStatus(val value: String) {
    SULANMALI("Sulanmalı"),
    SULANMIS("Sulanmış"),
    KRITIK("Kritik")
}

data class WateringHistory(
    val date: String,
    val notes: String? = null
)

data class CareNote(
    val id: String,
    val date: String,
    val content: String,
    val isCompleted: Boolean = false
)

data class Plant(
    val id: String,
    val name: String,
    val type: String,
    val room: String,
    val wateringIntervalDays: Int,
    var lastWateredDate: String,
    val sunRequirement: SunRequirement,
    val soilType: String,
    val imageUrl: String,
    var history: List<WateringHistory> = emptyList(),
    var careNotes: List<CareNote> = emptyList()
) {

    fun getStatus(): PlantStatus {
        return try {
            val lastWateredInstant = Instant.parse(lastWateredDate)
            val today = Instant.now()
            
            val nextWateringInstant = lastWateredInstant.plus(wateringIntervalDays.toLong(), ChronoUnit.DAYS)
            
            val daysOverdue = ChronoUnit.DAYS.between(nextWateringInstant, today)
            
            when {
                daysOverdue > 1 -> PlantStatus.KRITIK
                daysOverdue >= 0 -> PlantStatus.SULANMALI
                else -> PlantStatus.SULANMIS
            }
        } catch (e: Exception) {
            PlantStatus.SULANMIS
        }
    }


    fun getHydrationProgress(): Float {
        return try {
            val lastWateredInstant = Instant.parse(lastWateredDate)
            val today = Instant.now()
            val nextWateringInstant = lastWateredInstant.plus(wateringIntervalDays.toLong(), ChronoUnit.DAYS)
            
            val totalIntervalMs = ChronoUnit.MILLIS.between(lastWateredInstant, nextWateringInstant).toFloat()
            val timeElapsedMs = ChronoUnit.MILLIS.between(lastWateredInstant, today).toFloat()
            
            val ratio = 1f - (timeElapsedMs / totalIntervalMs)
            ratio.coerceIn(0f, 1f)
        } catch (e: Exception) {
            1.0f
        }
    }
}
