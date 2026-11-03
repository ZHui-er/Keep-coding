package com.keepcoding.blehealth.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.keepcoding.blehealth.data.model.HealthRecord

@Entity(tableName = "health_records")
data class HealthRecordEntity(
    @PrimaryKey val id: String,
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

fun HealthRecordEntity.toDomain() = HealthRecord(
    id = id,
    deviceId = deviceId,
    deviceName = deviceName,
    measuredAt = measuredAt,
    syncedAt = syncedAt,
    heartRateBpm = heartRateBpm,
    bloodOxygenPercent = bloodOxygenPercent,
    steps = steps,
    sleepMinutes = sleepMinutes,
    isPartial = isPartial,
)
