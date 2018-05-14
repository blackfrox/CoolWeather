package com.example.coolweather.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.example.coolweather.AutoUpdateService
import com.example.coolweather.R
import com.example.coolweather.contract.WeatherContract
import com.example.coolweather.gson.Weather
import com.example.coolweather.presenter.WeatherPresenter
import com.example.coolweather.util.HttpUtil
import com.example.coolweather.util.Utility
import com.example.coolweather.util.translateStatusBar
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

class WeatherActivity : AppCompatActivity(),WeatherContract.View {

    override lateinit var presenter: WeatherContract.Presenter

//    private val TAG="Weather"
    companion object {
       private val WEATHER_ID="weather_id"
        fun launch(context: Context,weatherId: String?=null){
            val intent=Intent(context, WeatherActivity::class.java)
            if (weatherId!=null)
                intent.putExtra(WEATHER_ID,weatherId)
            context.startActivity(intent)
        }
    }

    private lateinit var mWeatherId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        translateStatusBar()
        setContentView(R.layout.activity_weather)

        initDrawerAndToolbar()

        WeatherPresenter(this,this)
        presenter.start()

        swipeLayout.apply {
            setColorSchemeResources(R.color.colorPrimary)
            setOnRefreshListener {
                presenter.start()
            }
        }
    }

    private fun initDrawerAndToolbar() {
        val toggle = ActionBarDrawerToggle(
                this, drawLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun getWeatherId(): String {
        return intent.getStringExtra(WEATHER_ID)
    }
    override fun showView() {
        weatherLayout.visibility= View.VISIBLE
    }

    override fun showBingPic(bingPic: String) {
        Glide.with(this).load(bingPic).into(bingPicImg)
    }

    override fun showFail() {
        toast("获取天气信息失败")
        swipeLayout.isRefreshing=false
    }

    /**
     * 处理并展示天气数据
     */
    @SuppressLint("SetTextI18n")
    override fun showWeatherInfo(weather: Weather?){
        swipeLayout.isRefreshing=false
        if (weather!=null&&"ok".equals(weather.status)){
            val i=Intent(this,AutoUpdateService::class.java)
            startService(i)
            with(weather){

                titleTv.text=basic.cityName
                updateTimeTv.text=basic.update.updateTime.split(" ")[1] //updateTime: 2018-04-11 13:47
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
