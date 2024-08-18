package com.finance.trade_learn.view.commonui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

@Composable
fun SimpleBackButtonHeader(
    title: String,
    onBackClick: () -> Unit,
    backgroundColor: Color = Color(0xFF1E88E5) // Mavi arka plan rengi
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor) // Arka plan rengi
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White // Buton ikonu beyaz yapıldı
            )
        }

        Text(
            text = title,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White // Metin rengi beyaz yapıldı
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SimpleBackButtonHeaderPreview() {
    SimpleBackButtonHeader(title = "Page Title", onBackClick = {})
}
