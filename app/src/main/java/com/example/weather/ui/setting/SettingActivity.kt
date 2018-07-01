package com.example.weather.ui.setting

import android.content.res.Resources
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.StyleRes
import com.afollestad.materialdialogs.color.ColorChooserDialog
import com.example.weather.base.BaseActivity
import com.example.weather.R
import com.example.weather.other.RxBus.RxBus
import com.example.weather.other.RxBus.event.ThemeChangedEvent
import com.example.weather.util.LogUtil
import com.example.weather.util.SettingsUtil
import com.example.weather.util.ThemeUtil
import com.example.weather.util.initToolbar
import kotlinx.android.synthetic.main.toolbar.*
import org.greenrobot.eventbus.EventBus

class SettingActivity : BaseActivity()
        , ColorChooserDialog.ColorCallback
{

    override fun getLayoutId(): Int {
        return R.layout.activity_setting
    }

    override fun initView(savedInstanceState: Bundle?) {
        initToolbar(toolbar)
        fragmentManager.beginTransaction().replace(R.id.container, SettingFragment()).commit()
    }

    override fun onColorSelection(dialog: ColorChooserDialog, selectedColor: Int) {

        if (selectedColor == ThemeUtil.getThemeColor(this, R.attr.colorPrimary))
            return;
        toolbar.setBackgroundColor(selectedColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window.statusBarColor = selectedColor
        switchTheme(selectedColor)
        fragmentManager.beginTransaction().replace(R.id.container, SettingFragment()).commit()
        EventBus.getDefault().post(ThemeChangedEvent(selectedColor))
//        RxBus.instance.post(ThemeChangedEvent(selectedColor))
    }


    override fun onColorChooserDismissed(dialog: ColorChooserDialog) {
    }


}
