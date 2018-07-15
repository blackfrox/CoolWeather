package com.example.weather.ui

import android.os.Bundle
import com.example.weather.R
import com.example.weather.base.BaseActivity

/**
 * 原本打算使用RxBus+SplashActivity优化冷启动，但是算了，以后再说，优化已经用代码混淆替代了
 */
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
