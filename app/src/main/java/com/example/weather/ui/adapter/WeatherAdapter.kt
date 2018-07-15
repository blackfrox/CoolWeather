package com.example.weather.ui.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.weather.R
import com.example.weather.network.gson.HeWeatherForecast
import com.example.weather.util.getCurrentTime
import com.example.weather.util.getWeek
import com.example.weather.util.parse

class WeatherAdapter(layoutRes: Int = R.layout.item_forecast, list: List<HeWeatherForecast.HeWeather6Bean.DailyForecastBean>)

    : BaseQuickAdapter<HeWeatherForecast.HeWeather6Bean.DailyForecastBean, BaseViewHolder>(layoutRes, list) {
    override fun convert(helper: BaseViewHolder, item: HeWeatherForecast.HeWeather6Bean.DailyForecastBean) {
        helper.apply {
            item.apply {
                val time = if (date == getCurrentTime()) "今天" else getWeek(date)
                setText(R.id.forecast_time, time)
                setText(R.id.forecast_temp, "$tmp_max°/$tmp_min°")
                getView<ImageView>(R.id.forecast_icon)
                        .setImageResource(parse(cond_code_d))
            }
        }
    }
}
