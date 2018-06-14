package com.example.weather.util

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

/**
 * Created by Administrator on 2018/4/8 0008.
 */
object HttpUtil {

    private val client=OkHttpClient()
    //callback 方法在子线程中,
    //如需更新UI,需要切换到UI线程
    fun sendOkHttpRequest(url: String, callback: okhttp3.Callback){
        val request=Request.Builder().url(url).build()
        client.newCall(request).enqueue(callback)
    }

    //报错,必须在子线程才能用
//    fun getResponse(url:String): Response?{
//        val request=Request.Builder()
//                .url(url)
//                .build()
//        return client.newCall(request).execute()
//    }
}