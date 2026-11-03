package com.keepcoding.blehealth.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [HealthRecordEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun healthRecordDao(): HealthRecordDao
}
