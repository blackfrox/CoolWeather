package com.example.weather.mvp.presenter

import com.example.wanandroidtest.util.applyScheduler
import com.example.weather.mvp.contract.Choose00Contract
import com.example.weather.network.api.RetrofitHelper

class Choose00Presenter(val view: Choose00Contract.View)
    :Choose00Contract.Presenter{

    private val model by lazy { RetrofitHelper.instance.getWeatherApi() }
    override fun getHotCity() {
        addSubscribe(
                model.getHotCity()
                        .applyScheduler()
                        .subscribe({
                            if (it.heWeather6[0].status=="ok")
                            else
                                view.showMessage("网络请求失败")
                        },{
                            view.showMessage("发生未知错误")
                        })
        )
    }
}