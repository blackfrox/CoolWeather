package com.example.weather.network.gson


/**
 * Created by Administrator on 2018/4/11 0011.
 */
data class AQI(var city: City) {
    data class City(var aqi: String,
                    var pm25: String)
}