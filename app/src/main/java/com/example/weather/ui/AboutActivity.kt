package com.example.weather.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.example.weather.R
import com.example.weather.base.BaseActivity
import com.example.weather.util.ShareUtils
import com.example.weather.util.initToolbar
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_about
    }

    override fun initView(savedInstanceState: Bundle?) {
        initToolbar(toolbar)
        //项目主页
        btn_web_home.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/blackfrox/CoolWeather")))
        }
        //分享
        btn_share_app.setOnClickListener {
            ShareUtils.shareText(this, getString(R.string.share_message), "分享到")
        }
        //评论
        btn_comment.setOnClickListener {
            val uri = Uri.parse("market://details?id=$packageName")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }
    }
}
