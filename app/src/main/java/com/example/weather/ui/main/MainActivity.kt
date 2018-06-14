package com.example.weather.ui.main

import android.Manifest
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
import com.example.weather.network.LocationHelper
import com.example.weather.base.BaseActivity
import com.example.weather.ui.citymanager.CityManagerActivity
import com.example.weather.mvp.contract.MainContract
import com.example.weather.mvp.presenter.MainPresenter
import com.example.weather.other.db.CityWeather
import com.example.weather.ui.AboutActivity
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

    override lateinit var presenter: MainContract.Presenter

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
        presenter.start()
        //MMP，activity里能调用成功，写在presenter中就无效
        LocationHelper.instance
                .locate {
                    Log.d("MainActivity", "BDCode: ${it.locType}")
                    presenter.start(it)
                }
    }

    private val titles = arrayListOf<String>()
    private fun initViewPager() {
        val alphaAnimation = AlphaAnimation(0f, 1f)
        alphaAnimation.duration = 260
        alphaAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                window.setBackgroundDrawable(//getResources().getDrawable(R.drawable.window_frame_color));
                        ColorDrawable(Color.BLACK))
                //				WeatherNotificationService.startServiceWithNothing(MainActivity.this);
            }

            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {}
        })
        viewPager.setAnimation(alphaAnimation)

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

        viewPager.offscreenPageLimit = mAdapter.count//设置预加载的页数
        viewPager.adapter = mAdapter
        pageTitle.setViewPager(viewPager)
    }

    private fun initNavView() {
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_city_manager -> {
                    CityManagerActivity.start(this@MainActivity)
                }
                R.id.nav_about -> {
                    startActivity(Intent(this@MainActivity,AboutActivity::class.java))
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
                                        val shareData =(mAdapter.getItem(viewPager.currentItem) as WeatherFragment).shareData
                                        if (shareData!=null)
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
    private lateinit var mAdapter: FragmentPagerAdapter
    private var fragmentId = 0
    override fun initFragment(list: MutableList<CityWeather>) {
        //TODO: 测试用
//        if (list.size <= 1 && list.size > 0) {
//            list.add(list[0])
//            CityWeather("嵊州市").save()
//        }

        list
        if (fragments.size > 0) //因为该方法会重复调用，所以每次都需要吧其中的list清空
            fragments.clear()
        for ((i, item) in list.withIndex()) {
            fragments.add(WeatherFragment.newInstance(item.countyName, i))
            fragmentId = i
        }
        fragments
        if(titles.size>0) titles.clear()
        list.forEach { titles.add(it.countyName) }
        mAdapter.notifyDataSetChanged()
        pageTitle.notifyDataSetChanged()
        viewPager.currentItem=0
    }

    override fun deleteFragment(deletePosition: Int) {
        fragments.removeAt(deletePosition)
        mAdapter.notifyDataSetChanged()
        pageTitle.notifyDataSetChanged()
        Log.d("MainActivity","deleteFragment")
    }

    //添加后，MainActivity没有调用，删除倒是调用了
    override fun addFragment(countyName: String) {
        Log.d("MainActivity","addFragment")
        fragmentId++
        val fragment = WeatherFragment.newInstance(countyName, fragmentId)
        fragments.add(fragment)
        mAdapter.notifyDataSetChanged()
        pageTitle.notifyDataSetChanged()
    }

    override fun swipeFragment() {
        val list=DataSupport.findAll(CityWeather::class.java)
        fragments.clear()
        for ((i, item) in list.withIndex()) {
            fragments.add(WeatherFragment.newInstance(item.countyName, i))
            fragmentId = i
        }
        mAdapter.notifyDataSetChanged()
        pageTitle.notifyDataSetChanged()
    }

    override fun reduceFragment(pos: Int, countyName: String) {

    }

    override fun showErrorMessage(message: String) {
        toast(message)
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}
