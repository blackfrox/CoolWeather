package com.example.weather.mvp.contract

import com.example.weather.base.BasePresenter
import com.example.weather.base.BaseView
import com.example.weather.other.db.CityWeather

interface MainContract {

    interface View: BaseView<Presenter>{
        fun showErrorMessage(message: String)
        //初始化
        fun initFragment(list: MutableList<CityWeather>)

        //删除
        fun deleteFragment(deletePosition: Int)
        //添加
        fun addFragment(countyName: String)
        //交换
        fun swipeFragment()
        //撤销
        fun reduceFragment(pos: Int, countyName: String)
    }

    interface Presenter: BasePresenter{

    }
}