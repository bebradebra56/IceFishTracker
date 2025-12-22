package com.icefishi.trackesof.hutrjk.data.repo

import android.util.Log
import com.icefishi.trackesof.hutrjk.domain.model.IceFishTrackerEntity
import com.icefishi.trackesof.hutrjk.domain.model.IceFishTrackerParam
import com.icefishi.trackesof.hutrjk.presentation.app.IceFishTrackerApplication.Companion.ICE_FISH_TRACKER_MAIN_TAG
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface IceFishTrackerApi {
    @Headers("Content-Type: application/json")
    @POST("config.php")
    fun iceFishTrackerGetClient(
        @Body jsonString: JsonObject,
    ): Call<IceFishTrackerEntity>
}


private const val ICE_FISH_TRACKER_MAIN = "https://icefishtracker.com/"
class IceFishTrackerRepository {

    suspend fun iceFishTrackerGetClient(
        iceFishTrackerParam: IceFishTrackerParam,
        iceFishTrackerConversion: MutableMap<String, Any>?
    ): IceFishTrackerEntity? {
        val gson = Gson()
        val api = iceFishTrackerGetApi(ICE_FISH_TRACKER_MAIN, null)

        val iceFishTrackerJsonObject = gson.toJsonTree(iceFishTrackerParam).asJsonObject
        iceFishTrackerConversion?.forEach { (key, value) ->
            val element: JsonElement = gson.toJsonTree(value)
            iceFishTrackerJsonObject.add(key, element)
        }
        return try {
            val iceFishTrackerRequest: Call<IceFishTrackerEntity> = api.iceFishTrackerGetClient(
                jsonString = iceFishTrackerJsonObject,
            )
            val iceFishTrackerResult = iceFishTrackerRequest.awaitResponse()
            Log.d(ICE_FISH_TRACKER_MAIN_TAG, "Retrofit: Result code: ${iceFishTrackerResult.code()}")
            if (iceFishTrackerResult.code() == 200) {
                Log.d(ICE_FISH_TRACKER_MAIN_TAG, "Retrofit: Get request success")
                Log.d(ICE_FISH_TRACKER_MAIN_TAG, "Retrofit: Code = ${iceFishTrackerResult.code()}")
                Log.d(ICE_FISH_TRACKER_MAIN_TAG, "Retrofit: ${iceFishTrackerResult.body()}")
                iceFishTrackerResult.body()
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(ICE_FISH_TRACKER_MAIN_TAG, "Retrofit: Get request failed")
            Log.d(ICE_FISH_TRACKER_MAIN_TAG, "Retrofit: ${e.message}")
            null
        }
    }


    private fun iceFishTrackerGetApi(url: String, client: OkHttpClient?) : IceFishTrackerApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }


}
