package com.example.coolweather.presenter

import android.app.Activity
import com.example.coolweather.contract.ChooseContract
import com.example.coolweather.db.City
import com.example.coolweather.db.County
import com.example.coolweather.db.Province
import com.example.coolweather.ui.ChooseFragment
import com.example.coolweather.util.HttpUtil
import com.example.coolweather.util.Utility
import okhttp3.Call
import okhttp3.Response
import org.litepal.crud.DataSupport
import java.io.IOException

/**
 * 网络访问，有几个地点的天气是无法获取的，这是api的问题
 */
class ChoosePresenter(
        val mView: ChooseContract.View,val activity: Activity)
    :ChooseContract.Presenter{

    init {
        mView.presenter=this
    }
    override fun start() {
        mView.showMessage("presenter start")
    }

    private lateinit var provinceList: List<Province>
    private lateinit var cityList: List<City>
    override lateinit var countyList: List<County>

    private lateinit var selectedProvince: Province
    private lateinit var selectedCity: City

    private val dataList = arrayListOf<String>()
    /**
     * 优先查询数据库,其次网络
     */
    override fun queryProvinces(){
        mView.setupToolbar("中国",false)

        provinceList= DataSupport.findAll(Province::class.java)
        if (provinceList.size>0){
            dataList.clear()
            provinceList.forEach { dataList.add(it.name) }
            mView.showChange(dataList,ChooseFragment.LEVEL_PROVINCE)
        }else{
            val url="http://guolin.tech/api/china/"
            queryFromService(url,"province")
        }
    }

    /**
     * 查询市级
     */
    override fun queryCities(position: Int){
       if (position!=-1) selectedProvince=provinceList.get(position)
        mView.setupToolbar(selectedProvince.name,true)
        cityList= DataSupport.where("provinceid=?","${selectedProvince.id}")
                .find(City::class.java)
        if (cityList.size>0){
            dataList.clear()
            cityList.forEach { dataList.add(it.cityName) }
            mView.showChange(dataList,ChooseFragment.LEVEL_CITY)
        }else{
            val url="http://guolin.tech/api/china/${selectedProvince.code}"
            queryFromService(url,"city")
        }
    }

    /**
     * 查询县级数据
     */
    override fun queryCounties(position: Int){
       if (position!=-1) selectedCity=cityList[position]
        mView.setupToolbar(selectedCity.cityName,true)
        countyList= DataSupport.where("cityid=?","${selectedCity.id}")
                .find(County::class.java)
        if (countyList.size>0){
            dataList.clear()
            countyList.forEach { dataList.add(it.countyName) }
            mView.showChange(dataList,ChooseFragment.LEVEL_COUNTY)
        }else{
            val url="http://guolin.tech/api/china/${selectedProvince.code}/${selectedCity.cityCode}"
            queryFromService(url,"county")
        }
    }

    private fun queryFromService(url:String,type: String){
        mView.showProgress()
        HttpUtil.sendOkHttpRequest(url,object : okhttp3.Callback{
            override fun onResponse(call: Call?, response: Response) {
                val responseText=response.body().string()
                val result = when (type) {
                    "province" -> Utility.handlerProvince(responseText)
                    "city" -> Utility.handleCityResponse(responseText, selectedProvince.id)
                    "county" -> Utility.handleCountyResponse(responseText, selectedCity.id)
                    else -> false
                }
                if (result){
                    activity.runOnUiThread {
                        mView.closeProgress()
                        when(type){
                            "province" -> queryProvinces()
                            "city" -> queryCities()
                            "county" -> queryCounties()
                        }
                    }
                }
            }

            override fun onFailure(call: Call?, e: IOException?) {
                mView.closeProgress()
                mView.showMessage("加载失败")
            }
        })
    }
}