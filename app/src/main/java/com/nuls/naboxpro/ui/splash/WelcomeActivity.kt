package com.nuls.naboxpro.ui.splash

import android.content.Intent
import android.os.Looper
import com.gyf.immersionbar.ktx.immersionBar
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.db.WalletInfoDaoHelper
import com.nuls.naboxpro.entity.ExchangeEntity
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.BaseResponse
import com.nuls.naboxpro.net.api.RetrofitClient
import com.nuls.naboxpro.net.observable.RecObserver
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.ui.main.MainMenuActivity

class WelcomeActivity: BaseActivity() {
    override fun getLayoutId() = R.layout.activity_welcome

    override fun initView() {

        immersionBar {
            statusBarDarkFont(true,0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }
//        getExchange()
        Thread(
            Runnable {
                try {
                    Thread.sleep(1500)
                    Looper.prepare()
                    if(WalletInfoDaoHelper.loadAllWallet()!=null&&WalletInfoDaoHelper.loadAllWallet().size>0){
                        startActivity(Intent(this, MainMenuActivity().javaClass))
                    }else{
                        startActivity(Intent(this, WelcomeSecondActivity().javaClass))
                    }
                    finish()
                    Looper.loop()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        ).start()
    }

    override fun initData() {

    }

    /**
     * 查询汇率
     */
    private fun getExchange(){
        RetrofitClient.getInstance().invokeGet(this,Api.GET_EXCHANGE, mapOf())
            .subscribe(object :RecObserver<BaseResponse<ExchangeEntity>>(this,false,false,Api.GET_EXCHANGE){
                override fun onSuccess(t: BaseResponse<ExchangeEntity>?) {
                    if(t!=null&&t.data!=null){
                        UserDao.rmbExcahnge = t.data.cny
                    }
                }
                override fun onFail(msg: String, code: Int) {

                }
            })
    }
}