package com.keepcoding.blehealth.data.repository

import com.keepcoding.blehealth.data.model.ConnectionState
import com.keepcoding.blehealth.data.model.HealthRecord
import com.keepcoding.blehealth.data.model.ScanState
import com.keepcoding.blehealth.data.model.SyncResult
import com.keepcoding.blehealth.data.model.WearableDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface DeviceRepository {
    val scanState: StateFlow<ScanState>
    val connectionState: StateFlow<ConnectionState>

    suspend fun scan()
    suspend fun connect(device: WearableDevice)
    fun disconnect()
    fun resetTransientState()
}

interface HealthRepository {
    val latestRecord: Flow<HealthRecord?>
    val history: Flow<List<HealthRecord>>

    suspend fun sync(device: WearableDevice): SyncResult
}
