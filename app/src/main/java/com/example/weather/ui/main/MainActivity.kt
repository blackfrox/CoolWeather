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
import android.view.Gravity
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.example.weather.R
import com.example.weather.base.BaseActivity
import com.example.weather.mvp.contract.MainContract
import com.example.weather.mvp.presenter.MainPresenter
import com.example.weather.network.LocationHelper
import com.example.weather.other.RxBus.event.ThemeChangedEvent
import com.example.weather.other.db.CityWeather
import com.example.weather.ui.AboutActivity
import com.example.weather.ui.citymanager.CityManagerActivity
import com.example.weather.ui.setting.SettingActivity
import com.example.weather.util.LogUtil
import com.example.weather.util.ShareUtils
import com.example.weather.util.StatusBarUtil
import com.example.weather.util.WeatherUtil
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.toast

/**
 * ViewPager+Fragment
 */
class MainActivity : BaseActivity(), MainContract.View {

    companion object {
        val CHANGE = "change"
        val SELECTED_ITEM = "selected_item"
    }

    override lateinit var presenter: MainContract.Presenter

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
        EventBus.getDefault().unregister(this)

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initView(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)

        //todo: 测试用，否则注释掉
//        startActivity(Intent(this,SettingActivity::class.java))
        initToolbar()
        initNavView()
        initViewPager()

        MainPresenter(this, this)
        //申请定位权限
        presenter.start()
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
//            setAnimation(alphaAnimation)
//            offscreenPageLimit = mAdapter.count//设置预加载的页数
            adapter = mAdapter
        }
        pageTitle.setViewPager(viewPager)
    }

    private fun initNavView() {
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_city_manager -> startActivityForResult(Intent(this@MainActivity, CityManagerActivity::class.java), 1)
                R.id.nav_settings -> startActivity(Intent(this@MainActivity,SettingActivity::class.java))
                R.id.nav_about -> startActivity(Intent(this@MainActivity, AboutActivity::class.java))
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
    //因为该方法会重复调用，记得将其中的所有list集合清空
    override fun initFragment(list: MutableList<CityWeather>, selectedItem: Int) {
        //TODO: 测试用
//        if (list.size <= 1 && list.size > 0) {
//            list.add(list[0])
//            CityWeather("嵊州市")
//              .save()
//        }
        if (fragments.size > 0)
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
            //            pageTitle.setCurrentItem(selectedItem)
            LogUtil.d("MainActivity", "selectedItem: $selectedItem")
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onThemeChangedEvent(event: ThemeChangedEvent){
       this@MainActivity.recreate()
//        initTheme()
//        switchTheme(event.themeColor)
    }

    override fun showThemeChange() {
//        recreate()
//        initTheme()
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
