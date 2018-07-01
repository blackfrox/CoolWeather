package com.example.weather.util

import com.example.wanandroidtest.SPUtil
import com.example.wanandroidtest.putInt


/**
 *
 */

object SettingsUtil {

    //    val WEATHER_SHARE_TYPE = "weather_share_type"//天气分享形式
//    val WEATHER_KEY = "weather_key"//天气 key
//    val WEATHER_DATE_TYPE = "weather_date_type"//天气日期显示样式，日期 or 星期
    val THEME = "theme_color"//主题
//    val CLEAR_CACHE = "clean_cache"//清空缓存

//    val WEATHER_DATE_TYPE_WEEK = 0
//
//    val WEATHER_DATE_TYPE_DATE = 1
//
//    val WEATHER_SRC_HEFENG = 0
//
//    val WEATHER_SRC_XIAOMI = 1

    //卧槽,filed是默认值，value是赋值，
    var theme: Int
        get() = SPUtil.instance.getInt(THEME, 0)
        set(value) = SPUtil.instance.putInt(THEME, value)

//    fun setTheme(themeIndex: Int) {
//        SPUtil.instance.putInt(THEME,themeIndex)
//    }
//
//    fun getTheme()=
//            SPUtil.instance.getInt(THEME,0)

}
