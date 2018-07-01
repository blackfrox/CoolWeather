package com.example.weather.mvp.presenter

import android.Manifest
import android.app.Activity
import android.text.TextUtils
import com.baidu.location.BDLocation
import com.example.weather.mvp.contract.MainContract
import com.example.weather.other.db.CityWeather
import com.example.weather.other.RxBus.RxBus
import com.example.wanandroidtest.SPUtil
import com.example.wanandroidtest.putString
import com.example.weather.other.RxBus.event.MainRefresh
import com.example.weather.other.RxBus.event.ThemeChangedEvent
import com.tbruyelle.rxpermissions2.RxPermissions
import org.litepal.crud.DataSupport

/**
 *
 *第一个Fragment使用定位
 */
class MainPresenter(val view: MainContract.View,
                    val activity: Activity)
    : MainContract.Presenter {

    init {
        view.presenter = this
        registerEvent()
    }

    private fun registerEvent() {
//        addSubscribe(
//                RxBus.instance.toFlowable(ThemeChangedEvent::class.java)
//                .subscribe {
//                    view.showThemeChange()
//                })
    }


    /**
     * @param selectedItem 跳转的position，暂时没写
     */
    override fun refresh(selectedItem: Int) {
        val list = DataSupport.order("countyId").find(CityWeather::class.java)
        view.initFragment(list, selectedItem)
    }


    private val LAST_LOCATED_CITY = "last_located_city"

    /**
     * 第一个Fragment自动定位，其他使用管理城市修改地名
     * 将定位信息更新到db数据库中
     */
    override fun start() {
        //百度定位所需权限(PS：部分国产系统会默认授权其中一些权限)
        RxPermissions(activity)
                .request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe {
                    if (!it) view.showErrorMessage("权限被拒绝，无法正常定位")
                }

    }

    override fun start(bdLocation: BDLocation) {
        //获取nowCity
        val nowCity: String
        if (TextUtils.isEmpty(bdLocation.district)) {
            nowCity = SPUtil.instance.getString(LAST_LOCATED_CITY, "嵊州市")
        } else {
            nowCity = bdLocation.district
            SPUtil.instance.putString(LAST_LOCATED_CITY, nowCity)
        }

        //如果定位发生改变,就重新更新数据
        DataSupport.findFirst(CityWeather::class.java)
                .apply {
                    if (this != null) {
                        if (!TextUtils.isEmpty(countyName) && !countyName.equals(nowCity)) {
                            countyName = nowCity //更改城市名字
                            save()
                        }
                    } else
                        CityWeather(nowCity).save()
                }
        val list = DataSupport.order("countyId").find(CityWeather::class.java)
        view.initFragment(list)
    }

}


