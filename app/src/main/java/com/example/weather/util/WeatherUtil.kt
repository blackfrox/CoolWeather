package com.example.weather.util

import com.example.weather.other.ShareData

object WeatherUtil {

    fun getShareMessage(shareData: ShareData): String? {
        val stringBuffer =StringBuffer()
        stringBuffer.apply {
            with(shareData){
                append("$countyName 天气:")
                append("\r\n")
                append("$weather $tmp  $aqi")
                append("\r\n")
                append(today)
                append("\r\n")
                append(tommor)
                append("\r\n")
                append(getTime())
            }
        }
        return stringBuffer.toString()
    }
}