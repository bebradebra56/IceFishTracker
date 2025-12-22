package com.icefishi.trackesof.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.icefishi.trackesof.ui.theme.*
import kotlin.random.Random

@Composable
fun IceBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "ice_shimmer")
    
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        // Base ice gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    LightIceBlue,
                    IceBlue,
                    FrostedWhite
                )
            ),
            size = size
        )
        
        // Ice crystals patterns
        val random = Random(42) // Fixed seed for consistent pattern
        for (i in 0..20) {
            val startX = random.nextFloat() * size.width
            val startY = random.nextFloat() * size.height
            
            drawIceCrystal(
                center = Offset(startX, startY),
                size = random.nextFloat() * 40f + 20f,
                alpha = shimmerAlpha * 0.3f
            )
        }
        
        // Ice cracks for texture
        for (i in 0..5) {
            val startX = random.nextFloat() * size.width
            val startY = random.nextFloat() * size.height
            val endX = startX + (random.nextFloat() - 0.5f) * 200f
            val endY = startY + (random.nextFloat() - 0.5f) * 200f
            
            drawLine(
                color = DarkIceBlue.copy(alpha = 0.15f),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 1f
            )
        }
        
        // Shimmer effects
        for (i in 0..10) {
            val x = random.nextFloat() * size.width
            val y = random.nextFloat() * size.height
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SnowWhite.copy(alpha = shimmerAlpha * 0.6f),
                        Color.Transparent
                    ),
                    center = Offset(x, y),
                    radius = 50f
                ),
                radius = 50f,
                center = Offset(x, y)
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawIceCrystal(
    center: Offset,
    size: Float,
    alpha: Float
) {
    val path = Path().apply {
        // Six-pointed star pattern for ice crystal
        for (i in 0..5) {
            val angle = (i * 60f) * Math.PI / 180f
            val outerX = center.x + (size * kotlin.math.cos(angle)).toFloat()
            val outerY = center.y + (size * kotlin.math.sin(angle)).toFloat()
            
            if (i == 0) {
                moveTo(center.x, center.y)
            }
            
            lineTo(outerX, outerY)
            moveTo(center.x, center.y)
        }
    }
    
    drawPath(
        path = path,
        color = SnowWhite.copy(alpha = alpha),
        style = Stroke(width = 1.5f)
    )
}

