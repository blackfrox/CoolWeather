package com.example.weather.ui.choose

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.weather.R
import com.example.weather.base.BaseActivity
import com.example.weather.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_choose.*

class ChooseActivity : BaseActivity() {

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
