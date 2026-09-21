package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.SimulationRepository
import com.example.model.Screen
import com.example.ui.components.TabletopBottomNavBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SlateDark
import com.example.viewmodel.BreachSimulatorViewModel
import com.example.viewmodel.BreachSimulatorViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.decorView.setBackgroundColor(android.graphics.Color.parseColor("#0B101B"))

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = SimulationRepository(database.simulationDao())
        val factory = BreachSimulatorViewModelFactory(repository)

        setContent {
            MyApplicationTheme {
                val viewModel: BreachSimulatorViewModel = viewModel(factory = factory)
                TabletopBreachApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TabletopBreachApp(viewModel: BreachSimulatorViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val selectedScenario by viewModel.selectedScenario.collectAsStateWithLifecycle()
    val currentPhaseIndex by viewModel.currentPhaseIndex.collectAsStateWithLifecycle()
    val liveMetrics by viewModel.liveMetrics.collectAsStateWithLifecycle()
    val activeFeedback by viewModel.activeDecisionFeedback.collectAsStateWithLifecycle()
    val afterActionReport by viewModel.afterActionReport.collectAsStateWithLifecycle()
    val pastDrills by viewModel.pastDrills.collectAsStateWithLifecycle()

    val showBottomBar = currentScreen in listOf(
        Screen.HOME,
        Screen.DOCTRINE_LIST,
        Screen.HISTORY_LOGS
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark),
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            if (showBottomBar) {
                TabletopBottomNavBar(
                    currentScreen = currentScreen,
                    onNavigate = { viewModel.navigateTo(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = currentScreen,
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    Screen.HOME -> {
                        HomeScreen(
                            pastDrills = pastDrills,
                            onSelectScenario = { scenario ->
                                viewModel.selectScenario(scenario)
                            },
                            onNavigateToDoctrine = {
                                viewModel.navigateTo(Screen.DOCTRINE_LIST)
                            },
                            onNavigateToHistory = {
                                viewModel.navigateTo(Screen.HISTORY_LOGS)
                            }
                        )
                    }

                    Screen.SCENARIO_DETAIL -> {
                        selectedScenario?.let { scenario ->
                            ScenarioDetailScreen(
                                scenario = scenario,
                                onStartSimulation = {
                                    viewModel.startSimulation(scenario)
                                },
                                onBack = {
                                    viewModel.navigateTo(Screen.HOME)
                                }
                            )
                        } ?: run {
                            viewModel.navigateTo(Screen.HOME)
                        }
                    }

                    Screen.SIMULATION -> {
                        selectedScenario?.let { scenario ->
                            SimulationScreen(
                                scenario = scenario,
                                currentPhaseIndex = currentPhaseIndex,
                                liveMetrics = liveMetrics,
                                activeFeedback = activeFeedback,
                                onSubmitDecision = { choice ->
                                    viewModel.submitDecision(choice)
                                },
                                onProceedFromFeedback = {
                                    viewModel.proceedFromFeedback()
                                },
                                onAbort = {
                                    viewModel.navigateTo(Screen.HOME)
                                }
                            )
                        } ?: run {
                            viewModel.navigateTo(Screen.HOME)
                        }
                    }

                    Screen.AAR_REPORT -> {
                        afterActionReport?.let { report ->
                            AarReportScreen(
                                report = report,
                                onReturnToHome = {
                                    viewModel.navigateTo(Screen.HOME)
                                }
                            )
                        } ?: run {
                            viewModel.navigateTo(Screen.HOME)
                        }
                    }

                    Screen.DOCTRINE_LIST, Screen.DOCTRINE_DETAIL -> {
                        DoctrineScreen(
                            onSelectPlaybook = { playbook ->
                                viewModel.selectDoctrine(playbook)
                            }
                        )
                    }

                    Screen.HISTORY_LOGS -> {
                        HistoryScreen(
                            records = pastDrills,
                            onDeleteRecord = { id ->
                                viewModel.deletePastDrill(id)
                            },
                            onClearAll = {
                                viewModel.clearAllPastDrills()
                            },
                            onStartNewDrill = {
                                viewModel.navigateTo(Screen.HOME)
                            }
                        )
                    }
                }
            }
        }
    }
}
