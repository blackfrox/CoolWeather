package com.example.coolweather.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import com.example.coolweather.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val prefs=PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs.getString("weather",null)!=null){
            WeatherActivity.launch(this)
            finish()
        }
    }

    override fun onBackPressed() {
        if (choose_fragment is ChooseFragment)
            (choose_fragment as ChooseFragment).onBackPress()
        else
            super.onBackPressed()
    }
}
