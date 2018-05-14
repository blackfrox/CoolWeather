package com.example.coolweather.contract

import com.example.coolweather.base.BasePresenter
import com.example.coolweather.base.BaseView
import com.example.coolweather.gson.Weather

interface WeatherContract {

    interface View: BaseView<Presenter>{
        fun showView()
        fun getWeatherId(): String
        fun showWeatherInfo(weather: Weather?)
        fun showBingPic(bingPic: String)
        fun showFail()

    }

    interface Presenter: BasePresenter{
        fun requestWeather(weatherId: String)

    }
}