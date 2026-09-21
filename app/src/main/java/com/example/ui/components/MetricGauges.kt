package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ForensicIntegrity
import com.example.model.LegalRiskLevel
import com.example.model.LiveMetrics
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun LiveWarRoomMeters(
    metrics: LiveMetrics,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, SlateCardBorder, RoundedCornerShape(14.dp)),
        color = SlateCardElevated,
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(AlertRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE INCIDENT TELEMETRY",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }
                Text(
                    text = "${metrics.timeElapsedHours}h ELAPSED",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Cost & Trust Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Cost Metric Box
                MetricBox(
                    modifier = Modifier.weight(1f),
                    title = "EST. IMPACT COST",
                    value = formatCurrency(metrics.financialCostUsd),
                    icon = Icons.Default.AttachMoney,
                    accentColor = if (metrics.financialCostUsd > 2_000_000) AlertRed else WarningAmber
                )

                // Public Trust Box
                TrustMetricBox(
                    modifier = Modifier.weight(1f),
                    trustPercent = metrics.publicTrustPercent
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Legal Risk & Forensic Integrity Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Legal Risk Pill
                StatusPill(
                    modifier = Modifier.weight(1f),
                    label = "LEGAL RISK",
                    value = metrics.legalRisk.label,
                    icon = Icons.Default.Gavel,
                    color = when (metrics.legalRisk) {
                        LegalRiskLevel.MINIMAL -> ContainedGreen
                        LegalRiskLevel.MODERATE -> WarningAmber
                        LegalRiskLevel.ELEVATED -> Color(0xFFFB923C)
                        LegalRiskLevel.CRITICAL -> AlertRed
                    }
                )

                // Forensics Pill
                StatusPill(
                    modifier = Modifier.weight(1f),
                    label = "FORENSICS",
                    value = metrics.forensicIntegrity.label,
                    icon = Icons.Default.Security,
                    color = when (metrics.forensicIntegrity) {
                        ForensicIntegrity.INTACT -> ContainedGreen
                        ForensicIntegrity.PARTIALLY_COMPROMISED -> WarningAmber
                        ForensicIntegrity.TAINTED -> AlertRed
                    }
                )
            }
        }
    }
}

@Composable
fun MetricBox(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(SlateDark)
            .border(1.dp, SlateCardBorder, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = accentColor,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun TrustMetricBox(
    trustPercent: Int,
    modifier: Modifier = Modifier
) {
    val progress by animateFloatAsState(targetValue = trustPercent / 100f, label = "trust")
    val trustColor by animateColorAsState(
        targetValue = when {
            trustPercent >= 75 -> ContainedGreen
            trustPercent >= 50 -> WarningAmber
            else -> AlertRed
        },
        label = "trustColor"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(SlateDark)
            .border(1.dp, SlateCardBorder, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = trustColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "PUBLIC TRUST",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "$trustPercent%",
                    style = MaterialTheme.typography.labelSmall,
                    color = trustColor,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = trustColor,
                trackColor = MetricTrackBg
            )
        }
    }
}

@Composable
fun StatusPill(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SlateDark)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }
    }
}

fun formatCurrency(amount: Long): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)
    formatter.maximumFractionDigits = 0
    return formatter.format(amount)
}
