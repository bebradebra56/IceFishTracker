package com.icefishi.trackesof.hutrjk.data.shar

import android.content.Context
import androidx.core.content.edit

class IceFishTrackerSharedPreference(context: Context) {
    private val iceFishTrackerPrefs = context.getSharedPreferences("iceFishTrackerSharedPrefsAb", Context.MODE_PRIVATE)

    var iceFishTrackerSavedUrl: String
        get() = iceFishTrackerPrefs.getString(ICE_FISH_TRACKER_SAVED_URL, "") ?: ""
        set(value) = iceFishTrackerPrefs.edit { putString(ICE_FISH_TRACKER_SAVED_URL, value) }

    var iceFishTrackerExpired : Long
        get() = iceFishTrackerPrefs.getLong(ICE_FISH_TRACKER_EXPIRED, 0L)
        set(value) = iceFishTrackerPrefs.edit { putLong(ICE_FISH_TRACKER_EXPIRED, value) }

    var iceFishTrackerAppState: Int
        get() = iceFishTrackerPrefs.getInt(ICE_FISH_TRACKER_APPLICATION_STATE, 0)
        set(value) = iceFishTrackerPrefs.edit { putInt(ICE_FISH_TRACKER_APPLICATION_STATE, value) }

    var iceFishTrackerNotificationRequest: Long
        get() = iceFishTrackerPrefs.getLong(ICE_FISH_TRACKER_NOTIFICAITON_REQUEST, 0L)
        set(value) = iceFishTrackerPrefs.edit { putLong(ICE_FISH_TRACKER_NOTIFICAITON_REQUEST, value) }

    var iceFishTrackerNotificationRequestedBefore: Boolean
        get() = iceFishTrackerPrefs.getBoolean(ICE_FISH_TRACKER_NOTIFICATION_REQUEST_BEFORE, false)
        set(value) = iceFishTrackerPrefs.edit { putBoolean(
            ICE_FISH_TRACKER_NOTIFICATION_REQUEST_BEFORE, value) }

    companion object {
        private const val ICE_FISH_TRACKER_SAVED_URL = "iceFishTrackerSavedUrl"
        private const val ICE_FISH_TRACKER_EXPIRED = "iceFishTrackerExpired"
        private const val ICE_FISH_TRACKER_APPLICATION_STATE = "iceFishTrackerApplicationState"
        private const val ICE_FISH_TRACKER_NOTIFICAITON_REQUEST = "iceFishTrackerNotificationRequest"
        private const val ICE_FISH_TRACKER_NOTIFICATION_REQUEST_BEFORE = "iceFishTrackerNotificationRequestedBefore"
    }
}