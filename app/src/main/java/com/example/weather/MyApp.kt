package com.example.weather

import android.app.Application
import com.example.weather.other.RxBus.RxBus
import com.example.weather.other.RxBus.event.InitApplicationEvent
import com.lljjcoder.style.citylist.utils.CityListLoader
import com.tencent.bugly.crashreport.CrashReport
import org.litepal.LitePal
import kotlin.properties.Delegates

//注: 别忘了把MyApp的名字注册到manifests中的application里,不然不起作用
class MyApp :Application() {

    companion object {
        var instance by Delegates.notNull<MyApp>()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        //将初始化交给RxBus，为了优化冷启动的时间效率
//        RxBus.instance.post(InitApplicationEvent())
        LitePal.initialize(this) //litePal
        /**
         * 预先加载一级列表显示 全国所有城市的数据
         */
        CityListLoader.getInstance().loadCityData(this)
        Thread({
            CrashReport.initCrashReport(getApplicationContext(), "35e91c27-bb7d-4fd5-b474-14f96504202b", false) //bugly
        }).start()
        }
}