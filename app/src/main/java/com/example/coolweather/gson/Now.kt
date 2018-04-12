package com.example.coolweather.gson

import com.google.gson.annotations.SerializedName

/**
 * Created by Administrator on 2018/4/11 0011.
 */
data class Now(@SerializedName("tmp") var temperature: String,
               @SerializedName("cond") var more: More) {
    data class More(@SerializedName("txt") var info: String)

}