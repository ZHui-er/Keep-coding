package com.keepcoding.blehealth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.keepcoding.blehealth.data.mock.DemoScenario
import com.keepcoding.blehealth.data.mock.ScenarioController
import com.keepcoding.blehealth.data.model.ConnectionState
import com.keepcoding.blehealth.data.model.SyncResult
import com.keepcoding.blehealth.data.model.SyncState
import com.keepcoding.blehealth.data.model.WearableDevice
import com.keepcoding.blehealth.data.repository.DeviceRepository
import com.keepcoding.blehealth.data.repository.HealthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(
    private val deviceRepository: DeviceRepository,
    private val healthRepository: HealthRepository,
    private val scenarioController: ScenarioController,
) : ViewModel() {
    val scanState = deviceRepository.scanState
    val connectionState = deviceRepository.connectionState
    val scenario = scenarioController.scenario
    val latestRecord = healthRepository.latestRecord
    val history = healthRepository.history

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private var scanJob: Job? = null
    private var connectionJob: Job? = null
    private var syncJob: Job? = null

    fun scan() {
        if (scanJob?.isActive == true) return
        scanJob = viewModelScope.launch { deviceRepository.scan() }
    }

    fun connect(device: WearableDevice) {
        if (connectionJob?.isActive == true) return
        connectionJob = viewModelScope.launch { deviceRepository.connect(device) }
    }

    fun disconnect() {
        connectionJob?.cancel()
        syncJob?.cancel()
        deviceRepository.disconnect()
        _syncState.value = SyncState.Idle
    }

    fun sync() {
        if (syncJob?.isActive == true) return
        val device = (connectionState.value as? ConnectionState.Connected)?.device ?: run {
            _syncState.value = SyncState.Failed("请先连接设备")
            return
        }

        syncJob = viewModelScope.launch {
            _syncState.value = SyncState.Syncing
            try {
                _syncState.value = when (val result = healthRepository.sync(device)) {
                    is SyncResult.Success -> SyncState.Success(
                        record = result.record,
                        message = when {
                            result.isDuplicate -> "重复记录已忽略，历史未新增"
                            result.isPartial -> "部分数据已保存"
                            else -> "同步并保存成功"
                        },
                    )
                    is SyncResult.Failure -> SyncState.Failed(result.message)
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _syncState.value = SyncState.Failed("同步失败，请重试")
            }
        }
    }

    fun selectScenario(value: DemoScenario) {
        scanJob?.cancel()
        connectionJob?.cancel()
        syncJob?.cancel()
        scenarioController.select(value)
        deviceRepository.resetTransientState()
        _syncState.value = SyncState.Idle
    }
}

class AppViewModelFactory(
    private val deviceRepository: DeviceRepository,
    private val healthRepository: HealthRepository,
    private val scenarioController: ScenarioController,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = AppViewModel(
        deviceRepository,
        healthRepository,
        scenarioController,
    ) as T
}
