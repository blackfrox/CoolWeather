package com.example.weather.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.example.weather.R
import com.example.weather.util.StatusBarUtil


abstract class BaseActivity: AppCompatActivity(){

//    open lateinit var presenter: BasePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        //沉浸式状态栏
        StatusBarUtil.immersive(this)
        val toolbar =findViewById<Toolbar>(R.id.toolbar)
        if (toolbar!=null) StatusBarUtil.setPaddingSmart(this,toolbar)
        //
        initView(savedInstanceState)
    }

    abstract fun getLayoutId(): Int //当前布局的id

    abstract fun initView(savedInstanceState: Bundle?)

//    override fun onDestroy() {
//        super.onDestroy()
//        presenter.onDestroy()
//    }
}