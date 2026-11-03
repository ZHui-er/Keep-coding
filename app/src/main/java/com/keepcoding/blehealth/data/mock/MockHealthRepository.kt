package com.keepcoding.blehealth.data.mock

import com.keepcoding.blehealth.data.local.HealthRecordDao
import com.keepcoding.blehealth.data.local.HealthRecordEntity
import com.keepcoding.blehealth.data.local.toDomain
import com.keepcoding.blehealth.data.model.HealthRecord
import com.keepcoding.blehealth.data.model.SyncResult
import com.keepcoding.blehealth.data.model.WearableDevice
import com.keepcoding.blehealth.data.repository.HealthRepository
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MockHealthRepository(
    private val dao: HealthRecordDao,
    private val scenarioController: ScenarioController,
) : HealthRepository {
    override val latestRecord: Flow<HealthRecord?> = dao.observeLatest().map { it?.toDomain() }
    override val history: Flow<List<HealthRecord>> = dao.observeAll().map { records ->
        records.map { it.toDomain() }
    }

    override suspend fun sync(device: WearableDevice): SyncResult {
        delay(SYNC_DELAY_MS)
        return when (scenarioController.scenario.value) {
            DemoScenario.SYNC_FAILURE -> SyncResult.Failure("同步失败，设备未返回健康数据")
            DemoScenario.STORAGE_FAILURE -> SyncResult.Failure("数据已获取，但保存失败，请重试")
            DemoScenario.PARTIAL_DATA -> createAndPersist(device, isPartial = true)
            DemoScenario.DUPLICATE_DATA -> createAndPersist(
                device = device,
                isPartial = false,
                fixedId = "${device.id}-duplicate-demo",
            )
            else -> createAndPersist(device, isPartial = false)
        }
    }

    private suspend fun createAndPersist(
        device: WearableDevice,
        isPartial: Boolean,
        fixedId: String? = null,
    ): SyncResult {
        val sequence = samples[sampleIndex.getAndIncrement() % samples.size]
        val measuredAt = System.currentTimeMillis()
        val entity = HealthRecordEntity(
            id = fixedId ?: "${device.id}-$measuredAt",
            deviceId = device.id,
            deviceName = device.name,
            measuredAt = measuredAt,
            syncedAt = System.currentTimeMillis(),
            heartRateBpm = HealthValidator.heartRate(sequence.heartRate),
            bloodOxygenPercent = HealthValidator.bloodOxygen(sequence.bloodOxygen),
            steps = HealthValidator.steps(sequence.steps),
            sleepMinutes = if (isPartial) null else HealthValidator.sleepMinutes(sequence.sleepMinutes),
            isPartial = isPartial,
        )

        val hasAnyData = listOf(
            entity.heartRateBpm,
            entity.bloodOxygenPercent,
            entity.steps,
            entity.sleepMinutes,
        ).any { it != null }
        if (!hasAnyData) return SyncResult.Failure("所有健康指标均无效，未保存数据")

        dao.insert(entity)
        return SyncResult.Success(entity.toDomain(), isPartial)
    }

    private data class Sample(
        val heartRate: Int,
        val bloodOxygen: Int,
        val steps: Int,
        val sleepMinutes: Int,
    )

    private companion object {
        const val SYNC_DELAY_MS = 1_000L
        val sampleIndex = AtomicInteger(0)
        val samples = listOf(
            Sample(72, 98, 6_420, 438),
            Sample(76, 97, 7_115, 421),
            Sample(68, 99, 8_240, 462),
        )
    }
}
