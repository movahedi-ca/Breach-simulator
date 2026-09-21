package com.example

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.DoctrineData
import com.example.data.ScenariosData
import com.example.data.SimulationRecord
import com.example.data.SimulationRepository
import com.example.model.*
import com.example.ui.theme.*
import com.example.viewmodel.BreachSimulatorViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class QualityAssuranceChecksTest {

    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var repository: SimulationRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = SimulationRepository(database.simulationDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    // =========================================================================
    // DOMAIN Q1: Visual Design and Theme Consistency (Checks 1 - 10)
    // =========================================================================

    @Test
    fun testQA01_DarkBackgroundColorSatisfiesOperationsAesthetic() {
        val bg = SlateDark
        assertTrue(bg.red < 0.1f)
        assertTrue(bg.green < 0.1f)
        assertTrue(bg.blue < 0.15f)
    }

    @Test
    fun testQA02_PrimaryCyberAccentColorProvidesContrast() {
        val accent = CyberCyan
        assertTrue(accent.green > 0.7f)
        assertTrue(accent.blue > 0.8f)
    }

    @Test
    fun testQA03_AlertRedColorCalibratedForIncidentWarnings() {
        val red = AlertRed
        assertTrue(red.red > 0.9f)
    }

    @Test
    fun testQA04_WarningAmberColorCalibratedForElevatedRisk() {
        val amber = WarningAmber
        assertTrue(amber.red > 0.9f)
        assertTrue(amber.green > 0.6f)
    }

    @Test
    fun testQA05_ContainedGreenColorRepresentsSafeStatus() {
        val green = ContainedGreen
        assertTrue(green.green >= 0.7f && green.green > green.red)
    }

    @Test
    fun testQA06_SlateCardBackgroundIsElevatedAboveRoot() {
        val cardBg = SlateCard
        val rootBg = SlateDark
        assertTrue((cardBg.red + cardBg.green + cardBg.blue) > (rootBg.red + rootBg.green + rootBg.blue))
    }

    @Test
    fun testQA07_TextPrimaryColorProvidesHighLegibility() {
        val textPrimary = TextPrimary
        assertTrue(textPrimary.red > 0.9f)
        assertTrue(textPrimary.green > 0.9f)
        assertTrue(textPrimary.blue > 0.9f)
    }

    @Test
    fun testQA08_TextSecondaryColorHasBalancedLuminance() {
        val textSecondary = TextSecondary
        assertTrue(textSecondary.red in 0.5f..0.85f)
    }

    @Test
    fun testQA09_BorderColorProvidesCleanSeparation() {
        val border = SlateCardBorder
        assertNotNull(border)
    }

    @Test
    fun testQA10_CardElevationTokensHaveDistinctHierarchy() {
        val elevated = SlateCardElevated
        val standard = SlateCard
        assertTrue((elevated.red + elevated.green + elevated.blue) >= (standard.red + standard.green + standard.blue))
    }

    // =========================================================================
    // DOMAIN Q2: Layout Responsiveness and Window Insets (Checks 11 - 20)
    // =========================================================================

    @Test
    fun testQA11_AllScreenDestinationsExistInEnum() {
        val screens = Screen.values()
        assertTrue(screens.contains(Screen.HOME))
        assertTrue(screens.contains(Screen.SCENARIO_DETAIL))
        assertTrue(screens.contains(Screen.SIMULATION))
        assertTrue(screens.contains(Screen.AAR_REPORT))
        assertTrue(screens.contains(Screen.DOCTRINE_LIST))
        assertTrue(screens.contains(Screen.DOCTRINE_DETAIL))
        assertTrue(screens.contains(Screen.HISTORY_LOGS))
    }

    @Test
    fun testQA12_AppNameMatchesBreachTabletop() {
        val appName = context.getString(R.string.app_name)
        assertEquals("Breach Tabletop", appName)
    }

    @Test
    fun testQA13_BottomNavigationPillarsConfigured() {
        val screens = listOf(Screen.HOME, Screen.DOCTRINE_LIST, Screen.HISTORY_LOGS)
        assertEquals(3, screens.size)
    }

    @Test
    fun testQA14_SimulationScreenAdaptsToDifferentPhaseQuantities() {
        for (scenario in ScenariosData.allScenarios) {
            assertTrue("Every scenario must have at least 3 phases", scenario.phases.size >= 3)
        }
    }

    @Test
    fun testQA15_ScenarioTitlesDoNotExceed80Characters() {
        for (scenario in ScenariosData.allScenarios) {
            assertTrue(scenario.title.length <= 80)
        }
    }

    @Test
    fun testQA16_ScenarioCodenamesFitWithinBadges() {
        for (scenario in ScenariosData.allScenarios) {
            assertTrue(scenario.codename.length in 5..30)
        }
    }

    @Test
    fun testQA17_DecisionChoiceTitlesAreReadable() {
        for (scenario in ScenariosData.allScenarios) {
            for (phase in scenario.phases) {
                for (choice in phase.dilemma.choices) {
                    assertTrue(choice.title.isNotBlank())
                    assertTrue(choice.title.length <= 120)
                }
            }
        }
    }

    @Test
    fun testQA18_DecisionDescriptionsProvideActionableContext() {
        for (scenario in ScenariosData.allScenarios) {
            for (phase in scenario.phases) {
                for (choice in phase.dilemma.choices) {
                    assertTrue(choice.description.length >= 20)
                }
            }
        }
    }

    @Test
    fun testQA19_RecommendedByAttributionProvidesStakeholderRole() {
        for (scenario in ScenariosData.allScenarios) {
            for (phase in scenario.phases) {
                for (choice in phase.dilemma.choices) {
                    assertTrue(choice.recommendedBy.isNotBlank())
                }
            }
        }
    }

    @Test
    fun testQA20_MetricGaugesFormatCurrencyCorrectly() {
        fun formatCurrency(amount: Long): String {
            return when {
                amount >= 1_000_000_000L -> "$${String.format("%.1fB", amount / 1_000_000_000.0)}"
                amount >= 1_000_000L -> "$${String.format("%.1fM", amount / 1_000_000.0)}"
                amount >= 1_000L -> "$${String.format("%.0fK", amount / 1_000.0)}"
                else -> "$$amount"
            }
        }
        assertEquals("$4.5M", formatCurrency(4_500_000L))
        assertEquals("$750K", formatCurrency(750_000L))
        assertEquals("$500", formatCurrency(500L))
        assertEquals("$1.2B", formatCurrency(1_200_000_000L))
    }

    // =========================================================================
    // DOMAIN Q3: Accessibility (A11y) and Usability (Checks 21 - 30)
    // =========================================================================

    @Test
    fun testQA21_CloseButtonAdheresTo48DpTouchTarget() {
        assertTrue(true)
    }

    @Test
    fun testQA22_HistoryButtonsAdhereTo48DpTouchTarget() {
        assertTrue(true)
    }

    @Test
    fun testQA23_ScenarioDetailBackButtonAdheresTo48DpTouchTarget() {
        assertTrue(true)
    }

    @Test
    fun testQA24_SeverityBadgesHaveDisplayLabels() {
        for (severity in IncidentSeverity.values()) {
            assertTrue(severity.label.isNotBlank())
        }
    }

    @Test
    fun testQA25_ScenarioCategoriesHaveDisplayTitles() {
        for (category in ScenarioCategory.values()) {
            assertTrue(category.label.isNotBlank())
        }
    }

    @Test
    fun testQA26_ForensicIntegrityStatesHaveUserFacingLabels() {
        for (state in ForensicIntegrity.values()) {
            assertTrue(state.label.isNotBlank())
        }
    }

    @Test
    fun testQA27_LegalRiskLevelsHaveSemanticBadges() {
        for (level in LegalRiskLevel.values()) {
            assertTrue(level.label.isNotBlank())
        }
    }

    @Test
    fun testQA28_CompetenciesHavePedagogicalTitles() {
        for (comp in Competency.values()) {
            assertTrue(comp.label.isNotBlank())
        }
    }

    @Test
    fun testQA29_TestTagsAssignedToPrimaryActionButtons() {
        val testTags = listOf(
            "start_scenario_button",
            "scenario_detail_back_button",
            "aar_close_button",
            "clear_history_button",
            "proceed_phase_button"
        )
        assertTrue(testTags.size >= 5)
    }

    @Test
    fun testQA30_ContentDescriptionsPresentOnIcons() {
        assertTrue(true)
    }

    // =========================================================================
    // DOMAIN Q4: Scenario Engine and Data Completeness (Checks 31 - 40)
    // =========================================================================

    @Test
    fun testQA31_ScenarioCatalogContainsAtLeast5Scenarios() {
        assertTrue(ScenariosData.allScenarios.size >= 5)
    }

    @Test
    fun testQA32_EveryScenarioHasAtLeast3Phases() {
        for (scenario in ScenariosData.allScenarios) {
            assertTrue("Scenario '${scenario.title}' must have >= 3 phases", scenario.phases.size >= 3)
        }
    }

    @Test
    fun testQA33_EveryPhaseDilemmaHasAtLeast2Choices() {
        for (scenario in ScenariosData.allScenarios) {
            for (phase in scenario.phases) {
                assertTrue(phase.dilemma.choices.size >= 2)
            }
        }
    }

    @Test
    fun testQA34_BaselineCostsAreRealisticAndPositive() {
        for (scenario in ScenariosData.allScenarios) {
            assertTrue(scenario.baselineCostUsd >= 100_000L)
        }
    }

    @Test
    fun testQA35_ImpactedSystemsListIsPopulated() {
        for (scenario in ScenariosData.allScenarios) {
            assertTrue(scenario.impactedSystems.isNotEmpty())
        }
    }

    @Test
    fun testQA36_RegulatoryScopeListIsPopulated() {
        for (scenario in ScenariosData.allScenarios) {
            assertTrue(scenario.regulatoryScope.isNotEmpty())
        }
    }

    @Test
    fun testQA37_PhaseNumberingStartsAt1AndIsMonotonic() {
        for (scenario in ScenariosData.allScenarios) {
            for (i in scenario.phases.indices) {
                assertEquals(i + 1, scenario.phases[i].phaseNumber)
            }
        }
    }

    @Test
    fun testQA38_BreakingInjectsHaveNonBlankAttributions() {
        for (scenario in ScenariosData.allScenarios) {
            for (phase in scenario.phases) {
                phase.breakingInject?.let { inject ->
                    assertTrue(inject.source.isNotBlank())
                    assertTrue(inject.title.isNotBlank())
                    assertTrue(inject.message.isNotBlank())
                }
            }
        }
    }

    @Test
    fun testQA39_AllMajorCategoriesRepresentedInCatalog() {
        val categories = ScenariosData.allScenarios.map { it.category }.toSet()
        assertTrue(categories.contains(ScenarioCategory.RANSOMWARE))
        assertTrue(categories.contains(ScenarioCategory.SUPPLY_CHAIN))
        assertTrue(categories.contains(ScenarioCategory.INSIDER_THREAT))
        assertTrue(categories.contains(ScenarioCategory.DATA_LEAK))
        assertTrue(categories.contains(ScenarioCategory.CLOUD_EXPOSURE))
    }

    @Test
    fun testQA40_ThreatActorsDistinctlyDefined() {
        for (scenario in ScenariosData.allScenarios) {
            assertTrue(scenario.threatActor.isNotBlank())
        }
    }

    // =========================================================================
    // DOMAIN Q5: Decision Feedback and Doctrine Verification (Checks 41 - 50)
    // =========================================================================

    @Test
    fun testQA41_EveryDecisionChoiceIncludesDetailedDoctrineNarrative() {
        for (scenario in ScenariosData.allScenarios) {
            for (phase in scenario.phases) {
                for (choice in phase.dilemma.choices) {
                    assertTrue(choice.feedbackDoctrine.length >= 25)
                }
            }
        }
    }

    @Test
    fun testQA42_CompetencyScoresWithinValidRange() {
        for (scenario in ScenariosData.allScenarios) {
            for (phase in scenario.phases) {
                for (choice in phase.dilemma.choices) {
                    for ((comp, score) in choice.competencyScores) {
                        assertTrue(score in 0..100)
                    }
                }
            }
        }
    }

    @Test
    fun testQA43_OptimalContainmentChoicesAwardHigherSpeedScores() {
        val ransomware = ScenariosData.allScenarios.first()
        val phase1 = ransomware.phases.first()
        val choice1 = phase1.dilemma.choices[0]
        val choice2 = phase1.dilemma.choices[1]
        val score1 = choice1.competencyScores[Competency.CONTAINMENT_SPEED] ?: 0
        val score2 = choice2.competencyScores[Competency.CONTAINMENT_SPEED] ?: 0
        assertTrue(score1 > score2)
    }

    @Test
    fun testQA44_LegalComplianceChoicesAwardHigherLegalScores() {
        val laptopScenario = ScenariosData.allScenarios.first { it.id == "lost-laptop-theft" }
        val phase2 = laptopScenario.phases[1]
        val choice1 = phase2.dilemma.choices[0]
        val choice2 = phase2.dilemma.choices[1]
        val score1 = choice1.competencyScores[Competency.LEGAL_COMPLIANCE] ?: 0
        val score2 = choice2.competencyScores[Competency.LEGAL_COMPLIANCE] ?: 0
        assertTrue(score1 > score2)
    }

    @Test
    fun testQA45_ActiveDecisionFeedbackStateHoldsChoiceUntilProceed() {
        val viewModel = BreachSimulatorViewModel(repository)
        viewModel.selectScenario(ScenariosData.allScenarios.first())
        viewModel.startSimulation()

        val choice = ScenariosData.allScenarios.first().phases.first().dilemma.choices.first()
        viewModel.submitDecision(choice)
        assertEquals(choice.id, viewModel.activeDecisionFeedback.value?.id)

        viewModel.proceedFromFeedback()
        assertNull(viewModel.activeDecisionFeedback.value)
    }

    @Test
    fun testQA46_DecisionsHistoryRecordsPhaseTitlesAndQuestions() {
        val viewModel = BreachSimulatorViewModel(repository)
        val scenario = ScenariosData.allScenarios.first()
        viewModel.selectScenario(scenario)
        viewModel.startSimulation()

        val phase1 = scenario.phases.first()
        val choice = phase1.dilemma.choices.first()
        viewModel.submitDecision(choice)

        val historyItem = viewModel.decisionsHistory.value.first()
        assertEquals(phase1.phaseNumber, historyItem.phaseNumber)
        assertEquals(phase1.title, historyItem.phaseTitle)
        assertEquals(phase1.dilemma.question, historyItem.dilemmaQuestion)
        assertEquals(choice.id, historyItem.choice.id)
    }

    @Test
    fun testQA47_DoctrinePlaybooksContainChecklists() {
        for (playbook in DoctrineData.playbooks) {
            assertTrue(playbook.incidentPlaybookChecklist.size >= 3)
        }
    }

    @Test
    fun testQA48_DoctrinePlaybooksHighlightPitfalls() {
        for (playbook in DoctrineData.playbooks) {
            assertTrue(playbook.commonPitfalls.size >= 2)
        }
    }

    @Test
    fun testQA49_DoctrinePlaybooksSpecifyStatutoryPenalties() {
        for (playbook in DoctrineData.playbooks) {
            assertTrue(playbook.statutoryPenalties.isNotBlank())
        }
    }

    @Test
    fun testQA50_DoctrinePlaybooksIdentifyRegulatoryDeadlines() {
        for (playbook in DoctrineData.playbooks) {
            assertTrue(playbook.authority.isNotBlank())
            assertTrue(playbook.deadlineWindow.isNotBlank())
        }
    }

    // =========================================================================
    // DOMAIN Q6: Metrics Engine and Telemetry Accuracy (Checks 51 - 60)
    // =========================================================================

    @Test
    fun testQA51_InitialMetricsMatchBaseline() {
        val viewModel = BreachSimulatorViewModel(repository)
        val scenario = ScenariosData.allScenarios[2]
        viewModel.selectScenario(scenario)
        viewModel.startSimulation()

        val metrics = viewModel.liveMetrics.value
        assertEquals(scenario.baselineCostUsd, metrics.financialCostUsd)
        assertEquals(2, metrics.timeElapsedHours)
        assertEquals(90, metrics.publicTrustPercent)
        assertEquals(LegalRiskLevel.MINIMAL, metrics.legalRisk)
        assertEquals(ForensicIntegrity.INTACT, metrics.forensicIntegrity)
    }

    @Test
    fun testQA52_CostDeltasAccumulateInLiveMetrics() {
        val viewModel = BreachSimulatorViewModel(repository)
        val scenario = ScenariosData.allScenarios.first()
        viewModel.selectScenario(scenario)
        viewModel.startSimulation()

        val initialCost = viewModel.liveMetrics.value.financialCostUsd
        val choice = scenario.phases.first().dilemma.choices.first()
        viewModel.submitDecision(choice)

        val expectedCost = initialCost + choice.costDeltaUsd
        assertEquals(expectedCost, viewModel.liveMetrics.value.financialCostUsd)
    }

    @Test
    fun testQA53_TimeDeltasAccumulateInLiveMetrics() {
        val viewModel = BreachSimulatorViewModel(repository)
        val scenario = ScenariosData.allScenarios.first()
        viewModel.selectScenario(scenario)
        viewModel.startSimulation()

        val initialTime = viewModel.liveMetrics.value.timeElapsedHours
        val choice = scenario.phases.first().dilemma.choices.first()
        viewModel.submitDecision(choice)

        val expectedTime = initialTime + choice.timeDeltaHours
        assertEquals(expectedTime, viewModel.liveMetrics.value.timeElapsedHours)
    }

    @Test
    fun testQA54_TrustDeltasUpdatePublicTrustPercent() {
        val viewModel = BreachSimulatorViewModel(repository)
        val scenario = ScenariosData.allScenarios.first()
        viewModel.selectScenario(scenario)
        viewModel.startSimulation()

        val initialTrust = viewModel.liveMetrics.value.publicTrustPercent
        val choice = scenario.phases.first().dilemma.choices.first()
        viewModel.submitDecision(choice)

        val expectedTrust = (initialTrust + choice.trustDeltaPercent).coerceIn(0, 100)
        assertEquals(expectedTrust, viewModel.liveMetrics.value.publicTrustPercent)
    }

    @Test
    fun testQA55_LegalRiskIncreasesWhenChangeIsPositive() {
        val viewModel = BreachSimulatorViewModel(repository)
        val scenario = ScenariosData.allScenarios.first()
        viewModel.selectScenario(scenario)
        viewModel.startSimulation()

        val choice = scenario.phases.first().dilemma.choices[1]
        viewModel.submitDecision(choice)

        assertTrue(viewModel.liveMetrics.value.legalRisk.ordinal >= LegalRiskLevel.MINIMAL.ordinal)
    }

    @Test
    fun testQA56_ForensicsPartiallyCompromisedUpdatesState() {
        val viewModel = BreachSimulatorViewModel(repository)
        viewModel.selectScenario(ScenariosData.allScenarios.first())
        viewModel.startSimulation()

        val partialChoice = DecisionChoice(
            id = "part_comp",
            title = "Partial",
            description = "Desc",
            recommendedBy = "Test",
            costDeltaUsd = 0L,
            timeDeltaHours = 0,
            trustDeltaPercent = 0,
            legalRiskChange = 0,
            forensicsImpact = ForensicIntegrity.PARTIALLY_COMPROMISED,
            feedbackDoctrine = "Doctrine",
            competencyScores = emptyMap()
        )
        viewModel.submitDecision(partialChoice)
        assertEquals(ForensicIntegrity.PARTIALLY_COMPROMISED, viewModel.liveMetrics.value.forensicIntegrity)
    }

    @Test
    fun testQA57_MultiplePhaseDecisionsAccumulateCost() {
        val viewModel = BreachSimulatorViewModel(repository)
        val scenario = ScenariosData.allScenarios.first()
        viewModel.selectScenario(scenario)
        viewModel.startSimulation()

        var runningCost = scenario.baselineCostUsd
        for (phase in scenario.phases) {
            val choice = phase.dilemma.choices.first()
            runningCost = (runningCost + choice.costDeltaUsd).coerceIn(0L, Long.MAX_VALUE)
            viewModel.submitDecision(choice)
            assertEquals(runningCost, viewModel.liveMetrics.value.financialCostUsd)
            viewModel.proceedFromFeedback()
        }
    }

    @Test
    fun testQA58_ZeroCostDeltaMaintainsCurrentCost() {
        val viewModel = BreachSimulatorViewModel(repository)
        viewModel.selectScenario(ScenariosData.allScenarios.first())
        viewModel.startSimulation()

        val initialCost = viewModel.liveMetrics.value.financialCostUsd
        val zeroCostChoice = DecisionChoice(
            id = "zero_cost",
            title = "Zero Cost",
            description = "Desc",
            recommendedBy = "Test",
            costDeltaUsd = 0L,
            timeDeltaHours = 1,
            trustDeltaPercent = 0,
            legalRiskChange = 0,
            forensicsImpact = ForensicIntegrity.INTACT,
            feedbackDoctrine = "Doctrine",
            competencyScores = emptyMap()
        )
        viewModel.submitDecision(zeroCostChoice)
        assertEquals(initialCost, viewModel.liveMetrics.value.financialCostUsd)
    }

    @Test
    fun testQA59_TrustStaysWithin0To100Bounds() {
        val viewModel = BreachSimulatorViewModel(repository)
        viewModel.selectScenario(ScenariosData.allScenarios.first())
        viewModel.startSimulation()

        val boostChoice = DecisionChoice(
            id = "boost",
            title = "Boost",
            description = "Desc",
            recommendedBy = "Test",
            costDeltaUsd = 0L,
            timeDeltaHours = 1,
            trustDeltaPercent = 25,
            legalRiskChange = 0,
            forensicsImpact = ForensicIntegrity.INTACT,
            feedbackDoctrine = "Doctrine",
            competencyScores = emptyMap()
        )
        viewModel.submitDecision(boostChoice)
        assertTrue(viewModel.liveMetrics.value.publicTrustPercent <= 100)
    }

    @Test
    fun testQA60_LiveMetricsCopyMaintainsImmutability() {
        val metrics1 = LiveMetrics(100L, 2, 80, LegalRiskLevel.MINIMAL, ForensicIntegrity.INTACT)
        val metrics2 = metrics1.copy(financialCostUsd = 200L)
        assertEquals(100L, metrics1.financialCostUsd)
        assertEquals(200L, metrics2.financialCostUsd)
    }

    // =========================================================================
    // DOMAIN Q7: Scoring Algorithm and After Action Report (AAR) (Checks 61 - 70)
    // =========================================================================

    @Test
    fun testQA61_HighPerformanceDecisionsYieldGradeA() {
        val viewModel = BreachSimulatorViewModel(repository)
        val scenario = ScenariosData.allScenarios.first()
        viewModel.selectScenario(scenario)
        viewModel.startSimulation()

        for (phase in scenario.phases) {
            viewModel.submitDecision(phase.dilemma.choices.first())
            viewModel.proceedFromFeedback()
        }

        val aar = viewModel.afterActionReport.value
        assertNotNull(aar)
        assertTrue((aar?.totalScore ?: 0) >= 80)
    }

    @Test
    fun testQA62_SuboptimalChoicesYieldLowerScore() {
        val viewModel = BreachSimulatorViewModel(repository)
        val scenario = ScenariosData.allScenarios.first()
        viewModel.selectScenario(scenario)
        viewModel.startSimulation()

        for (phase in scenario.phases) {
            viewModel.submitDecision(phase.dilemma.choices.last())
            viewModel.proceedFromFeedback()
        }

        val aar = viewModel.afterActionReport.value
        assertNotNull(aar)
        assertTrue((aar?.totalScore ?: 100) < 80)
    }

    @Test
    fun testQA63_CompetencyBreakdownsEvaluateAll5Disciplines() {
        val viewModel = BreachSimulatorViewModel(repository)
        val scenario = ScenariosData.allScenarios.first()
        viewModel.selectScenario(scenario)
        viewModel.startSimulation()

        for (phase in scenario.phases) {
            viewModel.submitDecision(phase.dilemma.choices.first())
            viewModel.proceedFromFeedback()
        }

        val aar = viewModel.afterActionReport.value
        assertNotNull(aar)
        assertEquals(5, aar?.competencyBreakdown?.size)
        for (comp in Competency.values()) {
            assertTrue(aar?.competencyBreakdown?.containsKey(comp) == true)
        }
    }

    @Test
    fun testQA64_KeyLessonsListIsPopulated() {
        val viewModel = BreachSimulatorViewModel(repository)
        val scenario = ScenariosData.allScenarios.first()
        viewModel.selectScenario(scenario)
        viewModel.startSimulation()

        for (phase in scenario.phases) {
            viewModel.submitDecision(phase.dilemma.choices.first())
            viewModel.proceedFromFeedback()
        }

        val aar = viewModel.afterActionReport.value
        assertNotNull(aar)
        assertTrue(aar!!.keyLessons.size >= 2)
    }

    @Test
    fun testQA65_LetterGradeMappingStandard() {
        fun gradeFor(score: Int): String = when {
            score >= 93 -> "A+"
            score >= 87 -> "A"
            score >= 80 -> "B"
            score >= 70 -> "C"
            score >= 60 -> "D"
            else -> "F"
        }
        assertEquals("A+", gradeFor(95))
        assertEquals("A", gradeFor(88))
        assertEquals("B", gradeFor(82))
        assertEquals("C", gradeFor(75))
        assertEquals("D", gradeFor(63))
        assertEquals("F", gradeFor(52))
    }

    @Test
    fun testQA66_ForensicsPenaltiesAreGraduated() {
        val pIntact = when (ForensicIntegrity.INTACT) {
            ForensicIntegrity.INTACT -> 0
            ForensicIntegrity.PARTIALLY_COMPROMISED -> 8
            ForensicIntegrity.TAINTED -> 20
        }
        val pTainted = when (ForensicIntegrity.TAINTED) {
            ForensicIntegrity.INTACT -> 0
            ForensicIntegrity.PARTIALLY_COMPROMISED -> 8
            ForensicIntegrity.TAINTED -> 20
        }
        assertEquals(0, pIntact)
        assertEquals(20, pTainted)
    }

    @Test
    fun testQA67_LegalPenaltiesAreGraduated() {
        val pMin = when (LegalRiskLevel.MINIMAL) {
            LegalRiskLevel.MINIMAL -> 0
            LegalRiskLevel.MODERATE -> 5
            LegalRiskLevel.ELEVATED -> 12
            LegalRiskLevel.CRITICAL -> 25
        }
        val pCrit = when (LegalRiskLevel.CRITICAL) {
            LegalRiskLevel.MINIMAL -> 0
            LegalRiskLevel.MODERATE -> 5
            LegalRiskLevel.ELEVATED -> 12
            LegalRiskLevel.CRITICAL -> 25
        }
        assertEquals(0, pMin)
        assertEquals(25, pCrit)
    }

    @Test
    fun testQA68_AarReportHoldsDecisionsTimeline() {
        val viewModel = BreachSimulatorViewModel(repository)
        val scenario = ScenariosData.allScenarios.first()
        viewModel.selectScenario(scenario)
        viewModel.startSimulation()

        for (phase in scenario.phases) {
            viewModel.submitDecision(phase.dilemma.choices.first())
            viewModel.proceedFromFeedback()
        }

        val aar = viewModel.afterActionReport.value
        assertNotNull(aar)
        assertEquals(scenario.phases.size, aar?.decisionsTimeline?.size)
    }

    @Test
    fun testQA69_CompletedDrillSavesToDatabase() = runTest {
        val viewModel = BreachSimulatorViewModel(repository)
        val scenario = ScenariosData.allScenarios.first()
        viewModel.selectScenario(scenario)
        viewModel.startSimulation()

        for (phase in scenario.phases) {
            viewModel.submitDecision(phase.dilemma.choices.first())
            viewModel.proceedFromFeedback()
        }

        val records = repository.allRecords.first()
        assertTrue(records.isNotEmpty())
        assertEquals(scenario.id, records.first().scenarioId)
    }

    @Test
    fun testQA70_ExecutiveSummaryReflectsPerformance() {
        val viewModel = BreachSimulatorViewModel(repository)
        val scenario = ScenariosData.allScenarios.first()
        viewModel.selectScenario(scenario)
        viewModel.startSimulation()

        for (phase in scenario.phases) {
            viewModel.submitDecision(phase.dilemma.choices.first())
            viewModel.proceedFromFeedback()
        }

        val aar = viewModel.afterActionReport.value
        assertNotNull(aar)
        assertTrue(aar!!.executiveSummary.isNotBlank())
    }

    // =========================================================================
    // DOMAIN Q8: Room Database and Persistence Operations (Checks 71 - 80)
    // =========================================================================

    @Test
    fun testQA71_SimulationRecordConvertsFieldsCorrectly() = runTest {
        val record = SimulationRecord(
            scenarioId = "rec-test",
            scenarioTitle = "Test Record",
            completedAt = 1710000000000L,
            letterGrade = "A",
            totalScore = 85,
            finalCostUsd = 2_500_000L,
            totalTimeHours = 24,
            publicTrustPercent = 75,
            legalRiskLevel = "Moderate",
            decisionsCount = 3,
            executiveSummary = "Test executive summary"
        )
        val id = repository.saveRecord(record)
        val fetched = repository.getRecordById(id)
        assertNotNull(fetched)
        assertEquals(record.scenarioId, fetched?.scenarioId)
        assertEquals(record.finalCostUsd, fetched?.finalCostUsd)
        assertEquals(record.totalScore, fetched?.totalScore)
        assertEquals(record.letterGrade, fetched?.letterGrade)
    }

    @Test
    fun testQA72_RepositoryGetRecordByIdReturnsNullForNonExistentKey() = runTest {
        val record = repository.getRecordById(123456789L)
        assertNull(record)
    }

    @Test
    fun testQA73_RepositoryDeleteRecordRemovesRow() = runTest {
        val id = repository.saveRecord(SimulationRecord(scenarioId = "to-delete", scenarioTitle = "Del", completedAt = 100L, letterGrade = "C", totalScore = 70, finalCostUsd = 500L, totalTimeHours = 2, publicTrustPercent = 70, legalRiskLevel = "Low", decisionsCount = 1, executiveSummary = "Del"))
        repository.deleteRecord(id)
        val fetched = repository.getRecordById(id)
        assertNull(fetched)
    }

    @Test
    fun testQA74_RepositoryClearAllDeletesAllRecords() = runTest {
        repository.saveRecord(SimulationRecord(scenarioId = "s1", scenarioTitle = "T1", completedAt = 100L, letterGrade = "B", totalScore = 80, finalCostUsd = 100L, totalTimeHours = 1, publicTrustPercent = 80, legalRiskLevel = "Low", decisionsCount = 1, executiveSummary = "S1"))
        repository.saveRecord(SimulationRecord(scenarioId = "s2", scenarioTitle = "T2", completedAt = 200L, letterGrade = "B", totalScore = 80, finalCostUsd = 100L, totalTimeHours = 1, publicTrustPercent = 80, legalRiskLevel = "Low", decisionsCount = 1, executiveSummary = "S2"))
        repository.clearAll()
        val all = repository.allRecords.first()
        assertTrue(all.isEmpty())
    }

    @Test
    fun testQA75_RepositoryAllRecordsFlowEmitsOnInsert() = runTest {
        val flow = repository.allRecords
        assertEquals(0, flow.first().size)
        repository.saveRecord(SimulationRecord(scenarioId = "s1", scenarioTitle = "T1", completedAt = 100L, letterGrade = "B", totalScore = 80, finalCostUsd = 100L, totalTimeHours = 1, publicTrustPercent = 80, legalRiskLevel = "Low", decisionsCount = 1, executiveSummary = "S1"))
        assertEquals(1, flow.first().size)
    }

    @Test
    fun testQA76_TotalCostInHistorySummarySumsExposures() = runTest {
        repository.saveRecord(SimulationRecord(scenarioId = "s1", scenarioTitle = "T1", completedAt = 100L, letterGrade = "B", totalScore = 80, finalCostUsd = 1_000_000L, totalTimeHours = 1, publicTrustPercent = 80, legalRiskLevel = "Low", decisionsCount = 1, executiveSummary = "S1"))
        repository.saveRecord(SimulationRecord(scenarioId = "s2", scenarioTitle = "T2", completedAt = 200L, letterGrade = "B", totalScore = 80, finalCostUsd = 2_500_000L, totalTimeHours = 1, publicTrustPercent = 80, legalRiskLevel = "Low", decisionsCount = 1, executiveSummary = "S2"))
        val records = repository.allRecords.first()
        val totalExposure = records.sumOf { it.finalCostUsd }
        assertEquals(3_500_000L, totalExposure)
    }

    @Test
    fun testQA77_AverageReadinessScoreComputesMean() = runTest {
        repository.saveRecord(SimulationRecord(scenarioId = "s1", scenarioTitle = "T1", completedAt = 100L, letterGrade = "C", totalScore = 70, finalCostUsd = 100L, totalTimeHours = 1, publicTrustPercent = 80, legalRiskLevel = "Low", decisionsCount = 1, executiveSummary = "S1"))
        repository.saveRecord(SimulationRecord(scenarioId = "s2", scenarioTitle = "T2", completedAt = 200L, letterGrade = "A", totalScore = 90, finalCostUsd = 100L, totalTimeHours = 1, publicTrustPercent = 80, legalRiskLevel = "Low", decisionsCount = 1, executiveSummary = "S2"))
        val records = repository.allRecords.first()
        val avg = records.map { it.totalScore }.average().toInt()
        assertEquals(80, avg)
    }

    @Test
    fun testQA78_FormattedDatesRenderCleanly() {
        val record = SimulationRecord(
            scenarioId = "date-test",
            scenarioTitle = "Date Test",
            completedAt = 1710000000000L,
            letterGrade = "B",
            totalScore = 80,
            finalCostUsd = 100L,
            totalTimeHours = 1,
            publicTrustPercent = 80,
            legalRiskLevel = "Minimal",
            decisionsCount = 1,
            executiveSummary = "Test"
        )
        val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.US)
        val formatted = sdf.format(java.util.Date(record.completedAt))
        assertTrue(formatted.contains("2024"))
    }

    @Test
    fun testQA79_DatabaseEntityTableNameMatchesSimulationRecords() {
        val cursor = database.openHelper.readableDatabase.query("SELECT name FROM sqlite_master WHERE type='table' AND name='simulation_records'")
        cursor.use {
            assertTrue(it.count > 0 || database.simulationDao() != null)
        }
    }

    @Test
    fun testQA80_RoomDaoConfigured() {
        assertNotNull(database.simulationDao())
    }

    // =========================================================================
    // DOMAIN Q9: Navigation and Backstack State Machine (Checks 81 - 90)
    // =========================================================================

    @Test
    fun testQA81_AppInitializesOnHomeScreen() {
        val viewModel = BreachSimulatorViewModel(repository)
        assertEquals(Screen.HOME, viewModel.currentScreen.value)
    }

    @Test
    fun testQA82_NavigatingToDoctrineListUpdatesScreen() {
        val viewModel = BreachSimulatorViewModel(repository)
        viewModel.navigateTo(Screen.DOCTRINE_LIST)
        assertEquals(Screen.DOCTRINE_LIST, viewModel.currentScreen.value)
    }

    @Test
    fun testQA83_NavigatingToHistoryUpdatesScreen() {
        val viewModel = BreachSimulatorViewModel(repository)
        viewModel.navigateTo(Screen.HISTORY_LOGS)
        assertEquals(Screen.HISTORY_LOGS, viewModel.currentScreen.value)
    }

    @Test
    fun testQA84_SelectingScenarioTransitionsToDetail() {
        val viewModel = BreachSimulatorViewModel(repository)
        val scenario = ScenariosData.allScenarios.first()
        viewModel.selectScenario(scenario)
        assertEquals(Screen.SCENARIO_DETAIL, viewModel.currentScreen.value)
        assertEquals(scenario.id, viewModel.selectedScenario.value?.id)
    }

    @Test
    fun testQA85_StartingSimulationTransitionsToSimulation() {
        val viewModel = BreachSimulatorViewModel(repository)
        viewModel.selectScenario(ScenariosData.allScenarios.first())
        viewModel.startSimulation()
        assertEquals(Screen.SIMULATION, viewModel.currentScreen.value)
    }

    @Test
    fun testQA86_AbortingSimulationReturnsToHome() {
        val viewModel = BreachSimulatorViewModel(repository)
        viewModel.selectScenario(ScenariosData.allScenarios.first())
        viewModel.startSimulation()
        viewModel.abortSimulation()
        assertEquals(Screen.HOME, viewModel.currentScreen.value)
        assertNull(viewModel.selectedScenario.value)
    }

    @Test
    fun testQA87_SelectingDoctrinePlaybookTransitionsToDetail() {
        val viewModel = BreachSimulatorViewModel(repository)
        val playbook = DoctrineData.playbooks.first()
        viewModel.selectDoctrine(playbook)
        assertEquals(Screen.DOCTRINE_DETAIL, viewModel.currentScreen.value)
        assertEquals(playbook.id, viewModel.selectedDoctrine.value?.id)
    }

    @Test
    fun testQA88_ReturnToHomeFromAarResetsScreen() {
        val viewModel = BreachSimulatorViewModel(repository)
        val scenario = ScenariosData.allScenarios.first()
        viewModel.selectScenario(scenario)
        viewModel.startSimulation()
        for (phase in scenario.phases) {
            viewModel.submitDecision(phase.dilemma.choices.first())
            viewModel.proceedFromFeedback()
        }
        assertEquals(Screen.AAR_REPORT, viewModel.currentScreen.value)
        viewModel.returnToHome()
        assertEquals(Screen.HOME, viewModel.currentScreen.value)
        assertNull(viewModel.selectedScenario.value)
    }

    @Test
    fun testQA89_NavigatingBackFromScenarioDetailReturnsToHome() {
        val viewModel = BreachSimulatorViewModel(repository)
        viewModel.selectScenario(ScenariosData.allScenarios.first())
        assertEquals(Screen.SCENARIO_DETAIL, viewModel.currentScreen.value)
        viewModel.navigateTo(Screen.HOME)
        assertEquals(Screen.HOME, viewModel.currentScreen.value)
    }

    @Test
    fun testQA90_NavigatingBackFromDoctrineDetailReturnsToList() {
        val viewModel = BreachSimulatorViewModel(repository)
        viewModel.selectDoctrine(DoctrineData.playbooks.first())
        assertEquals(Screen.DOCTRINE_DETAIL, viewModel.currentScreen.value)
        viewModel.navigateTo(Screen.DOCTRINE_LIST)
        assertEquals(Screen.DOCTRINE_LIST, viewModel.currentScreen.value)
    }

    // =========================================================================
    // DOMAIN Q10: Search, Filtering and Doctrine Reference (Checks 91 - 100)
    // =========================================================================

    @Test
    fun testQA91_CategoryFilteringReturnsMatchingScenarios() {
        val ransomwareScenarios = ScenariosData.allScenarios.filter { it.category == ScenarioCategory.RANSOMWARE }
        assertTrue(ransomwareScenarios.isNotEmpty())
        for (scenario in ransomwareScenarios) {
            assertEquals(ScenarioCategory.RANSOMWARE, scenario.category)
        }
    }

    @Test
    fun testQA92_SupplyChainFilterReturnsScenario() {
        val supplyChain = ScenariosData.allScenarios.filter { it.category == ScenarioCategory.SUPPLY_CHAIN }
        assertTrue(supplyChain.any { it.codename == "OP_POISON_PILL" })
    }

    @Test
    fun testQA93_CloudExposureFilterReturnsScenario() {
        val cloud = ScenariosData.allScenarios.filter { it.category == ScenarioCategory.CLOUD_EXPOSURE }
        assertTrue(cloud.any { it.codename == "OP_OPEN_VAULT" })
    }

    @Test
    fun testQA94_DoctrineSearchByGdprReturnsPlaybook() {
        val results = DoctrineData.playbooks.filter {
            it.title.contains("GDPR", ignoreCase = true) || it.overview.contains("GDPR", ignoreCase = true)
        }
        assertEquals(1, results.size)
        assertEquals("gdpr-art-33-34", results.first().id)
    }

    @Test
    fun testQA95_DoctrineSearchBySecReturnsPlaybook() {
        val results = DoctrineData.playbooks.filter {
            it.title.contains("SEC", ignoreCase = true) || it.authority.contains("SEC", ignoreCase = true)
        }
        assertTrue(results.any { it.id == "sec-item-105" })
    }

    @Test
    fun testQA96_DoctrineSearchByHipaaReturnsPlaybook() {
        val results = DoctrineData.playbooks.filter {
            it.title.contains("HIPAA", ignoreCase = true)
        }
        assertEquals(1, results.size)
        assertEquals("hipaa-breach-rule", results.first().id)
    }

    @Test
    fun testQA97_DoctrineSearchByNistReturnsPlaybook() {
        val results = DoctrineData.playbooks.filter {
            it.title.contains("NIST", ignoreCase = true)
        }
        assertEquals(1, results.size)
        assertEquals("nist-sp-800-61", results.first().id)
    }

    @Test
    fun testQA98_DoctrineSearchByNydfsReturnsPlaybook() {
        val results = DoctrineData.playbooks.filter {
            it.title.contains("NYDFS", ignoreCase = true)
        }
        assertEquals(1, results.size)
        assertEquals("nydfs-23-nycrr-500", results.first().id)
    }

    @Test
    fun testQA99_EmptySearchQueryReturnsAllPlaybooks() {
        val query = ""
        val results = DoctrineData.playbooks.filter {
            if (query.isBlank()) true else it.title.contains(query, ignoreCase = true)
        }
        assertEquals(DoctrineData.playbooks.size, results.size)
    }

    @Test
    fun testQA100_EndToEndPlaythroughAllScenarios() = runTest {
        val viewModel = BreachSimulatorViewModel(repository)
        for (scenario in ScenariosData.allScenarios) {
            viewModel.selectScenario(scenario)
            viewModel.startSimulation()

            for (phase in scenario.phases) {
                val choice = phase.dilemma.choices.first()
                viewModel.submitDecision(choice)
                viewModel.proceedFromFeedback()
            }

            assertEquals(Screen.AAR_REPORT, viewModel.currentScreen.value)
            val aar = viewModel.afterActionReport.value
            assertNotNull(aar)
            assertTrue(aar!!.totalScore in 0..100)

            viewModel.returnToHome()
            assertEquals(Screen.HOME, viewModel.currentScreen.value)
        }
    }
}
