package com.icefishi.trackesof.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.icefishi.trackesof.model.Task
import com.icefishi.trackesof.ui.theme.*
import kotlin.math.sin

@Composable
fun IceHoleWithFish(
    task: Task,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val infiniteTransition = rememberInfiniteTransition(label = "fish_swim")
    
    val fishOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fish_offset"
    )
    
    val fishRotation by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fish_rotation"
    )
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    
    val baseSize = when (task.size) {
        com.icefishi.trackesof.model.TaskSize.SMALL -> 110.dp
        com.icefishi.trackesof.model.TaskSize.MEDIUM -> 140.dp
        com.icefishi.trackesof.model.TaskSize.LARGE -> 180.dp
    }
    
    Box(
        modifier = modifier
            .size(baseSize)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    isPressed = true
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        // Ice hole with fish
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val holeRadius = size.minDimension * 0.35f
            
            // Glow effect
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        IceShadow.copy(alpha = glowAlpha),
                        Color.Transparent
                    ),
                    center = Offset(centerX, centerY),
                    radius = holeRadius * 1.5f
                ),
                radius = holeRadius * 1.5f,
                center = Offset(centerX, centerY)
            )
            
            // Ice hole
            drawCircle(
                color = LightIceBlue,
                radius = holeRadius,
                center = Offset(centerX, centerY)
            )
            
            // Water inside hole
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        WaterBlue.copy(alpha = 0.8f),
                        DarkWater.copy(alpha = 0.6f)
                    ),
                    center = Offset(centerX, centerY),
                    radius = holeRadius * 0.9f
                ),
                radius = holeRadius * 0.9f,
                center = Offset(centerX, centerY)
            )
            
            // Ice hole border (white rim)
            drawCircle(
                color = SnowWhite,
                radius = holeRadius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 4.dp.toPx())
            )
            
            // Fish shadow under ice
            val fishSize = task.size.sizeMultiplier * 30f
            val fishX = centerX + fishOffset
            val fishY = centerY + 10f
            
            drawFish(
                center = Offset(fishX, fishY),
                size = fishSize,
                rotation = fishRotation,
                task = task
            )
        }
        
        // Task title below hole
        Text(
            text = task.title,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 11.sp,
            color = DeepBlue,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 8.dp)
                .offset(y = 20.dp)
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFish(
    center: Offset,
    size: Float,
    rotation: Float,
    task: Task
) {
    // Generate color based on task ID for variety
    val colorIndex = task.id.hashCode() % 5
    val fishColor = when (task.size) {
        com.icefishi.trackesof.model.TaskSize.SMALL -> when (colorIndex) {
            0 -> FishYellow
            1 -> FishGreen
            2 -> IceShadow
            3 -> FishOrange.copy(alpha = 0.8f)
            else -> FishYellow
        }
        com.icefishi.trackesof.model.TaskSize.MEDIUM -> when (colorIndex) {
            0 -> FishOrange
            1 -> FishPink.copy(alpha = 0.8f)
            2 -> FishPurple.copy(alpha = 0.8f)
            3 -> FishGreen
            else -> FishOrange
        }
        com.icefishi.trackesof.model.TaskSize.LARGE -> when (colorIndex) {
            0 -> FishPink
            1 -> FishPurple
            2 -> FishOrange
            3 -> FishGreen.copy(alpha = 0.9f)
            else -> FishPink
        }
    }
    
    // Fish body (simple oval shape)
    val bodyPath = Path().apply {
        // Simple fish shape using path
        moveTo(center.x - size, center.y)
        cubicTo(
            center.x - size, center.y - size * 0.6f,
            center.x + size * 0.5f, center.y - size * 0.6f,
            center.x + size, center.y
        )
        cubicTo(
            center.x + size * 0.5f, center.y + size * 0.6f,
            center.x - size, center.y + size * 0.6f,
            center.x - size, center.y
        )
        close()
    }
    
    drawPath(
        path = bodyPath,
        color = fishColor.copy(alpha = 0.7f),
        style = Fill
    )
    
    // Fish tail
    val tailPath = Path().apply {
        moveTo(center.x - size, center.y)
        lineTo(center.x - size * 1.5f, center.y - size * 0.5f)
        lineTo(center.x - size * 1.5f, center.y + size * 0.5f)
        close()
    }
    
    drawPath(
        path = tailPath,
        color = fishColor.copy(alpha = 0.6f),
        style = Fill
    )
    
    // Fish eye
    drawCircle(
        color = Color.White,
        radius = size * 0.15f,
        center = Offset(center.x + size * 0.4f, center.y - size * 0.2f)
    )
    
    drawCircle(
        color = Color.Black,
        radius = size * 0.08f,
        center = Offset(center.x + size * 0.4f, center.y - size * 0.2f)
    )
}

