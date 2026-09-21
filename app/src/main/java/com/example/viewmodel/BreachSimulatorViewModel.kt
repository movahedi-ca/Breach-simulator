package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DoctrineData
import com.example.data.ScenariosData
import com.example.data.SimulationRecord
import com.example.data.SimulationRepository
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

class BreachSimulatorViewModel(
    private val repository: SimulationRepository
) : ViewModel() {

    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _selectedScenario = MutableStateFlow<IncidentScenario?>(null)
    val selectedScenario: StateFlow<IncidentScenario?> = _selectedScenario.asStateFlow()

    private val _currentPhaseIndex = MutableStateFlow(0)
    val currentPhaseIndex: StateFlow<Int> = _currentPhaseIndex.asStateFlow()

    private val _liveMetrics = MutableStateFlow(
        LiveMetrics(
            financialCostUsd = 0L,
            timeElapsedHours = 0,
            publicTrustPercent = 90,
            legalRisk = LegalRiskLevel.MINIMAL,
            forensicIntegrity = ForensicIntegrity.INTACT
        )
    )
    val liveMetrics: StateFlow<LiveMetrics> = _liveMetrics.asStateFlow()

    private val _decisionsHistory = MutableStateFlow<List<SelectedDecision>>(emptyList())
    val decisionsHistory: StateFlow<List<SelectedDecision>> = _decisionsHistory.asStateFlow()

    private val _activeDecisionFeedback = MutableStateFlow<DecisionChoice?>(null)
    val activeDecisionFeedback: StateFlow<DecisionChoice?> = _activeDecisionFeedback.asStateFlow()

    private val _afterActionReport = MutableStateFlow<AfterActionReport?>(null)
    val afterActionReport: StateFlow<AfterActionReport?> = _afterActionReport.asStateFlow()

    private val _selectedDoctrine = MutableStateFlow<DoctrinePlaybook?>(null)
    val selectedDoctrine: StateFlow<DoctrinePlaybook?> = _selectedDoctrine.asStateFlow()

    val pastDrills: StateFlow<List<SimulationRecord>> = repository.allRecords
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun selectScenario(scenario: IncidentScenario) {
        _selectedScenario.value = scenario
        _currentScreen.value = Screen.SCENARIO_DETAIL
    }

    fun startSimulation(scenario: IncidentScenario) {
        _selectedScenario.value = scenario
        _currentPhaseIndex.value = 0
        _liveMetrics.value = LiveMetrics(
            financialCostUsd = scenario.baselineCostUsd,
            timeElapsedHours = 2,
            publicTrustPercent = 90,
            legalRisk = LegalRiskLevel.MINIMAL,
            forensicIntegrity = ForensicIntegrity.INTACT
        )
        _decisionsHistory.value = emptyList()
        _activeDecisionFeedback.value = null
        _afterActionReport.value = null
        _currentScreen.value = Screen.SIMULATION
    }

    fun startSimulation() {
        val scenario = _selectedScenario.value ?: return
        startSimulation(scenario)
    }

    fun returnToHome() {
        _currentScreen.value = Screen.HOME
        _selectedScenario.value = null
        _activeDecisionFeedback.value = null
        _decisionsHistory.value = emptyList()
    }

    fun abortSimulation() {
        returnToHome()
    }

    fun submitDecision(choice: DecisionChoice) {
        // Prevent concurrent or duplicate decision submissions
        if (_activeDecisionFeedback.value != null) return

        val scenario = _selectedScenario.value ?: return
        val currentPhase = scenario.phases.getOrNull(_currentPhaseIndex.value) ?: return

        // Update live metrics with safe arithmetic bounds
        val current = _liveMetrics.value
        val newCost = (current.financialCostUsd + choice.costDeltaUsd).coerceIn(0L, Long.MAX_VALUE)
        val newTime = max(0, current.timeElapsedHours + choice.timeDeltaHours)
        val newTrust = (current.publicTrustPercent + choice.trustDeltaPercent).coerceIn(0, 100)

        val riskLevels = LegalRiskLevel.values()
        val riskIndex = riskLevels.indexOf(current.legalRisk).coerceAtLeast(0)
        val updatedRiskIndex = (riskIndex + choice.legalRiskChange).coerceIn(0, riskLevels.size - 1)
        val newLegalRisk = riskLevels[updatedRiskIndex]

        val newForensics = if (choice.forensicsImpact == ForensicIntegrity.TAINTED ||
            current.forensicIntegrity == ForensicIntegrity.TAINTED
        ) {
            ForensicIntegrity.TAINTED
        } else if (choice.forensicsImpact == ForensicIntegrity.PARTIALLY_COMPROMISED ||
            current.forensicIntegrity == ForensicIntegrity.PARTIALLY_COMPROMISED
        ) {
            ForensicIntegrity.PARTIALLY_COMPROMISED
        } else {
            ForensicIntegrity.INTACT
        }

        _liveMetrics.value = LiveMetrics(
            financialCostUsd = newCost,
            timeElapsedHours = newTime,
            publicTrustPercent = newTrust,
            legalRisk = newLegalRisk,
            forensicIntegrity = newForensics
        )

        val record = SelectedDecision(
            phaseNumber = currentPhase.phaseNumber,
            phaseTitle = currentPhase.title,
            dilemmaQuestion = currentPhase.dilemma.question,
            choice = choice
        )
        _decisionsHistory.value = _decisionsHistory.value + record
        _activeDecisionFeedback.value = choice
    }

    fun proceedFromFeedback() {
        if (_activeDecisionFeedback.value == null) return
        _activeDecisionFeedback.value = null
        val scenario = _selectedScenario.value ?: return
        val nextIndex = _currentPhaseIndex.value + 1

        if (nextIndex < scenario.phases.size) {
            _currentPhaseIndex.value = nextIndex
        } else {
            // Simulation finished! Compile After Action Report (AAR)
            compileAndSaveAar(scenario)
        }
    }

    private fun compileAndSaveAar(scenario: IncidentScenario) {
        val decisions = _decisionsHistory.value
        val metrics = _liveMetrics.value

        // Compute weighted competency scores
        val competencyAverages = mutableMapOf<Competency, Int>()
        for (comp in Competency.values()) {
            val scoresForComp = decisions.mapNotNull { it.choice.competencyScores[comp] }
            val avg = if (scoresForComp.isNotEmpty()) scoresForComp.average().toInt() else 75
            competencyAverages[comp] = avg
        }

        var weightedSum = 0.0
        var totalWeight = 0
        for ((comp, score) in competencyAverages) {
            weightedSum += score * comp.weight
            totalWeight += comp.weight
        }

        val baseScore = if (totalWeight > 0) (weightedSum / totalWeight).toInt() else 75
        // Adjust for trust and forensics penalties
        val forensicsDeduction = when (metrics.forensicIntegrity) {
            ForensicIntegrity.INTACT -> 0
            ForensicIntegrity.PARTIALLY_COMPROMISED -> 8
            ForensicIntegrity.TAINTED -> 20
        }
        val legalPenalty = when (metrics.legalRisk) {
            LegalRiskLevel.MINIMAL -> 0
            LegalRiskLevel.MODERATE -> 5
            LegalRiskLevel.ELEVATED -> 12
            LegalRiskLevel.CRITICAL -> 25
        }

        val finalScore = (baseScore - forensicsDeduction - legalPenalty).coerceIn(0, 100)

        val letterGrade = when {
            finalScore >= 93 -> "A+"
            finalScore >= 87 -> "A"
            finalScore >= 80 -> "B"
            finalScore >= 70 -> "C"
            finalScore >= 60 -> "D"
            else -> "F"
        }

        val summary = when (letterGrade) {
            "A+", "A" -> "Exemplary crisis containment. Strict adherence to incident response frameworks minimized financial exposure, preserved legal privilege, and maintained public stakeholder trust."
            "B" -> "Competent crisis resolution. Incident contained with moderate financial and regulatory impact. Improvements needed in forensic preservation speed and stakeholder advisory cadence."
            "C" -> "Mixed incident handling. Substantial regulatory exposure incurred due to delayed containment or communication gaps. Remediation doctrine recommended."
            "D" -> "High-risk response. Critical delays in containment and potential statutory violation of breach disclosure windows. Substantial litigation exposure."
            else -> "Crisis response failure. Severe spoliation of digital evidence, regulatory penalties incurred, and catastrophic brand trust collapse."
        }

        val lessons = listOf(
            "Evidence Isolation: Always capture volatile host memory before powering off or wiping infected infrastructure.",
            "Materiality Window: Formulate SEC/GDPR disclosure filings with legal counsel early rather than stalling for complete certainty.",
            "Stakeholder Transparency: Proactive, verified technical briefings neutralize adversarial media cycles and preserve brand equity."
        )

        val report = AfterActionReport(
            scenarioId = scenario.id,
            scenarioTitle = scenario.title,
            completedAt = System.currentTimeMillis(),
            letterGrade = letterGrade,
            totalScore = finalScore,
            finalCostUsd = metrics.financialCostUsd,
            totalTimeHours = metrics.timeElapsedHours,
            publicTrustPercent = metrics.publicTrustPercent,
            legalRisk = metrics.legalRisk,
            forensicIntegrity = metrics.forensicIntegrity,
            competencyBreakdown = competencyAverages,
            decisionsTimeline = decisions,
            executiveSummary = summary,
            keyLessons = lessons
        )

        _afterActionReport.value = report
        _currentScreen.value = Screen.AAR_REPORT

        // Save to Room database
        viewModelScope.launch {
            repository.saveRecord(
                SimulationRecord(
                    scenarioId = scenario.id,
                    scenarioTitle = scenario.title,
                    completedAt = report.completedAt,
                    letterGrade = report.letterGrade,
                    totalScore = report.totalScore,
                    finalCostUsd = report.finalCostUsd,
                    totalTimeHours = report.totalTimeHours,
                    publicTrustPercent = report.publicTrustPercent,
                    legalRiskLevel = report.legalRisk.label,
                    decisionsCount = decisions.size,
                    executiveSummary = report.executiveSummary
                )
            )
        }
    }

    fun selectDoctrine(playbook: DoctrinePlaybook) {
        _selectedDoctrine.value = playbook
        _currentScreen.value = Screen.DOCTRINE_DETAIL
    }

    fun deletePastDrill(id: Long) {
        viewModelScope.launch {
            repository.deleteRecord(id)
        }
    }

    fun clearAllPastDrills() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }
}
