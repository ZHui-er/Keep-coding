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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.keepcoding.blehealth.data.model.ConnectionState
import com.keepcoding.blehealth.data.model.ScanState
import com.keepcoding.blehealth.data.model.WearableDevice

@Composable
internal fun DeviceScreen(
    modifier: Modifier,
    scanState: ScanState,
    connectionState: ConnectionState,
    onScan: () -> Unit,
    onConnect: (WearableDevice) -> Unit,
    onDisconnect: () -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            SectionTitle("当前连接")
            ConnectionPanel(connectionState, onDisconnect)
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SectionTitle("附近设备")
                Button(
                    enabled = scanState !is ScanState.Scanning,
                    onClick = onScan,
                ) {
                    Text(if (scanState is ScanState.Scanning) "扫描中" else "开始扫描")
                }
            }
        }

        when (scanState) {
            ScanState.Idle -> item { HintCard("点击“开始扫描”发现 Mock BLE 设备") }
            ScanState.Scanning -> item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.width(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("正在扫描附近设备…")
                }
            }
            ScanState.Empty -> item {
                StatusPanel("未发现设备，请重新扫描或切换演示场景", isError = true)
            }
            is ScanState.Failed -> item { StatusPanel(scanState.message, isError = true) }
            is ScanState.Results -> items(scanState.devices, key = { it.id }) { device ->
                DeviceCard(
                    device = device,
                    connectionState = connectionState,
                    onConnect = { onConnect(device) },
                )
            }
        }
    }
}

@Composable
private fun ConnectionPanel(
    state: ConnectionState,
    onDisconnect: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            when (state) {
                ConnectionState.Disconnected -> {
                    Text("未连接", fontWeight = FontWeight.Bold)
                    Text("扫描并选择一台设备进行连接")
                }
                is ConnectionState.Connecting -> {
                    Text("正在连接 ${state.device.name}", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    CircularProgressIndicator(modifier = Modifier.width(24.dp))
                }
                is ConnectionState.Connected -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                state.device.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Text("型号 ${state.device.model} · 固件 ${state.device.firmware}")
                            Text("电量 ${state.device.batteryPercent}% · RSSI ${state.device.rssi} dBm")
                        }
                        StatusBadge("已连接")
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = onDisconnect) { Text("断开连接") }
                }
                is ConnectionState.Failed -> StatusPanel(state.message, isError = true)
            }
        }
    }
}

@Composable
private fun DeviceCard(
    device: WearableDevice,
    connectionState: ConnectionState,
    onConnect: () -> Unit,
) {
    val isConnected = (connectionState as? ConnectionState.Connected)?.device?.id == device.id
    val isConnecting = (connectionState as? ConnectionState.Connecting)?.device?.id == device.id

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(device.name, fontWeight = FontWeight.Bold)
                Text("${device.model} · ${device.rssi} dBm")
                Text(device.id, style = MaterialTheme.typography.labelSmall)
            }
            Button(
                enabled = !isConnected && !isConnecting,
                onClick = onConnect,
            ) {
                Text(
                    when {
                        isConnected -> "已连接"
                        isConnecting -> "连接中"
                        else -> "连接"
                    },
                )
            }
        }
    }
}
