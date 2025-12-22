package com.icefishi.trackesof.hutrjk

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.core.view.WindowCompat

class IceFishTrackerSystemBarsController(private val activity: Activity) {

    private val iceFishTrackerWindow = activity.window
    private val iceFishTrackerDecorView = iceFishTrackerWindow.decorView

    fun iceFishTrackerSetupSystemBars() {
        val isLandscape = activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            iceFishTrackerSetupForApi30Plus(isLandscape)
        } else {
            iceFishTrackerSetupForApi24To29(isLandscape)
        }
    }

    private fun iceFishTrackerSetupForApi30Plus(isLandscape: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(iceFishTrackerWindow, false)

            val insetsController = iceFishTrackerWindow.insetsController ?: return

            insetsController.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            if (isLandscape) {
                insetsController.hide(
                    WindowInsets.Type.navigationBars() or WindowInsets.Type.statusBars()
                )
            } else {
                insetsController.hide(WindowInsets.Type.navigationBars())
                insetsController.show(WindowInsets.Type.statusBars())
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun iceFishTrackerSetupForApi24To29(isLandscape: Boolean) {
        var flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        if (isLandscape) {
            flags = flags or (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        } else {
            iceFishTrackerWindow.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        iceFishTrackerDecorView.systemUiVisibility = flags

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            iceFishTrackerWindow.navigationBarColor = android.graphics.Color.TRANSPARENT
            // Светлые иконки навигации (если нужно)
            iceFishTrackerDecorView.systemUiVisibility = flags and
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
    }


}

fun Activity.iceFishTrackerSetupSystemBars() {
    IceFishTrackerSystemBarsController(this).iceFishTrackerSetupSystemBars()
}
