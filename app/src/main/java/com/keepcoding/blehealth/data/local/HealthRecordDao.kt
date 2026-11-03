package com.keepcoding.blehealth.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthRecordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(record: HealthRecordEntity): Long

    @Query("SELECT * FROM health_records ORDER BY measuredAt DESC LIMIT 1")
    fun observeLatest(): Flow<HealthRecordEntity?>

    @Query("SELECT * FROM health_records ORDER BY measuredAt DESC")
    fun observeAll(): Flow<List<HealthRecordEntity>>
}
