package com.icefishi.trackesof.data

import android.content.Context
import android.content.SharedPreferences
import com.icefishi.trackesof.model.Statistics
import com.icefishi.trackesof.model.Task
import com.icefishi.trackesof.model.TaskSize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TaskRepository(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("ice_fish_prefs", Context.MODE_PRIVATE)
    
    private val _tasks = MutableStateFlow<List<Task>>(loadTasks())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()
    
    private val _statistics = MutableStateFlow(loadStatistics())
    val statistics: StateFlow<Statistics> = _statistics.asStateFlow()
    
    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean("first_launch", true)
    }
    
    fun setOnboardingComplete() {
        prefs.edit().putBoolean("first_launch", false).apply()
    }
    
    fun addTask(task: Task) {
        _tasks.value = _tasks.value + task
        saveTasks()
    }
    
    fun removeTask(taskId: String) {
        _tasks.value = _tasks.value.filterNot { it.id == taskId }
        saveTasks()
    }
    
    fun updateStatistics(statistics: Statistics) {
        _statistics.value = statistics
        saveStatistics()
    }
    
    private fun saveTasks() {
        val tasksJson = _tasks.value.joinToString("|||") { task ->
            "${task.id}::${task.title}::${task.size.name}::${task.createdAt}"
        }
        prefs.edit().putString("tasks", tasksJson).apply()
    }
    
    private fun loadTasks(): List<Task> {
        val tasksJson = prefs.getString("tasks", "") ?: ""
        if (tasksJson.isBlank()) return emptyList()
        
        return tasksJson.split("|||").mapNotNull { taskString ->
            try {
                val parts = taskString.split("::")
                if (parts.size == 4) {
                    Task(
                        id = parts[0],
                        title = parts[1],
                        size = TaskSize.valueOf(parts[2]),
                        createdAt = parts[3].toLong()
                    )
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }
    
    private fun saveStatistics() {
        val stats = _statistics.value
        prefs.edit().apply {
            putInt("todayCaught", stats.todayCaught)
            putInt("weekBest", stats.weekBest)
            putInt("currentStreak", stats.currentStreak)
            putInt("totalCaught", stats.totalCaught)
            putLong("lastCatchDate", stats.lastCatchDate ?: 0L)
            apply()
        }
    }
    
    private fun loadStatistics(): Statistics {
        return Statistics(
            todayCaught = prefs.getInt("todayCaught", 0),
            weekBest = prefs.getInt("weekBest", 0),
            currentStreak = prefs.getInt("currentStreak", 0),
            totalCaught = prefs.getInt("totalCaught", 0),
            lastCatchDate = prefs.getLong("lastCatchDate", 0L).takeIf { it > 0 }
        )
    }
}

