package com.example.weather.network.gson

/**
 * {"HeWeather6":[{"basic":[{"cid":"CN101210505","location":"嵊州","parent_city":"绍兴",
 * "admin_area":"浙江","cnty":"中国","lat":"29.58660507","lon":"120.82888031","tz":"+8.00",
 * "type":"city"}],"status":"ok"}]}
 */
data class CidLocation(var HeWeather6: Array<Heaweather6>) {
    data class Heaweather6(var basic: Array<Basic>,
                           var status: String)
    data class Basic(var cid: String,
                     var location: String,
                     var parent_city: String,
                     var admin_area: String,
                     var cnty: String,
                     var lat: String,
                     var lon:String,
                     var tz: String,
                     var type: String)
}