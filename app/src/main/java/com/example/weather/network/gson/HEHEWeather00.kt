package com.example.weather.network.gson

import com.google.gson.annotations.SerializedName

/**
 * Created by Administrator on 2018/4/11 0011.
 */
//已弃用
data class HEHEWeather00(var status: String,
                         var basic: Basic,
                         var aqi: AQI,
                         var now: Now,
                         var suggestion: Suggestion,
                         @SerializedName("daily_forecast") var forecastList: List<Forecast>)
