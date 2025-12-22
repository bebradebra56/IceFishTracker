package com.icefishi.trackesof.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.icefishi.trackesof.model.TaskSize
import com.icefishi.trackesof.ui.theme.*

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onAdd: (String, TaskSize) -> Unit
) {
    var taskTitle by remember { mutableStateOf("") }
    var selectedSize by remember { mutableStateOf(TaskSize.MEDIUM) }
    
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Cast a New Line",
                    style = MaterialTheme.typography.headlineSmall,
                    color = DeepBlue,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Add a task to catch",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkWater.copy(alpha = 0.7f)
                )
                
                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("Task name") },
                    placeholder = { Text("What needs to be done?") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        focusedLabelColor = AccentBlue,
                        cursorColor = AccentBlue
                    )
                )
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Fish Size (Task Complexity)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DeepBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TaskSize.entries.forEach { size ->
                            SizeOption(
                                size = size,
                                isSelected = size == selectedSize,
                                onClick = { selectedSize = size },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = DarkWater
                        )
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = {
                            if (taskTitle.isNotBlank()) {
                                onAdd(taskTitle, selectedSize)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = taskTitle.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentBlue,
                            contentColor = SnowWhite
                        )
                    ) {
                        Text("Add Task")
                    }
                }
            }
        }
    }
}

@Composable
private fun SizeOption(
    size: TaskSize,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) AccentBlue else FrostedWhite
    val textColor = if (isSelected) SnowWhite else DeepBlue
    val borderColor = if (isSelected) AccentBlue else IceShadow
    
    Box(
        modifier = modifier
            .height(70.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = when (size) {
                    TaskSize.SMALL -> "🐟"
                    TaskSize.MEDIUM -> "🐠"
                    TaskSize.LARGE -> "🐋"
                },
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = size.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

