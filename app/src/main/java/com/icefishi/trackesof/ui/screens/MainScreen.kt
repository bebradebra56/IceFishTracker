package com.icefishi.trackesof.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.icefishi.trackesof.model.Task
import com.icefishi.trackesof.ui.components.*
import com.icefishi.trackesof.ui.theme.*
import com.icefishi.trackesof.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val statistics by viewModel.statistics.collectAsState()
    val showAddTaskDialog by viewModel.showAddTaskDialog.collectAsState()
    val showStatistics by viewModel.showStatistics.collectAsState()
    val selectedTask by viewModel.selectedTask.collectAsState()
    val showOnboarding by viewModel.showOnboarding.collectAsState()
    
    var animatingTask by remember { mutableStateOf<Pair<Task, String>?>(null) }
    var showTaskList by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Ice background with crystals
        IceBackground()
        
        // Snowfall effect
        SnowfallEffect()
        
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Ice Fish Tracker",
                                style = MaterialTheme.typography.headlineMedium,
                                color = DeepBlue,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Cast your tasks, pull success",
                                style = MaterialTheme.typography.bodySmall,
                                color = DarkWater.copy(alpha = 0.7f)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = FrostedWhite.copy(alpha = 0.9f)
                    ),
                    actions = {
                        // Task list button
                        if (tasks.isNotEmpty()) {
                            IconButton(onClick = { showTaskList = true }) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(FishOrange.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "📋",
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                }
                            }
                        }
                        
                        // Statistics button
                        IconButton(onClick = { viewModel.toggleStatistics() }) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(AccentBlue.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "📊",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                            }
                        }
                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://icefishtracker.com/privacy-policy.html"))
                            context.startActivity(intent)
                        }) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(AccentBlue.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "\uD83D\uDCDC",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                            }
                        }
                    }
                )
            },
            floatingActionButton = {
                Box {
                    // Pulse effect behind FAB when no tasks
                    if (tasks.isEmpty()) {
                        PulseEffect(
                            color = AccentBlue,
                            modifier = Modifier.matchParentSize()
                        )
                    }
                    
                    ExtendedFloatingActionButton(
                        onClick = { viewModel.showAddTaskDialog() },
                        containerColor = AccentBlue,
                        contentColor = SnowWhite,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "➕ Add Task",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (tasks.isEmpty()) {
                    EmptyState()
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 120.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(tasks, key = { it.id }) { task ->
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                AnimatedVisibility(
                                    visible = true,
                                    enter = scaleIn() + fadeIn(),
                                    exit = scaleOut() + fadeOut()
                                ) {
                                    IceHoleWithFish(
                                        task = task,
                                        onClick = { viewModel.selectTask(task) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Dialogs
        if (showAddTaskDialog) {
            AddTaskDialog(
                onDismiss = { viewModel.hideAddTaskDialog() },
                onAdd = { title, size ->
                    viewModel.addTask(title, size)
                }
            )
        }
        
        selectedTask?.let { task ->
            TaskActionDialog(
                task = task,
                onPull = {
                    animatingTask = task to "pull"
                    viewModel.selectTask(null)
                },
                onRelease = {
                    animatingTask = task to "release"
                    viewModel.selectTask(null)
                },
                onDismiss = { viewModel.selectTask(null) }
            )
        }
        
        animatingTask?.let { (task, action) ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (action) {
                    "pull" -> PullAnimationEffect(task = task) {
                        viewModel.pullTask(task)
                        animatingTask = null
                    }
                    "release" -> ReleaseAnimationEffect(task = task) {
                        viewModel.releaseTask(task)
                        animatingTask = null
                    }
                }
            }
        }
        
        if (showStatistics) {
            StatisticsDialog(
                statistics = statistics,
                onDismiss = { viewModel.toggleStatistics() }
            )
        }
        
        if (showTaskList) {
            TaskListSheet(
                tasks = tasks,
                onTaskClick = { task ->
                    showTaskList = false
                    viewModel.selectTask(task)
                },
                onDismiss = { showTaskList = false }
            )
        }
        
        if (showOnboarding) {
            OnboardingScreen(
                onComplete = { viewModel.completeOnboarding() }
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "empty_float")
        
        val floatOffset by infiniteTransition.animateFloat(
            initialValue = -10f,
            targetValue = 10f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "float"
        )
        
        val rotation by infiniteTransition.animateFloat(
            initialValue = -5f,
            targetValue = 5f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "rotation"
        )
        
        Box(
            modifier = Modifier
                .offset(y = floatOffset.dp)
                .graphicsLayer {
                    rotationZ = rotation
                }
        ) {
            Text(
                text = "❄️",
                style = MaterialTheme.typography.displayLarge
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "No Fish to Catch",
            style = MaterialTheme.typography.headlineMedium,
            color = DeepBlue,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "The ice is quiet...\nAdd your first task to start fishing!",
            style = MaterialTheme.typography.bodyLarge,
            color = DarkWater.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        val hookSwing by infiniteTransition.animateFloat(
            initialValue = -8f,
            targetValue = 8f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "hook_swing"
        )
        
        Box(
            modifier = Modifier.graphicsLayer {
                rotationZ = hookSwing
            }
        ) {
            Text(
                text = "🎣",
                style = MaterialTheme.typography.displayMedium
            )
        }
    }
}

