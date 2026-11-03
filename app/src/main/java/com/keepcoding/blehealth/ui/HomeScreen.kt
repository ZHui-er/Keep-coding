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
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.keepcoding.blehealth.data.model.ConnectionState
import com.keepcoding.blehealth.data.model.HealthRecord
import com.keepcoding.blehealth.data.model.SyncState

@Composable
internal fun HomeScreen(
    modifier: Modifier,
    connectionState: ConnectionState,
    syncState: SyncState,
    latestRecord: HealthRecord?,
    onSync: () -> Unit,
    onOpenDevices: () -> Unit,
) {
    val connectedDevice = (connectionState as? ConnectionState.Connected)?.device

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            SectionTitle("当前设备")
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                connectedDevice?.name ?: "未连接设备",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                connectedDevice?.let {
                                    "${it.model} · 固件 ${it.firmware} · 电量 ${it.batteryPercent}%"
                                } ?: "连接 Mock 设备后可同步健康数据",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        if (connectedDevice != null) StatusBadge("已连接")
                    }
                    if (connectedDevice == null) {
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(onClick = onOpenDevices) { Text("前往设备页") }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SectionTitle("最新健康数据")
                Text(
                    latestRecord?.let { formatDateTime(it.syncedAt) } ?: "尚未同步",
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MetricCard("心率", latestRecord?.heartRateBpm?.let { "$it bpm" } ?: "--", Modifier.weight(1f))
                MetricCard("血氧", latestRecord?.bloodOxygenPercent?.let { "$it%" } ?: "--", Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MetricCard("步数", latestRecord?.steps?.toString() ?: "--", Modifier.weight(1f))
                MetricCard("睡眠", latestRecord?.sleepMinutes?.let(::formatSleep) ?: "暂无数据", Modifier.weight(1f))
            }
        }

        item {
            when (syncState) {
                SyncState.Idle -> Unit
                SyncState.Syncing -> StatusPanel("正在同步健康数据…", isError = false, showProgress = true)
                is SyncState.Success -> StatusPanel(syncState.message, isError = false)
                is SyncState.Failed -> StatusPanel(syncState.message, isError = true)
            }
            Spacer(Modifier.height(8.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = connectedDevice != null && syncState !is SyncState.Syncing,
                onClick = onSync,
            ) {
                Text(if (syncState is SyncState.Syncing) "同步中" else "同步健康数据")
            }
        }
    }
}
