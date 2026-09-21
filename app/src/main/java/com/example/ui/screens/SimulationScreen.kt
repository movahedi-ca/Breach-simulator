package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DecisionChoice
import com.example.model.IncidentScenario
import com.example.model.LiveMetrics
import com.example.ui.components.BreakingInjectCard
import com.example.ui.components.DecisionCard
import com.example.ui.components.DecisionFeedbackDialog
import com.example.ui.components.LiveWarRoomMeters
import com.example.ui.theme.*

@Composable
fun SimulationScreen(
    scenario: IncidentScenario,
    currentPhaseIndex: Int,
    liveMetrics: LiveMetrics,
    activeFeedback: DecisionChoice?,
    onSubmitDecision: (DecisionChoice) -> Unit,
    onProceedFromFeedback: () -> Unit,
    onAbort: () -> Unit,
    modifier: Modifier = Modifier
) {
    val phase = scenario.phases.getOrNull(currentPhaseIndex) ?: return
    var showAbortDialog by remember { mutableStateOf(false) }

    val isFinalPhase = currentPhaseIndex >= scenario.phases.size - 1

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SlateDark)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AlertRed)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = scenario.codename,
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberCyan,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "PHASE ${phase.phaseNumber} OF ${scenario.phases.size}",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Black
                        )
                    }

                    OutlinedButton(
                        onClick = { showAbortDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AlertRed
                        ),
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                            brush = androidx.compose.ui.graphics.SolidColor(AlertRed.copy(alpha = 0.5f))
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("abort_drill_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Abort",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ABORT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Live War Room Telemetry Gauges
            item {
                LiveWarRoomMeters(metrics = liveMetrics)
            }

            // Phase Briefing Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, SlateCardBorder, RoundedCornerShape(14.dp)),
                    color = SlateCardElevated,
                    tonalElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = phase.title.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberCyan,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = phase.timeLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = phase.briefing,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            // Breaking Inject Banner if present
            phase.breakingInject?.let { inject ->
                item {
                    BreakingInjectCard(inject = inject)
                }
            }

            // Operational Dilemma & Decision Prompt
            item {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = null,
                            tint = WarningAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CRISIS DILEMMA",
                            style = MaterialTheme.typography.labelSmall,
                            color = WarningAmber,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = phase.dilemma.question,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = phase.dilemma.operationalContext,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            // Interactive Decision Cards
            itemsIndexed(phase.dilemma.choices) { index, choice ->
                DecisionCard(
                    choice = choice,
                    optionIndex = index,
                    onSelect = { onSubmitDecision(choice) }
                )
            }
        }

        // Decision Feedback Dialogue overlay
        activeFeedback?.let { feedbackChoice ->
            DecisionFeedbackDialog(
                choice = feedbackChoice,
                isFinalPhase = isFinalPhase,
                onProceed = onProceedFromFeedback
            )
        }

        // Abort Confirmation Dialog
        if (showAbortDialog) {
            AlertDialog(
                onDismissRequest = { showAbortDialog = false },
                title = {
                    Text(
                        text = "Abort Simulation Drill?",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "Current simulation progress will be discarded. Are you sure you want to return to the incident command center?",
                        color = TextSecondary
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showAbortDialog = false
                            onAbort()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AlertRed)
                    ) {
                        Text("Confirm Abort")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAbortDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                },
                containerColor = SlateCardElevated
            )
        }
    }
}
