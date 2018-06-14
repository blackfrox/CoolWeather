package com.example.weather.util

import android.content.Context
import android.content.Intent
import com.example.weather.R
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*


/**
 * 根据天气返回图标
 */
fun parse(cond_code_d: String?): Int {
    if (cond_code_d==null)
        return -1
    return when(cond_code_d){
        "晴" -> R.mipmap.icon_100
        "多云" -> R.mipmap.icon_101
        "少云" -> R.mipmap.icon_102
        "晴间多云" -> R.mipmap.icon_103
        "阴" -> R.mipmap.icon_104
        "阵雨" -> R.mipmap.icon_300
        "强阵雨" -> R.mipmap.icon_301
        "雷阵雨" -> R.mipmap.icon_302
        "强阵雨" -> R.mipmap.icon_303
        "雷阵雨伴有冰雹" -> R.mipmap.icon_304
        "小雨" -> R.mipmap.icon_305
        "中雨" -> R.mipmap.icon_306
        "大雨" -> R.mipmap.icon_307
//            "极端降雨" -> 木有icon
        "毛毛雨/细雨" -> R.mipmap.icon_309
        "暴雨" -> R.mipmap.icon_310
        "大暴雨" -> R.mipmap.icon_311
        "特大暴雨" -> R.mipmap.icon_312
        "冻雨" -> R.mipmap.icon_313
        "小雪" -> R.mipmap.icon_400
        "中雪" -> R.mipmap.icon_401
        "大雪" -> R.mipmap.icon_402
        "暴雪" -> R.mipmap.icon_404
        "雨夹雪" -> R.mipmap.icon_404
        "雨雪天气" -> R.mipmap.icon_405
        "阵雨夹雪" -> R.mipmap.icon_406
        "阵雪"  -> R.mipmap.icon_407
        else -> R.mipmap.icon_100
    }
}

fun getWeek(dateString: String): String {
    // 再转换为时间
    val date = strToDate(dateString)
    val c = Calendar.getInstance()
    c.setTime(date)
    // int hour=c.get(Calendar.DAY_OF_WEEK);
    // hour中存的就是星期几了，其范围 1~7
    // 1=星期日 7=星期六，其他类推
    return SimpleDateFormat("EEEE").format(c.getTime())
}

fun strToDate(strDate: String): Date {
    val formatter = SimpleDateFormat("yyyy-MM-dd")
    val pos = ParsePosition(0)
    return formatter.parse(strDate, pos)
}

fun getTime(): String {
    val sDateFormat = SimpleDateFormat("yyyy-MM-dd   hh:mm")
    val date = sDateFormat.format(java.util.Date())
    return date
}
fun <T>Context.startActivity(clazz: Class<T>){
    startActivity(Intent(this,clazz))
}