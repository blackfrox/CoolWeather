package com.example.weather.other.db

import org.litepal.crud.DataSupport
import java.io.Serializable

/**
 * Created by Administrator on 2018/4/11 0011.
 */

class County(var countyName: String, var weatherId: String,
             var cityId: Int, var id: Int=0)
    : DataSupport(),Serializable
