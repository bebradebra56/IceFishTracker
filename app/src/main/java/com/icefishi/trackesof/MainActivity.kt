package com.icefishi.trackesof

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.icefishi.trackesof.ui.screens.MainScreen
import com.icefishi.trackesof.ui.theme.IceFishTrackerTheme
import com.icefishi.trackesof.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IceFishTrackerTheme {
                val viewModel: TaskViewModel = viewModel()
                MainScreen(viewModel = viewModel)
            }
        }
    }
}