package com.example.weather.ui.setting

import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import com.afollestad.materialdialogs.color.ColorChooserDialog
import com.example.wanandroidtest.util.applyScheduler
import com.example.weather.MyApp
import com.example.weather.R
import com.example.weather.util.FileSizeUtil
import com.example.weather.util.FileUtil
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class SettingFragment: PreferenceFragment() {
    companion object {
        const val THEME = "pref_theme"
        const val CLEAN_CACHE = "pref_clean_cache"
    }

    private val compositeDisposable by lazy { CompositeDisposable() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.setting)
        initView()
    }

    private fun initView(){
        val themePreference =findPreference(THEME) as Preference
        val cleanCache = findPreference(CLEAN_CACHE) as Preference

        val cachePaths= arrayOf(FileUtil.getInternalCacheDir(MyApp.instance),FileUtil.getExternalCacheDir(MyApp.instance))
       compositeDisposable.add(
               Observable.just(cachePaths)
                       .map{ FileSizeUtil.getAutoFileOrFilesSize(it) }
                       .applyScheduler()
                       .subscribe{
                           cleanCache.summary=it
                       }
       )

        themePreference.setOnPreferenceClickListener {
            ColorChooserDialog.Builder(activity as SettingActivity, R.string.theme)
                    .customColors(R.array.colors, null)
                    .doneButton(R.string.done)
                    .cancelButton(R.string.cancel)
                    .allowUserColorInput(false)
                    .allowUserColorInputAlpha(false)
                    .show((activity as SettingActivity).supportFragmentManager)
            true
        }
        cleanCache.setOnPreferenceClickListener {
            true
        }
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}