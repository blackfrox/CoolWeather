package com.example.coolweather.contract

import com.example.coolweather.base.BasePresenter
import com.example.coolweather.base.BaseView
import com.example.coolweather.db.County

interface ChooseContract {
    interface View: BaseView<Presenter>{

        fun setupToolbar(title: String, showBack: Boolean)

        fun showMessage(message: String)
        fun showChange(dataList: ArrayList<String>, level: Int)
        fun showProgress()
        fun closeProgress()

    }

    interface Presenter: BasePresenter{
        val countyList: List<County>

        fun queryProvinces()
        fun queryCounties(position: Int=-1)
        fun queryCities(position: Int=-1)
    }
}