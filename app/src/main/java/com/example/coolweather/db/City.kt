package com.example.coolweather.db

import org.litepal.crud.DataSupport

/**
 * Created by Administrator on 2018/4/11 0011.
 */

class City(var cityName: String,var cityCode: Int,var provinceId: Int,var id: Int=0): DataSupport()
