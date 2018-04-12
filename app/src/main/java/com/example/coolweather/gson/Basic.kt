package com.example.coolweather.gson

import com.google.gson.annotations.SerializedName

/**
 * Created by Administrator on 2018/4/11 0011.
 */
data class Basic(@SerializedName("city") var cityName: String,
                 @SerializedName("id") var weatherId: String,
                 var update: Update) {
    data class Update(@SerializedName("loc") var updateTime: String)
}