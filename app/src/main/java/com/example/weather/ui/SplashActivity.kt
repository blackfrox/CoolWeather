package com.example.weather.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.weather.R
import com.example.weather.base.BaseActivity
import com.example.weather.other.RxBus.RxBus
import com.example.weather.other.RxBus.event.InitApplicationEvent
import com.example.weather.other.RxBus.event.MainEvent
import com.example.weather.ui.main.MainActivity
import com.lljjcoder.style.citylist.utils.CityListLoader
import com.tencent.bugly.crashreport.CrashReport

import org.litepal.LitePal

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        EventBus.getDefault().register(this)
    }
    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun initView(savedInstanceState: Bundle?) {
//        RxBus.instance.apply {
//            post(InitApplicationEvent())
//            toFlowable(MainEvent::class.java)
//                    .subscribe {
//                        startActivity(Intent(this@SplashActivity,MainActivity::class.java))
//                        finish()
//                    }
//        }

    }



}
