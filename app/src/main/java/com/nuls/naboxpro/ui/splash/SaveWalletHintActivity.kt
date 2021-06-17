package com.nuls.naboxpro.ui.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.gyf.immersionbar.ktx.immersionBar
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.db.WalletInfoDaoHelper
import com.nuls.naboxpro.entity.WalletInfo
import com.nuls.naboxpro.entity.WalletSyncRequestEntity
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.BaseResponse
import com.nuls.naboxpro.net.api.RetrofitClient
import com.nuls.naboxpro.net.observable.RecObserver
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.ui.main.MainMenuActivity
import com.nuls.naboxpro.ui.popup.InputPasswordPop
import com.nuls.naboxpro.utils.getChainList
import com.nuls.naboxpro.utils.showPasswordPop
import com.nuls.naboxpro.utils.showToast
import com.nuls.naboxpro.wallet.NaboxAccount
import kotlinx.android.synthetic.main.activity_save_wallet_hint.*
import kotlinx.android.synthetic.main.activity_save_wallet_hint.back
import kotlinx.android.synthetic.main.base_title_layout.*
import network.nerve.core.crypto.HexUtil

class SaveWalletHintActivity:BaseActivity() {
    override fun getLayoutId() = R.layout.activity_save_wallet_hint
    var naboxAccount:NaboxAccount?=null
    var alias:String?=null
    var colorIndex:Int?=null
    override fun initView() {
        immersionBar {
            statusBarDarkFont(true,0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }
    }

    override fun initData() {
        back.setOnClickListener {
            finish()
        }

        if(intent.getBundleExtra("data")!=null){
            naboxAccount = intent.getBundleExtra("data").getSerializable("wallet") as NaboxAccount?
            if(intent.getBundleExtra("data").getString("alias")!=null){
                alias = intent.getBundleExtra("data").getString("alias")
            }
            if(intent.getBundleExtra("data").getInt("colorIndex")!=null){
                colorIndex = intent.getBundleExtra("data").getInt("colorIndex")
            }
        }
        btn_backup_prikey.setOnClickListener {
            showPasswordPop(this,object :InputPasswordPop.InputListener{
                override fun inputSuccess(password: String?) {
                  if(naboxAccount?.validatePassword(password)!!){
                      naboxAccount?.let { it1 -> colorIndex?.let { it2 ->
                          password?.let { it3 ->
                              BackupPriKeyActivity.start(this@SaveWalletHintActivity,
                                  it2, it3,it1,alias)
                          }
                      } }
                  }else{
                      showToast(resources.getString(R.string.password_error))
                  }
                }
            })

        }
        btn_backup_keystore.setOnClickListener {
            showPasswordPop(this,object :InputPasswordPop.InputListener{
                override fun inputSuccess(password: String?) {
                    if(naboxAccount?.validatePassword(password)!!){
                        naboxAccount?.let { it1 -> colorIndex?.let { it2 ->
                            password?.let { it3 ->
                                BackupKeystoreActivity.start(this@SaveWalletHintActivity,
                                    it2, it3,it1,alias)
                            }
                        } }
                    }else{
                        showToast(resources.getString(R.string.password_error))
                    }
                }
            })

        }
        //暂不备份
        tv_no_copy.setOnClickListener {
            walletSync()
        }

    }


    /**
     * 同步钱包
     */
    private fun walletSync(){
        var walletSyncRequestEntity: WalletSyncRequestEntity = WalletSyncRequestEntity()
        walletSyncRequestEntity.pubKey =  HexUtil.encode(naboxAccount?.compressedPubKey)
        walletSyncRequestEntity.terminal = Api.IMEI
        walletSyncRequestEntity.tag = UserDao.language
        walletSyncRequestEntity.language = UserDao.language
        walletSyncRequestEntity.type = "ANDROID"
        walletSyncRequestEntity.addressList = naboxAccount?.let { getChainList(it) }
        RetrofitClient.getInstance().invokePostBody(this, Api.WALLET_SYNC, walletSyncRequestEntity).subscribe(object :
            RecObserver<BaseResponse<Any>>(this,true,false,Api.WALLET_SYNC){
            override fun onSuccess(t: BaseResponse<Any>?) {
                //同步成功。将钱包信息存入本地数据库
                var  newWallet: WalletInfo? = colorIndex?.let {
                    WalletInfo(naboxAccount,
                        it,alias)
                }
                WalletInfoDaoHelper.insertWallet(newWallet)
                UserDao.defaultWallet = newWallet?.nulsAddress.toString()
                startActivity(Intent(this@SaveWalletHintActivity, MainMenuActivity().javaClass))
                finish()

            }

            override fun onFail(msg: String, code: Int) {
                showToast(msg)
            }
        })
    }



    companion object{

        fun start(context: Context, colorIndex:Int,naboxAccount: NaboxAccount, alias:String?=null){
            var intent: Intent = Intent(context,SaveWalletHintActivity().javaClass)
            var bundle: Bundle = Bundle()
            bundle.putString("alias",alias)
            bundle.putInt("colorIndex",colorIndex)
            bundle.putSerializable("wallet",naboxAccount)
            intent.putExtra("data",bundle)
            context.startActivity(intent)
        }


    }
}