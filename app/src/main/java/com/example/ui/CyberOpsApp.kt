package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.PresentToAll
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AiTerminalView
import com.example.ui.components.CyberHeader
import com.example.ui.components.DiagnosticLogsView
import com.example.ui.components.LoginScreen
import com.example.ui.components.P2pChatView
import com.example.ui.components.ScreenShareContainer
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberSurfaceDark
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun CyberOpsApp(
    viewModel: CyberOpsViewModel
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val telemetry by viewModel.telemetry.collectAsStateWithLifecycle()
    val allMessages by viewModel.allMessages.collectAsStateWithLifecycle()
    val diagnosticLogs by viewModel.diagnosticLogs.collectAsStateWithLifecycle()
    val isServiceRunning by viewModel.isForegroundServiceRunning.collectAsStateWithLifecycle()

    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val aiInputText by viewModel.aiInputText.collectAsStateWithLifecycle()
    val p2pInputText by viewModel.p2pInputText.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val isAutoFixing by viewModel.isAutoFixing.collectAsStateWithLifecycle()

    val configuration = LocalConfiguration.current
    val isTabletOrWide = configuration.screenWidthDp >= 600

    if (currentUser == null) {
        LoginScreen(
            accounts = viewModel.presetAccounts,
            onSelectAccount = { viewModel.login(it) }
        )
    } else {
        Scaffold(
            topBar = {
                CyberHeader(
                    telemetry = telemetry,
                    currentUser = currentUser,
                    isForegroundServiceRunning = isServiceRunning,
                    isAutoFixing = isAutoFixing,
                    onToggleConnect = { viewModel.toggleConnectPeer() },
                    onToggleService = { viewModel.toggleForegroundProjectionService() },
                    onAutoFixErrors = { viewModel.autoFixSystemErrors() },
                    onLogout = { viewModel.logout() }
                )
            },
            bottomBar = {
                if (!isTabletOrWide) {
                    CyberBottomBar(
                        selectedTab = selectedTab,
                        onSelectTab = { viewModel.selectTab(it) }
                    )
                }
            },
            containerColor = ObsidianBackground
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isTabletOrWide) {
                    // Wide / Tablet Dual-Pane Side-by-Side View
                    Row(modifier = Modifier.fillMaxSize()) {
                        // Left Workspace Pane: Video Stream & Controls (55% width)
                        Box(
                            modifier = Modifier
                                .weight(0.55f)
                                .fillMaxHeight()
                        ) {
                            ScreenShareContainer(
                                telemetry = telemetry,
                                onSelectResolution = { viewModel.setManualResolution(it) },
                                onToggleAutoFallback = { viewModel.toggleAutoFallback(it) },
                                onToggleNetworkJitter = { viewModel.toggleSimulatedNetworkJitter(it) },
                                onConnectPeer = { viewModel.p2pEngine.connectPeer() }
                            )
                        }

                        // Vertical Divider
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp)
                                .background(CyberBorder)
                        )

                        // Right Dispatch Console Pane (45% width)
                        Column(
                            modifier = Modifier
                                .weight(0.45f)
                                .fillMaxHeight()
                        ) {
                            // Wide Pane Tab Selector
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CyberSurfaceDark)
                                    .border(1.dp, CyberBorder),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                WideTabButton(
                                    label = "AI TERMINAL",
                                    icon = Icons.Default.AutoAwesome,
                                    activeColor = ElectricPurple,
                                    isSelected = selectedTab == AppTab.AI_TERMINAL,
                                    onClick = { viewModel.selectTab(AppTab.AI_TERMINAL) }
                                )
                                WideTabButton(
                                    label = "P2P CHAT",
                                    icon = Icons.Default.Forum,
                                    activeColor = NeonGreen,
                                    isSelected = selectedTab == AppTab.P2P_CHAT,
                                    onClick = { viewModel.selectTab(AppTab.P2P_CHAT) }
                                )
                                WideTabButton(
                                    label = "LOGS",
                                    icon = Icons.Default.ListAlt,
                                    activeColor = CyberCyan,
                                    isSelected = selectedTab == AppTab.DIAGNOSTICS,
                                    onClick = { viewModel.selectTab(AppTab.DIAGNOSTICS) }
                                )
                            }

                            // Wide Right Pane Active View
                            Box(modifier = Modifier.weight(1f)) {
                                when (selectedTab) {
                                    AppTab.AI_TERMINAL -> {
                                        AiTerminalView(
                                            messages = allMessages,
                                            inputText = aiInputText,
                                            isLoading = isAiLoading,
                                            onInputChange = { viewModel.updateAiInput(it) },
                                            onSendPrompt = { viewModel.sendAiPrompt(it) }
                                        )
                                    }
                                    AppTab.P2P_CHAT -> {
                                        P2pChatView(
                                            messages = allMessages,
                                            inputText = p2pInputText,
                                            webRtcState = telemetry.state,
                                            onInputChange = { viewModel.updateP2pInput(it) },
                                            onSendMessage = { viewModel.sendP2pMessage() },
                                            onSyncPending = { viewModel.syncOfflineMessages() }
                                        )
                                    }
                                    else -> {
                                        DiagnosticLogsView(
                                            logs = diagnosticLogs,
                                            onClearLogs = { viewModel.clearLogs() }
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Mobile Stacked Single-Column View
                    when (selectedTab) {
                        AppTab.STREAM -> {
                            ScreenShareContainer(
                                telemetry = telemetry,
                                onSelectResolution = { viewModel.setManualResolution(it) },
                                onToggleAutoFallback = { viewModel.toggleAutoFallback(it) },
                                onToggleNetworkJitter = { viewModel.toggleSimulatedNetworkJitter(it) },
                                onConnectPeer = { viewModel.p2pEngine.connectPeer() }
                            )
                        }
                        AppTab.AI_TERMINAL -> {
                            AiTerminalView(
                                messages = allMessages,
                                inputText = aiInputText,
                                isLoading = isAiLoading,
                                onInputChange = { viewModel.updateAiInput(it) },
                                onSendPrompt = { viewModel.sendAiPrompt(it) }
                            )
                        }
                        AppTab.P2P_CHAT -> {
                            P2pChatView(
                                messages = allMessages,
                                inputText = p2pInputText,
                                webRtcState = telemetry.state,
                                onInputChange = { viewModel.updateP2pInput(it) },
                                onSendMessage = { viewModel.sendP2pMessage() },
                                onSyncPending = { viewModel.syncOfflineMessages() }
                            )
                        }
                        AppTab.DIAGNOSTICS -> {
                            DiagnosticLogsView(
                                logs = diagnosticLogs,
                                onClearLogs = { viewModel.clearLogs() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WideTabButton(
    label: String,
    icon: ImageVector,
    activeColor: androidx.compose.ui.graphics.Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) activeColor else TextMuted,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) activeColor else TextMuted
            )
        }
    }
}

@Composable
fun CyberBottomBar(
    selectedTab: AppTab,
    onSelectTab: (AppTab) -> Unit
) {
    NavigationBar(
        containerColor = CyberSurfaceDark,
        tonalElevation = 8.dp,
        modifier = Modifier.border(1.dp, CyberBorder)
    ) {
        NavigationBarItem(
            selected = selectedTab == AppTab.STREAM,
            onClick = { onSelectTab(AppTab.STREAM) },
            icon = { Icon(imageVector = Icons.Default.PresentToAll, contentDescription = "Share Screen") },
            label = { Text("SHARE SCREEN", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyberCyan,
                selectedTextColor = CyberCyan,
                indicatorColor = CyberCyan.copy(alpha = 0.2f),
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_tab_stream")
        )
        NavigationBarItem(
            selected = selectedTab == AppTab.AI_TERMINAL,
            onClick = { onSelectTab(AppTab.AI_TERMINAL) },
            icon = { Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI Terminal") },
            label = { Text("AI TERMINAL", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ElectricPurple,
                selectedTextColor = ElectricPurple,
                indicatorColor = ElectricPurple.copy(alpha = 0.2f),
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_tab_ai")
        )
        NavigationBarItem(
            selected = selectedTab == AppTab.P2P_CHAT,
            onClick = { onSelectTab(AppTab.P2P_CHAT) },
            icon = { Icon(imageVector = Icons.Default.Forum, contentDescription = "P2P Chat") },
            label = { Text("P2P CHAT", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonGreen,
                selectedTextColor = NeonGreen,
                indicatorColor = NeonGreen.copy(alpha = 0.2f),
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_tab_chat")
        )
        NavigationBarItem(
            selected = selectedTab == AppTab.DIAGNOSTICS,
            onClick = { onSelectTab(AppTab.DIAGNOSTICS) },
            icon = { Icon(imageVector = Icons.Default.ListAlt, contentDescription = "Logs") },
            label = { Text("LOGS", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyberCyan,
                selectedTextColor = CyberCyan,
                indicatorColor = CyberCyan.copy(alpha = 0.2f),
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_tab_logs")
        )
    }
}
