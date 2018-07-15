package com.example.weather.ui.setting

import android.os.Build
import android.os.Bundle
import com.afollestad.materialdialogs.color.ColorChooserDialog
import com.example.weather.R
import com.example.weather.base.BaseActivity
import com.example.weather.other.RxBus.RxBus
import com.example.weather.other.RxBus.event.ThemeChangedEvent
import com.example.weather.util.ThemeUtil
import com.example.weather.util.initToolbar
import kotlinx.android.synthetic.main.toolbar.*

class SettingActivity
    : BaseActivity(), ColorChooserDialog.ColorCallback {

    override fun getLayoutId(): Int {
        return R.layout.activity_setting
    }

    override fun initView(savedInstanceState: Bundle?) {
        initToolbar(toolbar)
        fragmentManager.beginTransaction().replace(R.id.container, SettingFragment()).commit()
    }

    override fun onColorSelection(dialog: ColorChooserDialog, selectedColor: Int) {

        if (selectedColor == ThemeUtil.getThemeColor(this, R.attr.colorPrimary))
            return
        toolbar.setBackgroundColor(selectedColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window.statusBarColor = selectedColor
        switchTheme(selectedColor)
        fragmentManager.beginTransaction().replace(R.id.container, SettingFragment()).commit()
//        EventBus.getDefault().post(ThemeChangedEvent(selectedColor))
        RxBus.getDefault().post(ThemeChangedEvent(selectedColor))
    }


    override fun onColorChooserDismissed(dialog: ColorChooserDialog) {
    }


}
