package com.icefishi.trackesof.hutrjk.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.icefishi.trackesof.hutrjk.data.shar.IceFishTrackerSharedPreference
import com.icefishi.trackesof.hutrjk.data.utils.IceFishTrackerSystemService
import com.icefishi.trackesof.hutrjk.domain.usecases.IceFishTrackerGetAllUseCase
import com.icefishi.trackesof.hutrjk.presentation.app.IceFishTrackerAppsFlyerState
import com.icefishi.trackesof.hutrjk.presentation.app.IceFishTrackerApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IceFishTrackerLoadViewModel(
    private val iceFishTrackerGetAllUseCase: IceFishTrackerGetAllUseCase,
    private val iceFishTrackerSharedPreference: IceFishTrackerSharedPreference,
    private val iceFishTrackerSystemService: IceFishTrackerSystemService
) : ViewModel() {

    private val _iceFishTrackerHomeScreenState: MutableStateFlow<IceFishTrackerHomeScreenState> =
        MutableStateFlow(IceFishTrackerHomeScreenState.IceFishTrackerLoading)
    val iceFishTrackerHomeScreenState = _iceFishTrackerHomeScreenState.asStateFlow()

    private var iceFishTrackerGetApps = false


    init {
        viewModelScope.launch {
            when (iceFishTrackerSharedPreference.iceFishTrackerAppState) {
                0 -> {
                    if (iceFishTrackerSystemService.iceFishTrackerIsOnline()) {
                        IceFishTrackerApplication.iceFishTrackerConversionFlow.collect {
                            when(it) {
                                IceFishTrackerAppsFlyerState.IceFishTrackerDefault -> {}
                                IceFishTrackerAppsFlyerState.IceFishTrackerError -> {
                                    iceFishTrackerSharedPreference.iceFishTrackerAppState = 2
                                    _iceFishTrackerHomeScreenState.value =
                                        IceFishTrackerHomeScreenState.IceFishTrackerError
                                    iceFishTrackerGetApps = true
                                }
                                is IceFishTrackerAppsFlyerState.IceFishTrackerSuccess -> {
                                    if (!iceFishTrackerGetApps) {
                                        iceFishTrackerGetData(it.iceFishTrackerData)
                                        iceFishTrackerGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _iceFishTrackerHomeScreenState.value =
                            IceFishTrackerHomeScreenState.IceFishTrackerNotInternet
                    }
                }
                1 -> {
                    if (iceFishTrackerSystemService.iceFishTrackerIsOnline()) {
                        if (IceFishTrackerApplication.ICE_FISH_TRACKER_FB_LI != null) {
                            _iceFishTrackerHomeScreenState.value =
                                IceFishTrackerHomeScreenState.IceFishTrackerSuccess(
                                    IceFishTrackerApplication.ICE_FISH_TRACKER_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > iceFishTrackerSharedPreference.iceFishTrackerExpired) {
                            Log.d(IceFishTrackerApplication.ICE_FISH_TRACKER_MAIN_TAG, "Current time more then expired, repeat request")
                            IceFishTrackerApplication.iceFishTrackerConversionFlow.collect {
                                when(it) {
                                    IceFishTrackerAppsFlyerState.IceFishTrackerDefault -> {}
                                    IceFishTrackerAppsFlyerState.IceFishTrackerError -> {
                                        _iceFishTrackerHomeScreenState.value =
                                            IceFishTrackerHomeScreenState.IceFishTrackerSuccess(
                                                iceFishTrackerSharedPreference.iceFishTrackerSavedUrl
                                            )
                                        iceFishTrackerGetApps = true
                                    }
                                    is IceFishTrackerAppsFlyerState.IceFishTrackerSuccess -> {
                                        if (!iceFishTrackerGetApps) {
                                            iceFishTrackerGetData(it.iceFishTrackerData)
                                            iceFishTrackerGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(IceFishTrackerApplication.ICE_FISH_TRACKER_MAIN_TAG, "Current time less then expired, use saved url")
                            _iceFishTrackerHomeScreenState.value =
                                IceFishTrackerHomeScreenState.IceFishTrackerSuccess(
                                    iceFishTrackerSharedPreference.iceFishTrackerSavedUrl
                                )
                        }
                    } else {
                        _iceFishTrackerHomeScreenState.value =
                            IceFishTrackerHomeScreenState.IceFishTrackerNotInternet
                    }
                }
                2 -> {
                    _iceFishTrackerHomeScreenState.value =
                        IceFishTrackerHomeScreenState.IceFishTrackerError
                }
            }
        }
    }


    private suspend fun iceFishTrackerGetData(conversation: MutableMap<String, Any>?) {
        val iceFishTrackerData = iceFishTrackerGetAllUseCase.invoke(conversation)
        if (iceFishTrackerSharedPreference.iceFishTrackerAppState == 0) {
            if (iceFishTrackerData == null) {
                iceFishTrackerSharedPreference.iceFishTrackerAppState = 2
                _iceFishTrackerHomeScreenState.value =
                    IceFishTrackerHomeScreenState.IceFishTrackerError
            } else {
                iceFishTrackerSharedPreference.iceFishTrackerAppState = 1
                iceFishTrackerSharedPreference.apply {
                    iceFishTrackerExpired = iceFishTrackerData.iceFishTrackerExpires
                    iceFishTrackerSavedUrl = iceFishTrackerData.iceFishTrackerUrl
                }
                _iceFishTrackerHomeScreenState.value =
                    IceFishTrackerHomeScreenState.IceFishTrackerSuccess(iceFishTrackerData.iceFishTrackerUrl)
            }
        } else  {
            if (iceFishTrackerData == null) {
                _iceFishTrackerHomeScreenState.value =
                    IceFishTrackerHomeScreenState.IceFishTrackerSuccess(iceFishTrackerSharedPreference.iceFishTrackerSavedUrl)
            } else {
                iceFishTrackerSharedPreference.apply {
                    iceFishTrackerExpired = iceFishTrackerData.iceFishTrackerExpires
                    iceFishTrackerSavedUrl = iceFishTrackerData.iceFishTrackerUrl
                }
                _iceFishTrackerHomeScreenState.value =
                    IceFishTrackerHomeScreenState.IceFishTrackerSuccess(iceFishTrackerData.iceFishTrackerUrl)
            }
        }
    }


    sealed class IceFishTrackerHomeScreenState {
        data object IceFishTrackerLoading : IceFishTrackerHomeScreenState()
        data object IceFishTrackerError : IceFishTrackerHomeScreenState()
        data class IceFishTrackerSuccess(val data: String) : IceFishTrackerHomeScreenState()
        data object IceFishTrackerNotInternet: IceFishTrackerHomeScreenState()
    }
}