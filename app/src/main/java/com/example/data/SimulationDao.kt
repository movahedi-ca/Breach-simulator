package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SimulationDao {
    @Query("SELECT * FROM simulation_records ORDER BY completedAt DESC")
    fun getAllRecords(): Flow<List<SimulationRecord>>

    @Query("SELECT * FROM simulation_records WHERE id = :id")
    suspend fun getRecordById(id: Long): SimulationRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: SimulationRecord): Long

    @Query("DELETE FROM simulation_records WHERE id = :id")
    suspend fun deleteRecord(id: Long)

    @Query("DELETE FROM simulation_records")
    suspend fun clearAll()
}
