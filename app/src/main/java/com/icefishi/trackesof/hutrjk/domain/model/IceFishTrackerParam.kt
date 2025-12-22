package com.icefishi.trackesof.hutrjk.domain.model

import com.google.gson.annotations.SerializedName


private const val ICE_FISH_TRACKER_A = "com.icefishi.trackesof"
private const val ICE_FISH_TRACKER_B = "icefishtracker"
data class IceFishTrackerParam (
    @SerializedName("af_id")
    val iceFishTrackerAfId: String,
    @SerializedName("bundle_id")
    val iceFishTrackerBundleId: String = ICE_FISH_TRACKER_A,
    @SerializedName("os")
    val iceFishTrackerOs: String = "Android",
    @SerializedName("store_id")
    val iceFishTrackerStoreId: String = ICE_FISH_TRACKER_A,
    @SerializedName("locale")
    val iceFishTrackerLocale: String,
    @SerializedName("push_token")
    val iceFishTrackerPushToken: String,
    @SerializedName("firebase_project_id")
    val iceFishTrackerFirebaseProjectId: String = ICE_FISH_TRACKER_B,

    )