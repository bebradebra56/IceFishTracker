package com.icefishi.trackesof.hutrjk.domain.model

import com.google.gson.annotations.SerializedName


data class IceFishTrackerEntity (
    @SerializedName("ok")
    val iceFishTrackerOk: String,
    @SerializedName("url")
    val iceFishTrackerUrl: String,
    @SerializedName("expires")
    val iceFishTrackerExpires: Long,
)