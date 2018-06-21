package com.example.weather.other.RxBus.event

import java.io.Serializable

/**
 * 用于传递数据
 */
//城市名字，天气，温度
class CityManagerEvent(val countyName: String,
                       val weather: String ="N/A",
                       val tmp: String ="N/A") :Serializable