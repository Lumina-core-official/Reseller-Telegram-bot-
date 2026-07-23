package com.example.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Reusable Glassmorphic Panel Container for Cyber-Operations Theme.
 * Uses obsidian dark background with semi-transparent surface (#030508 + #0D121D opacity)
 * and subtle cyan/neon green glass borders.
 */
@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = RoundedCornerShape(12.dp),
    borderColor: Color = CyberCyan.copy(alpha = 0.35f),
    backgroundColor: Color = CyberSurfaceDark.copy(alpha = 0.82f),
    borderWidth: Dp = 1.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        ObsidianBackground.copy(alpha = 0.90f)
                    )
                )
            )
            .border(
                borderWidth,
                Brush.linearGradient(
                    colors = listOf(
                        borderColor,
                        NeonGreen.copy(alpha = 0.20f),
                        CyberBorder
                    )
                ),
                shape
            ),
        content = content
    )
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = RoundedCornerShape(12.dp),
    borderColor: Color = CyberCyan.copy(alpha = 0.3f),
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = CyberSurfaceDark.copy(alpha = 0.85f)
        ),
        border = BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                colors = listOf(
                    borderColor,
                    ElectricPurple.copy(alpha = 0.25f),
                    CyberBorder
                )
            )
        )
    ) {
        content()
    }
}
