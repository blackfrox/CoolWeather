package com.example.weather.ui.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.Gravity
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.example.weather.R
import com.example.weather.base.BaseActivity
import com.example.weather.mvp.contract.MainContract
import com.example.weather.mvp.presenter.MainPresenter
import com.example.weather.network.LocationHelper
import com.example.weather.other.db.CityWeather
import com.example.weather.ui.AboutActivity
import com.example.weather.ui.citymanager.CityManagerActivity
import com.example.weather.util.LogUtil
import com.example.weather.util.ShareUtils
import com.example.weather.util.StatusBarUtil
import com.example.weather.util.WeatherUtil
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import org.litepal.crud.DataSupport

/**
 * ViewPager+Fragment
 */
class MainActivity : BaseActivity(), MainContract.View {

    companion object {
        val CHANGE = "change"
        val SELECTED_ITEM = "selected_item"
    }

    override lateinit var presenter: MainContract.Presenter

    //todo: (不要紧)想在base基类中添加presenter.onDestroy()反而要写更多的代码，以后有机会优化一下，现在先这么写
    //todo : 不过也不是全部的Activity/Fragment都需要，所以好像也没封装的必要
    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initView(savedInstanceState: Bundle?) {
        //todo: 测试用，否则注释掉
//        CityManagerActivity.start(this)
        initToolbar()
        initNavView()
        initViewPager()

        MainPresenter(this, this)
        presenter.start() //申请定位权限
        //MMP，activity里能调用成功，写在presenter中就无效
        LocationHelper.instance
                .locate {
                    presenter.start(it)
                }
    }

    private fun initViewPager() {
        val alphaAnimation = AlphaAnimation(0f, 1f)
                .apply {
                    duration = 260
                    setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation) {
                            window.setBackgroundDrawable(//getResources().getDrawable(R.drawable.window_frame_color));
                                    ColorDrawable(Color.BLACK))
                            //TODO: 增加后台更新服务
                            //				WeatherNotificationService.startServiceWithNothing(MainActivity.this); //设置前台服务，保活机制
                        }

                        override fun onAnimationRepeat(animation: Animation) {}
                        override fun onAnimationEnd(animation: Animation) {}
                    })
                }

        mAdapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return fragments[position]
            }

            override fun getCount(): Int {
                return fragments.size
            }

            override fun getPageTitle(position: Int): CharSequence {
                return titles[position]
            }
        }

        viewPager.apply {
            setAnimation(alphaAnimation)
//            offscreenPageLimit = mAdapter.count//设置预加载的页数
            adapter = mAdapter
            //解决viewPager和swipeRefreshLayout的滑动冲突
//            setOnTouchListener { v, event ->
//                when(event.action){
//                    MotionEvent.ACTION_MOVE -> swipeLayout.isEnabled=false
//                    MotionEvent.ACTION_UP,
//                    MotionEvent.ACTION_CANCEL -> swipeLayout.isEnabled=true
//                }
//               false
//            }
        }
        pageTitle.setViewPager(viewPager)
    }

    private fun initNavView() {
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_city_manager -> {
                    startActivityForResult(Intent(this@MainActivity, CityManagerActivity::class.java), 1)
                }
                R.id.nav_about -> {
                    startActivity(Intent(this@MainActivity, AboutActivity::class.java))
                }
            }
            drawer_layout.closeDrawer(Gravity.START)
            true
        }
    }

    private fun initToolbar() {
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        StatusBarUtil.setPaddingSmart(this, pageTitle)
        toolbar.apply {
            inflateMenu(R.menu.menu_main)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_share -> {
                        RxPermissions(this@MainActivity).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .subscribe {
                                    if (it) {
//                                        String shareType = SettingsUtil.getWeatherShareType();
//                                        if (shareType.equals("纯文本")) {
                                        val shareData = (mAdapter.getItem(viewPager.currentItem) as WeatherFragment).shareData
                                        if (shareData != null)
                                            ShareUtils.shareText(this@MainActivity,
                                                    WeatherUtil.getShareMessage(shareData), "分享到");
//                                        } else if (shareType.equals("仿锤子便签")) {
//                                            ShareActivity.start(getActivity(), WeatherUtil.getInstance().getShareMessage(weather));
//                                        }
                                    }
                                }

                    }
                }
                true
            }

        }
    }

    private val fragments by lazy { arrayListOf<Fragment>() }
    private val titles by lazy { arrayListOf<String>() }
    private lateinit var mAdapter: FragmentPagerAdapter
    override fun initFragment(list: MutableList<CityWeather>, selectedItem: Int) {
        //TODO: 测试用
//        if (list.size <= 1 && list.size > 0) {
//            list.add(list[0])
//            CityWeather("嵊州市").save()
//        }
        if (fragments.size > 0) //因为该方法会重复调用，所以每次都需要吧其中的list清空
            fragments.clear()
        for ((i, item) in list.withIndex()) {
            fragments.add(WeatherFragment.newInstance(item, i))
        }
        if (titles.size > 0) titles.clear()
        list.forEach { titles.add(it.countyName) }
        mAdapter.notifyDataSetChanged()
//        viewPager.offscreenPageLimit = mAdapter.getCount()
        pageTitle.notifyDataSetChanged()
        if (selectedItem > -1) {
            viewPager.currentItem = selectedItem
            LogUtil.d("MainActivity", "selectedItem: $selectedItem")
        }
//        if (selectedItem!=-1){
//            viewPager.currentItem=selectedItem
//            pageTitle.setCurrentItem(selectedItem)
//        }
    }


    override fun showErrorMessage(message: String) {
        toast(message)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (resultCode) {
            Activity.RESULT_OK -> {
//                val boolean=data.getBooleanExtra(CHANGE,false)
                val selectedItem = data.getIntExtra(SELECTED_ITEM, -1)
                presenter.refresh(selectedItem)
            }

        }
    }
}
