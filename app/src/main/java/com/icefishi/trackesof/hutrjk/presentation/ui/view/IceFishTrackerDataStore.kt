package com.icefishi.trackesof.hutrjk.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class IceFishTrackerDataStore : ViewModel(){
    val iceFishTrackerViList: MutableList<IceFishTrackerVi> = mutableListOf()
    var iceFishTrackerIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var iceFishTrackerContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var iceFishTrackerView: IceFishTrackerVi

}