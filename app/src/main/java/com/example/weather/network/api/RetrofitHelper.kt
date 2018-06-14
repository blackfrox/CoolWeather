package com.example.weather.network.api

import com.example.weather.network.api.service.HeWeatherService
import com.example.weather.util.NetworkUtil
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by Administrator on 2017/8/15.
 */
class RetrofitHelper{
    private val cache by lazy {
        Cache(File("app_cache"),1024*1024*10L)
    }
    private val client by lazy {
        val cacheInterceptor= Interceptor{
            var request=it.request()
            if (!NetworkUtil.isNetConnected())
                request=request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build()
            val response=it.proceed(request)
            if (NetworkUtil.isNetConnected()){
                //有网络时，不缓存，最大保存时长为0
                val maxAge=0
                response.newBuilder()
                        .header("Cache-Control","public,max-age=$maxAge")
                        .removeHeader("Pragma")
                        .build()
            }else{
                //无网络时，设置超时为4周
                val maxStale=60*60*24*28
                response.newBuilder()
                        .header("Cache-Control","public,only-if-cached,max-stale=$maxStale")
                        .removeHeader("Pragma")
                        .build()
            }
            response
        }
        OkHttpClient.Builder().apply{
            //设置缓存
            addNetworkInterceptor(cacheInterceptor)
            addInterceptor(cacheInterceptor)
            this.cache(cache)
            //设置超时
            connectTimeout(10,TimeUnit.SECONDS)
            readTimeout(20,TimeUnit.SECONDS)
            writeTimeout(20,TimeUnit.SECONDS)
            //错误重连
            retryOnConnectionFailure(true)
        }.build()
    }
   private var retrofit: Retrofit
    private val DEFAULT_TIMEOUT: Long=20
    private val baseUrl="https://free-api.heweather.com/"
    init {
        //retrofit创建
        retrofit=Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    companion object {
        val instance by lazy { RetrofitHelper() }
    }

   private fun <T> create(service: Class<T>): T{
        return retrofit.create(service)
    }

    fun getWeatherApi(): HeWeatherService{
        return create(HeWeatherService::class.java)
    }
}