package com.example.weather.mvp.presenter

import android.text.TextUtils
import com.example.weather.mvp.contract.CityManagerContract
import com.example.weather.other.RxBus.event.CityManagerEvent
import com.example.weather.other.RxBus.RxBus

class CityManagerPresenter(val view: CityManagerContract.View) :
        CityManagerContract.Presenter {

    init {
        view.presenter = this
//        registerEvent()
    }

    private fun registerEvent() {
        addSubscribe(RxBus.instance.toFlowable(CityManagerEvent::class.java)
                .subscribe {
                    if (!TextUtils.isEmpty(it.countyName))
                        view.addData(it.countyName)
                })
    }

}