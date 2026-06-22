package com.example.plantify.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantify.data.api.PlantifyApis
import com.example.plantify.data.repository.PlantRepository
import kotlinx.coroutines.launch
import com.example.plantify.ui.util.NotificationHelper
import com.example.plantify.data.model.SunRequirement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    val isDark = PlantRepository.isDarkMode.value
    var notificationsEnabled by remember { mutableStateOf(true) }
    val sunNotificationsEnabled = PlantRepository.sunNotificationsEnabled.value


    val backgroundColor = if (isDark) Color(0xFF121614) else Color(0xFFF8FAFC)
    val cardColor = if (isDark) Color(0xFF1E2521) else Color.White
    val primaryTextColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F172A)
    val secondaryTextColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
    val inputColor = if (isDark) Color(0xFF171C19) else Color.White


    var plantNetKeyInput by remember { mutableStateOf(PlantifyApis.plantNetApiKey) }
    var geminiKeyInput by remember { mutableStateOf(PlantifyApis.geminiApiKey) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ayarlar",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = primaryTextColor,
                letterSpacing = (-1).sp
            )

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(cardColor)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Geri Dön",
                    tint = secondaryTextColor
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))


        Text(
            text = "API ANAHTARLARI",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = secondaryTextColor,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )


        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Pl@ntNet API Key",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = secondaryTextColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = plantNetKeyInput,
                    onValueChange = { 
                        plantNetKeyInput = it
                        PlantifyApis.plantNetApiKey = it
                    },
                    placeholder = { Text("Pl@ntNet API Key girin...", color = secondaryTextColor.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = primaryTextColor,
                        unfocusedTextColor = primaryTextColor,
                        focusedContainerColor = inputColor,
                        unfocusedContainerColor = inputColor,
                        focusedBorderColor = Color(0xFF2E7D32),
                        unfocusedBorderColor = if (isDark) Color(0xFF2E3531) else Color(0xFFE2E8F0)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))


        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Gemini API Key",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = secondaryTextColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = geminiKeyInput,
                    onValueChange = { 
                        geminiKeyInput = it
                        PlantifyApis.geminiApiKey = it
                    },
                    placeholder = { Text("Gemini Pro API Key girin...", color = secondaryTextColor.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = primaryTextColor,
                        unfocusedTextColor = primaryTextColor,
                        focusedContainerColor = inputColor,
                        unfocusedContainerColor = inputColor,
                        focusedBorderColor = Color(0xFF2E7D32),
                        unfocusedBorderColor = if (isDark) Color(0xFF2E3531) else Color(0xFFE2E8F0)
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Girilmediğinde veya boş bırakıldığında, sistem yerel çevrimdışı botanik verilerini kullanarak kesintisiz öneri üretir.",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = secondaryTextColor,
                    lineHeight = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "UYGULAMA SEÇENEKLERİ",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = secondaryTextColor,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )


        SettingsToggleRow(
            icon = Icons.Default.Notifications,
            label = "Sulama Hatırlatıcıları",
            value = if (notificationsEnabled) "AÇIK" else "KAPALI",
            checked = notificationsEnabled,
            onCheckedChange = { notificationsEnabled = it }
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingsToggleRow(
            icon = Icons.Default.Analytics,
            label = "Güneş Bildirimleri",
            value = if (sunNotificationsEnabled) "AÇIK" else "KAPALI",
            checked = sunNotificationsEnabled,
            onCheckedChange = {
                PlantRepository.toggleSunNotifications(it)
                if (it) {
                    Toast.makeText(context, "Yapay Zeka Güneş Analizleri aktif!", Toast.LENGTH_SHORT).show()
                    val sunLoving = PlantRepository.plants.find { p -> p.sunRequirement == SunRequirement.YUKSEK }
                    val name = sunLoving?.name ?: "Barış Çiçeği"
                    NotificationHelper.sendNotification(
                        context = context,
                        title = "Güneş Işığı Analizi: $name",
                        message = "$name bitkiniz yüksek ışık seviyor. Bugün güneşlenme banyosu alması için ideal!"
                    )
                }
            }
        )


        if (sunNotificationsEnabled) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF2C2417) else Color(0xFFFEF3C7))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF59E0B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Analiz",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "YAPAY ZEKA GÜNEŞ TAKİP RAPORU",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706),
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Bu özellik, bitkilerinizin (Çok, Orta, Az Işık) gereksinimlerini oda düzeyinde analiz ederek anlık konumlandırma uyarıları üretir. Yaprak yanmasını önler.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) Color(0xFFFDE68A) else Color(0xFF78350F),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val activePlants = PlantRepository.plants
                    if (activePlants.isEmpty()) {
                        Text(
                            text = "Evinizde kayıtlı bitki bulunamadı.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
                        )
                    } else {
                        activePlants.take(3).forEach { p ->
                            val req = p.sunRequirement
                            val desc = if (req == SunRequirement.YUKSEK) {
                                "Yüksek ışık ister. Pencere kenarına konumlandırın."
                            } else if (req == SunRequirement.DUSUK) {
                                "Doğrudan kızgın güneş istemez, yarı gölgeye alın."
                            } else {
                                "Dengeli gün ışığı sever. Tül arkasından süzün."
                            }
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("☀️", fontSize = 12.sp)
                                Text(
                                    text = "${p.name} (${p.room}): $desc",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1E293B)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        SettingsToggleRow(
            icon = Icons.Default.DarkMode,
            label = "Gece Modu",
            value = if (isDark) "AÇIK" else "KAPALI",
            checked = isDark,
            onCheckedChange = {
                PlantRepository.toggleDarkMode(it)
                Toast.makeText(context, if (it) "Karanlık Tema etkinleştirildi" else "Aydınlık Tema etkinleştirildi", Toast.LENGTH_SHORT).show()
            }
        )

        Spacer(modifier = Modifier.height(24.dp))


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(if (isDark) Color(0xFF4B1C1C) else Color(0xFFFEF2F2))
                .clickable {
                    PlantRepository.plants.clear()
                    Toast.makeText(context, "Tüm bitki verileri başarıyla sıfırlandı.", Toast.LENGTH_SHORT).show()
                }
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Tüm Verileri Sıfırla",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFEF4444)
            )
        }
    }
}

@Composable
fun SettingsToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val isDark = PlantRepository.isDarkMode.value
    val cardColor = if (isDark) Color(0xFF1E2521) else Color.White
    val primaryTextColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F172A)
    val secondaryTextColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
    val iconBgColor = if (isDark) Color(0xFF171C19) else Color(0xFFF1F5F9)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = secondaryTextColor
                    )
                }

                Column {
                    Text(
                        text = label,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = primaryTextColor
                    )
                    Text(
                        text = value,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = secondaryTextColor
                    )
                }
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF2E7D32)
                )
            )
        }
    }
}
