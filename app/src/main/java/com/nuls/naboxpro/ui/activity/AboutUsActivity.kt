package com.nuls.naboxpro.ui.activity

import android.text.TextUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.nuls.naboxpro.BuildConfig
import com.nuls.naboxpro.R
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.utils.showUpdatePopWindow
import kotlinx.android.synthetic.main.activity_about_us.*
import kotlinx.android.synthetic.main.base_title_layout.*

class AboutUsActivity:BaseActivity() {
    override fun getLayoutId() = R.layout.activity_about_us

    override fun initView() {
        immersionBar {
            statusBarDarkFont(true,0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }
        tv_title.text = resources.getString(R.string.about_us)
        back.setOnClickListener {
            finish()
        }
        tv_version.text = BuildConfig.VERSION_NAME
        check_update.setOnClickListener {

            showUpdatePopWindow(this,true)

        }
    }

    override fun initData() {

    }
}