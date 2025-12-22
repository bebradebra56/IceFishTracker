package com.icefishi.trackesof.hutrjk.domain.usecases

import android.util.Log
import com.icefishi.trackesof.hutrjk.data.repo.IceFishTrackerRepository
import com.icefishi.trackesof.hutrjk.data.utils.IceFishTrackerPushToken
import com.icefishi.trackesof.hutrjk.data.utils.IceFishTrackerSystemService
import com.icefishi.trackesof.hutrjk.domain.model.IceFishTrackerEntity
import com.icefishi.trackesof.hutrjk.domain.model.IceFishTrackerParam
import com.icefishi.trackesof.hutrjk.presentation.app.IceFishTrackerApplication

class IceFishTrackerGetAllUseCase(
    private val iceFishTrackerRepository: IceFishTrackerRepository,
    private val iceFishTrackerSystemService: IceFishTrackerSystemService,
    private val iceFishTrackerPushToken: IceFishTrackerPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : IceFishTrackerEntity?{
        val params = IceFishTrackerParam(
            iceFishTrackerLocale = iceFishTrackerSystemService.iceFishTrackerGetLocale(),
            iceFishTrackerPushToken = iceFishTrackerPushToken.iceFishTrackerGetToken(),
            iceFishTrackerAfId = iceFishTrackerSystemService.iceFishTrackerGetAppsflyerId()
        )
        Log.d(IceFishTrackerApplication.ICE_FISH_TRACKER_MAIN_TAG, "Params for request: $params")
        return iceFishTrackerRepository.iceFishTrackerGetClient(params, conversion)
    }



}