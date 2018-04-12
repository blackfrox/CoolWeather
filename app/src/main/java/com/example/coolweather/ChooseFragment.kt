package com.example.coolweather

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.coolweather.db.City
import com.example.coolweather.db.County
import com.example.coolweather.db.Province
import com.example.coolweather.ui.MainActivity
import com.example.coolweather.ui.WeatherActivity
import com.example.coolweather.util.HttpUtil
import com.example.coolweather.util.Utility
import kotlinx.android.synthetic.main.fragment_choose_area.*
import okhttp3.Call
import okhttp3.Response
import org.jetbrains.anko.support.v4.act
import org.jetbrains.anko.support.v4.toast
import org.litepal.crud.DataSupport
import java.io.IOException
import kotlinx.android.synthetic.main.activity_weather.*

/**
 * Created by Administrator on 2018/4/9 0009.
 */
class ChooseFragment: Fragment() {

    private val LEVEL_PROVINCE=1
    private val LEVEL_CITY=2
    private val LEVEL_COUNTY=3

    private var currentLevel=LEVEL_PROVINCE

    private lateinit var provinceList: List<Province>
    private lateinit var cityList: List<City>
    private lateinit var countyList: List<County>

    private lateinit var selectedProvince: Province
    private lateinit var selectedCity: City
    private lateinit var selectedCounty: County

    private var progressDialog: ProgressDialog?=null

    private val URL="http://guolin.tech/api/china/"
    private val dataList = arrayListOf<String>()
    private val  mAdapter by lazy { ArrayAdapter(context,android.R.layout.simple_list_item_1, dataList) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater
                .inflate(R.layout.fragment_choose_area,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listView.adapter=mAdapter
        listView.setOnItemClickListener { parent, view, position, id ->
            when(currentLevel){
                LEVEL_PROVINCE ->{
                    selectedProvince=provinceList.get(position)
                    queryCities()
                }
                LEVEL_CITY ->{
                    selectedCity=cityList[position]
                    queryCounties()
                }
                LEVEL_COUNTY ->{
                    val weatherId=countyList[position].weatherId
                    when(activity){
                        is MainActivity ->{
                            WeatherActivity.launch(activity,weatherId!!)
                            activity.finish()
                        }
                        is WeatherActivity ->{
                            with(activity){
                                drawLayout.closeDrawer(Gravity.START)
                                swipeLayout.isRefreshing=true
                                if (weatherId != null) {
                                    (activity as WeatherActivity).requestWeather(weatherId)
                                }
                            }
                        }
                    }
                }
            }
        }
        backBtn.setOnClickListener {
            when(currentLevel){
                LEVEL_COUNTY -> queryCities()
                LEVEL_CITY -> queryProvinces()
            }
        }
        queryProvinces()
    }

    /**
     * 优先查询数据库,其次网络
     */
    private fun queryProvinces(){
        title.text="中国"
        backBtn.visibility=View.GONE
        provinceList=DataSupport.findAll(Province::class.java)
        if (provinceList.size>0){
            dataList.clear()
//            for (i in 0 until provinceList.size) {
//                val name = provinceList[i].name
//                dataList.add(name)
//            }
            provinceList.forEach { dataList.add(it.name) }
            mAdapter.notifyDataSetChanged()
            listView.setSelection(0)
            currentLevel=LEVEL_PROVINCE
        }else{
            val url="http://guolin.tech/api/China/"
            queryFromService(URL,"province")
        }
    }

    /**
     * 查询市级
     */
    private fun queryCities(){
        title.text=selectedProvince.name
        backBtn.visibility=View.VISIBLE
        cityList=DataSupport.where("provinceid=?","${selectedProvince.id}")
                .find(City::class.java)
        if (cityList.size>0){
            dataList.clear()
            cityList.forEach { dataList.add(it.cityName) }
            mAdapter.notifyDataSetChanged()
            listView.setSelection(0)
            currentLevel=LEVEL_CITY
        }else{
            val url="http://guolin.tech/api/china/${selectedProvince.code}"
            queryFromService(url,"city")
        }
    }

    /**
     * 查询县级数据
     */
    private fun queryCounties(){
        title.text=selectedCity.cityName
        backBtn.visibility=View.VISIBLE
        countyList=DataSupport.where("cityid=?","${selectedCity.id}")
                .find(County::class.java)
        if (countyList.size>0){
            dataList.clear()
            countyList.forEach { dataList.add(it.countyName) }
            mAdapter.notifyDataSetChanged()
            listView.setSelection(0)
            currentLevel=LEVEL_COUNTY
        }else{
            val url="http://guolin.tech/api/china/${selectedProvince.code}/${selectedCity.cityCode}"
            queryFromService(url,"county")
        }
    }

    private fun queryFromService(url:String,type: String){
        showProgress()
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
                       closeProgress()
                       when(type){
                           "province" -> queryProvinces()
                           "city" -> queryCities()
                           "county" -> queryCounties()
                       }
                   }
                }
            }

            override fun onFailure(call: Call?, e: IOException?) {
                closeProgress()
                toast("加载失败")
            }
        })
    }

    private fun showProgress(){
        if (progressDialog==null){
            progressDialog= ProgressDialog(activity)
            progressDialog!!.setMessage("正在加载...")
            progressDialog!!.setCancelable(false)
        }
        progressDialog?.show()
    }

    private fun closeProgress(){
        progressDialog?.dismiss()
    }

    fun onBackPress(){
        backBtn.performClick()
    }
}