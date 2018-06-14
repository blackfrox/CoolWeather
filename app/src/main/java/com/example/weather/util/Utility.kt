package com.example.weather.util

import android.text.TextUtils
import com.example.weather.other.db.City
import com.example.weather.other.db.County
import com.example.weather.other.db.Province
import com.example.weather.network.gson.HEHEWeather00
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by Administrator on 2018/4/9 0009.
 */
object Utility {
    /**
     * 解析城市
     */
    fun handlerProvince(response: String): Boolean {
        if (!TextUtils.isEmpty(response)) {
            try {
                val jsonArray = JSONArray(response)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    with(jsonObject) {
                        Province(getString("name"),
                                getInt("id"))
                                .save()
                    }
                }
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    /**
     * 解析市级数据，并存储到数据库
     * (var cityName: String,
    var cityCode: Int,
    var provinceId: Int)
     */
    fun handleCityResponse(response: String, provinceId: Int): Boolean {
        if (!TextUtils.isEmpty(response)) {
            try {
                val jsonArray = JSONArray(response)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    with(jsonObject) {
                        City(getString("name"),
                                getInt("id"),
                                provinceId)
                                .save()
                    }
                }
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    /**
     * 解析县级数据，并保存
     */
    fun handleCountyResponse(response: String, cityId: Int): Boolean {
        if (!TextUtils.isEmpty(response)) {
            try {
                val jsonArray = JSONArray(response)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    with(jsonObject) {
                        County(getString("name"),
                                getString("weather_id"),
                                cityId)
                                .save()
                    }
                }
                return true
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
        return false
    }

    /**
     * 将返回的JSON数据解析成Weather实体类
     */
    fun handleWeatherResponse(response: String): HEHEWeather00? {
        if (!TextUtils.isEmpty(response)){
            try {
                val jsonObject=JSONObject(response)
                val jsonArray=jsonObject.getJSONArray("HeWeather")
                val weatherContent=jsonArray.getJSONObject(0).toString()
                return Gson().fromJson(weatherContent,HEHEWeather00::class.java)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
        return null
    }

}