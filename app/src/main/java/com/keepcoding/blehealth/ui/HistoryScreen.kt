package com.keepcoding.blehealth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.keepcoding.blehealth.data.model.HealthRecord

@Composable
internal fun HistoryScreen(
    modifier: Modifier,
    history: List<HealthRecord>,
    onOpenDevices: () -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { SectionTitle("同步历史") }
        if (history.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp)) {
                        Text("暂无历史记录", fontWeight = FontWeight.Bold)
                        Text("连接设备并完成一次同步后，记录会保存在这里。")
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(onClick = onOpenDevices) { Text("前往设备页") }
                    }
                }
            }
        } else {
            items(history, key = { it.id }) { record -> HistoryCard(record) }
        }
    }
}

@Composable
private fun HistoryCard(record: HealthRecord) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(record.deviceName, fontWeight = FontWeight.Bold)
                    Text(formatDateTime(record.measuredAt), style = MaterialTheme.typography.bodySmall)
                }
                AssistChip(
                    onClick = {},
                    label = { Text(if (record.isPartial) "部分数据" else "完整") },
                )
            }
            HorizontalDivider(Modifier.padding(vertical = 10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                HistoryMetric("心率", record.heartRateBpm?.let { "$it bpm" } ?: "暂无")
                HistoryMetric("血氧", record.bloodOxygenPercent?.let { "$it%" } ?: "暂无")
                HistoryMetric("步数", record.steps?.toString() ?: "暂无")
                HistoryMetric("睡眠", record.sleepMinutes?.let(::formatSleep) ?: "暂无")
            }
        }
    }
}

@Composable
private fun HistoryMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}
