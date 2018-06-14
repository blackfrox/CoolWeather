package com.example.weather.mvp.contract

import com.example.weather.base.BasePresenter
import com.example.weather.base.BaseView

interface CityManagerContract {

    interface View: BaseView<Presenter> {
        fun addData(countyName: String)

    }

    interface Presenter: BasePresenter{

    }
}