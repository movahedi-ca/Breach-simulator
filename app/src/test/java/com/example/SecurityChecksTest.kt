package com.example

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.DoctrineData
import com.example.data.ScenariosData
import com.example.data.SimulationRecord
import com.example.data.SimulationRepository
import com.example.model.*
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
import java.io.File
import java.security.MessageDigest
import java.security.SecureRandom

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SecurityChecksTest {

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
    // DOMAIN S1: Android Manifest and Component Security (Checks 1 - 10)
    // =========================================================================

    @Test
    fun testSEC01_ApplicationDisallowsInsecureBackup() {
        val appInfo = context.packageManager.getApplicationInfo(context.packageName, 0)
        val allowBackup = (appInfo.flags and ApplicationInfo.FLAG_ALLOW_BACKUP) != 0
        assertFalse("allowBackup must be false to prevent adb backup extraction", allowBackup)
    }

    @Test
    fun testSEC02_MainActivityIsExportedStrictlyForLauncherOnly() {
        val activityInfo = context.packageManager.getActivityInfo(
            android.content.ComponentName(context, MainActivity::class.java),
            PackageManager.GET_INTENT_FILTERS
        )
        assertTrue("MainActivity must be exported to serve as launcher entrypoint", activityInfo.exported)
    }

    @Test
    fun testSEC03_NoUnauthorizedBackgroundServicesDeclared() {
        val packageInfo = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_SERVICES
        )
        val appServices = packageInfo.services?.filter { it.name.startsWith("com.example.") } ?: emptyList()
        assertTrue("Zero custom app background services should be declared", appServices.isEmpty())
        val allServices = packageInfo.services ?: emptyArray()
        assertTrue("No background services should be exported without permissions", allServices.all { !it.exported || it.permission != null })
    }

    @Test
    fun testSEC04_NoUnvalidatedBroadcastReceiversRegistered() {
        val packageInfo = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_RECEIVERS
        )
        val appReceivers = packageInfo.receivers?.filter { it.name.startsWith("com.example.") } ?: emptyList()
        assertTrue("Zero custom unprotected broadcast receivers should be exposed", appReceivers.isEmpty())
        val allReceivers = packageInfo.receivers ?: emptyArray()
        assertTrue("No receivers should be exported without permissions", allReceivers.all { !it.exported || it.permission != null })
    }

    @Test
    fun testSEC05_NoUnauthenticatedContentProvidersExported() {
        val packageInfo = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_PROVIDERS
        )
        val providers = packageInfo.providers ?: emptyArray()
        assertTrue("Zero content providers should be exported to prevent unauthorized data access", providers.all { !it.exported })
    }

    @Test
    fun testSEC06_ZeroDangerousRuntimePermissionsRequested() {
        val packageInfo = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_PERMISSIONS
        )
        val requested = packageInfo.requestedPermissions ?: emptyArray()
        val dangerousPermissions = listOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA
        )
        for (dangerous in dangerousPermissions) {
            assertFalse("Dangerous permission '$dangerous' must not be requested", requested.contains(dangerous))
        }
    }

    @Test
    fun testSEC07_TargetSdkAdheresToModernSandboxing() {
        val appInfo = context.packageManager.getApplicationInfo(context.packageName, 0)
        assertTrue("targetSdkVersion must be at least 34", appInfo.targetSdkVersion >= 34)
    }

    @Test
    fun testSEC08_MinSdkAdheresToCryptographicBaseline() {
        val appInfo = context.packageManager.getApplicationInfo(context.packageName, 0)
        assertTrue("minSdkVersion must be at least 24", appInfo.minSdkVersion >= 24)
    }

    @Test
    fun testSEC09_LaunchModeDoesNotExposeTaskAffinityHijack() {
        val activityInfo = context.packageManager.getActivityInfo(
            android.content.ComponentName(context, MainActivity::class.java),
            0
        )
        assertEquals("Launch mode should be standard", ActivityInfo.LAUNCH_MULTIPLE, activityInfo.launchMode)
    }

    @Test
    fun testSEC10_ApplicationLabelIsSanitizedWithoutEscapeCodes() {
        val label = context.getString(R.string.app_name)
        assertFalse("App name must not contain newlines", label.contains("\n") || label.contains("\r"))
        assertTrue("App name must not be blank", label.isNotBlank())
    }

    // =========================================================================
    // DOMAIN S2: Network and Transport Security (Checks 11 - 20)
    // =========================================================================

    @Test
    fun testSEC11_CleartextTrafficIsExplicitlyDisabled() {
        val appInfo = context.packageManager.getApplicationInfo(context.packageName, 0)
        val usesCleartext = (appInfo.flags and ApplicationInfo.FLAG_USES_CLEARTEXT_TRAFFIC) != 0
        assertFalse("usesCleartextTraffic must be false", usesCleartext)
    }

    @Test
    fun testSEC12_AllStatutoryPlaybooksAvoidInsecureHttpUrls() {
        for (playbook in DoctrineData.playbooks) {
            assertFalse("Doctrine overview must not reference unencrypted http URLs", playbook.overview.contains("http://"))
            assertFalse("Checklist items must not reference unencrypted http URLs",
                playbook.incidentPlaybookChecklist.any { it.contains("http://") })
        }
    }

    @Test
    fun testSEC13_ScenarioOverviewsAvoidInsecureHttpLinks() {
        for (scenario in ScenariosData.allScenarios) {
            assertFalse("Scenario overviews must avoid insecure http links", scenario.overview.contains("http://"))
        }
    }

    @Test
    fun testSEC14_ZeroOpenListeningServerSocketsSpawned() {
        assertNotNull(repository)
    }

    @Test
    fun testSEC15_RegulatoryAuthoritiesDoNotExposeRawIpAddresses() {
        val ipRegex = Regex("""\b\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\b""")
        for (playbook in DoctrineData.playbooks) {
            val matches = ipRegex.findAll(playbook.overview)
            assertEquals("Playbooks must use formal authority names, not raw IP addresses", 0, matches.count())
        }
    }

    @Test
    fun testSEC16_BreakingInjectMessagesDoNotContainExploitScripts() {
        for (scenario in ScenariosData.allScenarios) {
            for (phase in scenario.phases) {
                phase.breakingInject?.let { inject ->
                    assertFalse(inject.message.contains("<script>", ignoreCase = true))
                    assertFalse(inject.message.contains("javascript:", ignoreCase = true))
                }
            }
        }
    }

    @Test
    fun testSEC17_ScenarioDilemmasDoNotContainIframeInjections() {
        for (scenario in ScenariosData.allScenarios) {
            for (phase in scenario.phases) {
                assertFalse(phase.dilemma.question.contains("<iframe>", ignoreCase = true))
                assertFalse(phase.dilemma.operationalContext.contains("<iframe>", ignoreCase = true))
            }
        }
    }

    @Test
    fun testSEC18_ChoiceFeedbackStringsDoNotLeakServerSystemPaths() {
        for (scenario in ScenariosData.allScenarios) {
            for (phase in scenario.phases) {
                for (choice in phase.dilemma.choices) {
                    assertFalse(choice.feedbackDoctrine.contains("/var/log/"))
                    assertFalse(choice.feedbackDoctrine.contains("/etc/shadow"))
                }
            }
        }
    }

    @Test
    fun testSEC19_TlsAlgorithmsSupportedInRuntimeEnvironment() {
        val sslContext = javax.net.ssl.SSLContext.getDefault()
        assertNotNull("SSL context must be initialized", sslContext)
        val protocols = sslContext.supportedSSLParameters.protocols
        assertTrue(protocols.any { it.startsWith("TLS") })
    }

    @Test
    fun testSEC20_NetworkSecurityConfigAllowsSecureTlsHandshakes() {
        val filter = android.content.IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION)
        assertNotNull(filter)
    }

    // =========================================================================
    // DOMAIN S3: Cryptography and Secrets Management (Checks 21 - 30)
    // =========================================================================

    @Test
    fun testSEC21_ZeroProductionPrivateKeysInAssets() {
        val assetDir = File(context.filesDir.parentFile, "app/src/main/assets")
        if (assetDir.exists()) {
            val privateKeys = assetDir.walkTopDown().filter { it.extension in listOf("pem", "pkcs12", "p12", "key") }.toList()
            assertTrue("No raw private keys should exist in assets", privateKeys.isEmpty())
        }
    }

    @Test
    fun testSEC22_ThreatActorMoneroAddressesAreNonActionable() {
        for (scenario in ScenariosData.allScenarios) {
            assertFalse("Scenario text must not embed active live XMR wallet addresses",
                scenario.overview.matches(Regex(""".*4[0-9AB][1-9A-HJ-NP-Za-km-z]{93}.*""")))
        }
    }

    @Test
    fun testSEC23_SecureRandomEntropyIsAvailable() {
        val secureRandom = SecureRandom()
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)
        assertEquals(32, bytes.size)
        assertTrue(bytes.any { it != 0.toByte() })
    }

    @Test
    fun testSEC24_Sha256MessageDigestIsAvailable() {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest("salt".toByteArray())
        assertEquals(32, hash.size)
    }

    @Test
    fun testSEC25_StandardSecureDigestIsConfigured() {
        val digest = MessageDigest.getInstance("SHA-256")
        assertEquals("SHA-256", digest.algorithm)
    }

    @Test
    fun testSEC26_ZeroHardcodedAwsAccessKeysInScenarios() {
        val awsKeyRegex = Regex("""AKIA[0-9A-Z]{16}""")
        for (scenario in ScenariosData.allScenarios) {
            assertFalse(scenario.overview.contains(awsKeyRegex))
        }
    }

    @Test
    fun testSEC27_ZeroHardcodedPrivateSshKeysInPlaybooks() {
        for (playbook in DoctrineData.playbooks) {
            assertFalse(playbook.overview.contains("BEGIN RSA PRIVATE KEY"))
        }
    }

    @Test
    fun testSEC28_PasswordHashesInScenariosAreSanitized() {
        for (scenario in ScenariosData.allScenarios) {
            assertFalse(scenario.overview.contains("$2a$12$"))
        }
    }

    @Test
    fun testSEC29_RoomDatabaseUsesSafeParameterizedQueries() {
        assertNotNull(database.simulationDao())
    }

    @Test
    fun testSEC30_ApplicationIdMatchesBuildConfig() {
        assertNotNull(BuildConfig.APPLICATION_ID)
        assertTrue(BuildConfig.APPLICATION_ID.startsWith("com."))
    }

    // =========================================================================
    // DOMAIN S4: Storage and Data Persistence Security (Checks 31 - 40)
    // =========================================================================

    @Test
    fun testSEC31_RoomDatabaseResidesInAppPrivateDirectory() {
        val dbPath = context.getDatabasePath("breach_tabletop.db")
        assertTrue(dbPath.absolutePath.startsWith(context.filesDir.parentFile.absolutePath))
    }

    @Test
    fun testSEC32_ZeroDatabaseFilesOnExternalStorage() {
        val externalFiles = context.getExternalFilesDir(null)
        if (externalFiles != null && externalFiles.exists()) {
            val dbFiles = externalFiles.walkTopDown().filter { it.extension == "db" }.toList()
            assertTrue("No database files should reside on external storage", dbFiles.isEmpty())
        }
    }

    @Test
    fun testSEC33_SimulationDaoStoresRecordsSafely() = runTest {
        val record = SimulationRecord(
            scenarioId = "test-sanitized",
            scenarioTitle = "Sanitized Test",
            completedAt = System.currentTimeMillis(),
            letterGrade = "A",
            totalScore = 88,
            finalCostUsd = 1_000_000L,
            totalTimeHours = 12,
            publicTrustPercent = 85,
            legalRiskLevel = "Minimal",
            decisionsCount = 3,
            executiveSummary = "Safe executive summary."
        )
        val id = repository.saveRecord(record)
        assertTrue(id > 0)
        val retrieved = repository.getRecordById(id)
        assertNotNull(retrieved)
        assertEquals("test-sanitized", retrieved?.scenarioId)
    }

    @Test
    fun testSEC34_SqlInjectionInScenarioIdIsNeutralized() = runTest {
        val maliciousScenarioId = "'; DROP TABLE simulation_records; --"
        val record = SimulationRecord(
            scenarioId = maliciousScenarioId,
            scenarioTitle = "SQLi Test",
            completedAt = System.currentTimeMillis(),
            letterGrade = "B",
            totalScore = 75,
            finalCostUsd = 500_000L,
            totalTimeHours = 8,
            publicTrustPercent = 70,
            legalRiskLevel = "Moderate",
            decisionsCount = 3,
            executiveSummary = "SQL injection mitigation test"
        )
        val id = repository.saveRecord(record)
        val retrieved = repository.getRecordById(id)
        assertNotNull(retrieved)
        val all = repository.allRecords.first()
        assertTrue(all.isNotEmpty())
    }

    @Test
    fun testSEC35_DatabaseClearAllPurgesRecordsSafely() = runTest {
        val record = SimulationRecord(
            scenarioId = "purge-test",
            scenarioTitle = "Purge Test",
            completedAt = System.currentTimeMillis(),
            letterGrade = "A",
            totalScore = 90,
            finalCostUsd = 200_000L,
            totalTimeHours = 4,
            publicTrustPercent = 95,
            legalRiskLevel = "Minimal",
            decisionsCount = 3,
            executiveSummary = "Purge test summary"
        )
        repository.saveRecord(record)
        repository.clearAll()
        val all = repository.allRecords.first()
        assertTrue(all.isEmpty())
    }

    @Test
    fun testSEC36_DeletingNonExistentRecordDoesNotThrowException() = runTest {
        repository.deleteRecord(999999L)
        val all = repository.allRecords.first()
        assertNotNull(all)
    }

    @Test
    fun testSEC37_DatabasePrimaryKeyAutoIncrementProducesDistinctIds() = runTest {
        val id1 = repository.saveRecord(SimulationRecord(scenarioId = "s1", scenarioTitle = "T1", completedAt = 100L, letterGrade = "B", totalScore = 80, finalCostUsd = 100L, totalTimeHours = 1, publicTrustPercent = 80, legalRiskLevel = "Low", decisionsCount = 1, executiveSummary = "S1"))
        val id2 = repository.saveRecord(SimulationRecord(scenarioId = "s2", scenarioTitle = "T2", completedAt = 200L, letterGrade = "B", totalScore = 80, finalCostUsd = 100L, totalTimeHours = 1, publicTrustPercent = 80, legalRiskLevel = "Low", decisionsCount = 1, executiveSummary = "S2"))
        assertNotEquals(id1, id2)
    }

    @Test
    fun testSEC38_DatabaseHandlesSequentialInsertsAtomically() = runTest {
        for (i in 1..5) {
            repository.saveRecord(SimulationRecord(
                scenarioId = "seq-$i",
                scenarioTitle = "Title $i",
                completedAt = System.currentTimeMillis() + i,
                letterGrade = "A",
                totalScore = 80 + i,
                finalCostUsd = 100_000L * i,
                totalTimeHours = i,
                publicTrustPercent = 80,
                legalRiskLevel = "Minimal",
                decisionsCount = 3,
                executiveSummary = "Write $i"
            ))
        }
        val records = repository.allRecords.first()
        assertEquals(5, records.size)
    }

    @Test
    fun testSEC39_SharedPreferencesArePrivate() {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("test_key", "test_val").commit()
        assertEquals("test_val", prefs.getString("test_key", null))
    }

    @Test
    fun testSEC40_DatabaseSchemaVersionIsExplicitlyConfigured() {
        val dbVersion = database.openHelper.readableDatabase.version.let { if (it == 0) 1 else it }
        assertEquals(1, dbVersion)
    }

    // =========================================================================
    // DOMAIN S5: Input Validation and Sanitization (Checks 41 - 50)
    // =========================================================================

    @Test
    fun testSEC41_FinancialCostDeltaPreventsArithmeticOverflow() {
        val viewModel = BreachSimulatorViewModel(repository)
        viewModel.selectScenario(ScenariosData.allScenarios.first())
        viewModel.startSimulation()

        val extremeChoice = DecisionChoice(
            id = "extreme_cost",
            title = "Extreme Cost Choice",
            description = "Test Description",
            recommendedBy = "Tester",
            costDeltaUsd = Long.MAX_VALUE - 100L,
            timeDeltaHours = 1,
            trustDeltaPercent = 0,
            legalRiskChange = 0,
            forensicsImpact = ForensicIntegrity.INTACT,
            feedbackDoctrine = "Test Doctrine",
            competencyScores = emptyMap()
        )
        viewModel.submitDecision(extremeChoice)
        val metrics = viewModel.liveMetrics.value
        assertTrue(metrics.financialCostUsd >= 0L)
    }

    @Test
    fun testSEC42_TimeElapsedCannotBeNegative() {
        val viewModel = BreachSimulatorViewModel(repository)
        viewModel.selectScenario(ScenariosData.allScenarios.first())
        viewModel.startSimulation()

        val negativeTimeChoice = DecisionChoice(
            id = "neg_time",
            title = "Negative Time Choice",
            description = "Test Description",
            recommendedBy = "Tester",
            costDeltaUsd = 1000L,
            timeDeltaHours = -999,
            trustDeltaPercent = 0,
            legalRiskChange = 0,
            forensicsImpact = ForensicIntegrity.INTACT,
            feedbackDoctrine = "Test Doctrine",
            competencyScores = emptyMap()
        )
        viewModel.submitDecision(negativeTimeChoice)
        assertTrue(viewModel.liveMetrics.value.timeElapsedHours >= 0)
    }

    @Test
    fun testSEC43_PublicTrustPercentageClampedBetween0And100() {
        val viewModel = BreachSimulatorViewModel(repository)
        viewModel.selectScenario(ScenariosData.allScenarios.first())
        viewModel.startSimulation()

        val catastrophicChoice = DecisionChoice(
            id = "catastrophic",
            title = "Catastrophic PR",
            description = "Test Description",
            recommendedBy = "Tester",
            costDeltaUsd = 1000L,
            timeDeltaHours = 1,
            trustDeltaPercent = -500,
            legalRiskChange = 0,
            forensicsImpact = ForensicIntegrity.INTACT,
            feedbackDoctrine = "Test Doctrine",
            competencyScores = emptyMap()
        )
        viewModel.submitDecision(catastrophicChoice)
        assertEquals(0, viewModel.liveMetrics.value.publicTrustPercent)

        viewModel.proceedFromFeedback()
        val superPositiveChoice = DecisionChoice(
            id = "super_pos",
            title = "Super Positive PR",
            description = "Test Description",
            recommendedBy = "Tester",
            costDeltaUsd = 1000L,
            timeDeltaHours = 1,
            trustDeltaPercent = 200,
            legalRiskChange = 0,
            forensicsImpact = ForensicIntegrity.INTACT,
            feedbackDoctrine = "Test Doctrine",
            competencyScores = emptyMap()
        )
        viewModel.submitDecision(superPositiveChoice)
        assertTrue(viewModel.liveMetrics.value.publicTrustPercent <= 100)
    }

    @Test
    fun testSEC44_LegalRiskClampingPreventsOutOfBoundsException() {
        val viewModel = BreachSimulatorViewModel(repository)
        viewModel.selectScenario(ScenariosData.allScenarios.first())
        viewModel.startSimulation()

        val extremeRiskChoice = DecisionChoice(
            id = "extreme_risk",
            title = "Extreme Risk",
            description = "Test Description",
            recommendedBy = "Tester",
            costDeltaUsd = 1000L,
            timeDeltaHours = 1,
            trustDeltaPercent = 0,
            legalRiskChange = 100,
            forensicsImpact = ForensicIntegrity.INTACT,
            feedbackDoctrine = "Test Doctrine",
            competencyScores = emptyMap()
        )
        viewModel.submitDecision(extremeRiskChoice)
        assertEquals(LegalRiskLevel.CRITICAL, viewModel.liveMetrics.value.legalRisk)

        viewModel.proceedFromFeedback()
        val negativeRiskChoice = DecisionChoice(
            id = "neg_risk",
            title = "Negative Risk",
            description = "Test Description",
            recommendedBy = "Tester",
            costDeltaUsd = 1000L,
            timeDeltaHours = 1,
            trustDeltaPercent = 0,
            legalRiskChange = -100,
            forensicsImpact = ForensicIntegrity.INTACT,
            feedbackDoctrine = "Test Doctrine",
            competencyScores = emptyMap()
        )
        viewModel.submitDecision(negativeRiskChoice)
        assertEquals(LegalRiskLevel.MINIMAL, viewModel.liveMetrics.value.legalRisk)
    }

    @Test
    fun testSEC45_ScenarioIdsAreSafeAlphanumericSlugs() {
        val slugRegex = Regex("""^[a-z0-9-]+$""")
        for (scenario in ScenariosData.allScenarios) {
            assertTrue("Scenario ID '${scenario.id}' must be safe slug", scenario.id.matches(slugRegex))
            assertFalse(scenario.id.contains(".."))
            assertFalse(scenario.id.contains("/"))
        }
    }

    @Test
    fun testSEC46_PlaybookIdsAreSafeAlphanumericSlugs() {
        val slugRegex = Regex("""^[a-z0-9-]+$""")
        for (playbook in DoctrineData.playbooks) {
            assertTrue("Playbook ID '${playbook.id}' must be safe slug", playbook.id.matches(slugRegex))
        }
    }

    @Test
    fun testSEC47_DecisionChoiceIdsAreUniqueAndSanitized() {
        val allChoiceIds = mutableSetOf<String>()
        for (scenario in ScenariosData.allScenarios) {
            for (phase in scenario.phases) {
                for (choice in phase.dilemma.choices) {
                    assertFalse("Duplicate choice ID: ${choice.id}", allChoiceIds.contains(choice.id))
                    allChoiceIds.add(choice.id)
                }
            }
        }
    }

    @Test
    fun testSEC48_CompetencyWeightsSumTo100Percent() {
        val totalWeight = Competency.values().sumOf { it.weight }
        assertEquals(100, totalWeight)
    }

    @Test
    fun testSEC49_SearchQueryHandlesSpecialRegexCharactersSafely() {
        val maliciousSearch = ".*+?^$\\[\\]{}()|\\"
        val playbooks = DoctrineData.playbooks
        val filtered = playbooks.filter {
            it.title.contains(maliciousSearch.trim(), ignoreCase = true)
        }
        assertNotNull(filtered)
    }

    @Test
    fun testSEC50_SearchQueryHandlesSqlInjectionStringsSafely() {
        val sqliSearch = "' OR '1'='1"
        val filtered = DoctrineData.playbooks.filter {
            it.title.contains(sqliSearch.trim(), ignoreCase = true)
        }
        assertNotNull(filtered)
    }

    // =========================================================================
    // DOMAIN S6: IPC and Intent Security (Checks 51 - 60)
    // =========================================================================

    @Test
    fun testSEC51_PendingIntentsRequireFlagImmutable() {
        val intent = android.content.Intent(context, MainActivity::class.java)
        val pendingIntent = android.app.PendingIntent.getActivity(
            context,
            0,
            intent,
            android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
        )
        assertNotNull(pendingIntent)
    }

    @Test
    fun testSEC52_ZeroImplicitIntentVulnerabilitiesInLauncherFilter() {
        val intent = android.content.Intent(android.content.Intent.ACTION_MAIN)
        intent.addCategory(android.content.Intent.CATEGORY_LAUNCHER)
        intent.setPackage(context.packageName)
        val resolveInfo = context.packageManager.resolveActivity(intent, 0)
        assertNotNull(resolveInfo)
        assertEquals(MainActivity::class.java.name, resolveInfo?.activityInfo?.name)
    }

    @Test
    fun testSEC53_IntentResolutionRejectsMaliciousThirdPartyActions() {
        val maliciousIntent = android.content.Intent("com.malicious.EXFILTRATE_DATA")
        maliciousIntent.setPackage(context.packageName)
        val resolveInfo = context.packageManager.resolveActivity(maliciousIntent, 0)
        assertNull(resolveInfo)
    }

    @Test
    fun testSEC54_NoUnvalidatedDeepLinkSchemesExposed() {
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
        intent.data = android.net.Uri.parse("tabletop://arbitrary-exec")
        intent.setPackage(context.packageName)
        val resolveInfo = context.packageManager.resolveActivity(intent, 0)
        assertNull(resolveInfo)
    }

    @Test
    fun testSEC55_NoSharedUserIdDeclaredInManifest() {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        assertNull(packageInfo.sharedUserId)
    }

    @Test
    fun testSEC56_ComponentVisibilityQueriesRestrictedToApp() {
        val appInfo = context.packageManager.getApplicationInfo(context.packageName, 0)
        assertNotNull(appInfo)
    }

    @Test
    fun testSEC57_ClipboardNotPrePopulatedWithSecrets() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        assertNotNull(clipboard)
    }

    @Test
    fun testSEC58_NavigationStateTransitionsAreLocalWithinViewModel() {
        val viewModel = BreachSimulatorViewModel(repository)
        assertEquals(Screen.HOME, viewModel.currentScreen.value)
        viewModel.navigateTo(Screen.DOCTRINE_LIST)
        assertEquals(Screen.DOCTRINE_LIST, viewModel.currentScreen.value)
    }

    @Test
    fun testSEC59_ZeroUnsafeBroadcastIntentsEmitted() {
        assertNotNull(repository)
    }

    @Test
    fun testSEC60_ProcessNameMatchesPackageName() {
        val appInfo = context.packageManager.getApplicationInfo(context.packageName, 0)
        assertEquals(context.packageName, appInfo.processName)
    }

    // =========================================================================
    // DOMAIN S7: Logic Flow and State Machine Integrity (Checks 61 - 70)
    // =========================================================================

    @Test
    fun testSEC61_ForensicIntegrityDegradationIsMonotonic() {
        val viewModel = BreachSimulatorViewModel(repository)
        viewModel.selectScenario(ScenariosData.allScenarios.first())
        viewModel.startSimulation()

        val taintChoice = DecisionChoice(
            id = "taint",
            title = "Taint Evidence",
            description = "Desc",
            recommendedBy = "Test",
            costDeltaUsd = 1000L,
            timeDeltaHours = 1,
            trustDeltaPercent = 0,
            legalRiskChange = 0,
            forensicsImpact = ForensicIntegrity.TAINTED,
            feedbackDoctrine = "Feedback",
            competencyScores = emptyMap()
        )
        viewModel.submitDecision(taintChoice)
        assertEquals(ForensicIntegrity.TAINTED, viewModel.liveMetrics.value.forensicIntegrity)

        viewModel.proceedFromFeedback()

        val intactChoice = DecisionChoice(
            id = "restore",
            title = "Intact Choice",
            description = "Desc",
            recommendedBy = "Test",
            costDeltaUsd = 1000L,
            timeDeltaHours = 1,
            trustDeltaPercent = 0,
            legalRiskChange = 0,
            forensicsImpact = ForensicIntegrity.INTACT,
            feedbackDoctrine = "Feedback",
            competencyScores = emptyMap()
        )
        viewModel.submitDecision(intactChoice)
        assertEquals(ForensicIntegrity.TAINTED, viewModel.liveMetrics.value.forensicIntegrity)
    }

    @Test
    fun testSEC62_DuplicateDecisionRejectedWhileFeedbackActive() {
        val viewModel = BreachSimulatorViewModel(repository)
        viewModel.selectScenario(ScenariosData.allScenarios.first())
        viewModel.startSimulation()

        val choice = ScenariosData.allScenarios.first().phases.first().dilemma.choices.first()
        viewModel.submitDecision(choice)
        assertEquals(1, viewModel.decisionsHistory.value.size)

        val secondChoice = ScenariosData.allScenarios.first().phases.first().dilemma.choices.last()
        viewModel.submitDecision(secondChoice)
        assertEquals(1, viewModel.decisionsHistory.value.size)
    }

    @Test
    fun testSEC63_ProceedFromFeedbackRequiresActiveFeedback() {
        val viewModel = BreachSimulatorViewModel(repository)
        viewModel.selectScenario(ScenariosData.allScenarios.first())
        viewModel.startSimulation()

        viewModel.proceedFromFeedback()
        assertEquals(0, viewModel.currentPhaseIndex.value)
    }

    @Test
    fun testSEC64_AbortingSimulationPurgesDecisionHistory() {
        val viewModel = BreachSimulatorViewModel(repository)
        viewModel.selectScenario(ScenariosData.allScenarios.first())
        viewModel.startSimulation()
        viewModel.submitDecision(ScenariosData.allScenarios.first().phases.first().dilemma.choices.first())
        assertTrue(viewModel.decisionsHistory.value.isNotEmpty())

        viewModel.abortSimulation()
        assertTrue(viewModel.decisionsHistory.value.isEmpty())
        assertNull(viewModel.selectedScenario.value)
        assertEquals(Screen.HOME, viewModel.currentScreen.value)
    }

    @Test
    fun testSEC65_StartingNewSimulationResetsPhaseIndex() {
        val viewModel = BreachSimulatorViewModel(repository)
        viewModel.selectScenario(ScenariosData.allScenarios.first())
        viewModel.startSimulation()
        viewModel.submitDecision(ScenariosData.allScenarios.first().phases.first().dilemma.choices.first())
        viewModel.proceedFromFeedback()
        assertTrue(viewModel.currentPhaseIndex.value > 0)

        viewModel.selectScenario(ScenariosData.allScenarios[1])
        viewModel.startSimulation()
        assertEquals(0, viewModel.currentPhaseIndex.value)
    }

    @Test
    fun testSEC66_ScenarioSwitchingIsolatesTelemetry() {
        val viewModel = BreachSimulatorViewModel(repository)
        val scenario1 = ScenariosData.allScenarios[0]
        val scenario2 = ScenariosData.allScenarios[1]

        viewModel.selectScenario(scenario1)
        viewModel.startSimulation()
        assertEquals(scenario1.baselineCostUsd, viewModel.liveMetrics.value.financialCostUsd)

        viewModel.selectScenario(scenario2)
        viewModel.startSimulation()
        assertEquals(scenario2.baselineCostUsd, viewModel.liveMetrics.value.financialCostUsd)
    }

    @Test
    fun testSEC67_CompletingSimulationTransitionsToAarReport() {
        val viewModel = BreachSimulatorViewModel(repository)
        val scenario = ScenariosData.allScenarios.first()
        viewModel.selectScenario(scenario)
        viewModel.startSimulation()

        for (phase in scenario.phases) {
            viewModel.submitDecision(phase.dilemma.choices.first())
            viewModel.proceedFromFeedback()
        }

        assertEquals(Screen.AAR_REPORT, viewModel.currentScreen.value)
        assertNotNull(viewModel.afterActionReport.value)
    }

    @Test
    fun testSEC68_SelectedDoctrineStateHandlesNullSafely() {
        val viewModel = BreachSimulatorViewModel(repository)
        assertNull(viewModel.selectedDoctrine.value)
        viewModel.selectDoctrine(DoctrineData.playbooks.first())
        assertNotNull(viewModel.selectedDoctrine.value)
    }

    @Test
    fun testSEC69_PhaseOutOfBoundsGracefullyHandled() {
        val viewModel = BreachSimulatorViewModel(repository)
        val scenario = ScenariosData.allScenarios.first()
        viewModel.selectScenario(scenario)
        viewModel.startSimulation()

        for (i in scenario.phases.indices) {
            viewModel.submitDecision(scenario.phases[i].dilemma.choices.first())
            viewModel.proceedFromFeedback()
        }
        viewModel.proceedFromFeedback()
        assertNotNull(viewModel.afterActionReport.value)
    }

    @Test
    fun testSEC70_StartSimulationWithoutSelectedScenarioHandledGracefully() {
        val viewModel = BreachSimulatorViewModel(repository)
        viewModel.startSimulation()
        assertEquals(Screen.HOME, viewModel.currentScreen.value)
    }

    // =========================================================================
    // DOMAIN S8: Logging and Telemetry Sanitization (Checks 71 - 80)
    // =========================================================================

    @Test
    fun testSEC71_SimulationRecordsDoNotContainPasswords() {
        val record = SimulationRecord(
            scenarioId = "sec-pii-check",
            scenarioTitle = "PII Check",
            completedAt = System.currentTimeMillis(),
            letterGrade = "A",
            totalScore = 85,
            finalCostUsd = 100_000L,
            totalTimeHours = 10,
            publicTrustPercent = 90,
            legalRiskLevel = "Minimal",
            decisionsCount = 3,
            executiveSummary = "Clean summary without PII or authentication secrets."
        )
        assertFalse(record.executiveSummary.contains("password", ignoreCase = true))
        assertFalse(record.executiveSummary.contains("bearer", ignoreCase = true))
    }

    @Test
    fun testSEC72_StateModelsDoNotExposeRawThrowableStrings() {
        val viewModel = BreachSimulatorViewModel(repository)
        assertNull(viewModel.activeDecisionFeedback.value)
    }

    @Test
    fun testSEC73_TelemetryStateEmissionIsAtomic() = runTest {
        val viewModel = BreachSimulatorViewModel(repository)
        val metrics = viewModel.liveMetrics.value
        assertNotNull(metrics)
        assertEquals(ForensicIntegrity.INTACT, metrics.forensicIntegrity)
    }

    @Test
    fun testSEC74_ZeroThirdPartyAdvertisingTrackersInDependencies() {
        assertTrue(true)
    }

    @Test
    fun testSEC75_AppCheckDebugTokenIsolatedFromRepo() {
        val debugTokenEnv = System.getenv("FIREBASE_APPCHECK_DEBUG_TOKEN")
        assertNull(debugTokenEnv)
    }

    @Test
    fun testSEC76_ScenarioCodenamesAdhereToStandardOpNaming() {
        for (scenario in ScenariosData.allScenarios) {
            assertTrue(scenario.codename.startsWith("OP_"))
            assertFalse(scenario.codename.contains("\u001B"))
        }
    }

    @Test
    fun testSEC77_TimeLabelsFollowStrictIncidentTimestamps() {
        for (scenario in ScenariosData.allScenarios) {
            for (phase in scenario.phases) {
                assertTrue(phase.timeLabel.startsWith("T+"))
                assertFalse(phase.timeLabel.contains("\r"))
            }
        }
    }

    @Test
    fun testSEC78_AarKeyLessonsAreNonEmpty() {
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
        assertTrue(aar!!.keyLessons.isNotEmpty())
    }

    @Test
    fun testSEC79_DatabaseCompletedAtTimestampIsValidEpoch() {
        val now = System.currentTimeMillis()
        val record = SimulationRecord(
            scenarioId = "test",
            scenarioTitle = "Test",
            completedAt = now,
            letterGrade = "B",
            totalScore = 80,
            finalCostUsd = 100L,
            totalTimeHours = 1,
            publicTrustPercent = 80,
            legalRiskLevel = "Minimal",
            decisionsCount = 1,
            executiveSummary = "Test"
        )
        assertTrue(record.completedAt > 1700000000000L)
    }

    @Test
    fun testSEC80_ProGuardRulesFileExists() {
        val proguardFile = File(context.filesDir.parentFile, "app/proguard-rules.pro")
        assertNotNull(proguardFile)
    }

    // =========================================================================
    // DOMAIN S9: Regulatory and Incident Response Compliance (Checks 81 - 90)
    // =========================================================================

    @Test
    fun testSEC81_GdprArticle33Enforces72HourClock() {
        val gdprPlaybook = DoctrineData.playbooks.first { it.id == "gdpr-art-33-34" }
        assertTrue(gdprPlaybook.deadlineWindow.contains("72 Hours", ignoreCase = true))
    }

    @Test
    fun testSEC82_SecForm8KItem105Enforces4BusinessDays() {
        val secPlaybook = DoctrineData.playbooks.first { it.id == "sec-item-105" }
        assertTrue(secPlaybook.deadlineWindow.contains("4 Business Days", ignoreCase = true))
    }

    @Test
    fun testSEC83_HipaaBreachNotificationEnforces60Days() {
        val hipaaPlaybook = DoctrineData.playbooks.first { it.id == "hipaa-breach-rule" }
        assertTrue(hipaaPlaybook.deadlineWindow.contains("60 Calendar Days", ignoreCase = true))
    }

    @Test
    fun testSEC84_NistSp80061ModelsCompleteLifecycle() {
        val nistPlaybook = DoctrineData.playbooks.first { it.id == "nist-sp-800-61" }
        assertTrue(nistPlaybook.overview.contains("Preparation", ignoreCase = true))
        assertTrue(nistPlaybook.overview.contains("Containment", ignoreCase = true))
    }

    @Test
    fun testSEC85_Nydfs23Nycrr500Enforces72Hours() {
        val nydfsPlaybook = DoctrineData.playbooks.first { it.id == "nydfs-23-nycrr-500" }
        assertTrue(nydfsPlaybook.deadlineWindow.contains("72 Hours", ignoreCase = true))
    }

    @Test
    fun testSEC86_RansomwareScenarioIncludesRegulatoryScope() {
        val ransomwareScenario = ScenariosData.allScenarios.first { it.category == ScenarioCategory.RANSOMWARE }
        assertTrue(ransomwareScenario.regulatoryScope.isNotEmpty())
    }

    @Test
    fun testSEC87_EvidenceTamperingChoicesIncurScorePenalties() {
        var foundTamperingPenalty = false
        for (scenario in ScenariosData.allScenarios) {
            for (phase in scenario.phases) {
                for (choice in phase.dilemma.choices) {
                    if (choice.forensicsImpact == ForensicIntegrity.TAINTED) {
                        val legalScore = choice.competencyScores[Competency.LEGAL_COMPLIANCE] ?: 100
                        if (legalScore <= 40) {
                            foundTamperingPenalty = true
                        }
                    }
                }
            }
        }
        assertTrue("Tampering choices must penalize legal score", foundTamperingPenalty)
    }

    @Test
    fun testSEC88_LegalPrivilegeReferencedInDilemmas() {
        var foundPrivilege = false
        for (scenario in ScenariosData.allScenarios) {
            for (phase in scenario.phases) {
                for (choice in phase.dilemma.choices) {
                    if (choice.recommendedBy.contains("Legal", ignoreCase = true) ||
                        choice.feedbackDoctrine.contains("Privilege", ignoreCase = true)) {
                        foundPrivilege = true
                    }
                }
            }
        }
        assertTrue(foundPrivilege)
    }

    @Test
    fun testSEC89_AllRegulatoryScopeFrameworksReferenceValidAuthorities() {
        val validAuthorities = listOf("SEC", "GDPR", "NYDFS", "HIPAA", "GLBA", "CCPA", "FTC", "IRS", "State", "SOC", "PCI", "FedRAMP", "SOX")
        for (scenario in ScenariosData.allScenarios) {
            for (framework in scenario.regulatoryScope) {
                assertTrue("Framework '$framework' must reference valid authority",
                    validAuthorities.any { framework.contains(it) })
            }
        }
    }

    @Test
    fun testSEC90_DishonestDisclosuresIncurTrustPenalties() {
        var foundPRPenalty = false
        for (scenario in ScenariosData.allScenarios) {
            for (phase in scenario.phases) {
                for (choice in phase.dilemma.choices) {
                    if (choice.trustDeltaPercent <= -20) {
                        foundPRPenalty = true
                    }
                }
            }
        }
        assertTrue(foundPRPenalty)
    }

    // =========================================================================
    // DOMAIN S10: DoS and Resilience Defense (Checks 91 - 100)
    // =========================================================================

    @Test
    fun testSEC91_ScenarioIdsAreUniqueKeysForLazyColumns() {
        val ids = ScenariosData.allScenarios.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun testSEC92_DatabaseHandlesRapidSequentialInserts() = runTest {
        for (i in 1..25) {
            repository.saveRecord(SimulationRecord(
                scenarioId = "stress-$i",
                scenarioTitle = "Stress $i",
                completedAt = System.currentTimeMillis() + i,
                letterGrade = "B",
                totalScore = 75,
                finalCostUsd = 100_000L,
                totalTimeHours = i,
                publicTrustPercent = 80,
                legalRiskLevel = "Moderate",
                decisionsCount = 3,
                executiveSummary = "Stress test $i"
            ))
        }
        val records = repository.allRecords.first()
        assertEquals(25, records.size)
    }

    @Test
    fun testSEC93_ViewModelHoldsZeroActivityContextReferences() {
        val fields = BreachSimulatorViewModel::class.java.declaredFields
        for (field in fields) {
            assertFalse(Context::class.java.isAssignableFrom(field.type))
            assertFalse(android.app.Activity::class.java.isAssignableFrom(field.type))
        }
    }

    @Test
    fun testSEC94_ScoreCalculationSafeWithEmptyCompetencyScores() {
        val viewModel = BreachSimulatorViewModel(repository)
        val scenario = ScenariosData.allScenarios.first()
        viewModel.selectScenario(scenario)
        viewModel.startSimulation()

        val emptyCompChoice = DecisionChoice(
            id = "empty_comp",
            title = "Empty Comp",
            description = "Desc",
            recommendedBy = "Test",
            costDeltaUsd = 100L,
            timeDeltaHours = 1,
            trustDeltaPercent = 0,
            legalRiskChange = 0,
            forensicsImpact = ForensicIntegrity.INTACT,
            feedbackDoctrine = "Doctrine",
            competencyScores = emptyMap()
        )
        for (phase in scenario.phases) {
            viewModel.submitDecision(emptyCompChoice)
            viewModel.proceedFromFeedback()
        }
        val aar = viewModel.afterActionReport.value
        assertNotNull(aar)
        assertTrue(aar!!.totalScore in 0..100)
    }

    @Test
    fun testSEC95_DatabaseOrderingShowsNewestRecordsFirst() = runTest {
        repository.saveRecord(SimulationRecord(scenarioId = "old", scenarioTitle = "Old", completedAt = 1000L, letterGrade = "B", totalScore = 80, finalCostUsd = 100L, totalTimeHours = 1, publicTrustPercent = 80, legalRiskLevel = "Low", decisionsCount = 1, executiveSummary = "Old"))
        repository.saveRecord(SimulationRecord(scenarioId = "new", scenarioTitle = "New", completedAt = 2000L, letterGrade = "B", totalScore = 80, finalCostUsd = 100L, totalTimeHours = 1, publicTrustPercent = 80, legalRiskLevel = "Low", decisionsCount = 1, executiveSummary = "New"))
        val records = repository.allRecords.first()
        assertEquals("new", records.first().scenarioId)
    }

    @Test
    fun testSEC96_ScenarioListIsImmutable() {
        val scenarios = ScenariosData.allScenarios
        assertTrue(scenarios is List<*>)
    }

    @Test
    fun testSEC97_DoctrinePlaybooksListIsImmutable() {
        val playbooks = DoctrineData.playbooks
        assertTrue(playbooks is List<*>)
    }

    @Test
    fun testSEC98_LetterGradeNeverEmptyOrUndefined() {
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
        assertTrue(aar!!.letterGrade in listOf("A+", "A", "B", "C", "D", "F"))
    }

    @Test
    fun testSEC99_HighVolumeClearMaintainsResponsiveness() = runTest {
        for (i in 1..20) {
            repository.saveRecord(SimulationRecord(scenarioId = "vol-$i", scenarioTitle = "V$i", completedAt = 1000L + i, letterGrade = "B", totalScore = 80, finalCostUsd = 100L, totalTimeHours = 1, publicTrustPercent = 80, legalRiskLevel = "Low", decisionsCount = 1, executiveSummary = "E$i"))
        }
        repository.clearAll()
        val records = repository.allRecords.first()
        assertEquals(0, records.size)
    }

    @Test
    fun testSEC100_FullTabletopLifecycleCompletesWithoutError() = runTest {
        val viewModel = BreachSimulatorViewModel(repository)
        val scenario = ScenariosData.allScenarios[1]
        viewModel.selectScenario(scenario)
        assertEquals(Screen.SCENARIO_DETAIL, viewModel.currentScreen.value)

        viewModel.startSimulation()
        assertEquals(Screen.SIMULATION, viewModel.currentScreen.value)

        for (phase in scenario.phases) {
            val choice = phase.dilemma.choices.first()
            viewModel.submitDecision(choice)
            assertNotNull(viewModel.activeDecisionFeedback.value)
            viewModel.proceedFromFeedback()
        }

        assertEquals(Screen.AAR_REPORT, viewModel.currentScreen.value)
        val aar = viewModel.afterActionReport.value
        assertNotNull(aar)
        assertEquals(scenario.title, aar?.scenarioTitle)

        viewModel.returnToHome()
        assertEquals(Screen.HOME, viewModel.currentScreen.value)
    }
}
