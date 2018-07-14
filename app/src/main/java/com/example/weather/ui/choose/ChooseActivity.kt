package com.example.weather.ui.choose

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.weather.R
import com.example.weather.base.BaseActivity
import org.jetbrains.anko.toast

class ChooseActivity : BaseActivity(){

    companion object {
        fun laugch(context: Context){
            context.startActivity(Intent(context, ChooseActivity::class.java))
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_choose
    }

    override fun initView(savedInstanceState: Bundle?) {

    }


}
