package com.icefishi.trackesof.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.icefishi.trackesof.ui.theme.*

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    Scaffold {paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            LightIceBlue,
                            IceBlue,
                            FrostedWhite
                        )
                    )
                )
        ) {
            // Skip button
            TextButton(
                onClick = onComplete,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Skip →",
                    color = DeepBlue,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Animated welcome fish
                    WelcomeFishAnimation()

                    Text(
                        text = "Welcome to\nIce Fish Tracker!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = DeepBlue,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Turn your tasks into fish and catch them!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DarkWater.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OnboardingItem(
                        emoji = "➕",
                        title = "Add Tasks",
                        description = "Create tasks that become fish under the ice"
                    )

                    OnboardingItem(
                        emoji = "🎣",
                        title = "Pull to Complete",
                        description = "Catch your fish when tasks are done"
                    )

                    OnboardingItem(
                        emoji = "📊",
                        title = "Track Progress",
                        description = "See your daily catch and streaks"
                    )

                    OnboardingItem(
                        emoji = "🐟🐠🐋",
                        title = "Choose Size",
                        description = "Small, Medium, or Large for complexity"
                    )
                }

                Button(
                    onClick = onComplete,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlue,
                        contentColor = SnowWhite
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Start Fishing! 🎣",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingItem(
    emoji: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = FrostedWhite.copy(alpha = 0.8f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.headlineSmall
        )
        
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = DeepBlue,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = DarkWater.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun WelcomeFishAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "welcome_fish")
    
    val fishOffset by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fish_swim"
    )
    
    Canvas(modifier = Modifier.size(100.dp)) {
        val centerX = size.width / 2 + fishOffset
        val centerY = size.height / 2
        val fishSize = 40f
        
        // Glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    AccentBlue.copy(alpha = 0.3f),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = fishSize * 2
            ),
            radius = fishSize * 2,
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
            color = FishOrange,
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
            color = FishOrange.copy(alpha = 0.8f),
            style = Fill
        )
        
        // Eye
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

