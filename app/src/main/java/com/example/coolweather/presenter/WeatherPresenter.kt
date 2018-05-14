package com.example.coolweather.presenter

import android.annotation.SuppressLint
import android.content.Context
import android.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.example.coolweather.contract.WeatherContract
import com.example.coolweather.util.HttpUtil
import com.example.coolweather.util.Utility
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_weather.*
import okhttp3.Call
import okhttp3.Response
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import java.io.IOException

class WeatherPresenter(val view: WeatherContract.View,
                       val context: Context)
    :WeatherContract.Presenter{

    init {
        view.presenter=this
    }

    private lateinit var mWeatherId: String

    override fun start() {
        val prefs= PreferenceManager.getDefaultSharedPreferences(context)
        val weatherString=prefs.getString("weather",null)
        if (weatherString!=null){
            //有缓存时直接解析数据天气数据
            val weather= Utility.handleWeatherResponse(weatherString)
            if (weather!=null) mWeatherId=weather.basic.weatherId
            view.showWeatherInfo(weather)
        }else{
            //无缓存时去服务器查询天气
            mWeatherId=view.getWeatherId()
            view.showView()
            requestWeather(mWeatherId)
        }
        val bingPic=prefs.getString("bing_pic",null)
        if (bingPic!=null)
            view.showBingPic(bingPic)
        else
            loadBingPic()
    }

    /**
     * 根据天气id请求天气信息
     */
    override fun requestWeather(weatherId: String){
        val weatherUrl= "http://guolin.tech/api/weather?cityid=" +
                "$weatherId&key=bc0418b57b2d4918819d3974ac1285d9"
        HttpUtil.sendOkHttpRequest(weatherUrl,object : okhttp3.Callback{
            @SuppressLint("CommitPrefEdits")
            override fun onResponse(call: Call?, response: Response) {
                val responseText=response.body().string()
                val weather=Utility.handleWeatherResponse(responseText)
                context.runOnUiThread {
                    if (weather!=null&&"ok".equals(weather.status)){
                        val editor=PreferenceManager.getDefaultSharedPreferences(context)
                                .edit()
                        editor.putString("weather",responseText)
                        editor.apply()
                        view.showWeatherInfo(weather)
                    }else
                     view.showFail()
                }
            }

            override fun onFailure(call: Call?, e: IOException?) {
                Logger.d(call)
                context.runOnUiThread {
                    view.showFail()
                }
            }
        })
    }

    private fun loadBingPic() {
        val url="http://guolin.tech/api/bing_pic"
        HttpUtil.sendOkHttpRequest(url,object : okhttp3.Callback{
            override fun onResponse(call: Call?, response: Response) {
                val responseText=response.body().string()
                context.runOnUiThread {
                    val editor=PreferenceManager.getDefaultSharedPreferences(context)
                            .edit()
                    editor.putString("bing_pic",responseText)
                    editor.apply()
                    view.showBingPic(responseText)
                }
            }

            override fun onFailure(call: Call?, e: IOException?) {
                e?.printStackTrace()
            }
        })
    }
}