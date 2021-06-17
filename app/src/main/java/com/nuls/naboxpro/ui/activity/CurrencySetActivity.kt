package com.nuls.naboxpro.ui.activity

import com.gyf.immersionbar.ktx.immersionBar
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.enums.MoneyTypeEnum
import com.nuls.naboxpro.eventbus.RefreshEvent
import com.nuls.naboxpro.eventbus.SwitchWalletEvent
import com.nuls.naboxpro.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_common_setting.*
import org.greenrobot.eventbus.EventBus

class CurrencySetActivity :BaseActivity() {
    override fun getLayoutId() = R.layout.activity_common_setting

    override fun initView() {
        immersionBar {
            statusBarDarkFont(true,0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }
        back.setOnClickListener {
            finish()
        }
        tv_title.text = getString(R.string.currency_setting)
        //
        rb_1.text = getString(R.string.cny)
        rb_2.text = getString(R.string.usd)
        if (UserDao.currency == MoneyTypeEnum.RMB.code()) {
            rb_1.isChecked = true
        } else if (UserDao.currency == MoneyTypeEnum.USD.code()) {
            rb_2.isChecked = true
        }
        tv_save.setOnClickListener {
            if (rb_1.isChecked) {
               UserDao.currency = MoneyTypeEnum.RMB.code()
            } else if (rb_1.isChecked) {
                UserDao.currency = MoneyTypeEnum.USD.code()
            }
            EventBus.getDefault().post(RefreshEvent())
            finish()
        }

    }

    override fun initData() {

    }
}