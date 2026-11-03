package com.keepcoding.blehealth.data.mock

import com.keepcoding.blehealth.data.model.ConnectionState
import com.keepcoding.blehealth.data.model.ScanState
import com.keepcoding.blehealth.data.model.WearableDevice
import com.keepcoding.blehealth.data.repository.DeviceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MockDeviceRepository(
    private val scenarioController: ScenarioController,
) : DeviceRepository {
    private val operationMutex = Mutex()
    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    override val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    override suspend fun scan() = operationMutex.withLock {
        _scanState.value = ScanState.Scanning
        delay(SCAN_DELAY_MS)
        _scanState.value = if (scenarioController.scenario.value == DemoScenario.NO_DEVICES) {
            ScanState.Empty
        } else {
            ScanState.Results(devices)
        }
    }

    override suspend fun connect(device: WearableDevice) = operationMutex.withLock {
        if ((_connectionState.value as? ConnectionState.Connected)?.device?.id == device.id) return
        _connectionState.value = ConnectionState.Connecting(device)
        delay(CONNECT_DELAY_MS)
        _connectionState.value = if (
            scenarioController.scenario.value == DemoScenario.CONNECTION_FAILURE
        ) {
            ConnectionState.Failed(device, "无法连接设备，请重试")
        } else {
            ConnectionState.Connected(device)
        }
    }

    override fun disconnect() {
        _connectionState.value = ConnectionState.Disconnected
    }

    override fun resetTransientState() {
        _scanState.value = ScanState.Idle
        _connectionState.value = ConnectionState.Disconnected
    }

    private companion object {
        const val SCAN_DELAY_MS = 800L
        const val CONNECT_DELAY_MS = 600L

        val devices = listOf(
            WearableDevice("mock-band-001", "FitBand Air", "FB-A1", "1.4.2", 86, -52),
            WearableDevice("mock-watch-002", "Health Watch", "HW-S2", "2.0.8", 64, -67),
            WearableDevice("mock-ring-003", "Sleep Ring", "SR-3", "1.1.0", 41, -78),
        )
    }
}
