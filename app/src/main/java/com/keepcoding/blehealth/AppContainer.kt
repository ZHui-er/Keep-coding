package com.keepcoding.blehealth

import android.content.Context
import androidx.room.Room
import com.keepcoding.blehealth.data.local.AppDatabase
import com.keepcoding.blehealth.data.mock.MockDeviceRepository
import com.keepcoding.blehealth.data.mock.MockHealthRepository
import com.keepcoding.blehealth.data.mock.ScenarioController
import com.keepcoding.blehealth.data.repository.DeviceRepository
import com.keepcoding.blehealth.data.repository.HealthRepository

class AppContainer(context: Context) {
    val scenarioController = ScenarioController()

    private val database = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "ble-health.db",
    ).build()

    val deviceRepository: DeviceRepository = MockDeviceRepository(scenarioController)
    val healthRepository: HealthRepository = MockHealthRepository(
        dao = database.healthRecordDao(),
        scenarioController = scenarioController,
    )
}
