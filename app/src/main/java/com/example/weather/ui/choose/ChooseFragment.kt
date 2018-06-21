package com.example.weather.ui.choose

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.weather.R
import com.example.weather.base.BaseFragment
import com.example.weather.mvp.contract.ChooseContract
import com.example.weather.mvp.presenter.ChoosePresenter
import kotlinx.android.synthetic.main.fragment_choose_area.*
import org.jetbrains.anko.support.v4.toast


/**
 * Created by Administrator on 2018/4/9 0009.
 */
class ChooseFragment: Fragment(),ChooseContract.View{

    override lateinit var presenter: ChooseContract.Presenter

    companion object {
         val LEVEL_PROVINCE=1
         val LEVEL_CITY=2
         val LEVEL_COUNTY=3
    }

    private var currentLevel= LEVEL_PROVINCE

    private var progressDialog: ProgressDialog?=null

    private val dataList = arrayListOf<String>()
    private val  mAdapter by lazy { ArrayAdapter(context,android.R.layout.simple_list_item_1, dataList) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_choose_area,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView(savedInstanceState)
        super.onViewCreated(view, savedInstanceState)
    }

    fun initView(savedInstanceState: Bundle?) {
        presenter=ChoosePresenter(this, activity!!)

        listView.adapter=mAdapter
        listView.setOnItemClickListener { _, _, position, _ ->
            when(currentLevel){
                LEVEL_PROVINCE ->{
                    presenter.queryCities(position)
                }
                LEVEL_CITY ->{
                    presenter.queryCounties(position)
                }
                LEVEL_COUNTY ->{
                    val countyName=presenter.countyList[position].countyName
                    val intent=Intent().apply {
                        putExtra("item",countyName)
                    }
                    activity?.setResult(Activity.RESULT_OK,intent)
                    activity?.finish()
                }
            }
        }
        backBtn.setOnClickListener {
            when(currentLevel){
                LEVEL_COUNTY -> presenter.queryCities()
                LEVEL_CITY -> presenter.queryProvinces()
                LEVEL_PROVINCE -> activity?.onBackPressed()
            }
        }
        presenter.queryProvinces()
    }

    override fun setupToolbar(title: String, showBack: Boolean) {
        this@ChooseFragment.title.text=title
        if (showBack)
            backBtn.visibility= View.VISIBLE
        else
            backBtn.visibility=View.GONE

    }

    override fun showChange(dataList: ArrayList<String>, level: Int) {
        this.dataList.clear()
        this.dataList.addAll(dataList)
        mAdapter.notifyDataSetChanged()
        listView.setSelection(0)
        currentLevel= level
    }

    override fun showProgress(){
        if (progressDialog==null){
            progressDialog= ProgressDialog(activity)
            progressDialog!!.setMessage("正在加载...")
            progressDialog!!.setCancelable(false)
        }
        progressDialog?.show()
    }

     override fun closeProgress(){
        progressDialog?.dismiss()
    }

    override fun showMessage(message: String) {
        toast(message)
    }

//  fun onBackPress(){
//        backBtn.performClick()
//    }

    /**
     * 优先查询数据库,其次网络
     */
//    private fun queryProvinces(){
//        title.text="中国"
//        backBtn.visibility=View.GONE
//        provinceList=DataSupport.findAll(Province::class.java)
//        if (provinceList.size>0){
//            dataList.clear()
//            provinceList.forEach { dataList.add(it.name) }
//            mAdapter.notifyDataSetChanged()
//            listView.setSelection(0)
//            currentLevel=LEVEL_PROVINCE
//        }else{
//            val url="http://guolin.tech/api/China/"
//            queryFromService(URL,"province")
//        }
//    }

    /**
     * 查询市级
     */
//    private fun queryCities(){
//        title.text=selectedProvince.name
//        backBtn.visibility=View.VISIBLE
//        cityList=DataSupport.where("provinceid=?","${selectedProvince.id}")
//                .find(City::class.java)
//        if (cityList.size>0){
//            dataList.clear()
//            cityList.forEach { dataList.add(it.countyName) }
//            mAdapter.notifyDataSetChanged()
//            listView.setSelection(0)
//            currentLevel=LEVEL_CITY
//        }else{
//            val url="http://guolin.tech/api/china/${selectedProvince.code}"
//            queryFromService(url,"city")
//        }
//    }

    /**
     * 查询县级数据
     */
//    private fun queryCounties(){
//        title.text=selectedCity.countyName
//        backBtn.visibility=View.VISIBLE
//        countyList=DataSupport.where("cityid=?","${selectedCity.id}")
//                .find(County::class.java)
//        if (countyList.size>0){
//            dataList.clear()
//            countyList.forEach { dataList.add(it.countyName) }
//            mAdapter.notifyDataSetChanged()
//            listView.setSelection(0)
//            currentLevel=LEVEL_COUNTY
//        }else{
//            val url="http://guolin.tech/api/china/${selectedProvince.code}/${selectedCity.cityCode}"
//            queryFromService(url,"county")
//        }
//    }

//    private fun queryFromService(url:String,type: String){
//        showProgress()
//        HttpUtil.sendOkHttpRequest(url,object : okhttp3.Callback{
//            override fun onResponse(call: Call?, response: Response) {
//                val responseText=response.body().string()
//                val result = when (type) {
//                    "province" -> Utility.handlerProvince(responseText)
//                    "city" -> Utility.handleCityResponse(responseText, selectedProvince.id)
//                    "county" -> Utility.handleCountyResponse(responseText, selectedCity.id)
//                    else -> false
//                }
//                if (result){
//                   activity.runOnUiThread {
//                       closeProgress()
//                       when(type){
//                           "province" -> queryProvinces()
//                           "city" -> queryCities()
//                           "county" -> queryCounties()
//                       }
//                   }
//                }
//            }
//
//            override fun onFailure(call: Call?, e: IOException?) {
//                closeProgress()
//                toast("加载失败")
//            }
//        })
//    }
}