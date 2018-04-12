package com.example.coolweather.util

import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Created by Administrator on 2018/4/8 0008.
 */
object HttpUtil {

    fun sendOkHttpRequest(url: String, callback: okhttp3.Callback){
        val client=OkHttpClient()
        val request=Request.Builder().url(url).build()
        client.newCall(request).enqueue(callback)
    }
}