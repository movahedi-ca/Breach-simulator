package com.example.model

data class IncidentScenario(
    val id: String,
    val title: String,
    val codename: String,
    val threatActor: String,
    val severity: IncidentSeverity,
    val category: ScenarioCategory,
    val overview: String,
    val impactedSystems: List<String>,
    val regulatoryScope: List<String>,
    val baselineCostUsd: Long,
    val estimatedDurationHours: Int,
    val phases: List<SimulationPhase>
)

data class SimulationPhase(
    val phaseNumber: Int,
    val title: String,
    val timeLabel: String,
    val briefing: String,
    val breakingInject: BreakingInject? = null,
    val dilemma: IncidentDilemma
)

data class BreakingInject(
    val title: String,
    val source: String,
    val message: String,
    val urgent: Boolean = true
)

data class IncidentDilemma(
    val question: String,
    val operationalContext: String,
    val choices: List<DecisionChoice>
)

data class DecisionChoice(
    val id: String,
    val title: String,
    val description: String,
    val recommendedBy: String,
    val costDeltaUsd: Long,
    val timeDeltaHours: Int,
    val trustDeltaPercent: Int,
    val legalRiskChange: Int, // positive = risk increases, negative = risk decreases
    val forensicsImpact: ForensicIntegrity,
    val feedbackDoctrine: String,
    val competencyScores: Map<Competency, Int> // 0 to 100 for each evaluated competency
)

data class LiveMetrics(
    val financialCostUsd: Long,
    val timeElapsedHours: Int,
    val publicTrustPercent: Int,
    val legalRisk: LegalRiskLevel,
    val forensicIntegrity: ForensicIntegrity
)

data class SelectedDecision(
    val phaseNumber: Int,
    val phaseTitle: String,
    val dilemmaQuestion: String,
    val choice: DecisionChoice
)

data class AfterActionReport(
    val scenarioId: String,
    val scenarioTitle: String,
    val completedAt: Long,
    val letterGrade: String,
    val totalScore: Int,
    val finalCostUsd: Long,
    val totalTimeHours: Int,
    val publicTrustPercent: Int,
    val legalRisk: LegalRiskLevel,
    val forensicIntegrity: ForensicIntegrity,
    val competencyBreakdown: Map<Competency, Int>,
    val decisionsTimeline: List<SelectedDecision>,
    val executiveSummary: String,
    val keyLessons: List<String>
)

data class DoctrinePlaybook(
    val id: String,
    val title: String,
    val authority: String,
    val deadlineWindow: String,
    val statutoryPenalties: String,
    val overview: String,
    val mandatoryRequirements: List<String>,
    val incidentPlaybookChecklist: List<String>,
    val commonPitfalls: List<String>
)
