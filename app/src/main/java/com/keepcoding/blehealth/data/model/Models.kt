package com.keepcoding.blehealth.data.model

data class WearableDevice(
    val id: String,
    val name: String,
    val model: String,
    val firmware: String,
    val batteryPercent: Int,
    val rssi: Int,
)

data class HealthRecord(
    val id: String,
    val deviceId: String,
    val deviceName: String,
    val measuredAt: Long,
    val syncedAt: Long,
    val heartRateBpm: Int?,
    val bloodOxygenPercent: Int?,
    val steps: Int?,
    val sleepMinutes: Int?,
    val isPartial: Boolean,
)

sealed interface ScanState {
    data object Idle : ScanState
    data object Scanning : ScanState
    data class Results(val devices: List<WearableDevice>) : ScanState
    data object Empty : ScanState
    data class Failed(val message: String) : ScanState
}

sealed interface ConnectionState {
    data object Disconnected : ConnectionState
    data class Connecting(val device: WearableDevice) : ConnectionState
    data class Connected(val device: WearableDevice) : ConnectionState
    data class Failed(val device: WearableDevice, val message: String) : ConnectionState
}

sealed interface SyncState {
    data object Idle : SyncState
    data object Syncing : SyncState
    data class Success(val record: HealthRecord, val message: String) : SyncState
    data class Failed(val message: String) : SyncState
}

sealed interface SyncResult {
    data class Success(
        val record: HealthRecord,
        val isPartial: Boolean,
        val isDuplicate: Boolean = false,
    ) : SyncResult
    data class Failure(val message: String) : SyncResult
}
