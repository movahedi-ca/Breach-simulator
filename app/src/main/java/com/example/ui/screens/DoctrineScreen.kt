package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.data.DoctrineData
import com.example.model.DoctrinePlaybook
import com.example.ui.theme.*

@Composable
fun DoctrineScreen(
    onSelectPlaybook: (DoctrinePlaybook) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredPlaybooks = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            DoctrineData.playbooks
        } else {
            DoctrineData.playbooks.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.overview.contains(searchQuery, ignoreCase = true) ||
                        it.authority.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(CyberCyan)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "INCIDENT RESPONSE DOCTRINE",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Regulatory & Legal Playbooks",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Mandatory disclosure windows, statutory penalties, and NIST SP 800-61 response playbooks.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("doctrine_search_field"),
                placeholder = {
                    Text(
                        text = "Search GDPR, SEC 8-K, HIPAA, NIST...",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = CyberCyan
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = TextSecondary
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = SlateCardBorder,
                    focusedContainerColor = SlateCardElevated,
                    unfocusedContainerColor = SlateCardElevated,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
        }

        // Playbook Cards
        items(filteredPlaybooks) { playbook ->
            DoctrinePlaybookCard(
                playbook = playbook,
                onClick = { onSelectPlaybook(playbook) }
            )
        }
    }
}

@Composable
fun DoctrinePlaybookCard(
    playbook: DoctrinePlaybook,
    onClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, SlateCardBorder, RoundedCornerShape(14.dp))
            .clickable { isExpanded = !isExpanded }
            .testTag("playbook_card_${playbook.id}"),
        color = SlateCardElevated,
        tonalElevation = 3.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Authority and Deadline
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = playbook.authority.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AlertRed.copy(alpha = 0.15f))
                        .border(1.dp, AlertRed.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = playbook.deadlineWindow,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFCA5A5),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = playbook.title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = null,
                    tint = WarningAmber,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "PENALTIES: ${playbook.statutoryPenalties}",
                    style = MaterialTheme.typography.labelSmall,
                    color = WarningAmber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = playbook.overview,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )

            // Expanded checklist and requirements
            if (isExpanded) {
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "MANDATORY DISCLOSURE ELEMENTS",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                playbook.mandatoryRequirements.forEach { req ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier
                                .size(12.dp)
                                .padding(top = 3.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = req,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "CRISIS TIMELINE CHECKLIST",
                    style = MaterialTheme.typography.labelSmall,
                    color = ContainedGreen,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                playbook.incidentPlaybookChecklist.forEach { step ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowRight,
                            contentDescription = null,
                            tint = ContainedGreen,
                            modifier = Modifier
                                .size(14.dp)
                                .padding(top = 1.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "COMMON PITFALLS & REGULATORY TRAPS",
                    style = MaterialTheme.typography.labelSmall,
                    color = AlertRed,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                playbook.commonPitfalls.forEach { pitfall ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = AlertRed,
                            modifier = Modifier
                                .size(12.dp)
                                .padding(top = 3.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = pitfall,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFCA5A5),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "COLLAPSE PLAYBOOK" else "EXPAND PLAYBOOK",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
