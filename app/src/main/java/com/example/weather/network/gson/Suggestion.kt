package com.example.weather.network.gson

import com.google.gson.annotations.SerializedName

/**
 * Created by Administrator on 2018/4/11 0011.
 */
class Suggestion(@SerializedName("comf") var comfort: Comfort,
                 @SerializedName("cw") var carWash: CarWash,
                 var sport: Sport) {
    data class Comfort(@SerializedName("txt") var info: String)
    data class CarWash(@SerializedName("txt") var info: String)
    data class Sport(@SerializedName("txt") var info: String)
}