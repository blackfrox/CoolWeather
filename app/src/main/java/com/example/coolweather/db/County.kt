package com.example.coolweather.db

import org.litepal.crud.DataSupport

/**
 * Created by Administrator on 2018/4/11 0011.
 */

class County(var countyName: String, var weatherId: String,
             var cityId: Int, var id: Int=0)
    : DataSupport()
