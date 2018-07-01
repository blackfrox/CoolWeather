package com.example.weather.mvp.presenter

import android.util.Log
import com.example.wanandroidtest.util.applyScheduler
import com.example.weather.mvp.contract.WeatherContract
import com.example.weather.network.api.RetrofitHelper
import com.example.weather.network.gson.HeLifeStyle
import com.example.weather.network.gson.HeWeatherForecast
import com.example.weather.other.RxBus.RxBus
import com.example.weather.other.RxBus.event.MainRefresh
import io.reactivex.Observable
import org.litepal.util.LogUtil

class WeatherPresenter(val view: WeatherContract.View)
    : WeatherContract.Presenter {

    init {
        view.presenter = this
    }

    private val model by lazy { RetrofitHelper.instance.getWeatherApi() }

    override fun getWeather(countyName: String) {
        addSubscribe(
                Observable.merge(model.getWeatherForecast(countyName), model.getLifeStyle(countyName))
                        .applyScheduler()
                        .subscribe({
                            when (it) {
                                is HeWeatherForecast -> {
                                    if (it.heWeather6[0].status == "ok")
                                        view.showWeatherInfo(it.heWeather6[0].daily_forecast)
                                    else
                                        view.showMessage("访问天气数据失败")
                                }
                                is HeLifeStyle -> {
                                    view.showLifeStyle(it)
                                }
                            }
//                   RxBus.instance.post(MainRefresh(false))
                        }, {
                            LogUtil.d("MainPresenter", "error Message::::")
                            it.printStackTrace()
                        }))
    }

    override fun refresh(countyName: String) {
        addSubscribe(model.getWeatherNow(countyName)
                .applyScheduler()
                .subscribe({
                    if (it.heWeather6[0].status == "ok")
                        view.showRefresh(it.heWeather6[0])
                    else
                        view.showMessage("网络请求失败")
//                    RxBus.instance.post(MainRefresh(false))
                }, {
                    LogUtil.d("WeatherPresenter", it.toString())
                    view.showMessage("未知错误")
//                    RxBus.instance.post(MainRefresh(false))
                }))
    }
}