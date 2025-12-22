package com.icefishi.trackesof.hutrjk.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.icefishi.trackesof.hutrjk.presentation.app.IceFishTrackerApplication

class IceFishTrackerPushHandler {
    fun iceFishTrackerHandlePush(extras: Bundle?) {
        Log.d(IceFishTrackerApplication.ICE_FISH_TRACKER_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map = iceFishTrackerBundleToMap(extras)
            Log.d(IceFishTrackerApplication.ICE_FISH_TRACKER_MAIN_TAG, "Map from Push = $map")
            map?.let {
                if (map.containsKey("url")) {
                    IceFishTrackerApplication.ICE_FISH_TRACKER_FB_LI = map["url"]
                    Log.d(IceFishTrackerApplication.ICE_FISH_TRACKER_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(IceFishTrackerApplication.ICE_FISH_TRACKER_MAIN_TAG, "Push data no!")
        }
    }

    private fun iceFishTrackerBundleToMap(extras: Bundle): Map<String, String?>? {
        val map: MutableMap<String, String?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = extras.getString(key)
        }
        return map
    }

}