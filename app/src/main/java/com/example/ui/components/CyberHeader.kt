package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p2p.TelemetryData
import com.example.p2p.WebRtcState
import com.example.ui.NexusUser
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberSurfaceDark
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.HighVisAmber
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CyberHeader(
    telemetry: TelemetryData,
    currentUser: NexusUser?,
    isForegroundServiceRunning: Boolean,
    isAutoFixing: Boolean,
    onToggleConnect: () -> Unit,
    onToggleService: () -> Unit,
    onAutoFixErrors: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor by animateColorAsState(
        targetValue = when (telemetry.state) {
            WebRtcState.STREAMING, WebRtcState.CONNECTED -> NeonGreen
            WebRtcState.SIGNALING, WebRtcState.GATHERING_ICE -> HighVisAmber
            WebRtcState.DISCONNECTED -> CrimsonRed
        },
        label = "status_color"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        CyberSurfaceDark.copy(alpha = 0.9f),
                        ObsidianBackground.copy(alpha = 0.95f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        CyberCyan.copy(alpha = 0.35f),
                        NeonGreen.copy(alpha = 0.25f),
                        CyberBorder
                    )
                ),
                shape = RoundedCornerShape(0.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Title and Brand Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberBorder)
                            .border(1.dp, CyberCyan, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Security Shield",
                            tint = CyberCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "NEXUS",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CLASSIC P2P",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricPurple,
                                fontSize = 9.sp,
                                modifier = Modifier
                                    .background(ElectricPurple.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = currentUser?.let { "${it.name} | ${it.email}" } ?: "GUEST OPERATOR",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                // Action controls
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Auto-Fix System Errors Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(HighVisAmber.copy(alpha = 0.15f))
                            .border(1.dp, HighVisAmber.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable { onAutoFixErrors() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("auto_fix_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isAutoFixing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    color = HighVisAmber,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = "Auto Fix Errors",
                                    tint = HighVisAmber,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAutoFixing) "FIXING..." else "AUTO-FIX",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = HighVisAmber,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Status Pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .clickable { onToggleConnect() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("p2p_connect_button"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                                .alpha(if (telemetry.state == WebRtcState.STREAMING) pulseAlpha else 1f)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = telemetry.state.name,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Logout Button
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberBorder)
                            .testTag("logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Quick Telemetry Ribbon
            if (telemetry.state == WebRtcState.STREAMING) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RES: ${telemetry.resolution.width}x${telemetry.resolution.height}",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "FPS: ${telemetry.currentFps}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonGreen,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "BITRATE: ${telemetry.bitrateKbps} kbps",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "RTT: ${telemetry.latencyMs}ms",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (telemetry.latencyMs > 100) HighVisAmber else NeonGreen,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
