package com.nuls.naboxpro.ui.activity

import com.blankj.utilcode.util.ActivityUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.enums.LanguageEnum
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.ui.main.MainMenuActivity
import com.nuls.naboxpro.utils.LanguageConstants
import com.nuls.naboxpro.utils.LanguageUtil
import kotlinx.android.synthetic.main.activity_common_setting.*

class LanguageSetActivity:BaseActivity() {
    override fun getLayoutId()= R.layout.activity_common_setting

    override fun initView() {
        immersionBar {
            statusBarDarkFont(true,0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }
        back.setOnClickListener {
            finish()
        }
        tv_title.text = getString(R.string.language_settings)
        rb_1.text = "简体中文"
        rb_2.text ="English"
        if (UserDao.language == LanguageEnum.CHS.code()) {
            rb_1.isChecked = true
        } else if (UserDao.language == LanguageEnum.EN.code()) {
            rb_2.isChecked = true
        }
        tv_save.setOnClickListener {
            if (rb_1.isChecked) {
                LanguageUtil.applyLanguage(
                    applicationContext,
                    LanguageConstants.SIMPLIFIED_CHINESE
                )
                UserDao.language = LanguageEnum.CHS.code()

            } else if (rb_2.isChecked) {
                LanguageUtil.applyLanguage(applicationContext, LanguageConstants.ENGLISH)
                UserDao.language = LanguageEnum.EN.code()
            }


            ActivityUtils.startActivity(MainMenuActivity().javaClass)
            //需要重启页面才能生效
            ActivityUtils.finishOtherActivities(MainMenuActivity().javaClass)

        }
    }

    override fun initData() {

    }
}