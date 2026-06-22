package com.example.nutriscanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    onScanClick: () -> Unit,
    onSearchClick: (String) -> Unit,
    onProfileClick: () -> Unit
) {
    var barcodeInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(30.dp)
    ) {
        IconButton(
            onClick = onProfileClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(5.dp)
                .background(
                    color = Color.White,
                    shape = CircleShape
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFF111827),
                    shape = CircleShape
                )
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = "Profil",
                tint = Color(0xFF111827),
                modifier = Modifier.size(40.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
        Spacer(modifier = Modifier.weight(1f))


        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFE8F5E9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Eco,
                contentDescription = "Logo",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))



        Text(
            text = "NutriScanner",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )

        Text(
            text = "Yediğiniz gıdaların içeriğini keşfedin.",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))


            Button(
                onClick = onScanClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111827))
            ) {
                Icon(Icons.Default.CenterFocusWeak, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text(text = "Barkod Tara", fontSize = 16.sp)
            }

        Spacer(modifier = Modifier.height(24.dp))


            OutlinedTextField(
                value = barcodeInput,
                onValueChange = { barcodeInput = it },
                placeholder = { Text("Veya barkod numarası girin", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF9FAFB),
                    unfocusedContainerColor = Color(0xFFF9FAFB),
                    focusedIndicatorColor = Color(0xFFE5E7EB),
                    unfocusedIndicatorColor = Color(0xFFE5E7EB)
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (barcodeInput.isNotBlank()) {
                            focusManager.clearFocus()
                            onSearchClick(barcodeInput)
                        }
                    }
                )
            )




            Spacer(modifier = Modifier.weight(1f))


        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            InfoCard(
                icon = Icons.Default.Info,
                title = "Detaylı İçerik",
                desc = "Ürünlerin besin değerlerini inceleyin.",
                color = Color(0xFFE3F2FD),
                iconColor = Color(0xFF2196F3),
                modifier = Modifier.weight(1f)
            )
            InfoCard(
                icon = Icons.Default.Bolt,
                title = "Hızlı Erişim",
                desc = "Saniyeler içinde sonuç alın.",
                color = Color(0xFFFFF3E0),
                iconColor = Color(0xFFFF9800),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun InfoCard(icon: ImageVector, title: String, desc: String, color: Color, iconColor: Color, modifier: Modifier) {
    Card(
        modifier = modifier.height(140.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = iconColor)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = title, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
            Text(text = desc, fontSize = 12.sp, color = Color.DarkGray, lineHeight = 16.sp)
        }
    }
}