package com.icefishi.trackesof.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.icefishi.trackesof.model.Task
import com.icefishi.trackesof.ui.theme.*

@Composable
fun TaskActionDialog(
    task: Task,
    onPull: () -> Unit,
    onRelease: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = FrostedWhite
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Animated fish preview
                AnimatedFishPreview(task = task)
                
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = DeepBlue,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "What would you like to do?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkWater.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = onPull,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SuccessGreen,
                        contentColor = SnowWhite
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "🎣 Pull (Complete)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                OutlinedButton(
                    onClick = onRelease,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = DeleteRed
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "↓ Release (Delete)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = DarkWater)
                }
            }
        }
    }
}

@Composable
private fun AnimatedFishPreview(task: Task) {
    val infiniteTransition = rememberInfiniteTransition(label = "fish_preview")
    
    val fishOffset by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fish_preview_offset"
    )
    
    Canvas(modifier = Modifier.size(120.dp)) {
        val centerX = size.width / 2 + fishOffset
        val centerY = size.height / 2
        val fishSize = task.size.sizeMultiplier * 40f
        
        val fishColor = when (task.size) {
            com.icefishi.trackesof.model.TaskSize.SMALL -> FishYellow
            com.icefishi.trackesof.model.TaskSize.MEDIUM -> FishOrange
            com.icefishi.trackesof.model.TaskSize.LARGE -> FishPink
        }
        
        // Glow effect
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    fishColor.copy(alpha = 0.3f),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = fishSize * 2f
            ),
            radius = fishSize * 2f,
            center = Offset(centerX, centerY)
        )
        
        // Fish body
        val bodyPath = Path().apply {
            moveTo(centerX - fishSize, centerY)
            cubicTo(
                centerX - fishSize, centerY - fishSize * 0.6f,
                centerX + fishSize * 0.5f, centerY - fishSize * 0.6f,
                centerX + fishSize, centerY
            )
            cubicTo(
                centerX + fishSize * 0.5f, centerY + fishSize * 0.6f,
                centerX - fishSize, centerY + fishSize * 0.6f,
                centerX - fishSize, centerY
            )
            close()
        }
        
        drawPath(
            path = bodyPath,
            color = fishColor,
            style = Fill
        )
        
        // Fish tail
        val tailPath = Path().apply {
            moveTo(centerX - fishSize, centerY)
            lineTo(centerX - fishSize * 1.5f, centerY - fishSize * 0.5f)
            lineTo(centerX - fishSize * 1.5f, centerY + fishSize * 0.5f)
            close()
        }
        
        drawPath(
            path = tailPath,
            color = fishColor.copy(alpha = 0.8f),
            style = Fill
        )
        
        // Fish eye
        drawCircle(
            color = Color.White,
            radius = fishSize * 0.2f,
            center = Offset(centerX + fishSize * 0.4f, centerY - fishSize * 0.2f)
        )
        
        drawCircle(
            color = Color.Black,
            radius = fishSize * 0.1f,
            center = Offset(centerX + fishSize * 0.4f, centerY - fishSize * 0.2f)
        )
    }
}

