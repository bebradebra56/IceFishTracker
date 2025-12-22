package com.icefishi.trackesof.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.icefishi.trackesof.data.TaskRepository
import com.icefishi.trackesof.model.Statistics
import com.icefishi.trackesof.model.Task
import com.icefishi.trackesof.model.TaskSize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = TaskRepository(application)
    
    val tasks: StateFlow<List<Task>> = repository.tasks
    val statistics: StateFlow<Statistics> = repository.statistics
    
    private val _showAddTaskDialog = MutableStateFlow(false)
    val showAddTaskDialog: StateFlow<Boolean> = _showAddTaskDialog.asStateFlow()
    
    private val _showStatistics = MutableStateFlow(false)
    val showStatistics: StateFlow<Boolean> = _showStatistics.asStateFlow()
    
    private val _selectedTask = MutableStateFlow<Task?>(null)
    val selectedTask: StateFlow<Task?> = _selectedTask.asStateFlow()
    
    private val _showOnboarding = MutableStateFlow(repository.isFirstLaunch())
    val showOnboarding: StateFlow<Boolean> = _showOnboarding.asStateFlow()
    
    fun addTask(title: String, size: TaskSize) {
        if (title.isBlank()) return
        
        val newTask = Task(
            title = title.trim(),
            size = size
        )
        
        repository.addTask(newTask)
        hideAddTaskDialog()
    }
    
    fun pullTask(task: Task) {
        repository.removeTask(task.id)
        updateStatisticsOnCatch()
        _selectedTask.value = null
    }
    
    fun releaseTask(task: Task) {
        repository.removeTask(task.id)
        _selectedTask.value = null
    }
    
    fun selectTask(task: Task?) {
        _selectedTask.value = task
    }
    
    fun showAddTaskDialog() {
        _showAddTaskDialog.value = true
    }
    
    fun hideAddTaskDialog() {
        _showAddTaskDialog.value = false
    }
    
    fun toggleStatistics() {
        _showStatistics.value = !_showStatistics.value
    }
    
    fun completeOnboarding() {
        repository.setOnboardingComplete()
        _showOnboarding.value = false
    }
    
    private fun updateStatisticsOnCatch() {
        val current = statistics.value
        val today = getTodayStart()
        val isToday = current.lastCatchDate?.let { it >= today } ?: false
        
        val newTodayCaught = if (isToday) current.todayCaught + 1 else 1
        val newTotalCaught = current.totalCaught + 1
        
        // Calculate streak
        val yesterday = today - 24 * 60 * 60 * 1000
        val newStreak = when {
            current.lastCatchDate == null -> 1
            current.lastCatchDate >= today -> current.currentStreak
            current.lastCatchDate >= yesterday -> current.currentStreak + 1
            else -> 1
        }
        
        val newWeekBest = maxOf(current.weekBest, newTodayCaught)
        
        val updatedStats = current.copy(
            todayCaught = newTodayCaught,
            weekBest = newWeekBest,
            currentStreak = newStreak,
            totalCaught = newTotalCaught,
            lastCatchDate = System.currentTimeMillis()
        )
        
        repository.updateStatistics(updatedStats)
    }
    
    private fun getTodayStart(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}

