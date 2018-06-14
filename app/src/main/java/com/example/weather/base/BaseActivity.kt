package com.example.weather.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.example.weather.R
import com.example.weather.util.StatusBarUtil
import me.yokeyword.fragmentation.SupportActivity
import org.jetbrains.anko.find

abstract class BaseActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        StatusBarUtil.immersive(this)
        val toolbar =findViewById<Toolbar>(R.id.toolbar)
        if (toolbar!=null) StatusBarUtil.setPaddingSmart(this,toolbar)
        initView(savedInstanceState)
    }

    abstract fun getLayoutId(): Int

    abstract fun initView(savedInstanceState: Bundle?)

}