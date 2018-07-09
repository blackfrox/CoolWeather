package com.example.weather.util

import android.util.Log

/**
 * 注意！！！,打包的时候别忘了把level变量改为NOTHING
 *
 * 作用: 方便在需要时屏蔽所有日志(因为打印日志不仅会降低程序的运行效率，还可能将一些机密性的数据泄露出去)
 *
 * 方法:　只需要修改level变量的值，就可以自由的控制日志的打印行为。
 * (比如让level等于VERBOSE就可以把所有日志都打印出来，
 * 让level等于WARN就可以只打印警告以上级别的日志，
 * 让level等于NOTHING,就可以把左右日志都屏蔽掉)
 */
object LogUtil {
    val VERBOSE= 1
    val DEBUG = 2
    val INFO =3
    val WARN=4
    val ERROR=5
    val NOTHING=6

    val level= VERBOSE

    fun v(tag: String,msg: String){
        if (level<= VERBOSE)
            Log.v(tag, msg)
    }

    fun d(tag: String,msg: String){
        if (level<= DEBUG)
            Log.d(tag, msg)
    }

    fun i(tag: String,msg: String){
        if (level<= INFO)
            Log.i(tag, msg)
    }

    fun w(tag: String,msg: String){
        if (level<= WARN)
            Log.w(tag, msg)
    }

    fun e(tag: String,msg: String){
        if (level<= ERROR)
            Log.e(tag, msg)
    }
}