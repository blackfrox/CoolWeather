package com.example.weather.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.annotation.IdRes
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import java.text.SimpleDateFormat
import java.util.*


fun AppCompatActivity.setupActionBar(@IdRes toolbarId: Int, action: ActionBar.() -> Unit) {
    setSupportActionBar(findViewById(toolbarId))
    supportActionBar?.run {
        action()
    }
}

fun AppCompatActivity.initToolbar(toolbar: Toolbar) {
    setSupportActionBar(toolbar)
    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    toolbar.setNavigationOnClickListener {
        onBackPressed()
    }
}

fun Context.startActivity(clazz: Class<Activity>) {
    startActivity(Intent(this, clazz::class.java))
}


/**
 * 获取当前时间 格式: yyyy年MM月dd日    HH:mm:ss
 */
@SuppressLint("SimpleDateFormat")
fun getCurrentTime(): String? {
    val formatter = SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss     ")
    val curDate = Date(System.currentTimeMillis())//获取当前时间
    return formatter.format(curDate)
}
