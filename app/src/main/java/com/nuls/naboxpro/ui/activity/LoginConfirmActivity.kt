package com.nuls.naboxpro.ui.activity

import android.view.View
import com.gyf.immersionbar.ktx.immersionBar
import com.nuls.naboxpro.R
import com.nuls.naboxpro.db.WalletInfoDaoHelper
import com.nuls.naboxpro.entity.nuls.LoginConfirmEntity
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.NulsBaseResponse
import com.nuls.naboxpro.net.api.NulsClient
import com.nuls.naboxpro.net.observable.NulsRecObservable
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.utils.showToast
import kotlinx.android.synthetic.main.activity_login_confirm.*
import kotlinx.android.synthetic.main.activity_scan_layout.*


/**
 * app端 确认登录
 */
class LoginConfirmActivity:BaseActivity() {
    override fun getLayoutId() = R.layout.activity_login_confirm

    override fun initView() {
        immersionBar {
            statusBarDarkFont(true,0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }
        back.setOnClickListener {
            finish()
        }
        btn_cancel.setOnClickListener {
            finish()
        }
        btn_login.setOnClickListener {
            //发起登录
            comfimLogin(getIntent().getStringExtra("key"))

        }
    }

    override fun initData() {

    }



    private fun comfimLogin(key:String){
        val loginConfirmEntity = LoginConfirmEntity()
        loginConfirmEntity.id = 1234
        loginConfirmEntity.jsonrpc = "2.0"
        loginConfirmEntity.method = "commitData"
        val paramsBean = LoginConfirmEntity.ParamsBean()
        paramsBean.key = key
        val valueBean = LoginConfirmEntity.ParamsBean.ValueBean()
        valueBean.address = WalletInfoDaoHelper.loadDefaultWallet().nulsAddress
        valueBean.terminal = "Nabox"
        paramsBean.value = valueBean
        loginConfirmEntity.params = paramsBean
        NulsClient.getInstance().invokePostBody(this,Api.PUBLIC_SERVICE,loginConfirmEntity)
            .subscribe(object :NulsRecObservable<NulsBaseResponse<Any>>(this,true,false){
                override fun onSuccess(t: NulsBaseResponse<Any>?) {
                    showToast(getString(R.string.login_success))
                    finish()
                }

                override fun onFail(msg: String, code: Int) {
                    showToast(msg)
                }
            })

    }
}