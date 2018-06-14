package com.example.weather.mvp.presenter

import android.annotation.SuppressLint
import android.util.Log
import com.example.weather.MyApp
import com.example.weather.mvp.contract.WeatherContract
import com.example.weather.network.api.RetrofitHelper
import com.example.weather.network.gson.HeLifeStyle
import com.example.weather.network.gson.HeWeather
import com.example.weather.util.HttpUtil
import com.example.weather.util.Utility
import com.example.wanandroidtest.util.applyScheduler
import io.reactivex.Observable
import okhttp3.Call
import okhttp3.Response
import org.jetbrains.anko.runOnUiThread
import java.io.IOException

class WeatherPresenter(val view: WeatherContract.View)
    : WeatherContract.Presenter{

    init {
        view.presenter=this
    }

    private val model by lazy { RetrofitHelper.instance.getWeatherApi() }

    override fun getWeather(cityName: String){
       addSubscribe( Observable.merge(model.getWeather(cityName),model.getLifeStyle(cityName))
               .applyScheduler()
               .subscribe({
                   when(it){
                       is HeWeather ->{
                           if (it.heWeather6[0].status=="ok")
                               view.showWeatherInfo(it.heWeather6[0].daily_forecast)
                           else
                               view.showMessage("访问天气数据失败")
                       }
                       is HeLifeStyle ->{
                           view.showLifeStyle(it)
                       }
                   }
               },{
                   Log.d("MainPresenter","error Message::::")
                   it.printStackTrace()
               }))
    }

    /**
     * 根据天气id请求天气信息
     * @param weatherId 例如"CN101010100"
     */
    fun requestWeather(weatherId: String) {
        val weatherUrl = "http://guolin.tech/api/weather?cityid=$weatherId&key=df7cda9693794b85a9ffc8fdb781230c"
        HttpUtil.sendOkHttpRequest(weatherUrl, object : okhttp3.Callback {
            override fun onFailure(call: Call?, e: IOException?) {

            }

            @SuppressLint("CommitPrefEdits")
            override fun onResponse(call: Call?, response: Response) {
                val responseText = response.body()!!.string()
                val weather = Utility.handleWeatherResponse(responseText)
                MyApp.instance.runOnUiThread {
                    view.showMessage("network success")
                }
//                activity.runOnUiThread {
//                    if (weather != null && "ok".equals(weather.status)) {
//                        val editor = PreferenceManager.getDefaultSharedPreferences(activity)
//                                .edit()
//                        editor.putString("weather", responseText)
//                        editor.apply()
////                        view.showWeatherInfo(weather)
//                    }
//                    }else
//                     view.showFail()
                }
            })
    }

}