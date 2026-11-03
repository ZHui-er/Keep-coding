package com.keepcoding.blehealth.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
internal fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}

@Composable
internal fun StatusBadge(text: String) {
    AssistChip(onClick = {}, label = { Text(text) })
}

@Composable
internal fun HintCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Text(message, modifier = Modifier.padding(16.dp))
    }
}

@Composable
internal fun StatusPanel(
    message: String,
    isError: Boolean,
    showProgress: Boolean = false,
) {
    val container = if (isError) Color(0xFFFEE2E2) else Color(0xFFCCFBF1)
    val content = if (isError) Color(0xFF991B1B) else Color(0xFF115E59)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = container, contentColor = content),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (showProgress) {
                CircularProgressIndicator(
                    modifier = Modifier.width(22.dp),
                    color = content,
                    strokeWidth = 2.dp,
                )
                Spacer(Modifier.width(10.dp))
            }
            Text(message)
        }
    }
}

internal fun formatSleep(minutes: Int): String = "${minutes / 60}h ${minutes % 60}m"

internal fun formatDateTime(timestamp: Long): String = SimpleDateFormat(
    "MM-dd HH:mm",
    Locale.CHINA,
).format(Date(timestamp))
