package com.keepcoding.blehealth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.keepcoding.blehealth.data.mock.DemoScenario
import com.keepcoding.blehealth.data.model.ConnectionState
import com.keepcoding.blehealth.data.model.HealthRecord
import com.keepcoding.blehealth.data.model.ScanState
import com.keepcoding.blehealth.data.model.SyncState
import com.keepcoding.blehealth.data.model.WearableDevice
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class AppTab(val label: String, val marker: String) {
    HOME("首页", "●"),
    DEVICE("设备", "◆"),
    HISTORY("历史", "■"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BleHealthApp(viewModel: AppViewModel) {
    val scenario by viewModel.scenario.collectAsStateWithLifecycle()
    val scanState by viewModel.scanState.collectAsStateWithLifecycle()
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()
    val syncState by viewModel.syncState.collectAsStateWithLifecycle()
    val latestRecord by viewModel.latestRecord.collectAsStateWithLifecycle(initialValue = null)
    val history by viewModel.history.collectAsStateWithLifecycle(initialValue = emptyList())

    var selectedTab by rememberSaveable { mutableStateOf(AppTab.HOME) }
    var scenarioMenuOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("BLE Health Demo", fontWeight = FontWeight.Bold)
                        Text(
                            "Mock 数据 · 非真实健康测量",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
                actions = {
                    Box {
                        TextButton(onClick = { scenarioMenuOpen = true }) {
                            Text("场景：${scenario.label}")
                        }
                        DropdownMenu(
                            expanded = scenarioMenuOpen,
                            onDismissRequest = { scenarioMenuOpen = false },
                        ) {
                            DemoScenario.entries.forEach { item ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(item.label, fontWeight = FontWeight.SemiBold)
                                            Text(
                                                item.description,
                                                style = MaterialTheme.typography.bodySmall,
                                            )
                                        }
                                    },
                                    onClick = {
                                        scenarioMenuOpen = false
                                        viewModel.selectScenario(item)
                                        selectedTab = AppTab.DEVICE
                                    },
                                )
                            }
                        }
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                AppTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Text(tab.marker) },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
    ) { padding ->
        when (selectedTab) {
            AppTab.HOME -> HomeScreen(
                modifier = Modifier.padding(padding),
                connectionState = connectionState,
                syncState = syncState,
                latestRecord = latestRecord,
                onSync = viewModel::sync,
                onOpenDevices = { selectedTab = AppTab.DEVICE },
            )
            AppTab.DEVICE -> DeviceScreen(
                modifier = Modifier.padding(padding),
                scanState = scanState,
                connectionState = connectionState,
                onScan = viewModel::scan,
                onConnect = viewModel::connect,
                onDisconnect = viewModel::disconnect,
            )
            AppTab.HISTORY -> HistoryScreen(
                modifier = Modifier.padding(padding),
                history = history,
                onOpenDevices = { selectedTab = AppTab.DEVICE },
            )
        }
    }
}

@Composable
private fun HomeScreen(
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
            SectionTitle("设备状态")
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        connectedDevice?.name ?: "未连接设备",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        connectedDevice?.let { "${it.model} · 电量 ${it.batteryPercent}%" }
                            ?: "连接 Mock 设备后可同步健康数据",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
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

@Composable
private fun DeviceScreen(
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
private fun HistoryScreen(
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
                    Text(state.device.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("型号 ${state.device.model} · 固件 ${state.device.firmware}")
                    Text("电量 ${state.device.batteryPercent}% · RSSI ${state.device.rssi} dBm")
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

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
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
            Text("心率：${record.heartRateBpm?.let { "$it bpm" } ?: "暂无数据"}")
            Text("血氧：${record.bloodOxygenPercent?.let { "$it%" } ?: "暂无数据"}")
            Text("步数：${record.steps ?: "暂无数据"}")
            Text("睡眠：${record.sleepMinutes?.let(::formatSleep) ?: "暂无数据"}")
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}

@Composable
private fun HintCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Text(message, modifier = Modifier.padding(16.dp))
    }
}

@Composable
private fun StatusPanel(
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

private fun formatSleep(minutes: Int): String = "${minutes / 60}h ${minutes % 60}m"

private fun formatDateTime(timestamp: Long): String = SimpleDateFormat(
    "MM-dd HH:mm",
    Locale.CHINA,
).format(Date(timestamp))
