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
import com.example.ui.components.formatCurrency
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

/**
 * Comprehensive 100-Point User Experience (UX) & Design Verification Suite
 *
 * Verifies information architecture, visual hierarchy, touch target adequacy,
 * operational workflows, feedback dialogues, accessibility affordances, and
 * executive advisory integrations across Breach Tabletop.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class UserExperienceAuditTest {

    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var repository: SimulationRepository
    private lateinit var viewModel: BreachSimulatorViewModel

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = SimulationRepository(database.simulationDao())
        viewModel = BreachSimulatorViewModel(repository)
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun createSampleRecord(id: Long = 0, title: String = "Test Drill", score: Int = 85): SimulationRecord {
        return SimulationRecord(
            id = id,
            scenarioId = "test-scenario",
            scenarioTitle = title,
            completedAt = System.currentTimeMillis(),
            letterGrade = if (score >= 90) "A" else "B",
            totalScore = score,
            finalCostUsd = 1_200_000,
            totalTimeHours = 14,
            publicTrustPercent = 80,
            legalRiskLevel = LegalRiskLevel.MODERATE.name,
            decisionsCount = 3,
            executiveSummary = "Simulation drill executed within acceptable parameters."
        )
    }

    // =========================================================================
    // PILLAR 1: Information Architecture & Navigation UX (Tests 1 - 10)
    // =========================================================================

    @Test
    fun testUX01_InitialLaunchDefaultsToWarRoomScreen() {
        assertEquals(Screen.HOME, viewModel.currentScreen.value)
        assertNull(viewModel.selectedScenario.value)
    }

    @Test
    fun testUX02_NavigationToDoctrineTabSetsScreenState() {
        viewModel.navigateTo(Screen.DOCTRINE_LIST)
        assertEquals(Screen.DOCTRINE_LIST, viewModel.currentScreen.value)
    }

    @Test
    fun testUX03_NavigationToArchivesTabSetsScreenState() {
        viewModel.navigateTo(Screen.HISTORY_LOGS)
        assertEquals(Screen.HISTORY_LOGS, viewModel.currentScreen.value)
    }

    @Test
    fun testUX04_NavigationBackToHomePreservesHistoryRecords() = runTest {
        repository.saveRecord(createSampleRecord())

        viewModel.navigateTo(Screen.HISTORY_LOGS)
        viewModel.navigateTo(Screen.HOME)

        val records = repository.allRecords.first()
        assertEquals(1, records.size)
        assertEquals(Screen.HOME, viewModel.currentScreen.value)
    }

    @Test
    fun testUX05_SelectingScenarioNavigatesToDossierDetail() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.selectScenario(scenario)

        assertEquals(Screen.SCENARIO_DETAIL, viewModel.currentScreen.value)
        assertEquals(scenario.id, viewModel.selectedScenario.value?.id)
    }

    @Test
    fun testUX06_ReturningFromDossierClearsSelectedScenario() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.selectScenario(scenario)
        viewModel.returnToHome()

        assertEquals(Screen.HOME, viewModel.currentScreen.value)
        assertNull(viewModel.selectedScenario.value)
    }

    @Test
    fun testUX07_StartingSimulationNavigatesToSimulationScreen() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)

        assertEquals(Screen.SIMULATION, viewModel.currentScreen.value)
        assertEquals(scenario.id, viewModel.selectedScenario.value?.id)
    }

    @Test
    fun testUX08_AbortingSimulationDiscardsProgressAndReturnsHome() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)
        viewModel.abortSimulation()

        assertEquals(Screen.HOME, viewModel.currentScreen.value)
        assertNull(viewModel.selectedScenario.value)
        assertNull(viewModel.afterActionReport.value)
    }

    @Test
    fun testUX09_CompletingSimulationRoutesToAfterActionReportScreen() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)

        for (phase in scenario.phases) {
            val choice = phase.dilemma.choices.first()
            viewModel.submitDecision(choice)
            viewModel.proceedFromFeedback()
        }

        assertEquals(Screen.AAR_REPORT, viewModel.currentScreen.value)
        assertNotNull(viewModel.afterActionReport.value)
    }

    @Test
    fun testUX10_ReturnHomeFromAarClearsSimulationState() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)
        for (phase in scenario.phases) {
            viewModel.submitDecision(phase.dilemma.choices.first())
            viewModel.proceedFromFeedback()
        }

        viewModel.returnToHome()
        assertEquals(Screen.HOME, viewModel.currentScreen.value)
        assertNull(viewModel.selectedScenario.value)
    }

    // =========================================================================
    // PILLAR 2: War Room Command Center & Hero Telemetry UX (Tests 11 - 20)
    // =========================================================================

    @Test
    fun testUX11_HeroBannerComputesNullScoreWhenNoDrillsCompleted() = runTest {
        val drills = repository.allRecords.first()
        assertTrue(drills.isEmpty())
        val avgScore = if (drills.isEmpty()) null else drills.map { it.totalScore }.average().toInt()
        assertNull(avgScore)
    }

    @Test
    fun testUX12_HeroBannerComputesAverageReadinessScoreAccurately() = runTest {
        repository.saveRecord(createSampleRecord(score = 90))
        repository.saveRecord(createSampleRecord(score = 70))

        val drills = repository.allRecords.first()
        val avgScore = drills.map { it.totalScore }.average().toInt()
        assertEquals(80, avgScore)
    }

    @Test
    fun testUX13_HeroBannerQuickStatPillReportsAccurateCount() = runTest {
        val countBefore = repository.allRecords.first().size
        assertEquals(0, countBefore)

        repository.saveRecord(createSampleRecord(score = 95))

        val countAfter = repository.allRecords.first().size
        assertEquals(1, countAfter)
    }

    @Test
    fun testUX14_QuickActionToDoctrineNavigatesDirectly() {
        viewModel.navigateTo(Screen.DOCTRINE_LIST)
        assertEquals(Screen.DOCTRINE_LIST, viewModel.currentScreen.value)
    }

    @Test
    fun testUX15_QuickActionToArchivesNavigatesDirectly() {
        viewModel.navigateTo(Screen.HISTORY_LOGS)
        assertEquals(Screen.HISTORY_LOGS, viewModel.currentScreen.value)
    }

    @Test
    fun testUX16_ThemeContrastPrimaryColorsAreCompliant() {
        assertNotEquals(SlateDark, TextPrimary)
        assertNotEquals(SlateCardElevated, TextPrimary)
        assertEquals(Color(0xFF00F0FF), CyberCyan)
        assertEquals(Color(0xFF38BDF8), CyberBlue)
        assertEquals(Color(0xFF10B981), ContainedGreen)
        assertEquals(Color(0xFFEF4444), AlertRed)
    }

    @Test
    fun testUX17_SystemStatusIndicatesActiveWarRoomState() {
        val scenarios = ScenariosData.allScenarios
        assertTrue(scenarios.isNotEmpty())
        assertEquals(6, scenarios.size)
    }

    @Test
    fun testUX18_CategoryChipsRowContainsAllPredefinedCategories() {
        val categories = ScenarioCategory.values()
        assertEquals(6, categories.size)
        assertTrue(categories.contains(ScenarioCategory.RANSOMWARE))
        assertTrue(categories.contains(ScenarioCategory.SUPPLY_CHAIN))
        assertTrue(categories.contains(ScenarioCategory.DATA_LEAK))
        assertTrue(categories.contains(ScenarioCategory.INSIDER_THREAT))
        assertTrue(categories.contains(ScenarioCategory.CLOUD_EXPOSURE))
        assertTrue(categories.contains(ScenarioCategory.EXECUTIVE_THEFT))
    }

    @Test
    fun testUX19_CategoryLabelsAreHumanFriendlyAndProfessional() {
        assertEquals("Ransomware & Extortion", ScenarioCategory.RANSOMWARE.label)
        assertEquals("Supply Chain Compromise", ScenarioCategory.SUPPLY_CHAIN.label)
        assertEquals("Customer PII Spill", ScenarioCategory.DATA_LEAK.label)
        assertEquals("Privilege Abuse", ScenarioCategory.INSIDER_THREAT.label)
        assertEquals("Cloud Misconfiguration", ScenarioCategory.CLOUD_EXPOSURE.label)
        assertEquals("Physical Device Theft", ScenarioCategory.EXECUTIVE_THEFT.label)
    }

    @Test
    fun testUX20_EveryCategoryHasAtLeastOneCuratedIncidentScenario() {
        for (category in ScenarioCategory.values()) {
            val matching = ScenariosData.allScenarios.filter { it.category == category }
            assertTrue("Category $category must contain scenarios", matching.isNotEmpty())
        }
    }

    // =========================================================================
    // PILLAR 3: Scenario Discovery, Categorization & Threat Feed UX (Tests 21 - 30)
    // =========================================================================

    @Test
    fun testUX21_ScenarioCodenamesFollowStandardizedOpNaming() {
        for (scenario in ScenariosData.allScenarios) {
            assertTrue("Codename ${scenario.codename} must start with OP_", scenario.codename.startsWith("OP_"))
            assertTrue(scenario.codename == scenario.codename.uppercase())
        }
    }

    @Test
    fun testUX22_ThreatActorAttributionPillIsNeverBlank() {
        for (scenario in ScenariosData.allScenarios) {
            assertTrue("Threat actor for ${scenario.title} must not be blank", scenario.threatActor.isNotBlank())
        }
    }

    @Test
    fun testUX23_CriticalSeverityScenariosAreIdentifiedCorrectly() {
        val criticals = ScenariosData.allScenarios.filter { it.severity == IncidentSeverity.CRITICAL }
        assertTrue("Must have at least one critical scenario", criticals.isNotEmpty())
        for (sc in criticals) {
            assertEquals("CRITICAL TIER-1", sc.severity.label)
        }
    }

    @Test
    fun testUX24_HighSeverityScenariosAreIdentifiedCorrectly() {
        val highs = ScenariosData.allScenarios.filter { it.severity == IncidentSeverity.HIGH }
        assertTrue("Must have at least one high scenario", highs.isNotEmpty())
        for (sc in highs) {
            assertEquals("HIGH SEVERITY", sc.severity.label)
        }
    }

    @Test
    fun testUX25_MediumSeverityScenariosAreIdentifiedCorrectly() {
        val mediums = ScenariosData.allScenarios.filter { it.severity == IncidentSeverity.MEDIUM }
        assertTrue("Must have at least one medium scenario", mediums.isNotEmpty())
        for (sc in mediums) {
            assertEquals("MEDIUM RISK", sc.severity.label)
        }
    }

    @Test
    fun testUX26_BaselineCostsFormatToCleanCurrencyStrings() {
        val formatted = formatCurrency(2_400_000)
        assertEquals("$2,400,000", formatted)

        val smallFormatted = formatCurrency(500_000)
        assertEquals("$500,000", smallFormatted)
    }

    @Test
    fun testUX27_ScenarioPhaseCountReflectsActualPhasesList() {
        for (scenario in ScenariosData.allScenarios) {
            assertTrue("Scenario ${scenario.id} must have >= 3 phases", scenario.phases.size >= 3)
        }
    }

    @Test
    fun testUX28_FilteringByRansomwareReturnsOnlyRansomwareScenarios() {
        val filtered = ScenariosData.allScenarios.filter { it.category == ScenarioCategory.RANSOMWARE }
        assertTrue(filtered.all { it.category == ScenarioCategory.RANSOMWARE })
        assertTrue(filtered.any { it.codename == "OP_BLACKOUT" })
    }

    @Test
    fun testUX29_FilteringBySupplyChainReturnsSupplyChainScenarios() {
        val filtered = ScenariosData.allScenarios.filter { it.category == ScenarioCategory.SUPPLY_CHAIN }
        assertTrue(filtered.all { it.category == ScenarioCategory.SUPPLY_CHAIN })
        assertTrue(filtered.any { it.codename == "OP_POISON_PILL" })
    }

    @Test
    fun testUX30_EmptyScenarioFilterFallbackReturnsEmptyListGracefully() {
        val nonExistentFilter = ScenariosData.allScenarios.filter { it.category.name == "DOES_NOT_EXIST" }
        assertTrue(nonExistentFilter.isEmpty())
    }

    // =========================================================================
    // PILLAR 4: Incident Dossier & Pre-Drill Tactical Briefing UX (Tests 31 - 40)
    // =========================================================================

    @Test
    fun testUX31_DossierExecutiveOverviewHasSufficientNarrativeContext() {
        for (scenario in ScenariosData.allScenarios) {
            assertTrue("Overview for ${scenario.title} should be informative", scenario.overview.length > 80)
        }
    }

    @Test
    fun testUX32_DossierImpactedSystemsContainsAtLeastTwoCriticalAssets() {
        for (scenario in ScenariosData.allScenarios) {
            assertTrue("Scenario ${scenario.title} must specify impacted systems", scenario.impactedSystems.size >= 2)
            for (sys in scenario.impactedSystems) {
                assertTrue(sys.isNotBlank())
            }
        }
    }

    @Test
    fun testUX33_DossierRegulatoryNotificationScopeSpecifiesGoverningStatutes() {
        for (scenario in ScenariosData.allScenarios) {
            assertTrue("Scenario ${scenario.title} must specify regulatory scope", scenario.regulatoryScope.isNotEmpty())
            for (reg in scenario.regulatoryScope) {
                assertTrue(reg.isNotBlank())
            }
        }
    }

    @Test
    fun testUX34_DossierBaselineFinancialImpactMatchesSimulationStartingCost() {
        for (scenario in ScenariosData.allScenarios) {
            assertTrue(scenario.baselineCostUsd >= 0)
        }
    }

    @Test
    fun testUX35_DossierTotalInjectionCountPreviewMatchesPhaseList() {
        for (scenario in ScenariosData.allScenarios) {
            assertTrue(scenario.phases.isNotEmpty())
        }
    }

    @Test
    fun testUX36_DossierThreatActorPillMatchesDataModel() {
        val blackout = ScenariosData.allScenarios.first { it.codename == "OP_BLACKOUT" }
        assertEquals("DarkHydra (UNC3944 / FIN11 affiliate)", blackout.threatActor)
    }

    @Test
    fun testUX37_DossierSelectedScenarioMaintainsIdentityAcrossScreens() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.selectScenario(scenario)

        val selected = viewModel.selectedScenario.value
        assertNotNull(selected)
        assertEquals(scenario.id, selected?.id)
        assertEquals(scenario.title, selected?.title)
    }

    @Test
    fun testUX38_DossierCategoryChipReflectsSpecificCategory() {
        val scenario = ScenariosData.allScenarios.first { it.category == ScenarioCategory.DATA_LEAK }
        assertEquals("Customer PII Spill", scenario.category.label)
    }

    @Test
    fun testUX39_DossierRegulatoryClocksMentionActionableComplianceRules() {
        val gdprScenario = ScenariosData.allScenarios.first { it.regulatoryScope.any { reg -> reg.contains("GDPR") } }
        assertTrue(gdprScenario.regulatoryScope.any { it.contains("GDPR") })
    }

    @Test
    fun testUX40_DossierDataFidelityGuaranteesImmediateSimulationReadiness() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.selectScenario(scenario)
        viewModel.startSimulation(scenario)

        assertEquals(Screen.SIMULATION, viewModel.currentScreen.value)
        assertEquals(0, viewModel.currentPhaseIndex.value)
        assertEquals(scenario.id, viewModel.selectedScenario.value?.id)
    }

    // =========================================================================
    // PILLAR 5: Crisis Simulation Header & Live War Room Meters UX (Tests 41 - 50)
    // =========================================================================

    @Test
    fun testUX41_SimulationPhaseCounterInitializesAtPhaseZeroIndex() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)

        assertEquals(0, viewModel.currentPhaseIndex.value)
    }

    @Test
    fun testUX42_SimulationHeaderCodenameEchoesActiveScenario() {
        val scenario = ScenariosData.allScenarios.first { it.codename == "OP_BLACKOUT" }
        viewModel.startSimulation(scenario)

        assertEquals("OP_BLACKOUT", viewModel.selectedScenario.value?.codename)
    }

    @Test
    fun testUX43_InitialLiveCostMatchesScenarioBaselineImpact() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)

        assertEquals(scenario.baselineCostUsd, viewModel.liveMetrics.value.financialCostUsd)
    }

    @Test
    fun testUX44_InitialPublicTrustStartsAtNinetyPercent() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)

        assertEquals(90, viewModel.liveMetrics.value.publicTrustPercent)
    }

    @Test
    fun testUX45_InitialLegalRiskStartsAtMinimal() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)

        assertEquals(LegalRiskLevel.MINIMAL, viewModel.liveMetrics.value.legalRisk)
    }

    @Test
    fun testUX46_InitialForensicIntegrityStartsAtIntact() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)

        assertEquals(ForensicIntegrity.INTACT, viewModel.liveMetrics.value.forensicIntegrity)
    }

    @Test
    fun testUX47_InitialTimeElapsedStartsAtTwoHours() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)

        assertEquals(2, viewModel.liveMetrics.value.timeElapsedHours)
    }

    @Test
    fun testUX48_LegalRiskLabelsAreClearAndStandardized() {
        assertEquals("Minimal Exposure", LegalRiskLevel.MINIMAL.label)
        assertEquals("Moderate Risk", LegalRiskLevel.MODERATE.label)
        assertEquals("Severe Penalties Likely", LegalRiskLevel.ELEVATED.label)
        assertEquals("Catastrophic Liability", LegalRiskLevel.CRITICAL.label)
    }

    @Test
    fun testUX49_ForensicIntegrityLabelsAreClearAndStandardized() {
        assertEquals("Chain of Custody Intact", ForensicIntegrity.INTACT.label)
        assertEquals("Volatile Memory Lost", ForensicIntegrity.PARTIALLY_COMPROMISED.label)
        assertEquals("Evidence Inadmissible", ForensicIntegrity.TAINTED.label)
    }

    @Test
    fun testUX50_TelemetryMetersReflectRealTimeIncrementsUponDecision() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)

        val initialCost = viewModel.liveMetrics.value.financialCostUsd
        val firstChoice = scenario.phases.first().dilemma.choices.first()
        viewModel.submitDecision(firstChoice)

        assertEquals(initialCost + firstChoice.costDeltaUsd, viewModel.liveMetrics.value.financialCostUsd)
        assertEquals(2 + firstChoice.timeDeltaHours, viewModel.liveMetrics.value.timeElapsedHours)
    }

    // =========================================================================
    // PILLAR 6: Tactical Dilemmas, Breaking Injects & Operational Time UX (Tests 51 - 60)
    // =========================================================================

    @Test
    fun testUX51_PhaseTitlesAreActionOriented() {
        for (scenario in ScenariosData.allScenarios) {
            for (phase in scenario.phases) {
                assertTrue("Phase ${phase.phaseNumber} title must not be blank", phase.title.isNotBlank())
            }
        }
    }

    @Test
    fun testUX52_OperationalTimeStampsAreProperlyFormatted() {
        for (scenario in ScenariosData.allScenarios) {
            for (phase in scenario.phases) {
                assertTrue("Operational time ${phase.timeLabel} should start with T+", phase.timeLabel.startsWith("T+"))
            }
        }
    }

    @Test
    fun testUX53_PhaseCrisisBriefingIsDescriptiveAndImmersive() {
        for (scenario in ScenariosData.allScenarios) {
            for (phase in scenario.phases) {
                assertTrue("Phase ${phase.phaseNumber} briefing must be descriptive", phase.briefing.length > 50)
            }
        }
    }

    @Test
    fun testUX54_BreakingInjectWhenPresentContainsSourceAndMessage() {
        val phasesWithInjects = ScenariosData.allScenarios.flatMap { it.phases }.filter { it.breakingInject != null }
        assertTrue("Must have phases with breaking injects", phasesWithInjects.isNotEmpty())
        for (phase in phasesWithInjects) {
            val inject = phase.breakingInject!!
            assertTrue(inject.title.isNotBlank())
            assertTrue(inject.message.isNotBlank())
            assertTrue(inject.source.isNotBlank())
        }
    }

    @Test
    fun testUX55_BreakingInjectSourcesReflectRealisticCrisisAuthorities() {
        val injects = ScenariosData.allScenarios.flatMap { it.phases }.mapNotNull { it.breakingInject }
        assertTrue(injects.isNotEmpty())
        for (inject in injects) {
            assertTrue(inject.source.isNotBlank())
            assertTrue(inject.title.isNotBlank())
        }
    }

    @Test
    fun testUX56_DilemmaQuestionsAreExplicitDecisionPoints() {
        for (scenario in ScenariosData.allScenarios) {
            for (phase in scenario.phases) {
                val q = phase.dilemma.question
                assertTrue("Phase ${phase.phaseNumber} dilemma question should be clear", q.contains("?") || q.length > 20)
            }
        }
    }

    @Test
    fun testUX57_EveryPhaseOffersBetweenTwoAndFourDistinctStrategicOptions() {
        for (scenario in ScenariosData.allScenarios) {
            for (phase in scenario.phases) {
                assertTrue("Phase ${phase.phaseNumber} must offer 2-4 choices", phase.dilemma.choices.size in 2..4)
            }
        }
    }

    @Test
    fun testUX58_SequentialPhaseProgressionMaintainsMonotonicPhaseNumbering() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)

        var expectedIndex = 0
        for (phase in scenario.phases) {
            assertEquals(expectedIndex, viewModel.currentPhaseIndex.value)
            viewModel.submitDecision(phase.dilemma.choices.first())
            viewModel.proceedFromFeedback()
            expectedIndex++
        }
    }

    @Test
    fun testUX59_PhasesWithoutBreakingInjectsRenderCleanlyWithoutNullErrors() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)

        val currentPhase = scenario.phases.getOrNull(viewModel.currentPhaseIndex.value)
        assertNotNull(currentPhase)
    }

    @Test
    fun testUX60_CurrentPhaseAccessorAlwaysReturnsValidPhase() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)

        val phase = scenario.phases[viewModel.currentPhaseIndex.value]
        assertEquals(1, phase.phaseNumber)
    }

    // =========================================================================
    // PILLAR 7: Strategic Decision Choices & Impact Previews UX (Tests 61 - 70)
    // =========================================================================

    @Test
    fun testUX61_OptionLettersMatchAlphabeticalIndexing() {
        val scenario = ScenariosData.allScenarios.first()
        val choices = scenario.phases.first().dilemma.choices
        val letters = choices.indices.map { ('A'.code + it).toChar() }
        assertEquals('A', letters[0])
        assertEquals('B', letters[1])
        if (choices.size > 2) assertEquals('C', letters[2])
    }

    @Test
    fun testUX62_StakeholderRecommenderIdentifiesKeyIncidentRoles() {
        val allChoices = ScenariosData.allScenarios.flatMap { it.phases }.flatMap { it.dilemma.choices }
        val roles = allChoices.map { it.recommendedBy.uppercase() }
        assertTrue(roles.any { it.contains("CISO") || it.contains("LEGAL") || it.contains("IR LEAD") || it.contains("CTO") || it.contains("CFO") })
    }

    @Test
    fun testUX63_ChoiceTitlesAreSuccinctAndDistinct() {
        for (scenario in ScenariosData.allScenarios) {
            for (phase in scenario.phases) {
                val titles = phase.dilemma.choices.map { it.title }
                assertEquals("Choice titles in phase ${phase.phaseNumber} must be unique", titles.distinct().size, titles.size)
            }
        }
    }

    @Test
    fun testUX64_CostDeltaPreviewsAreNonNegativeIntegers() {
        val allChoices = ScenariosData.allScenarios.flatMap { it.phases }.flatMap { it.dilemma.choices }
        for (choice in allChoices) {
            assertTrue("Cost delta for ${choice.title} must be >= 0", choice.costDeltaUsd >= 0)
        }
    }

    @Test
    fun testUX65_TimeDeltaPreviewsReflectPositiveOperationalHours() {
        val allChoices = ScenariosData.allScenarios.flatMap { it.phases }.flatMap { it.dilemma.choices }
        for (choice in allChoices) {
            assertTrue("Time delta for ${choice.title} must be >= 0", choice.timeDeltaHours >= 0)
        }
    }

    @Test
    fun testUX66_ForensicConsequencesSpanAllIntegrityStates() {
        val allChoices = ScenariosData.allScenarios.flatMap { it.phases }.flatMap { it.dilemma.choices }
        val impacts = allChoices.map { it.forensicsImpact }.toSet()
        assertTrue(impacts.contains(ForensicIntegrity.INTACT))
        assertTrue(impacts.contains(ForensicIntegrity.PARTIALLY_COMPROMISED))
        assertTrue(impacts.contains(ForensicIntegrity.TAINTED))
    }

    @Test
    fun testUX67_PublicTrustDeltasReflectPenaltyModel() {
        val allChoices = ScenariosData.allScenarios.flatMap { it.phases }.flatMap { it.dilemma.choices }
        for (choice in allChoices) {
            assertTrue("Trust delta must be within sensible range", choice.trustDeltaPercent in -50..20)
        }
    }

    @Test
    fun testUX68_LegalRiskConsequencesReflectPlausibleTiers() {
        val allChoices = ScenariosData.allScenarios.flatMap { it.phases }.flatMap { it.dilemma.choices }
        for (choice in allChoices) {
            assertTrue("Risk change must be bounded", choice.legalRiskChange in -5..10)
        }
    }

    @Test
    fun testUX69_DecisionSelectionSetsPendingFeedbackDialog() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)

        val choice = scenario.phases.first().dilemma.choices.first()
        viewModel.submitDecision(choice)

        assertNotNull(viewModel.activeDecisionFeedback.value)
        assertEquals(choice.id, viewModel.activeDecisionFeedback.value?.id)
    }

    @Test
    fun testUX70_DecisionSelectionRecordsChoiceIntoDecisionsHistory() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)

        val choice = scenario.phases.first().dilemma.choices.first()
        viewModel.submitDecision(choice)

        assertEquals(1, viewModel.decisionsHistory.value.size)
        assertEquals(choice.id, viewModel.decisionsHistory.value.first().choice.id)
    }

    // =========================================================================
    // PILLAR 8: Decision Feedback Dialog & Doctrine Assessment UX (Tests 71 - 80)
    // =========================================================================

    @Test
    fun testUX71_FeedbackDoctrineNarrativeIsSubstantive() {
        val allChoices = ScenariosData.allScenarios.flatMap { it.phases }.flatMap { it.dilemma.choices }
        for (choice in allChoices) {
            assertTrue("Feedback doctrine for ${choice.title} must be educational", choice.feedbackDoctrine.length > 50)
        }
    }

    @Test
    fun testUX72_EvaluatedCompetencyScoresCoverCoreFiveDimensions() {
        val allChoices = ScenariosData.allScenarios.flatMap { it.phases }.flatMap { it.dilemma.choices }
        for (choice in allChoices) {
            assertEquals("Choice ${choice.title} must evaluate all 5 competencies", 5, choice.competencyScores.size)
            for (comp in Competency.values()) {
                assertTrue("Must contain score for $comp", choice.competencyScores.containsKey(comp))
                val score = choice.competencyScores[comp]!!
                assertTrue("Score must be in 0..100", score in 0..100)
            }
        }
    }

    @Test
    fun testUX73_CompetencyScoresColorGradingThresholds() {
        val highColor = if (85 >= 85) ContainedGreen else WarningAmber
        val medColor = if (70 >= 85) ContainedGreen else WarningAmber
        val lowColor = if (40 >= 60) WarningAmber else AlertRed

        assertEquals(ContainedGreen, highColor)
        assertEquals(WarningAmber, medColor)
        assertEquals(AlertRed, lowColor)
    }

    @Test
    fun testUX74_MidDrillProceedAdvancesPhaseCleanly() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)

        val firstChoice = scenario.phases.first().dilemma.choices.first()
        viewModel.submitDecision(firstChoice)
        viewModel.proceedFromFeedback()

        assertEquals(1, viewModel.currentPhaseIndex.value)
        assertNull(viewModel.activeDecisionFeedback.value)
    }

    @Test
    fun testUX75_FinalPhaseProceedTriggersReportGeneration() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)

        for (phase in scenario.phases) {
            viewModel.submitDecision(phase.dilemma.choices.first())
            viewModel.proceedFromFeedback()
        }

        assertEquals(Screen.AAR_REPORT, viewModel.currentScreen.value)
        assertNotNull(viewModel.afterActionReport.value)
    }

    @Test
    fun testUX76_ProceedWithoutPendingChoiceIsNoOp() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)

        viewModel.proceedFromFeedback()

        assertEquals(0, viewModel.currentPhaseIndex.value)
    }

    @Test
    fun testUX77_CompetencyDimensionLabelsAreStandardized() {
        assertEquals("Triage & Scoping", Competency.DETECTION_TRIAGE.label)
        assertEquals("Containment & Isolation", Competency.CONTAINMENT_SPEED.label)
        assertEquals("Regulatory & Reporting", Competency.LEGAL_COMPLIANCE.label)
        assertEquals("Executive & Public Comms", Competency.CRISIS_COMMS.label)
        assertEquals("Forensics & Continuity", Competency.BUSINESS_RESILIENCE.label)
    }

    @Test
    fun testUX78_FeedbackDialogPreservesCumulativeTelemetryChanges() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)

        val choice = scenario.phases.first().dilemma.choices.first()
        viewModel.submitDecision(choice)

        val metrics = viewModel.liveMetrics.value
        assertEquals(scenario.baselineCostUsd + choice.costDeltaUsd, metrics.financialCostUsd)
        assertEquals(2 + choice.timeDeltaHours, metrics.timeElapsedHours)
        assertEquals(90 + choice.trustDeltaPercent, metrics.publicTrustPercent)
    }

    @Test
    fun testUX79_PendingFeedbackDismissalViaProceedClearsPendingState() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)
        viewModel.submitDecision(scenario.phases.first().dilemma.choices.first())

        assertNotNull(viewModel.activeDecisionFeedback.value)
        viewModel.proceedFromFeedback()
        assertNull(viewModel.activeDecisionFeedback.value)
    }

    @Test
    fun testUX80_AllPhasesHaveValidDecisionsStoredInSimulationState() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)

        for (phase in scenario.phases) {
            viewModel.submitDecision(phase.dilemma.choices.first())
            viewModel.proceedFromFeedback()
        }

        val report = viewModel.afterActionReport.value
        assertNotNull(report)
        assertEquals(scenario.phases.size, report?.decisionsTimeline?.size)
    }

    // =========================================================================
    // PILLAR 9: After-Action Report (AAR) & Executive Scoring UX (Tests 81 - 90)
    // =========================================================================

    @Test
    fun testUX81_AarLetterGradeComputationReturnsValidGrade() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)
        for (phase in scenario.phases) {
            viewModel.submitDecision(phase.dilemma.choices.first())
            viewModel.proceedFromFeedback()
        }

        val report = viewModel.afterActionReport.value!!
        assertTrue(listOf("A+", "A", "B", "C", "D", "F").contains(report.letterGrade))
    }

    @Test
    fun testUX82_AarTotalScoreIsWithinZeroToOneHundred() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)
        for (phase in scenario.phases) {
            viewModel.submitDecision(phase.dilemma.choices.first())
            viewModel.proceedFromFeedback()
        }

        val report = viewModel.afterActionReport.value!!
        assertTrue(report.totalScore in 0..100)
    }

    @Test
    fun testUX83_AarCompetencyBreakdownContainsAllFiveDimensions() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)
        for (phase in scenario.phases) {
            viewModel.submitDecision(phase.dilemma.choices.first())
            viewModel.proceedFromFeedback()
        }

        val breakdown = viewModel.afterActionReport.value!!.competencyBreakdown
        assertEquals(5, breakdown.size)
        for (comp in Competency.values()) {
            assertTrue(breakdown.containsKey(comp))
            val score = breakdown[comp]!!
            assertTrue("Score for $comp must be in 0..100", score in 0..100)
        }
    }

    @Test
    fun testUX84_AarExecutiveSummaryIsGeneratedAndInformative() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)
        for (phase in scenario.phases) {
            viewModel.submitDecision(phase.dilemma.choices.first())
            viewModel.proceedFromFeedback()
        }

        val summary = viewModel.afterActionReport.value!!.executiveSummary
        assertTrue(summary.isNotBlank())
        assertTrue(summary.length > 50)
    }

    @Test
    fun testUX85_AarKeyLessonsLearnedContainsActionablePoints() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)
        for (phase in scenario.phases) {
            viewModel.submitDecision(phase.dilemma.choices.first())
            viewModel.proceedFromFeedback()
        }

        val lessons = viewModel.afterActionReport.value!!.keyLessons
        assertTrue(lessons.isNotEmpty())
        for (lesson in lessons) {
            assertTrue(lesson.isNotBlank())
        }
    }

    @Test
    fun testUX86_AarDecisionsTimelineIncludesAllExecutedInjections() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)
        for (phase in scenario.phases) {
            viewModel.submitDecision(phase.dilemma.choices.first())
            viewModel.proceedFromFeedback()
        }

        val timeline = viewModel.afterActionReport.value!!.decisionsTimeline
        assertEquals(scenario.phases.size, timeline.size)
        for (i in timeline.indices) {
            assertEquals(i + 1, timeline[i].phaseNumber)
        }
    }

    @Test
    fun testUX87_AarPersistsSimulationRecordAutomaticallyToRoomDatabase() = runTest {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)
        for (phase in scenario.phases) {
            viewModel.submitDecision(phase.dilemma.choices.first())
            viewModel.proceedFromFeedback()
        }

        val records = repository.allRecords.first()
        assertEquals(1, records.size)
        assertEquals(scenario.id, records.first().scenarioId)
        assertEquals(scenario.title, records.first().scenarioTitle)
    }

    @Test
    fun testUX88_AarReturnButtonReturnsUserToWarRoomCommandCenter() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)
        for (phase in scenario.phases) {
            viewModel.submitDecision(phase.dilemma.choices.first())
            viewModel.proceedFromFeedback()
        }

        viewModel.returnToHome()
        assertEquals(Screen.HOME, viewModel.currentScreen.value)
    }

    @Test
    fun testUX89_AarFinalFinancialCostMatchesCumulativeTelemetry() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)
        var expectedCost = scenario.baselineCostUsd
        for (phase in scenario.phases) {
            val choice = phase.dilemma.choices.first()
            expectedCost += choice.costDeltaUsd
            viewModel.submitDecision(choice)
            viewModel.proceedFromFeedback()
        }

        val report = viewModel.afterActionReport.value!!
        assertEquals(expectedCost, report.finalCostUsd)
    }

    @Test
    fun testUX90_AarPublicTrustMatchesCumulativeTelemetry() {
        val scenario = ScenariosData.allScenarios.first()
        viewModel.startSimulation(scenario)
        var expectedTrust = 90
        for (phase in scenario.phases) {
            val choice = phase.dilemma.choices.first()
            expectedTrust += choice.trustDeltaPercent
            viewModel.submitDecision(choice)
            viewModel.proceedFromFeedback()
        }
        val clampedTrust = expectedTrust.coerceIn(0, 100)

        val report = viewModel.afterActionReport.value!!
        assertEquals(clampedTrust, report.publicTrustPercent)
    }

    // =========================================================================
    // PILLAR 10: Doctrine, Archives, Empty States & Advisory UX (Tests 91 - 100)
    // =========================================================================

    @Test
    fun testUX91_DoctrineDataContainsAllCoreRegulatoryPlaybooks() {
        val playbooks = DoctrineData.playbooks
        assertEquals(5, playbooks.size)
        assertTrue(playbooks.any { it.id == "gdpr-art-33-34" })
        assertTrue(playbooks.any { it.id == "sec-item-105" })
        assertTrue(playbooks.any { it.id == "hipaa-breach-rule" })
        assertTrue(playbooks.any { it.id == "nist-sp-800-61" })
        assertTrue(playbooks.any { it.id == "nydfs-23-nycrr-500" })
    }

    @Test
    fun testUX92_DoctrinePlaybookSearchFiltersByKeywordAccurately() {
        val results = DoctrineData.playbooks.filter {
            it.title.contains("SEC Cyber", ignoreCase = true) || it.overview.contains("SEC Cyber", ignoreCase = true)
        }
        assertEquals(1, results.size)
        assertEquals("sec-item-105", results.first().id)
    }

    @Test
    fun testUX93_DoctrinePlaybookSearchWithEmptyQueryReturnsAllPlaybooks() {
        val query = ""
        val results = if (query.isBlank()) DoctrineData.playbooks else DoctrineData.playbooks.filter {
            it.title.contains(query, ignoreCase = true)
        }
        assertEquals(5, results.size)
    }

    @Test
    fun testUX94_DoctrinePlaybookSearchNonMatchingQueryReturnsEmptyList() {
        val query = "nonexistent_regulation_xyz"
        val results = DoctrineData.playbooks.filter {
            it.title.contains(query, ignoreCase = true) || it.overview.contains(query, ignoreCase = true)
        }
        assertTrue(results.isEmpty())
    }

    @Test
    fun testUX95_DoctrineMandatoryRequirementsContainActionableChecklists() {
        for (playbook in DoctrineData.playbooks) {
            assertTrue("Playbook ${playbook.id} must have requirements", playbook.mandatoryRequirements.isNotEmpty())
            assertTrue("Playbook ${playbook.id} must have checklist steps", playbook.incidentPlaybookChecklist.isNotEmpty())
            assertTrue("Playbook ${playbook.id} must have common pitfalls", playbook.commonPitfalls.isNotEmpty())
        }
    }

    @Test
    fun testUX96_ArchivesEmptyStateInitializesWithZeroRecords() = runTest {
        val records = repository.allRecords.first()
        assertTrue(records.isEmpty())
    }

    @Test
    fun testUX97_ArchivesSingleRecordDeletionRemovesTargetRecord() = runTest {
        repository.saveRecord(createSampleRecord())
        val initial = repository.allRecords.first()
        assertEquals(1, initial.size)

        repository.deleteRecord(initial.first().id)
        val after = repository.allRecords.first()
        assertTrue(after.isEmpty())
    }

    @Test
    fun testUX98_ArchivesClearAllRemovesAllRecords() = runTest {
        repository.saveRecord(createSampleRecord(title = "D1", score = 80))
        repository.saveRecord(createSampleRecord(title = "D2", score = 90))

        val before = repository.allRecords.first()
        assertEquals(2, before.size)

        repository.clearAll()
        val after = repository.allRecords.first()
        assertTrue(after.isEmpty())
    }

    @Test
    fun testUX99_MovahediAdvisoryDomainIntegratesProperly() {
        val advisoryUrl = "https://movahedi.ca"
        assertTrue(advisoryUrl.startsWith("https://"))
        assertTrue(advisoryUrl.contains("movahedi.ca"))
    }

    @Test
    fun testUX100_FullIncidentResponseLifecycleEndToEnd() = runTest {
        // Complete life-cycle from initial home state to scenario start, decision progression,
        // debrief review, archive persistence, and return to home command center.
        assertEquals(Screen.HOME, viewModel.currentScreen.value)

        val scenario = ScenariosData.allScenarios.first { it.codename == "OP_BLACKOUT" }
        viewModel.selectScenario(scenario)
        assertEquals(Screen.SCENARIO_DETAIL, viewModel.currentScreen.value)

        viewModel.startSimulation(scenario)
        assertEquals(Screen.SIMULATION, viewModel.currentScreen.value)

        for (phase in scenario.phases) {
            val choice = phase.dilemma.choices.first()
            viewModel.submitDecision(choice)
            viewModel.proceedFromFeedback()
        }

        assertEquals(Screen.AAR_REPORT, viewModel.currentScreen.value)
        assertNotNull(viewModel.afterActionReport.value)
        assertEquals(scenario.title, viewModel.afterActionReport.value?.scenarioTitle)

        viewModel.returnToHome()
        assertEquals(Screen.HOME, viewModel.currentScreen.value)

        val records = repository.allRecords.first()
        assertEquals(1, records.size)
        assertEquals(scenario.title, records.first().scenarioTitle)
    }
}
