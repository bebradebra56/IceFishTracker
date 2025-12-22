package com.icefishi.trackesof

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.icefishi.trackesof.hutrjk.IceFishTrackerGlobalLayoutUtil
import com.icefishi.trackesof.hutrjk.iceFishTrackerSetupSystemBars
import com.icefishi.trackesof.hutrjk.presentation.app.IceFishTrackerApplication
import com.icefishi.trackesof.hutrjk.presentation.pushhandler.IceFishTrackerPushHandler
import org.koin.android.ext.android.inject

class IceFishTrackerActivity : AppCompatActivity() {
    private val iceFishTrackerPushHandler by inject<IceFishTrackerPushHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        iceFishTrackerSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_ice_fish_tracker)

        val iceFishTrackerRootView = findViewById<View>(android.R.id.content)
        IceFishTrackerGlobalLayoutUtil().iceFishTrackerAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(iceFishTrackerRootView) { iceFishTrackerView, iceFishTrackerInsets ->
            val iceFishTrackerSystemBars = iceFishTrackerInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val iceFishTrackerDisplayCutout = iceFishTrackerInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val iceFishTrackerIme = iceFishTrackerInsets.getInsets(WindowInsetsCompat.Type.ime())


            val iceFishTrackerTopPadding = maxOf(iceFishTrackerSystemBars.top, iceFishTrackerDisplayCutout.top)
            val iceFishTrackerLeftPadding = maxOf(iceFishTrackerSystemBars.left, iceFishTrackerDisplayCutout.left)
            val iceFishTrackerRightPadding = maxOf(iceFishTrackerSystemBars.right, iceFishTrackerDisplayCutout.right)
            window.setSoftInputMode(IceFishTrackerApplication.iceFishTrackerInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(IceFishTrackerApplication.ICE_FISH_TRACKER_MAIN_TAG, "ADJUST PUN")
                val iceFishTrackerBottomInset = maxOf(iceFishTrackerSystemBars.bottom, iceFishTrackerDisplayCutout.bottom)

                iceFishTrackerView.setPadding(iceFishTrackerLeftPadding, iceFishTrackerTopPadding, iceFishTrackerRightPadding, 0)

                iceFishTrackerView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = iceFishTrackerBottomInset
                }
            } else {
                Log.d(IceFishTrackerApplication.ICE_FISH_TRACKER_MAIN_TAG, "ADJUST RESIZE")

                val iceFishTrackerBottomInset = maxOf(iceFishTrackerSystemBars.bottom, iceFishTrackerDisplayCutout.bottom, iceFishTrackerIme.bottom)

                iceFishTrackerView.setPadding(iceFishTrackerLeftPadding, iceFishTrackerTopPadding, iceFishTrackerRightPadding, 0)

                iceFishTrackerView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = iceFishTrackerBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(IceFishTrackerApplication.ICE_FISH_TRACKER_MAIN_TAG, "Activity onCreate()")
        iceFishTrackerPushHandler.iceFishTrackerHandlePush(intent.extras)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            iceFishTrackerSetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        iceFishTrackerSetupSystemBars()
    }
}