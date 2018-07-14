package com.example.weather

import android.content.Context
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.lljjcoder.style.citylist.utils.CityListLoader
import com.tencent.bugly.crashreport.CrashReport
import org.litepal.LitePal
import kotlin.properties.Delegates


class MyApp :MultiDexApplication() {

    companion object {
        var instance by Delegates.notNull<MyApp>()
    }

    override fun onCreate() {
        super.onCreate()
//        EventBus.getDefault().register(this)
        instance = this
        //将初始化交给RxBus，为了优化冷启动的时间效率

//        RxBus.instance.apply {
//            toFlowable(InitApplicationEvent::class.java)
//                    .subscribe {
//                        init()
//                        post(MainEvent())
//                    }
//        }

        Thread({
            init()
        }).start()
    }


    private fun init(){
        LitePal.initialize(this) //litePal
        /**
         * 预先加载一级列表显示 全国所有城市的数据
         */
        CityListLoader.getInstance().loadCityData(this)
        CrashReport.initCrashReport(getApplicationContext(), "35e91c27-bb7d-4fd5-b474-14f96504202b", false) //bugly

    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}