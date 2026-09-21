package com.example.data

import kotlinx.coroutines.flow.Flow

class SimulationRepository(private val dao: SimulationDao) {
    val allRecords: Flow<List<SimulationRecord>> = dao.getAllRecords()

    suspend fun saveRecord(record: SimulationRecord): Long {
        return dao.insertRecord(record)
    }

    suspend fun getRecordById(id: Long): SimulationRecord? {
        return dao.getRecordById(id)
    }

    suspend fun deleteRecord(id: Long) {
        dao.deleteRecord(id)
    }

    suspend fun clearAll() {
        dao.clearAll()
    }
}
