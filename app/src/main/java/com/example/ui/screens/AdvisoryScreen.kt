package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.data.MovahediInsight
import com.example.data.MovahediService
import com.example.ui.theme.*

private fun openBrowser(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}

@Composable
fun AdvisoryScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Advisory Services", "Practitioner Insights", "Breach Evaluator")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Consultant Profile Card
        item {
            ConsultantProfileCard(
                onVisitSite = { openBrowser(context, MovahediData.WEBSITE_URL) },
                onBookCall = { openBrowser(context, MovahediData.DISCOVERY_CALL_URL) }
            )
        }

        // Section Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SlateCardElevated,
                contentColor = CyberCyan,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, SlateCardBorder, RoundedCornerShape(12.dp))
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == index) CyberCyan else TextMuted
                            )
                        },
                        modifier = Modifier.testTag("advisory_tab_$index")
                    )
                }
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                // Services Tab
                item {
                    Text(
                        text = "CORE ADVISORY & CONSULTING SERVICES",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Strategic privacy engineering, AI risk frameworks, and process excellence for forward-thinking enterprises.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                items(MovahediData.services) { service ->
                    ServiceItemCard(
                        service = service,
                        onLearnMore = { openBrowser(context, service.url) }
                    )
                }
            }

            1 -> {
                // Insights Tab
                item {
                    Text(
                        text = "PRACTITIONER ARTICLES & DOCTRINE",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Expert commentary on Canadian privacy law, constitutional digital forensics, and AI safety by Mohammad Movahedi.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                items(MovahediData.insights) { insight ->
                    InsightItemCard(
                        insight = insight,
                        onReadArticle = { openBrowser(context, insight.url) }
                    )
                }
            }

            2 -> {
                // Canadian Breach & RROSH Evaluator
                item {
                    CanadianBreachEvaluatorCard(
                        onConsultExpert = { openBrowser(context, MovahediData.DISCOVERY_CALL_URL) }
                    )
                }
            }
        }

        // Bottom CTA Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, Brush.horizontalGradient(listOf(CyberCyan, CyberIndigo)), RoundedCornerShape(14.dp)),
                color = SlateCardElevated
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ready to Harden Your Privacy & AI Governance?",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Connect directly with Mohammad Movahedi at movahedi.ca to discuss tailored tabletop simulations, privacy program audits, or fractional advisory roles.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { openBrowser(context, MovahediData.WEBSITE_URL) },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("advisory_bottom_cta_button")
                    ) {
                        Text(
                            text = "Explore Advisory at movahedi.ca",
                            color = Color(0xFF001F2B),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            tint = Color(0xFF001F2B),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConsultantProfileCard(
    onVisitSite: () -> Unit,
    onBookCall: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, SlateCardBorder, RoundedCornerShape(16.dp)),
        color = SlateCard
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(CyberCyan, CyberIndigo))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "MM",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF001F2B),
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = MovahediData.CONSULTANT_NAME,
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Text(
                        text = MovahediData.SPECIALIZATION,
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${MovahediData.CREDENTIALS} • ${MovahediData.LOCATION}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Building trustworthy data ecosystems — where compliance is clear and artificial intelligence is securely governed.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onVisitSite,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("visit_movahedi_site_button")
                ) {
                    Text(
                        text = "movahedi.ca",
                        color = Color(0xFF001F2B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = Color(0xFF001F2B),
                        modifier = Modifier.size(14.dp)
                    )
                }

                OutlinedButton(
                    onClick = onBookCall,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(CyberCyan, CyberIndigo))
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("book_discovery_call_button")
                ) {
                    Text(
                        text = "Book Advisory",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ServiceItemCard(
    service: MovahediService,
    onLearnMore: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, SlateCardBorder, RoundedCornerShape(12.dp)),
        color = SlateCardElevated
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = service.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = onLearnMore,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowOutward,
                        contentDescription = "Open service details on movahedi.ca",
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = service.subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = CyberCyan,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = service.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(service.tags) { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SlateDark)
                            .border(1.dp, SlateCardBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InsightItemCard(
    insight: MovahediInsight,
    onReadArticle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, SlateCardBorder, RoundedCornerShape(12.dp)),
        color = SlateCardElevated
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberIndigo.copy(alpha = 0.2f))
                        .border(1.dp, CyberIndigo.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = insight.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberIndigo,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = insight.readTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = insight.title,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = insight.summary,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                color = SlateDark
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = WarningAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = insight.keyTakeaway,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onReadArticle() },
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Read on movahedi.ca",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowOutward,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun CanadianBreachEvaluatorCard(
    onConsultExpert: () -> Unit
) {
    var isSensitiveData by remember { mutableStateOf(true) }
    var isLikelyMisuse by remember { mutableStateOf(true) }
    var affectedVolume by remember { mutableStateOf(250) }

    val isRroshMet = isSensitiveData && (isLikelyMisuse || affectedVolume > 100)

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
                    imageVector = Icons.Default.Calculate,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CANADIAN PRIVACY BREACH EVALUATOR",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Interactive tool based on Movahedi.ca doctrine for PIPEDA & Quebec Law 25 Real Risk of Significant Harm (RROSH) determination.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Criteria 1: Sensitive Data
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Data Sensitivity Level",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Financial, health, passwords, or government IDs involved",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
                Switch(
                    checked = isSensitiveData,
                    onCheckedChange = { isSensitiveData = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = CyberCyan, checkedTrackColor = CyberCyan.copy(alpha = 0.3f))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Criteria 2: Likelihood of Misuse
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Probability of Misuse",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Malicious actor with known intent or unauthorized exfiltration",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
                Switch(
                    checked = isLikelyMisuse,
                    onCheckedChange = { isLikelyMisuse = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = CyberCyan, checkedTrackColor = CyberCyan.copy(alpha = 0.3f))
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Evaluation Result Box
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        1.dp,
                        if (isRroshMet) AlertRed.copy(alpha = 0.6f) else ContainedGreen.copy(alpha = 0.6f),
                        RoundedCornerShape(10.dp)
                    ),
                color = if (isRroshMet) AlertRed.copy(alpha = 0.1f) else ContainedGreen.copy(alpha = 0.1f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isRroshMet) Icons.Default.Warning else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (isRroshMet) AlertRed else ContainedGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRroshMet) "MANDATORY NOTIFICATION TRIGGERED (RROSH)" else "INTERNAL RECORD KEEPING MANDATED",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isRroshMet) AlertRed else ContainedGreen,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isRroshMet) {
                            "Under PIPEDA Section 10.1 and Quebec Law 25, you are legally required to report this confidentiality incident to the Office of the Privacy Commissioner (OPC) or CAI 'as soon as feasible', and notify all affected data subjects."
                        } else {
                            "While external disclosure to the OPC may not be immediately required, organizations must maintain an internal Breach Register for a minimum of 24 months under PIPEDA s. 10.3."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedButton(
                onClick = onConsultExpert,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Request Breach Review from Movahedi.ca",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
