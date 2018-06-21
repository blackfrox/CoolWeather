package com.example.weather.ui.choose

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.weather.R
import com.example.weather.base.BaseActivity

class ChooseActivity : AppCompatActivity() {

    companion object {
        fun laugch(context: Context){
            context.startActivity(Intent(context, ChooseActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)
    }
}
