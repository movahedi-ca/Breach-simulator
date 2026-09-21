package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MovahediData
import com.example.model.AfterActionReport
import com.example.model.Competency
import com.example.model.ForensicIntegrity
import com.example.model.LegalRiskLevel
import com.example.ui.components.formatCurrency
import com.example.ui.theme.*

@Composable
fun AarReportScreen(
    report: AfterActionReport,
    onReturnToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "INCIDENT DEBRIEF",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "After Action Report (AAR)",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Black
                    )
                }

                IconButton(
                    onClick = onReturnToHome,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SlateCardElevated)
                        .testTag("aar_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close debrief and return home",
                        tint = TextPrimary
                    )
                }
            }
        }

        // Executive Grade Hero Card
        item {
            ExecutiveGradeCard(report = report)
        }

        // 5 Core Competencies Evaluation
        item {
            CompetenciesBreakdownCard(breakdown = report.competencyBreakdown)
        }

        // Executive Summary
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, SlateCardBorder, RoundedCornerShape(14.dp)),
                color = SlateCardElevated
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "EXECUTIVE FINDINGS & DOCTRINE REVIEW",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberCyan,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = report.executiveSummary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "KEY LESSONS LEARNED",
                        style = MaterialTheme.typography.labelSmall,
                        color = WarningAmber,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    report.keyLessons.forEach { lesson ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = ContainedGreen,
                                modifier = Modifier
                                    .size(14.dp)
                                    .padding(top = 3.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = lesson,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // Chronological Decisions Audit Timeline
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, SlateCardBorder, RoundedCornerShape(14.dp)),
                color = SlateCardElevated
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = null,
                            tint = CyberBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "DECISION TIMELINE AUDIT",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberBlue,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    report.decisionsTimeline.forEachIndexed { index, decision ->
                        DecisionAuditItem(
                            decision = decision,
                            isLast = index == report.decisionsTimeline.size - 1
                        )
                    }
                }
            }
        }

        // Executive Tabletop & Advisory Card (movahedi.ca)
        item {
            MovahediExecutiveAdvisoryCard(
                onVisitSite = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(MovahediData.WEBSITE_URL))
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                },
                onBookConsultation = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(MovahediData.DISCOVERY_CALL_URL))
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                }
            )
        }

        // Return Button
        item {
            Button(
                onClick = onReturnToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("aar_return_home_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberCyan,
                    contentColor = Color(0xFF001F2B)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DoneAll,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "COMPLETE DRILL & RETURN TO COMMAND",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun ExecutiveGradeCard(report: AfterActionReport) {
    val gradeColor = when (report.letterGrade) {
        "A+", "A" -> ContainedGreen
        "B" -> CyberCyan
        "C" -> WarningAmber
        else -> AlertRed
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, gradeColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
        color = SlateCardElevated,
        tonalElevation = 6.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "INCIDENT RESPONSE GRADE",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = report.scenarioTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "READINESS SCORE: ${report.totalScore} / 100",
                        style = MaterialTheme.typography.labelSmall,
                        color = gradeColor,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Large Grade Badge
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    gradeColor.copy(alpha = 0.25f),
                                    gradeColor.copy(alpha = 0.08f)
                                )
                            )
                        )
                        .border(2.dp, gradeColor, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = report.letterGrade,
                        style = MaterialTheme.typography.headlineMedium,
                        color = gradeColor,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4 Key Output Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AarStatBox(
                    label = "TOTAL COST",
                    value = formatCurrency(report.finalCostUsd),
                    color = if (report.finalCostUsd > 2_000_000) AlertRed else WarningAmber,
                    modifier = Modifier.weight(1f)
                )
                AarStatBox(
                    label = "TIME TO CONTAIN",
                    value = "${report.totalTimeHours}h",
                    color = CyberBlue,
                    modifier = Modifier.weight(1f)
                )
                AarStatBox(
                    label = "PUBLIC TRUST",
                    value = "${report.publicTrustPercent}%",
                    color = if (report.publicTrustPercent >= 75) ContainedGreen else WarningAmber,
                    modifier = Modifier.weight(1f)
                )
                AarStatBox(
                    label = "LEGAL RISK",
                    value = report.legalRisk.name,
                    color = when (report.legalRisk) {
                        LegalRiskLevel.MINIMAL -> ContainedGreen
                        LegalRiskLevel.MODERATE -> WarningAmber
                        LegalRiskLevel.ELEVATED -> Color(0xFFFB923C)
                        LegalRiskLevel.CRITICAL -> AlertRed
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun AarStatBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SlateDark)
            .border(1.dp, SlateCardBorder, RoundedCornerShape(8.dp))
            .padding(6.dp)
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
fun CompetenciesBreakdownCard(breakdown: Map<Competency, Int>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, SlateCardBorder, RoundedCornerShape(14.dp)),
        color = SlateCardElevated
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "CRISIS COMPETENCY EVALUATION",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            breakdown.forEach { (competency, score) ->
                val progress by animateFloatAsState(targetValue = score / 100f, label = "comp_progress")
                val compColor = when {
                    score >= 85 -> ContainedGreen
                    score >= 70 -> CyberCyan
                    score >= 55 -> WarningAmber
                    else -> AlertRed
                }

                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = competency.label,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "$score%",
                            style = MaterialTheme.typography.labelSmall,
                            color = compColor,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = compColor,
                        trackColor = SlateDark
                    )
                }
            }
        }
    }
}

@Composable
fun DecisionAuditItem(
    decision: com.example.model.SelectedDecision,
    isLast: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(CyberCyan.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${decision.phaseNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = decision.phaseTitle,
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SlateDark)
                .padding(10.dp)
        ) {
            Column {
                Text(
                    text = "ACTION: ${decision.choice.title}",
                    style = MaterialTheme.typography.bodySmall,
                    color = CyberBlue,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = decision.choice.feedbackDoctrine,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }

        if (!isLast) {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun MovahediExecutiveAdvisoryCard(
    onVisitSite: () -> Unit,
    onBookConsultation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                1.dp,
                Brush.horizontalGradient(listOf(CyberCyan.copy(alpha = 0.5f), CyberIndigo.copy(alpha = 0.5f))),
                RoundedCornerShape(14.dp)
            ),
        color = SlateCardElevated
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "EXECUTIVE ADVISORY & CONSULTATION",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = SlateDark,
                    modifier = Modifier.border(1.dp, SlateCardBorder, RoundedCornerShape(4.dp))
                ) {
                    Text(
                        text = "movahedi.ca",
                        color = CyberCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Turn Tabletop Simulations into Enterprise Resilience",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Need customized incident response tabletop exercises for your board, or an independent audit of your privacy & AI risk frameworks? Connect with Mohammad Movahedi (CIPP/C, Lean Six Sigma Black Belt) at movahedi.ca.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onBookConsultation,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("aar_book_advisory_button")
                ) {
                    Text(
                        text = "Book Advisory Call",
                        color = Color(0xFF001F2B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                OutlinedButton(
                    onClick = onVisitSite,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(CyberCyan, CyberIndigo))
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("aar_visit_movahedi_button")
                ) {
                    Text(
                        text = "movahedi.ca",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}
