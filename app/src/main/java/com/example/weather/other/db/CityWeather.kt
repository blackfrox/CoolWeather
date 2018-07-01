package com.example.weather.other.db

import org.litepal.crud.DataSupport
import java.io.Serializable

/**
 *作为CityManager中的item，同时保存在数据库中
 *
 * 注意： 表中的任意数据修改，都需要重新更新!!!
 *     解决方法: 1 将数据库版本升级(修改版本号)
 *               2 将App重新安装
 */
class CityWeather(var countyName: String = "",
                  var countyId: Int = 0,
                  var tmp: String = "N/A",
                  var weather: String = "")
    : DataSupport(), Serializable