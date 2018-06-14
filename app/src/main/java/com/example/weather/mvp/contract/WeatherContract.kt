package com.example.weather.mvp.contract

import com.example.weather.base.BasePresenter
import com.example.weather.base.BaseView
import com.example.weather.network.gson.HeLifeStyle
import com.example.weather.network.gson.HeWeather

interface WeatherContract {
    interface View: BaseView<Presenter> {
        //通过和风api获取到的数据
        fun showWeatherInfo(list: MutableList<HeWeather.HeWeather6Bean.DailyForecastBean>)
        fun showMessage(message: String)
        fun showLifeStyle(heLifeStyle: HeLifeStyle)
    }
    interface Presenter: BasePresenter{
        fun getWeather(cityName: String)
    }
}