package com.icefishi.trackesof.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.icefishi.trackesof.model.Statistics
import com.icefishi.trackesof.ui.theme.*

@Composable
fun StatisticsDialog(
    statistics: Statistics,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = FrostedWhite
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "🎣 Statistics",
                    style = MaterialTheme.typography.headlineSmall,
                    color = DeepBlue,
                    fontWeight = FontWeight.Bold
                )
                
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = IceShadow,
                    thickness = 1.dp
                )
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatisticItem(
                        icon = "📅",
                        label = "Today",
                        value = statistics.todayCaught.toString(),
                        color = AccentBlue
                    )
                    
                    StatisticItem(
                        icon = "🏆",
                        label = "Week Best",
                        value = statistics.weekBest.toString(),
                        color = FishOrange
                    )
                    
                    StatisticItem(
                        icon = "🔥",
                        label = "Streak",
                        value = "${statistics.currentStreak}d",
                        color = FishPink
                    )
                    
                    StatisticItem(
                        icon = "🎯",
                        label = "Total",
                        value = statistics.totalCaught.toString(),
                        color = SuccessGreen
                    )
                }
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlue,
                        contentColor = SnowWhite
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Close", style = MaterialTheme.typography.titleSmall)
                }
            }
        }
    }
}

@Composable
private fun StatisticItem(
    icon: String,
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = DeepBlue,
                fontWeight = FontWeight.Medium
            )
        }
        
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

