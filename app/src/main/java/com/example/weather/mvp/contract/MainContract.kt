package com.example.weather.mvp.contract

import com.baidu.location.BDLocation
import com.example.weather.base.BasePresenter
import com.example.weather.base.BaseView
import com.example.weather.other.db.CityWeather

interface MainContract {

    interface View: BaseView<Presenter>{
        fun showErrorMessage(message: String)
        //初始化
        fun initFragment(list: MutableList<CityWeather>,selectedItem: Int = -1)
    }

    interface Presenter: BasePresenter{
        fun start(bdLocation: BDLocation) {}
        fun refresh(selectedItem: Int)
    }
}