package com.example.coolweather.util

import android.graphics.Color
import android.os.Build
import android.support.annotation.IdRes
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.View

/**
 *让背景图和状态栏融合到一起(需要写在setContentView之前才生效)
 */
fun AppCompatActivity.translateStatusBar(){
    if (Build.VERSION.SDK_INT>=21){
        val decorView=window.decorView
        decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor= Color.TRANSPARENT
    }
}
fun AppCompatActivity.setupActionBar(@IdRes toolbarId: Int,action: ActionBar.()-> Unit){
    setSupportActionBar(findViewById(toolbarId))
    supportActionBar?.run {
        action()
    }
}
