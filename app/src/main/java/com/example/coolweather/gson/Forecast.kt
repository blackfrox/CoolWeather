package com.example.coolweather.gson

import com.google.gson.annotations.SerializedName

/**
 * Created by Administrator on 2018/4/11 0011.
 */
data class Forecast(var date: String,
                    @SerializedName("tmp") var temperature: Temperature,
                    @SerializedName("cond") var more: More) {
    data class Temperature(var max: String,
                           var min:String)

    data class More(@SerializedName("txt_d") var info: String)
}