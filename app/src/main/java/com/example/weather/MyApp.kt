package com.example.weather

import android.app.Application
import com.tencent.bugly.crashreport.CrashReport
import org.litepal.LitePal
import kotlin.properties.Delegates

//注: 别忘了把MyApp的名字注册到manifests中的application里,不然不起作用
class MyApp : Application() {

    companion object {
        var instance by Delegates.notNull<MyApp>()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        LitePal.initialize(this) //litePal
        CrashReport.initCrashReport(getApplicationContext(), "35e91c27-bb7d-4fd5-b474-14f96504202b", false) //bugly
    }
}