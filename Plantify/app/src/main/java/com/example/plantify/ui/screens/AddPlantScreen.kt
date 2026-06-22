package com.example.plantify.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.plantify.data.api.PlantifyApis
import com.example.plantify.data.model.SunRequirement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantScreen(
    onSave: (name: String, scientific: String, room: String, interval: Int, sun: SunRequirement, soil: String, imagePath: String?) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf("") }
    var scientificType by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("Salon") }
    var wateringIntervalDays by remember { mutableStateOf("7") }
    var sunRequirement by remember { mutableStateOf(SunRequirement.ORTA) }
    var soilType by remember { mutableStateOf("Standart Toprak") }
    
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var localOptimizedFilePath by remember { mutableStateOf<String?>(null) }
    

    var isIdentifying by remember { mutableStateOf(false) }
    var analysisProgress by remember { mutableStateOf(0f) }


    val infiniteTransition = rememberInfiniteTransition(label = "LaserScan")
    val laserYRatio by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LaserLine"
    )


    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            

            coroutineScope.launch {
                isIdentifying = true
                analysisProgress = 0.1f
                
                try {

                    analysisProgress = 0.25f
                    val optimizedFile = copyAndCompressUri(context, uri)
                    if (optimizedFile != null) {
                        localOptimizedFilePath = optimizedFile.absolutePath
                        analysisProgress = 0.45f
                        

                        val parsedKeysResult = PlantifyApis.autoIdentifyPlantWithPlAntNet(optimizedFile)
                        analysisProgress = 0.85f
                        
                        if (parsedKeysResult != null) {
                            val (latinName, turkishName) = parsedKeysResult
                            scientificType = latinName
                            name = turkishName
                            Toast.makeText(context, "Bitki Başarıyla Tanındı: $turkishName", Toast.LENGTH_SHORT).show()
                        } else {
                            scientificType = "Tanımlanamadı"
                            Toast.makeText(context, "Pl@ntNet bitki türünü seçemedi. Lütfen elle girin.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, "Görsel işlenirken hata oluştu.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("AddPlantScreen", "Net classification check failed", e)
                    scientificType = "Bağlantı Başarısız"
                } finally {
                    analysisProgress = 1f
                    isIdentifying = false
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
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
                IconButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF1F5F9))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Geri Dön",
                        tint = Color(0xFF0F172A)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(width = 64.dp, height = 6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFE2E8F0))
                )

                Spacer(modifier = Modifier.width(48.dp)) // Equalizer horizontal weight
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Yeni Üye",
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A),
                letterSpacing = (-1.5).sp
            )
            
            Text(
                text = "Bahçene yeni bir dost ekle!",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(30.dp))


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .background(Color(0xFFF8FAFC))
                    .border(
                        width = 2.dp,
                        color = if (selectedImageUri != null) Color(0xFF2E7D32) else Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(36.dp)
                    )
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Görseli Değiştir",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                } else {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Camera Icon",
                            modifier = Modifier.size(44.dp),
                            tint = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Fotoğraf Çek / Görsel Seç",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF475569)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Pl@ntNet Yapay Zeka Desteğiyle",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))


            Text(
                text = "Bitki Adı",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF94A3B8),
                letterSpacing = 1.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Örn: Salon Sarmaşığı veya Benjamin Bey") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Tür / Cins",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF94A3B8),
                letterSpacing = 1.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
            OutlinedTextField(
                value = scientificType,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("AI ile otomatik taranacak...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE2E8F0),
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color(0xFFF8FAFC),
                    unfocusedContainerColor = Color(0xFFF8FAFC)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Konum",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                    
                    var expandedLocation by remember { mutableStateOf(false) }
                    Box {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF1F5F9))
                                .clickable { expandedLocation = true }
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(room, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                            }
                        }
                        
                        DropdownMenu(
                            expanded = expandedLocation,
                            onDismissRequest = { expandedLocation = false }
                        ) {
                            listOf("Salon", "Balkon", "Yatak Odası", "Mutfak", "Koridor").forEach { loc ->
                                DropdownMenuItem(
                                    text = { Text(loc) },
                                    onClick = {
                                        room = loc
                                        expandedLocation = false
                                    }
                                )
                            }
                        }
                    }
                }


                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Döngü (Gün)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = wateringIntervalDays,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() }) {
                                wateringIntervalDays = input
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2E7D32),
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))


            Button(
                onClick = {
                    val days = wateringIntervalDays.toIntOrNull() ?: 7
                    onSave(
                        name,
                        scientificType.ifEmpty { "Plan" },
                        room,
                        days,
                        sunRequirement,
                        soilType,
                        localOptimizedFilePath
                    )
                },
                enabled = name.isNotEmpty() && !isIdentifying,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32),
                    disabledContainerColor = Color(0xFF2E7D32).copy(alpha = 0.3f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                Text(
                    text = "Bitkiyi Ekle",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }


        if (isIdentifying) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xE42E7D32))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.size(200.dp)) {

                        CircularProgressIndicator(
                            progress = analysisProgress,
                            color = Color.White,
                            strokeWidth = 6.dp,
                            modifier = Modifier.fillMaxSize()
                        )
                        
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterCenterFocus,
                                contentDescription = "Scan Icon",
                                tint = Color.White,
                                modifier = Modifier.size(80.dp)
                            )
                        }


                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val laserY = size.height * laserYRatio
                            drawLine(
                                color = Color.White,
                                start = Offset(0f, laserY),
                                end = Offset(size.width, laserY),
                                strokeWidth = 4f
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(36.dp))
                    
                    Text(
                        text = "Yapay Zeka Analiz Ediyor...",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pl@ntNet botanik veritabanında en uygun türü arıyoruz.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}


private suspend fun copyAndCompressUri(context: Context, uri: Uri): File? = withContext(Dispatchers.IO) {
    try {
        val cacheDir = context.cacheDir
        val tempFile = File(cacheDir, "plant_cache_${UUID.randomUUID()}.jpg")
        
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        if (inputStream != null) {
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            
            if (originalBitmap != null) {

                val maxBound = 1200
                var newWidth = originalBitmap.width
                var newHeight = originalBitmap.height
                
                if (newWidth > maxBound || newHeight > maxBound) {
                    if (newWidth > newHeight) {
                        newHeight = (newHeight * (maxBound.toFloat() / newWidth)).toInt()
                        newWidth = maxBound
                    } else {
                        newWidth = (newWidth * (maxBound.toFloat() / newHeight)).toInt()
                        newHeight = maxBound
                    }
                }
                
                val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
                val outputStream = FileOutputStream(tempFile)
                

                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                outputStream.flush()
                outputStream.close()
                
                return@withContext tempFile
            }
        }
    } catch (e: Exception) {
        Log.e("CopyCompressUri", "Failed to cache selected photo locally", e)
    }
    return@withContext null
}
