package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PresentToAll
import androidx.compose.material.icons.filled.SignalCellularConnectedNoInternet4Bar
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p2p.ResolutionPreset
import com.example.p2p.TelemetryData
import com.example.p2p.WebRtcState
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberSurfaceDark
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.HighVisAmber
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ScreenShareContainer(
    telemetry: TelemetryData,
    onSelectResolution: (ResolutionPreset) -> Unit,
    onToggleAutoFallback: (Boolean) -> Unit,
    onToggleNetworkJitter: (Boolean) -> Unit,
    onConnectPeer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val isStreaming = telemetry.state == WebRtcState.STREAMING || telemetry.state == WebRtcState.CONNECTED

    val infiniteTransition = rememberInfiniteTransition(label = "scanline")
    val scanY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanline_y"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // PROMINENT SCREEN SHARE ACTION BAR / CONTROL CARD
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                if (isStreaming) NeonGreen else CyberCyan
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (isStreaming) NeonGreen.copy(alpha = 0.2f) else CyberCyan.copy(alpha = 0.2f)
                            )
                            .border(
                                1.dp,
                                if (isStreaming) NeonGreen else CyberCyan,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PresentToAll,
                            contentDescription = "Screen Share",
                            tint = if (isStreaming) NeonGreen else CyberCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (isStreaming) "SCREEN SHARING ACTIVE" else "SCREEN SHARE READY",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (isStreaming) "P2P WebRTC session broadcasting" else "Click to start sharing screen to peer",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isStreaming) NeonGreen else TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Primary Start / Stop Screen Share Button
                Button(
                    onClick = onConnectPeer,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isStreaming) CrimsonRed else CyberCyan,
                        contentColor = ObsidianBackground
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("share_screen_primary_button")
                ) {
                    Icon(
                        imageVector = if (isStreaming) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isStreaming) "STOP SHARE" else "START SHARE",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // Live Screen Share Video Viewport with 3D Depth Projection
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .graphicsLayer {
                    rotationX = if (isStreaming) 4f else 0f
                    cameraDistance = 14f * density
                    shadowElevation = 16.dp.toPx()
                    spotShadowColor = if (isStreaming) NeonGreen else CyberCyan
                }
                .clip(RoundedCornerShape(12.dp))
                .background(CyberSurfaceDark)
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            if (isStreaming) NeonGreen else CyberCyan,
                            ElectricPurple.copy(alpha = 0.5f),
                            CyberBorder
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .testTag("screen_share_viewport")
        ) {
            if (isStreaming) {
                // Canvas HUD Simulation
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Grid Lines
                    val gridSpacing = 40.dp.toPx()
                    val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)

                    for (x in 0..width.toInt() step gridSpacing.toInt()) {
                        drawLine(
                            color = CyberBorder.copy(alpha = 0.4f),
                            start = Offset(x.toFloat(), 0f),
                            end = Offset(x.toFloat(), height),
                            strokeWidth = 1f,
                            pathEffect = dashPathEffect
                        )
                    }
                    for (y in 0..height.toInt() step gridSpacing.toInt()) {
                        drawLine(
                            color = CyberBorder.copy(alpha = 0.4f),
                            start = Offset(0f, y.toFloat()),
                            end = Offset(width, y.toFloat()),
                            strokeWidth = 1f,
                            pathEffect = dashPathEffect
                        )
                    }

                    // Scanline laser
                    val laserY = height * scanY
                    drawLine(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, NeonGreen.copy(alpha = 0.8f), Color.Transparent)
                        ),
                        start = Offset(0f, laserY),
                        end = Offset(width, laserY),
                        strokeWidth = 3.dp.toPx()
                    )

                    // Target Crosshairs
                    val centerX = width / 2f
                    val centerY = height / 2f
                    drawCircle(
                        color = CyberCyan.copy(alpha = 0.6f),
                        radius = 28.dp.toPx(),
                        center = Offset(centerX, centerY),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx())
                    )
                }

                // HUD Overlays
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    // Top Left Status
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .background(ObsidianBackground.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                            .border(1.dp, CyberBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(NeonGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LIVE SCREEN SHARE",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }

                    // Top Right Resolution badge
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(ObsidianBackground.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                            .border(1.dp, CyberBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = telemetry.resolution.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }

                    // Bottom Overlay Metrics
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .background(ObsidianBackground.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                            .border(1.dp, CyberBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FPS: ${telemetry.currentFps}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = NeonGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "LOSS: ${"%.1f".format(telemetry.packetLossPct)}%",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = if (telemetry.packetLossPct > 5.0f) HighVisAmber else TextSecondary
                        )
                        Text(
                            text = "LATENCY: ${telemetry.latencyMs}ms",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = if (telemetry.latencyMs > 100) HighVisAmber else NeonGreen
                        )
                    }
                }
            } else {
                // Offline Placeholder State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PresentToAll,
                        contentDescription = "Screen Share Inactive",
                        tint = TextMuted,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Screen Sharing Inactive",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Start screen sharing to stream low-latency video to connected peers.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = onConnectPeer,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberCyan,
                            contentColor = ObsidianBackground
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("initiate_stream_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PresentToAll,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "START SCREEN SHARE NOW",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Resolution & Quality Preset Controls
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HighQuality,
                            contentDescription = "Resolution Quality",
                            tint = CyberCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "STREAM QUALITY & RESOLUTION",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Auto-scale quality based on network health",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }

                    // Auto Fallback Switch
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (telemetry.autoFallbackEnabled) "AUTO" else "MANUAL",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (telemetry.autoFallbackEnabled) NeonGreen else HighVisAmber,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Switch(
                            checked = telemetry.autoFallbackEnabled,
                            onCheckedChange = onToggleAutoFallback,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NeonGreen,
                                checkedTrackColor = NeonGreen.copy(alpha = 0.3f),
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = CyberBorder
                            ),
                            modifier = Modifier.testTag("auto_fallback_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Resolution Preset Selector Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ResolutionPreset.entries.forEach { preset ->
                        val isSelected = telemetry.resolution == preset
                        val chipColor = if (isSelected) CyberCyan else CyberSurfaceVariant
                        val borderColor = if (isSelected) CyberCyan else CyberBorder

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(chipColor.copy(alpha = if (isSelected) 0.2f else 1.0f))
                                .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                                .clickable { onSelectResolution(preset) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = preset.name.replace("RES_", ""),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) CyberCyan else TextPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "${preset.targetFps} FPS",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Network Simulation Controls
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (telemetry.isNetworkSimulatedPoor) Icons.Default.SignalCellularConnectedNoInternet4Bar else Icons.Default.NetworkCheck,
                        contentDescription = "Network Jitter Test",
                        tint = if (telemetry.isNetworkSimulatedPoor) HighVisAmber else NeonGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "SIMULATE POOR NETWORK",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (telemetry.isNetworkSimulatedPoor) "Active: 12% loss & 180ms jitter" else "Optimal broadband condition",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (telemetry.isNetworkSimulatedPoor) HighVisAmber else TextMuted
                        )
                    }
                }

                Switch(
                    checked = telemetry.isNetworkSimulatedPoor,
                    onCheckedChange = onToggleNetworkJitter,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = HighVisAmber,
                        checkedTrackColor = HighVisAmber.copy(alpha = 0.3f),
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = CyberBorder
                    ),
                    modifier = Modifier.testTag("simulate_jitter_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Telemetry Dashboard Metrics Grid
        Text(
            text = "REAL-TIME STREAM METRICS",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "BITRATE",
                value = "${telemetry.bitrateKbps} kbps",
                accentColor = CyberCyan,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "FRAME DROPS",
                value = "${telemetry.frameDrops}",
                accentColor = if (telemetry.frameDrops > 0) HighVisAmber else NeonGreen,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "PACKET LOSS",
                value = "${"%.1f".format(telemetry.packetLossPct)}%",
                accentColor = if (telemetry.packetLossPct > 5f) CrimsonRed else NeonGreen,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 9.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = accentColor
            )
        }
    }
}

