package com.icefishi.trackesof.hutrjk

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.icefishi.trackesof.hutrjk.presentation.app.IceFishTrackerApplication

class IceFishTrackerGlobalLayoutUtil {

    private var iceFishTrackerMChildOfContent: View? = null
    private var iceFishTrackerUsableHeightPrevious = 0

    fun iceFishTrackerAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        iceFishTrackerMChildOfContent = content.getChildAt(0)

        iceFishTrackerMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val iceFishTrackerUsableHeightNow = iceFishTrackerComputeUsableHeight()
        if (iceFishTrackerUsableHeightNow != iceFishTrackerUsableHeightPrevious) {
            val iceFishTrackerUsableHeightSansKeyboard = iceFishTrackerMChildOfContent?.rootView?.height ?: 0
            val iceFishTrackerHeightDifference = iceFishTrackerUsableHeightSansKeyboard - iceFishTrackerUsableHeightNow

            if (iceFishTrackerHeightDifference > (iceFishTrackerUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(IceFishTrackerApplication.iceFishTrackerInputMode)
            } else {
                activity.window.setSoftInputMode(IceFishTrackerApplication.iceFishTrackerInputMode)
            }
//            mChildOfContent?.requestLayout()
            iceFishTrackerUsableHeightPrevious = iceFishTrackerUsableHeightNow
        }
    }

    private fun iceFishTrackerComputeUsableHeight(): Int {
        val r = Rect()
        iceFishTrackerMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}