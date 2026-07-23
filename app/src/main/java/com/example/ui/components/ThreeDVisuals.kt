package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberSurfaceDark
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.NeonGreen
import kotlin.math.cos
import kotlin.math.sin

/**
 * Interactive 3D Perspective Card that responds to touch drag gestures
 * and renders with depth elevation and floating rotation.
 */
@Composable
fun ThreeDTiltCard(
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = RoundedCornerShape(16.dp),
    maxTiltDegrees: Float = 15f,
    enableAutoFloat: Boolean = true,
    borderColor: Color = CyberCyan,
    content: @Composable () -> Unit
) {
    var dragX by remember { mutableFloatStateOf(0f) }
    var dragY by remember { mutableFloatStateOf(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "3d_float")
    val autoRotY by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auto_y"
    )

    val rotX = (dragY / 10f).coerceIn(-maxTiltDegrees, maxTiltDegrees)
    val rotY = (dragX / 10f + if (enableAutoFloat) autoRotY else 0f).coerceIn(-maxTiltDegrees, maxTiltDegrees)

    Card(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        dragX = 0f
                        dragY = 0f
                    },
                    onDragCancel = {
                        dragX = 0f
                        dragY = 0f
                    }
                ) { change, dragAmount ->
                    change.consume()
                    dragX += dragAmount.x
                    dragY -= dragAmount.y
                }
            }
            .graphicsLayer {
                rotationX = rotX
                rotationY = rotY
                cameraDistance = 16f * density
                shadowElevation = 18.dp.toPx()
                spotShadowColor = borderColor
                ambientShadowColor = ElectricPurple
            },
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = CyberSurfaceDark.copy(alpha = 0.90f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            Brush.linearGradient(
                colors = listOf(
                    borderColor,
                    ElectricPurple.copy(alpha = 0.5f),
                    NeonGreen.copy(alpha = 0.3f),
                    CyberBorder
                )
            )
        )
    ) {
        content()
    }
}

/**
 * Animated 3D Holographic Canvas rendering revolving geometric projection rings
 * and perspective cyber grid in real-time.
 */
@Composable
fun Holographic3dCanvas(
    modifier: Modifier = Modifier,
    primaryColor: Color = CyberCyan,
    secondaryColor: Color = ElectricPurple
) {
    val infiniteTransition = rememberInfiniteTransition(label = "3d_canvas")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val radius = minOf(centerX, centerY) * 0.75f

        // Draw 3D Perspective Wireframe Ellipses
        val rad = Math.toRadians(angle.toDouble())
        val cosA = cos(rad).toFloat()
        val sinA = sin(rad).toFloat()

        // Outer Ring 1
        drawCircle(
            color = primaryColor.copy(alpha = 0.15f),
            center = Offset(centerX, centerY),
            radius = radius,
            style = Stroke(width = 2f)
        )

        // Revolving 3D Tilted Orbit Line 1
        val path1 = Path()
        val steps = 36
        for (i in 0..steps) {
            val stepRad = Math.toRadians((i * 360f / steps).toDouble())
            val rx = radius * cos(stepRad).toFloat()
            val ry = radius * 0.4f * sin(stepRad).toFloat()

            // 3D Rotation transform around Y axis
            val projX = centerX + (rx * cosA - ry * sinA)
            val projY = centerY + (rx * sinA + ry * cosA) * 0.5f

            if (i == 0) path1.moveTo(projX, projY) else path1.lineTo(projX, projY)
        }
        drawPath(
            path = path1,
            color = primaryColor.copy(alpha = 0.6f),
            style = Stroke(width = 2.5f)
        )

        // Revolving 3D Tilted Orbit Line 2 (Opposite tilt)
        val path2 = Path()
        for (i in 0..steps) {
            val stepRad = Math.toRadians((i * 360f / steps + 90).toDouble())
            val rx = radius * 0.8f * cos(stepRad).toFloat()
            val ry = radius * 0.8f * 0.4f * sin(stepRad).toFloat()

            val projX = centerX + (rx * cosA + ry * sinA)
            val projY = centerY + (-rx * sinA + ry * cosA) * 0.5f

            if (i == 0) path2.moveTo(projX, projY) else path2.lineTo(projX, projY)
        }
        drawPath(
            path = path2,
            color = secondaryColor.copy(alpha = 0.5f),
            style = Stroke(width = 2f)
        )

        // Glowing 3D Core Node
        drawCircle(
            color = primaryColor,
            center = Offset(centerX, centerY),
            radius = 6f
        )
        drawCircle(
            color = primaryColor.copy(alpha = 0.3f),
            center = Offset(centerX, centerY),
            radius = 16f
        )
    }
}
