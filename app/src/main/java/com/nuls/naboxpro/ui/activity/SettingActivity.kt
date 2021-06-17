package com.nuls.naboxpro.ui.activity
import android.content.Intent
import com.gyf.immersionbar.ktx.immersionBar
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.enums.LanguageEnum
import com.nuls.naboxpro.enums.MoneyTypeEnum
import com.nuls.naboxpro.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.base_title_layout.*


class SettingActivity:BaseActivity() {

    override fun getLayoutId() = R.layout.activity_setting

    override fun initView() {
        immersionBar {
            statusBarDarkFont(true,0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }
        tv_title.text = resources.getString(R.string.setting)
        back.setOnClickListener {
            finish()
        }
        layout_language_settings.setOnClickListener {
            startActivity(Intent(this,LanguageSetActivity().javaClass))
        }
        layout_common_currency_unit.setOnClickListener {

            startActivity(Intent(this,CurrencySetActivity().javaClass))
        }
        layout_safety_protection.setOnClickListener {

        }
    }

    override fun initData() {

    }


    override fun onResume() {
        super.onResume()
        if (UserDao.currency == MoneyTypeEnum.RMB.code()) {
            tv_currency.text = MoneyTypeEnum.RMB.message()
        } else if (UserDao.currency == MoneyTypeEnum.USD.code()) {
            tv_currency.text = MoneyTypeEnum.USD.message()
        }
        if (UserDao.language == LanguageEnum.CHS.code()) {
            tv_language.text = LanguageEnum.CHS.message()
        } else if (UserDao.language == LanguageEnum.EN.code()
        ) {
            tv_language.text = LanguageEnum.EN.message()
        }
    }
}