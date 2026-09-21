package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "simulation_records")
data class SimulationRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val scenarioId: String,
    val scenarioTitle: String,
    val completedAt: Long,
    val letterGrade: String,
    val totalScore: Int,
    val finalCostUsd: Long,
    val totalTimeHours: Int,
    val publicTrustPercent: Int,
    val legalRiskLevel: String,
    val decisionsCount: Int,
    val executiveSummary: String
)
