package com.example.coolweather.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.example.coolweather.AutoUpdateService
import com.example.coolweather.R
import com.example.coolweather.gson.Weather
import com.example.coolweather.util.HttpUtil
import com.example.coolweather.util.Utility
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.aqi.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.forecast_item.view.*
import kotlinx.android.synthetic.main.now.*
import kotlinx.android.synthetic.main.suggestion.*
import kotlinx.android.synthetic.main.title.*
import okhttp3.Call
import okhttp3.Response
import org.jetbrains.anko.toast
import java.io.IOException

class WeatherActivity : AppCompatActivity() {

    private val TAG="Weather"
    companion object {

        fun launch(context: Context,weatherId: String?=null){
            val intent=Intent(context, WeatherActivity::class.java)
            if (weatherId!=null)
                intent.putExtra("weather_id",weatherId)
            context.startActivity(intent)
        }
    }
    private lateinit var mWeatherId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //让背景图和状态栏融合到一起
        if (Build.VERSION.SDK_INT>=21){
            val decorView=window.decorView
            decorView.systemUiVisibility=View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor=Color.TRANSPARENT
        }
        setContentView(R.layout.activity_weather)

        val toggle = ActionBarDrawerToggle(
                this, drawLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawLayout.addDrawerListener(toggle)
        toggle.syncState()

        val prefs=PreferenceManager.getDefaultSharedPreferences(this)
        val weatherString=prefs.getString("weather",null)
        if (weatherString!=null){
            //有缓存时直接解析数据天气数据
            val weather=Utility.handleWeatherResponse(weatherString)
            if (weather!=null) mWeatherId=weather.basic.weatherId
            showWeatherInfo(weather)
        }else{
            //无缓存时去服务器查询天气
            mWeatherId=intent.getStringExtra("weather_id")
            weatherLayout.visibility=View.VISIBLE
            requestWeather(mWeatherId)
        }
        val bingPic=prefs.getString("bing_pic",null)
        if (bingPic!=null)
            Glide.with(this).load(bingPic).into(bingPicImg)
        else
            loadBingPic()
        swipeLayout.setColorSchemeResources(R.color.colorPrimary)
        swipeLayout.setOnRefreshListener {
            requestWeather(mWeatherId)
        }
    }

    private fun loadBingPic() {
        val url="http://guolin.tech/api/bing_pic"
        HttpUtil.sendOkHttpRequest(url,object : okhttp3.Callback{
            override fun onResponse(call: Call?, response: Response) {
                val responseText=response.body().string()
               runOnUiThread {
                   val editor=PreferenceManager.getDefaultSharedPreferences(this@WeatherActivity)
                           .edit()
                   editor.putString("bing_pic",responseText)
                   editor.apply()
                   Glide.with(this@WeatherActivity).load(responseText).into(bingPicImg)
               }
            }

            override fun onFailure(call: Call?, e: IOException?) {
                e?.printStackTrace()
            }
        })
    }

    /**
     * 根据天气id请求天气信息
     */
    fun requestWeather(weatherId: String){
        val weatherUrl= "http://guolin.tech/api/weather?cityid=" +
                "$weatherId&key=bc0418b57b2d4918819d3974ac1285d9"
        HttpUtil.sendOkHttpRequest(weatherUrl,object : okhttp3.Callback{
            @SuppressLint("CommitPrefEdits")
            override fun onResponse(call: Call?, response: Response) {
                val responseText=response.body().string()
                val weather=Utility.handleWeatherResponse(responseText)
                runOnUiThread {
                    if (weather!=null&&"ok".equals(weather.status)){
                        val editor=PreferenceManager.getDefaultSharedPreferences(this@WeatherActivity)
                                .edit()
                        editor.putString("weather",responseText)
                        editor.apply()
                        showWeatherInfo(weather)
                    }else
                        toast("获取天气信息失败")
                    swipeLayout.isRefreshing=false
                }
            }

            override fun onFailure(call: Call?, e: IOException?) {
                runOnUiThread {
                    toast("获取天气信息失败")
                    swipeLayout.isRefreshing=false
                }
            }
        })
    }

    /**
     * 处理并展示天气数据
     */
    @SuppressLint("SetTextI18n")
    private fun showWeatherInfo(weather: Weather?){
        if (weather!=null&&"ok".equals(weather.status)){
            val i=Intent(this,AutoUpdateService::class.java)
            startService(i)
            with(weather){

                titleTv.text=basic.cityName
                Log.d(TAG,"tv: ${basic.cityName}")
                updateTimeTv.text=basic.update.updateTime.split(" ")[1] //updateTime: 2018-04-11 13:47
//                Log.d(TAG,"updateTime: ${basic.update.updateTime}")
                degreeTv.text=now.temperature+"℃"
                infoTv.text=now.more.info
                //将天气信息动态添加到view中
                forecastLayout.removeAllViews()
                forecastList.forEach {
                    val view=LayoutInflater.from(this@WeatherActivity).inflate(R.layout.forecast_item,forecastLayout,false)
                    with(view) {
                        with(it) {
                            dateTv.text = date
                            infoTv.text = more.info
                            maxTv.text = temperature.max
                            minTv.text = temperature.min
                        }
                    }
                    forecastLayout.addView(view)
                }
                aqiTv.text=aqi.city.aqi
                pm25Tv.text=aqi.city.pm25
                with(suggestion){
                    comfortTv.text=comfort.info
                    carwashTv.text=carWash.info
                    sportTv.text=sport.info
                }
                weatherLayout.visibility=View.VISIBLE
            }
        }
    }

    override fun onBackPressed() {
        if (drawLayout.isDrawerOpen(GravityCompat.START)) {
            drawLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
