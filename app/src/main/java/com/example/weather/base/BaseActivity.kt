package com.example.weather.base

import android.os.Bundle
import android.support.annotation.StyleRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import com.example.weather.R
import com.example.weather.util.LogUtil
import com.example.weather.util.SettingsUtil
import com.example.weather.util.StatusBarUtil


abstract class  BaseActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //主题切换
        initTheme()
        setContentView(getLayoutId())
        //沉浸式状态栏
        StatusBarUtil.immersive(this)
        val toolbar =findViewById<Toolbar>(R.id.toolbar)
        if (toolbar!=null) StatusBarUtil.setPaddingSmart(this,toolbar)
        //给外部使用
        initView(savedInstanceState)
    }

    abstract fun getLayoutId(): Int //当前布局的id

    abstract fun initView(savedInstanceState: Bundle?)



    protected fun initTheme() {
        val themeIndex = SettingsUtil.theme
        LogUtil.d("BaseActivity","currentActivity $packageName,theme: ${SettingsUtil.theme}")
        when (themeIndex) {
            0 -> setTheme(R.style.LapisBlueTheme)
            1 -> setTheme(R.style.PaleDogwoodTheme)
            2 -> setTheme(R.style.GreeneryTheme)
            3 -> setTheme(R.style.PrimroseYellowTheme)
            4 -> setTheme(R.style.FlameTheme)
            5 -> setTheme(R.style.IslandParadiseTheme)
            6 -> setTheme(R.style.KaleTheme)
            7 -> setTheme(R.style.PinkYarrowTheme)
            8 -> setTheme(R.style.NiagaraTheme)
        }
    }


    protected fun switchTheme(selectedColor: Int) {
        resources.apply {
            when (selectedColor) {
                getColor(R.color.lapis_blue) -> setTheme(R.style.LapisBlueTheme, 0)
                getColor(R.color.pale_dogwood) -> setTheme(R.style.PaleDogwoodTheme, 1)
                getColor(R.color.greenery) -> setTheme(R.style.GreeneryTheme, 2)
                getColor(R.color.primrose_yellow) -> setTheme(R.style.PrimroseYellowTheme, 3)
                getColor(R.color.flame) -> setTheme(R.style.FlameTheme, 4)
                getColor(R.color.island_paradise) -> setTheme(R.style.IslandParadiseTheme, 5)
                getColor(R.color.kale) -> setTheme(R.style.KaleTheme, 6)
                getColor(R.color.pink_yarrow) -> setTheme(R.style.PinkYarrowTheme, 7)
                getColor(R.color.niagara) -> setTheme(R.style.NiagaraTheme, 8)
            }
        }
    }

    private fun setTheme(@StyleRes style: Int, id: Int) {
        setTheme(style)
        SettingsUtil.theme=id
        LogUtil.d("SettingActivity","theme is ${SettingsUtil.theme}")
    }


}