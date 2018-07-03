package com.example.wanandroidtest

import android.content.Context
import android.content.SharedPreferences
import com.example.weather.MyApp
import com.example.weather.other.db.CityWeather

/**
 * Created by Administrator on 2018/4/17 0017.
 */
class SPUtil{
    companion object {
        private const val SP_NAME="sp_name"
        val instance: SharedPreferences by lazy { MyApp.instance.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE) }
    }

}

fun SharedPreferences.getInt(key: String, value: Int=0): Int {
    return getInt(key, value)
}
fun SharedPreferences.putInt(key: String, value: Int) {
    edit().putInt(key, value).apply()
//    SPUtil.instance.edit().putInt(key, value).apply()
}
fun SharedPreferences.getBoolean(key: String, value: Boolean=false): Boolean {
    return getBoolean(key, value)
}
fun SharedPreferences.putBoolean(key: String, value: Boolean) {
    edit().putBoolean(key, value).apply()
}

fun SharedPreferences.getString(key: String, value: String = ""): String {
    return getString(key, value)
}

fun SharedPreferences.putString(key: String, value: String) {
    edit().putString(key, value).apply()
}