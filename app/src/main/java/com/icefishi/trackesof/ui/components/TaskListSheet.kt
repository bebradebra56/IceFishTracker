package com.icefishi.trackesof.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.icefishi.trackesof.model.Task
import com.icefishi.trackesof.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListSheet(
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = FrostedWhite,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "All Tasks (${tasks.size})",
                style = MaterialTheme.typography.headlineSmall,
                color = DeepBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tasks yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DarkWater.copy(alpha = 0.5f)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskListItem(
                            task = task,
                            onClick = { onTaskClick(task) }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TaskListItem(
    task: Task,
    onClick: () -> Unit
) {
    val fishIcon = when (task.size) {
        com.icefishi.trackesof.model.TaskSize.SMALL -> "🐟"
        com.icefishi.trackesof.model.TaskSize.MEDIUM -> "🐠"
        com.icefishi.trackesof.model.TaskSize.LARGE -> "🐋"
    }
    
    val backgroundColor = when (task.size) {
        com.icefishi.trackesof.model.TaskSize.SMALL -> FishYellow.copy(alpha = 0.1f)
        com.icefishi.trackesof.model.TaskSize.MEDIUM -> FishOrange.copy(alpha = 0.1f)
        com.icefishi.trackesof.model.TaskSize.LARGE -> FishPink.copy(alpha = 0.1f)
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = fishIcon,
            style = MaterialTheme.typography.headlineMedium
        )
        
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                color = DeepBlue,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = "Added: ${formatTime(task.createdAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = DarkWater.copy(alpha = 0.6f)
            )
        }
        
        Text(
            text = task.size.displayName,
            style = MaterialTheme.typography.labelMedium,
            color = DarkWater,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(IceShadow.copy(alpha = 0.3f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private fun formatTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000}m ago"
        diff < 86400_000 -> "${diff / 3600_000}h ago"
        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(timestamp))
    }
}

