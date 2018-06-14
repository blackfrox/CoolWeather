package com.example.weather.mvp.presenter

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.preference.PreferenceManager
import android.text.TextUtils
import com.baidu.location.BDLocation
import com.example.weather.mvp.contract.MainContract
import com.example.weather.other.db.CityWeather
import com.example.weather.util.HttpUtil
import com.example.weather.util.tool.RxBus
import com.example.weather.util.Utility
import com.example.weather.util.event.MainEvent
import com.example.wanandroidtest.SPUtil
import com.example.wanandroidtest.putString
import com.tbruyelle.rxpermissions2.RxPermissions
import okhttp3.Call
import okhttp3.Response
import org.litepal.crud.DataSupport
import java.io.IOException

/**
 *
 *
 *第一个Fragment使用定位，后面的Fragment根据litePal保存的数据进行
 */
class MainPresenter(val view: MainContract.View,
                    val activity: Activity)
    : MainContract.Presenter {

    init {
        //谷歌官方写法,但是不知道这么做有没有attachView和detachView的功能（这个功能是写在baseActivity/BaseFragment）
        view.presenter = this
        registerEvent()
    }

    private fun registerEvent() {
        addSubscribe(RxBus.instance.toFlowable(MainEvent::class.java)
                .subscribe {
                    val list=DataSupport.findAll(CityWeather::class.java)
                    view.initFragment(list)
                })
    }


    /**
     * //搜索城市
     * https://search.heweather.com/find?location=%E5%B5%8A%E5%B7%9E%E5%B8%82&key=df7cda9693794b85a9ffc8fdb781230c
     * city：城市名称，city可通过城市中英文名称、ID、IP和经纬度进行查询，经纬度查询格式为：经度,纬度。例：city=北京，city=beijing，city=CN101010100，city=
     */
    /**
     * 思路: 保存cityName，weatherResponseText
     * 架构: 一个Activity+多个Fragment
     */
    private val LAST_LOCATED_CITY = "last_located_city"

    /**
     * 第一个Fragment自动定位，其他使用管理城市修改地名
     * 将定位信息更新到db数据库中
     */
    override fun start() {
        //获取百度定位所需权限(PS：flyme默认授权以下权限)
        RxPermissions(activity)
                .request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe {
                    if (!it)
                        view.showErrorMessage("权限被拒绝，无法正常定位")
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

        //如果定位发生改变,就更新第一位的城市名字和清空天气数据，从网络上获取cid
        DataSupport.findFirst(CityWeather::class.java)
                .apply {
                    if (this != null) {
                        //如果定位地点发生改变
                        if (!TextUtils.isEmpty(countyName) && !countyName.equals(nowCity)) {
                            countyName = nowCity //更改城市名字
                            save()
                        }
                    } else
                        CityWeather(nowCity).save()

                    val mList = DataSupport.order("id").find(CityWeather::class.java)
                    view.initFragment(mList)
                }
    }


    /*旧方法，已弃用*/
    /**
     * 根据天气id请求天气信息
     * @param weatherId 例如"CN101010100"
     */
    fun requestWeather(weatherId: String) {
        val weatherUrl = "http://guolin.tech/api/weather?cityid=$weatherId&key=df7cda9693794b85a9ffc8fdb781230c"
        HttpUtil.sendOkHttpRequest(weatherUrl, object : okhttp3.Callback {
            @SuppressLint("CommitPrefEdits")
            override fun onResponse(call: Call?, response: Response) {
                val responseText = response.body()!!.string()
                val weather = Utility.handleWeatherResponse(responseText)
                activity.runOnUiThread {
                    if (weather != null && "ok".equals(weather.status)) {
                        val editor = PreferenceManager.getDefaultSharedPreferences(activity)
                                .edit()
                        editor.putString("weather", responseText)
                        editor.apply()
//                        view.showWeatherInfo(weather)
                    }
//                    }else
//                     view.showFail()
                }
            }

            override fun onFailure(call: Call?, e: IOException?) {
                activity.runOnUiThread {
                    //                    view.showFail()
                }
            }
        })
    }

    fun loadBingPic() {
        val url = "http://guolin.tech/api/bing_pic"
        HttpUtil.sendOkHttpRequest(url, object : okhttp3.Callback {
            override fun onResponse(call: Call?, response: Response) {
                val responseText = response.body()!!.string()
                activity.runOnUiThread {
                    val editor = PreferenceManager.getDefaultSharedPreferences(activity)
                            .edit()
                    editor.putString("bing_pic", responseText)
                    editor.apply()
//                    view.showBingPic(responseText)
                }
            }

            override fun onFailure(call: Call?, e: IOException?) {
                e?.printStackTrace()
            }
        })
    }
}


