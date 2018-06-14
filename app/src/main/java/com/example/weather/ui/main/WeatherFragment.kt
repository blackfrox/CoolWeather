package com.example.weather.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.weather.R
import com.example.weather.base.BaseFragment
import com.example.weather.mvp.contract.WeatherContract
import com.example.weather.mvp.presenter.WeatherPresenter
import com.example.weather.other.db.CityWeather
import com.example.weather.network.gson.HeLifeStyle
import com.example.weather.network.gson.HeWeather
import com.example.weather.other.ShareData
import com.example.weather.util.getCurrentTime
import com.example.weather.util.getWeek
import com.example.weather.util.parse
import kotlinx.android.synthetic.main.fragment_weather.*
import kotlinx.android.synthetic.main.suggestion.*
import org.jetbrains.anko.support.v4.toast
import org.litepal.crud.DataSupport

/**
 *
 *  主界面上半部分 显示：城市名，天气，温度
 */
class WeatherFragment : BaseFragment(), WeatherContract.View {

    override lateinit var presenter: WeatherContract.Presenter
    override fun getLayoutId(): Int {
        return R.layout.fragment_weather
    }

    private var mId: Int = 0 //好像没用，mId的值都没变过
    var cityWeather : CityWeather?=null
    private lateinit var countyName: String
    var shareData: ShareData?=null //作为分享时候的数据
    override fun initView(savedInstanceState: Bundle?) {
        val countyName = arguments?.getString(COUNTY_NAME)!!
        mId = arguments?.getInt(ID)!!
        cityWeather=DataSupport.find(CityWeather::class.java, mId.toLong())
        presenter = WeatherPresenter(this)
        if (!TextUtils.isEmpty(countyName)){
            presenter.getWeather(countyName)
            cityWeather?.countyName = countyName
            this@WeatherFragment.countyName=countyName
        }

    }

    @SuppressLint("SetTextI18n")
    override fun showWeatherInfo(list: MutableList<HeWeather.HeWeather6Bean.DailyForecastBean>) {
        list[0].apply {
            weatherTv.text = cond_txt_d
            val tmp=(tmp_min.toInt() + tmp_max.toInt()) / 2
            tempTv.text = tmp.toString()
            cityWeather?.tmp = tmp.toString()
            cityWeather?.weather=cond_txt_d
            cityWeather?.save()
            shareData= ShareData(countyName,"$cond_txt_d","$tmp℃","",
                    "今天:　${getWeek(date)}  $cond_txt_d  $tmp_min~$tmp_max℃",
                    "明天: ${getWeek(list[1].date)} ${list[1].cond_txt_d}  ${list[1].tmp_min}~${list[1].tmp_max}℃")
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
                    .apply { orientation = LinearLayoutManager.HORIZONTAL }
            adapter = object : BaseQuickAdapter<HeWeather.HeWeather6Bean.DailyForecastBean, BaseViewHolder>
            (R.layout.item_forecast, list) {
                override fun convert(helper: BaseViewHolder, item: HeWeather.HeWeather6Bean.DailyForecastBean) {
                    with(helper) {
                        with(item) {
                            val time = if (date == getCurrentTime()) "今天" else getWeek(date)
                            setText(R.id.forecast_time, time)
                            setText(R.id.forecast_temp, "$tmp_max°/$tmp_min°")
                            getView<ImageView>(R.id.forecast_icon)
                                    .setImageResource(parse(cond_code_d))
                        }
                    }
                }
            }
        }

    }

    override fun showLifeStyle(heLifeStyle: HeLifeStyle) {
        heLifeStyle.heWeather6[0].lifestyle.apply {
            for (item in this){
                item.apply {
                    when(type){
                        "comf" -> comfortTv.text=txt
                        "sport" -> sportTv.text=txt
                        "cw" -> carWashTv.text=txt
                        "air" -> {
                            aqiTv.text=brf
                            shareData?.aqi=brf
                        }
                    }
                }
            }
        }
        //为了解决recyclerView还没显示的画面  不知道行不行
        suggestion_root.postDelayed({
            suggestion_root.visibility=View.VISIBLE
        },500)
    }
    override fun showMessage(message: String) {
        toast(message)
    }

    companion object {
        private val COUNTY_NAME = "item"
        private val ID = "id"
        /**
         * @param id 代表当前fragment在viewPager中的位置
         */
        fun newInstance(countyName: String, id: Int): WeatherFragment = WeatherFragment().apply {
            val arg = Bundle()
                    .apply {
                        putString(COUNTY_NAME, countyName)
                        putInt(ID, id)
                    }
            arguments = arg
        }

    }


}