package com.example.weather.mvp.contract

import com.example.weather.base.BasePresenter
import com.example.weather.base.BaseView
import com.example.weather.network.gson.HeLifeStyle
import com.example.weather.network.gson.HeWeatherForecast
import com.example.weather.network.gson.HeWeatherNow

interface WeatherContract {
    interface View : BaseView<Presenter> {
        fun showWeatherInfo(list: MutableList<HeWeatherForecast.HeWeather6Bean.DailyForecastBean>)
        fun showMessage(message: String)
        fun showLifeStyle(heLifeStyle: HeLifeStyle)
        fun showRefresh(heWeather6Bean: HeWeatherNow.HeWeather6Bean)
    }

    interface Presenter : BasePresenter {
        fun getWeather(countyName: String)
        fun refresh(countyName: String)
    }
}