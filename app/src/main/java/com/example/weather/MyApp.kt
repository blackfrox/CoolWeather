package com.example.weather

import android.app.Application
import com.tencent.bugly.crashreport.CrashReport
import org.litepal.LitePal
import kotlin.properties.Delegates

class MyApp: Application() {

    companion object {
        var instance by Delegates.notNull<MyApp>()
    }
    override fun onCreate() {
        super.onCreate()
        instance =this
        LitePal.initialize(this)
        CrashReport.initCrashReport(getApplicationContext(), "35e91c27-bb7d-4fd5-b474-14f96504202b", false);
    }
}