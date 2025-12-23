package com.icefishi.trackesof.hutrjk.presentation.app

import android.app.Application
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.icefishi.trackesof.hutrjk.presentation.di.iceFishTrackerModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


sealed interface IceFishTrackerAppsFlyerState {
    data object IceFishTrackerDefault : IceFishTrackerAppsFlyerState
    data class IceFishTrackerSuccess(val iceFishTrackerData: MutableMap<String, Any>?) :
        IceFishTrackerAppsFlyerState

    data object IceFishTrackerError : IceFishTrackerAppsFlyerState
}

interface IceFishTrackerAppsApi {
    @Headers("Content-Type: application/json")
    @GET(ICE_FISH_TRACKER_LIN)
    fun iceFishTrackerGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}

private const val ICE_FISH_TRACKER_APP_DEV = "5aTQMBg6bvSumRC9p7r9HF"
private const val ICE_FISH_TRACKER_LIN = "com.icefishi.trackesof"

class IceFishTrackerApplication : Application() {

    private var iceFishTrackerIsResumed = false
    private var iceFishTrackerConversionTimeoutJob: Job? = null
    private var iceFishTrackerDeepLinkData: MutableMap<String, Any>? = null

    override fun onCreate() {
        super.onCreate()

        val appsflyer = AppsFlyerLib.getInstance()
        iceFishTrackerSetDebufLogger(appsflyer)
        iceFishTrackerMinTimeBetween(appsflyer)

        AppsFlyerLib.getInstance().subscribeForDeepLink(object : DeepLinkListener {
            override fun onDeepLinking(p0: DeepLinkResult) {
                when (p0.status) {
                    DeepLinkResult.Status.FOUND -> {
                        iceFishTrackerExtractDeepMap(p0.deepLink)
                        Log.d(ICE_FISH_TRACKER_MAIN_TAG, "onDeepLinking found: ${p0.deepLink}")

                    }

                    DeepLinkResult.Status.NOT_FOUND -> {
                        Log.d(ICE_FISH_TRACKER_MAIN_TAG, "onDeepLinking not found: ${p0.deepLink}")
                    }

                    DeepLinkResult.Status.ERROR -> {
                        Log.d(ICE_FISH_TRACKER_MAIN_TAG, "onDeepLinking error: ${p0.error}")
                    }
                }
            }

        })


        appsflyer.init(
            ICE_FISH_TRACKER_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    iceFishTrackerConversionTimeoutJob?.cancel()
                    Log.d(ICE_FISH_TRACKER_MAIN_TAG, "onConversionDataSuccess: $p0")

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = iceFishTrackerGetApi(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.iceFishTrackerGetClient(
                                    devkey = ICE_FISH_TRACKER_APP_DEV,
                                    deviceId = iceFishTrackerGetAppsflyerId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(ICE_FISH_TRACKER_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic" || resp?.get("af_status") == null) {
                                    iceFishTrackerResume(IceFishTrackerAppsFlyerState.IceFishTrackerSuccess(p0))
                                } else {
                                    iceFishTrackerResume(IceFishTrackerAppsFlyerState.IceFishTrackerSuccess(resp))
                                }
                            } catch (d: Exception) {
                                Log.d(ICE_FISH_TRACKER_MAIN_TAG, "Error: ${d.message}")
                                iceFishTrackerResume(IceFishTrackerAppsFlyerState.IceFishTrackerError)
                            }
                        }
                    } else {
                        iceFishTrackerResume(IceFishTrackerAppsFlyerState.IceFishTrackerSuccess(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    iceFishTrackerConversionTimeoutJob?.cancel()
                    Log.d(ICE_FISH_TRACKER_MAIN_TAG, "onConversionDataFail: $p0")
                    iceFishTrackerResume(IceFishTrackerAppsFlyerState.IceFishTrackerError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(ICE_FISH_TRACKER_MAIN_TAG, "onAppOpenAttribution")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(ICE_FISH_TRACKER_MAIN_TAG, "onAttributionFailure: $p0")
                }
            },
            this
        )

        appsflyer.start(this, ICE_FISH_TRACKER_APP_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(ICE_FISH_TRACKER_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(ICE_FISH_TRACKER_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
            }
        })
        iceFishTrackerStartConversionTimeout()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@IceFishTrackerApplication)
            modules(
                listOf(
                    iceFishTrackerModule
                )
            )
        }
    }

    private fun iceFishTrackerExtractDeepMap(dl: DeepLink) {
        val map = mutableMapOf<String, Any>()
        dl.deepLinkValue?.let { map["deep_link_value"] = it }
        dl.mediaSource?.let { map["media_source"] = it }
        dl.campaign?.let { map["campaign"] = it }
        dl.campaignId?.let { map["campaign_id"] = it }
        dl.afSub1?.let { map["af_sub1"] = it }
        dl.afSub2?.let { map["af_sub2"] = it }
        dl.afSub3?.let { map["af_sub3"] = it }
        dl.afSub4?.let { map["af_sub4"] = it }
        dl.afSub5?.let { map["af_sub5"] = it }
        dl.matchType?.let { map["match_type"] = it }
        dl.clickHttpReferrer?.let { map["click_http_referrer"] = it }
        dl.getStringValue("timestamp")?.let { map["timestamp"] = it }
        dl.isDeferred?.let { map["is_deferred"] = it }
        for (i in 1..10) {
            val key = "deep_link_sub$i"
            dl.getStringValue(key)?.let {
                if (!map.containsKey(key)) {
                    map[key] = it
                }
            }
        }
        Log.d(ICE_FISH_TRACKER_MAIN_TAG, "Extracted DeepLink data: $map")
        iceFishTrackerDeepLinkData = map
    }

    private fun iceFishTrackerStartConversionTimeout() {
        iceFishTrackerConversionTimeoutJob = CoroutineScope(Dispatchers.Main).launch {
            delay(30000)
            if (!iceFishTrackerIsResumed) {
                Log.d(ICE_FISH_TRACKER_MAIN_TAG, "TIMEOUT: No conversion data received in 30s")
                iceFishTrackerResume(IceFishTrackerAppsFlyerState.IceFishTrackerError)
            }
        }
    }

    private fun iceFishTrackerResume(state: IceFishTrackerAppsFlyerState) {
        iceFishTrackerConversionTimeoutJob?.cancel()
        if (state is IceFishTrackerAppsFlyerState.IceFishTrackerSuccess) {
            val convData = state.iceFishTrackerData ?: mutableMapOf()
            val deepData = iceFishTrackerDeepLinkData ?: mutableMapOf()
            val merged = mutableMapOf<String, Any>().apply {
                putAll(convData)
                for ((key, value) in deepData) {
                    if (!containsKey(key)) {
                        put(key, value)
                    }
                }
            }
            if (!iceFishTrackerIsResumed) {
                iceFishTrackerIsResumed = true
                iceFishTrackerConversionFlow.value =
                    IceFishTrackerAppsFlyerState.IceFishTrackerSuccess(merged)
            }
        } else {
            if (!iceFishTrackerIsResumed) {
                iceFishTrackerIsResumed = true
                iceFishTrackerConversionFlow.value = state
            }
        }
    }

    private fun iceFishTrackerGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(ICE_FISH_TRACKER_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun iceFishTrackerSetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun iceFishTrackerMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun iceFishTrackerGetApi(url: String, client: OkHttpClient?): IceFishTrackerAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

    companion object {

        var iceFishTrackerInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val iceFishTrackerConversionFlow: MutableStateFlow<IceFishTrackerAppsFlyerState> = MutableStateFlow(
            IceFishTrackerAppsFlyerState.IceFishTrackerDefault
        )
        var ICE_FISH_TRACKER_FB_LI: String? = null
        const val ICE_FISH_TRACKER_MAIN_TAG = "IceFishTrackerMainTag"
    }
}