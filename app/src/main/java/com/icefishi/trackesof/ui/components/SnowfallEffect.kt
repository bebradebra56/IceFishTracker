package com.icefishi.trackesof.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlin.random.Random

data class Snowflake(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val alpha: Float
)

@Composable
fun SnowfallEffect(modifier: Modifier = Modifier) {
    var snowflakes by remember { mutableStateOf(List(30) { generateSnowflake() }) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(50)
            snowflakes = snowflakes.map { flake ->
                val newY = flake.y + flake.speed
                if (newY > 1.2f) {
                    generateSnowflake()
                } else {
                    flake.copy(y = newY, x = flake.x + (Random.nextFloat() - 0.5f) * 0.002f)
                }
            }
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        snowflakes.forEach { flake ->
            drawCircle(
                color = Color.White.copy(alpha = flake.alpha),
                radius = flake.size * size.minDimension,
                center = Offset(
                    x = flake.x * size.width,
                    y = flake.y * size.height
                )
            )
        }
    }
}

private fun generateSnowflake(): Snowflake {
    return Snowflake(
        x = Random.nextFloat(),
        y = Random.nextFloat() * -0.2f,
        size = Random.nextFloat() * 0.003f + 0.001f,
        speed = Random.nextFloat() * 3f + 1f,
        alpha = Random.nextFloat() * 0.5f + 0.3f
    )
}

