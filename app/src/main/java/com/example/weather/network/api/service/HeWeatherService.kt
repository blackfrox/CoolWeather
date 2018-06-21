package com.example.weather.network.api.service

import com.example.weather.network.gson.*
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface HeWeatherService {

    //访问(从今天起)3~10天的天气预报(ps:免费api只返回3天的天气数据)
    //https://free-api.heweather.com/s6/weather/forecast?&location=北京&key=df7cda9693794b85a9ffc8fdb781230c
    @GET("s6/weather/forecast?")
    fun getWeatherForecast(@Query("location")  location: String,
                           @Query("key") key: String="df7cda9693794b85a9ffc8fdb781230c")
    : Observable<HeWeatherForecast>

    //生活指数 https://free-api.heweather.com/s6/weather/lifestyle?parameters
    @GET("s6/weather/lifestyle?")
    fun getLifeStyle(@Query("location")  location: String,
                     @Query("key") key: String="df7cda9693794b85a9ffc8fdb781230c")
    :Observable<HeLifeStyle>

    //当天天气预报
    //https://free-api.heweather.com/s6/weather/now?location=北京&key=df7cda9693794b85a9ffc8fdb781230c
    @GET("s6/weather/now?")
    fun getWeatherNow(@Query("location")  location: String,
                      @Query("key") key: String="df7cda9693794b85a9ffc8fdb781230c")
    :Observable<HeWeatherNow>

    //搜索城市
    //https://search.heweather.com/find?location=嵊州市&key=df7cda9693794b85a9ffc8fdb781230c
    @GET("https://search.heweather.com/find?")
    fun getFindCity(@Query("location") location: String,
                    @Query("key") key: String="df7cda9693794b85a9ffc8fdb781230c")
            :Observable<HeFind>



                                      /*已弃用*/
    //获取七天的天气预报，并且含有suggestion(不知道什么原因，报错了classNotFoundException：no presenter； 可能是因为baseUrl不同的问题吧)
    //http://guolin.tech/api/weather?cityid=CN101210505&key=df7cda9693794b85a9ffc8fdb781230c
    @GET("http://guolin.tech/api/weather?&key=df7cda9693794b85a9ffc8fdb781230c")
    fun getWeatherGuolin(@Query("cityid") cityid: String): Observable<HeHeWeather>

}