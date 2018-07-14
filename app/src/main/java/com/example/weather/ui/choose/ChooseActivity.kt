package com.example.weather.ui.choose

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.wanandroidtest.util.applyScheduler
import com.example.weather.R
import com.example.weather.base.BaseActivity
import com.example.weather.mvp.contract.Choose00Contract
import com.example.weather.mvp.presenter.Choose00Presenter
import com.example.weather.network.api.RetrofitHelper
import com.example.weather.network.gson.HeHotCity
import com.example.weather.other.db.CityWeather
import com.example.weather.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_choose.*
import org.jetbrains.anko.toast
import org.litepal.crud.DataSupport

class ChooseActivity : BaseActivity() ,Choose00Contract.View{

    override lateinit var presenter: Choose00Contract.Presenter

    companion object {
        fun laugch(context: Context){
            context.startActivity(Intent(context, ChooseActivity::class.java))
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_choose
    }

    override fun initView(savedInstanceState: Bundle?) {
//        Choose00Presenter(this)
//        presenter.getHotCity()


    }




    override fun showMessage(message: String) {
        toast(message)
    }
}
