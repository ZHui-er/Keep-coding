package com.keepcoding.blehealth.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.keepcoding.blehealth.data.mock.DemoScenario

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
                        Text("BLE Health", fontWeight = FontWeight.Bold)
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
                        ScenarioMenu(
                            expanded = scenarioMenuOpen,
                            onDismiss = { scenarioMenuOpen = false },
                            onSelect = { selectedScenario ->
                                scenarioMenuOpen = false
                                viewModel.selectScenario(selectedScenario)
                                selectedTab = AppTab.DEVICE
                            },
                        )
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
private fun ScenarioMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onSelect: (DemoScenario) -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
    ) {
        DemoScenario.entries.forEach { item ->
            DropdownMenuItem(
                text = {
                    Column {
                        Text(item.label, fontWeight = FontWeight.SemiBold)
                        Text(item.description, style = MaterialTheme.typography.bodySmall)
                    }
                },
                onClick = { onSelect(item) },
            )
        }
    }
}
