package com.ajantha.apsa.ui.dashboard.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun RadarScanner(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "radar")

    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2500,
                easing = LinearEasing
            )
        ),
        label = "angle"
    )

    val primary = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            val radius = size.minDimension / 2

            drawCircle(
                color = primary.copy(alpha = 0.12f),
                radius = radius
            )

            drawCircle(
                color = primary,
                radius = radius,
                style = Stroke(width = 2.dp.toPx())
            )

            rotate(angle) {
                drawLine(
                    color = primary,
                    start = center,
                    end = Offset(center.x, 0f),
                    strokeWidth = 4.dp.toPx()
                )
            }
        }

        Text(
            text = "SCANNING",
            color = onSurface,
            fontWeight = FontWeight.Bold
        )
    }
}