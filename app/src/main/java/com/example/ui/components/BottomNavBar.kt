package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Screen
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun TabletopBottomNavBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("main_navigation_bar"),
        containerColor = SlateCardElevated,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentScreen == Screen.HOME || currentScreen == Screen.SCENARIO_DETAIL,
            onClick = { onNavigate(Screen.HOME) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == Screen.HOME) Icons.Filled.Shield else Icons.Outlined.Shield,
                    contentDescription = "War Room",
                    modifier = Modifier.size(22.dp)
                )
            },
            label = {
                Text(
                    text = "War Room",
                    fontSize = 11.sp,
                    fontWeight = if (currentScreen == Screen.HOME) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF001F2B),
                selectedTextColor = CyberCyan,
                indicatorColor = CyberCyan,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_item_war_room")
        )

        NavigationBarItem(
            selected = currentScreen == Screen.DOCTRINE_LIST || currentScreen == Screen.DOCTRINE_DETAIL,
            onClick = { onNavigate(Screen.DOCTRINE_LIST) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == Screen.DOCTRINE_LIST) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                    contentDescription = "Doctrine",
                    modifier = Modifier.size(22.dp)
                )
            },
            label = {
                Text(
                    text = "Doctrine",
                    fontSize = 11.sp,
                    fontWeight = if (currentScreen == Screen.DOCTRINE_LIST) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF001F2B),
                selectedTextColor = CyberCyan,
                indicatorColor = CyberCyan,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_item_doctrine")
        )

        NavigationBarItem(
            selected = currentScreen == Screen.ADVISORY,
            onClick = { onNavigate(Screen.ADVISORY) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == Screen.ADVISORY) Icons.Filled.Lightbulb else Icons.Outlined.Lightbulb,
                    contentDescription = "Advisory & Tools",
                    modifier = Modifier.size(22.dp)
                )
            },
            label = {
                Text(
                    text = "Advisory",
                    fontSize = 11.sp,
                    fontWeight = if (currentScreen == Screen.ADVISORY) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF001F2B),
                selectedTextColor = CyberCyan,
                indicatorColor = CyberCyan,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_item_advisory")
        )

        NavigationBarItem(
            selected = currentScreen == Screen.HISTORY_LOGS,
            onClick = { onNavigate(Screen.HISTORY_LOGS) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == Screen.HISTORY_LOGS) Icons.Filled.History else Icons.Outlined.History,
                    contentDescription = "Archives",
                    modifier = Modifier.size(22.dp)
                )
            },
            label = {
                Text(
                    text = "Archives",
                    fontSize = 11.sp,
                    fontWeight = if (currentScreen == Screen.HISTORY_LOGS) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF001F2B),
                selectedTextColor = CyberCyan,
                indicatorColor = CyberCyan,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_item_archives")
        )
    }
}
