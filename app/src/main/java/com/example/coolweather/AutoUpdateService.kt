package com.example.coolweather

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import android.preference.PreferenceManager
import com.example.coolweather.util.HttpUtil
import com.example.coolweather.util.Utility
import okhttp3.Call
import okhttp3.Response
import java.io.IOException

class AutoUpdateService : Service() {

    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        updateWeather()
        updateBingPic()
        val manager=getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time=8*60*60*1000 //8小时的毫秒数
        val triggerAtTime=SystemClock.elapsedRealtime()+time
        val i=Intent(this,AutoUpdateService::class.java)
        val pi=PendingIntent.getService(this,0,i,0)
        manager.cancel(pi)
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun updateBingPic() {
        val url="http://guolin.tech/api/bing_pic"
        HttpUtil.sendOkHttpRequest(url,object : okhttp3.Callback{
            override fun onResponse(call: Call?, response: Response) {
                val responseText=response.body().string()
                prefs.edit()
                        .putString("bing_pic",responseText)
                        .apply()
            }

            override fun onFailure(call: Call?, e: IOException?) {
                e?.printStackTrace()
            }
        })
    }

    private fun updateWeather() {
        val weatherString=prefs.getString("weather",null)
        if (weatherString!=null){
            //有缓存直接解析天气数据
            val weather=Utility.handleWeatherResponse(weatherString)
            if (weather!=null){
                val weatherId=weather.basic.weatherId
                val weatherUrl= "http://guolin.tech/api/weather?cityid=" +
                        "$weatherId&key=bc0418b57b2d4918819d3974ac1285d9"
                HttpUtil.sendOkHttpRequest(weatherUrl,object : okhttp3.Callback{
                    override fun onResponse(call: Call?, response: Response) {
                        val responseText=response.body().string()
                        val editor=prefs.edit()
                        editor.putString("weather",responseText)
                                .apply()
                    }

                    override fun onFailure(call: Call?, e: IOException?) {
                        e?.printStackTrace()
                    }
                })
            }
        }
    }


}
