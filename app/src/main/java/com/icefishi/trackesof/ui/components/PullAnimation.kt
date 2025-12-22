package com.icefishi.trackesof.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp
import com.icefishi.trackesof.model.Task
import com.icefishi.trackesof.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun PullAnimationEffect(
    task: Task,
    onAnimationComplete: () -> Unit
) {
    var animationProgress by remember { mutableStateOf(0f) }
    
    LaunchedEffect(Unit) {
        // Animate from 0 to 1 over 1 second
        val startTime = System.currentTimeMillis()
        val duration = 1000L
        
        while (animationProgress < 1f) {
            val elapsed = System.currentTimeMillis() - startTime
            animationProgress = (elapsed.toFloat() / duration).coerceAtMost(1f)
            delay(16)
        }
        
        delay(500) // Show completed state
        onAnimationComplete()
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(200.dp)) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            
            // Water splash circles
            val splashRadius = animationProgress * 100f
            for (i in 0..2) {
                val radius = splashRadius + i * 20f
                val alpha = (1f - animationProgress) * 0.5f
                
                drawCircle(
                    color = WaterBlue.copy(alpha = alpha),
                    radius = radius,
                    center = Offset(centerX, centerY),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                )
            }
            
            // Fish jumping up
            val fishY = centerY - (animationProgress * 200f)
            val fishSize = task.size.sizeMultiplier * 50f
            
            val fishColor = when (task.size) {
                com.icefishi.trackesof.model.TaskSize.SMALL -> FishYellow
                com.icefishi.trackesof.model.TaskSize.MEDIUM -> FishOrange
                com.icefishi.trackesof.model.TaskSize.LARGE -> FishPink
            }
            
            // Fish glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        fishColor.copy(alpha = 0.6f),
                        Color.Transparent
                    ),
                    center = Offset(centerX, fishY),
                    radius = fishSize * 2
                ),
                radius = fishSize * 2,
                center = Offset(centerX, fishY)
            )
            
            // Fish body
            val bodyPath = Path().apply {
                moveTo(centerX - fishSize, fishY)
                cubicTo(
                    centerX - fishSize, fishY - fishSize * 0.6f,
                    centerX + fishSize * 0.5f, fishY - fishSize * 0.6f,
                    centerX + fishSize, fishY
                )
                cubicTo(
                    centerX + fishSize * 0.5f, fishY + fishSize * 0.6f,
                    centerX - fishSize, fishY + fishSize * 0.6f,
                    centerX - fishSize, fishY
                )
                close()
            }
            
            drawPath(
                path = bodyPath,
                color = fishColor,
                style = Fill
            )
            
            // Sparkles
            if (animationProgress > 0.5f) {
                for (i in 0..5) {
                    val angle = (i * 60f + animationProgress * 360f) * Math.PI / 180f
                    val sparkleRadius = 80f + animationProgress * 50f
                    val sparkleX = centerX + (sparkleRadius * kotlin.math.cos(angle)).toFloat()
                    val sparkleY = fishY + (sparkleRadius * kotlin.math.sin(angle)).toFloat()
                    
                    drawCircle(
                        color = FishYellow.copy(alpha = 1f - animationProgress),
                        radius = 4f,
                        center = Offset(sparkleX, sparkleY)
                    )
                }
            }
        }
    }
}

@Composable
fun ReleaseAnimationEffect(
    task: Task,
    onAnimationComplete: () -> Unit
) {
    var animationProgress by remember { mutableStateOf(0f) }
    
    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        val duration = 800L
        
        while (animationProgress < 1f) {
            val elapsed = System.currentTimeMillis() - startTime
            animationProgress = (elapsed.toFloat() / duration).coerceAtMost(1f)
            delay(16)
        }
        
        onAnimationComplete()
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(200.dp)) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            
            // Fish sinking down
            val fishY = centerY + (animationProgress * 200f)
            val fishSize = task.size.sizeMultiplier * 50f
            val fishAlpha = 1f - animationProgress
            
            val fishColor = when (task.size) {
                com.icefishi.trackesof.model.TaskSize.SMALL -> FishYellow
                com.icefishi.trackesof.model.TaskSize.MEDIUM -> FishOrange
                com.icefishi.trackesof.model.TaskSize.LARGE -> FishPink
            }
            
            // Fish body fading
            val bodyPath = Path().apply {
                moveTo(centerX - fishSize, fishY)
                cubicTo(
                    centerX - fishSize, fishY - fishSize * 0.6f,
                    centerX + fishSize * 0.5f, fishY - fishSize * 0.6f,
                    centerX + fishSize, fishY
                )
                cubicTo(
                    centerX + fishSize * 0.5f, fishY + fishSize * 0.6f,
                    centerX - fishSize, fishY + fishSize * 0.6f,
                    centerX - fishSize, fishY
                )
                close()
            }
            
            drawPath(
                path = bodyPath,
                color = fishColor.copy(alpha = fishAlpha),
                style = Fill
            )
            
            // Bubbles going up
            for (i in 0..3) {
                val bubbleY = fishY - (animationProgress * 150f) - i * 30f
                val bubbleAlpha = (1f - animationProgress * 0.5f).coerceAtLeast(0f)
                
                drawCircle(
                    color = WaterBlue.copy(alpha = bubbleAlpha * 0.6f),
                    radius = 5f + i * 2f,
                    center = Offset(centerX + i * 10f - 15f, bubbleY)
                )
            }
        }
    }
}

