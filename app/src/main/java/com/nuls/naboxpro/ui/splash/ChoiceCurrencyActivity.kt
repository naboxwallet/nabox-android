package com.nuls.naboxpro.ui.splash

import android.content.Intent
import com.gyf.immersionbar.ktx.immersionBar
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.enums.MoneyTypeEnum
import com.nuls.naboxpro.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_choice_currency.*
import kotlinx.android.synthetic.main.base_title_layout.*


/**
 * 选择货币
 */
class ChoiceCurrencyActivity:BaseActivity() {
    override fun getLayoutId() = R.layout.activity_choice_currency

    override fun initView() {

        immersionBar {
            statusBarDarkFont(true,0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }
        back.setOnClickListener {
            finish()
        }
        btn_next.setOnClickListener {
                if(rb_usd.isChecked){
                    UserDao.currency =MoneyTypeEnum.USD.code
                }else if(eb_rmb.isChecked){
                    UserDao.currency =MoneyTypeEnum.RMB.code
                }
                startActivity(Intent(this,CreateWalletActivity().javaClass))
        }
    }

    override fun initData() {

    }
}