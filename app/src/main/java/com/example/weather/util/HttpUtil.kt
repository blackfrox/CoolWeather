package com.example.weather.util

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

/**
 * Created by Administrator on 2018/4/8 0008.
 */
object HttpUtil {

    private val client=OkHttpClient()
    //callback 方法回调在子线程中,
    //如需更新UI,需要切换到UI线程(Activity.runOnUiThread())
    fun sendOkHttpRequest(url: String, callback: okhttp3.Callback){
        val request=Request.Builder().url(url).build()
        client.newCall(request).enqueue(callback)
    }

}