package com.icefishi.trackesof.hutrjk.data.utils

import android.util.Log
import com.icefishi.trackesof.hutrjk.presentation.app.IceFishTrackerApplication
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class IceFishTrackerPushToken {

    suspend fun iceFishTrackerGetToken(
        iceFishTrackerMaxAttempts: Int = 3,
        iceFishTrackerDelayMs: Long = 1500
    ): String {

        repeat(iceFishTrackerMaxAttempts - 1) {
            try {
                val iceFishTrackerToken = FirebaseMessaging.getInstance().token.await()
                return iceFishTrackerToken
            } catch (e: Exception) {
                Log.e(IceFishTrackerApplication.ICE_FISH_TRACKER_MAIN_TAG, "Token error (attempt ${it + 1}): ${e.message}")
                delay(iceFishTrackerDelayMs)
            }
        }

        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(IceFishTrackerApplication.ICE_FISH_TRACKER_MAIN_TAG, "Token error final: ${e.message}")
            "null"
        }
    }


}