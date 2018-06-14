package com.example.weather.other.db

import org.litepal.crud.DataSupport
import java.io.Serializable

/**
 *作为CityManager中的item，同时保存在数据库中
 */
class CityWeather(var countyName: String = "",
                  var id: Int = 0,
                  var tmp: String = "N/A",
                  var weather: String = ""
                 )
    : DataSupport(), Serializable