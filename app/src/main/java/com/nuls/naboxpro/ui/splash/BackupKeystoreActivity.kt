package com.nuls.naboxpro.ui.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.alibaba.fastjson.JSON
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
import com.nuls.naboxpro.utils.FileUtil
import com.nuls.naboxpro.utils.getChainList
import com.nuls.naboxpro.utils.showToast
import com.nuls.naboxpro.wallet.NaboxAccount
import com.nuls.naboxpro.wallet.NulsKeyStore
import kotlinx.android.synthetic.main.activity_backup_prikey_layout.*
import kotlinx.android.synthetic.main.base_title_layout.*
import kotlinx.android.synthetic.main.fragment_import_keystore.*
import kotlinx.android.synthetic.main.fragment_import_keystore.btn_next
import network.nerve.core.crypto.HexUtil

class BackupKeystoreActivity :BaseActivity(){
    var naboxAccount:NaboxAccount?=null
    var alias:String?=null
    var colorIndex:Int?=null
    override fun getLayoutId() = R.layout.activity_backup_prikey_layout
    override fun initView() {
        tv_title.text = getString(R.string.backup_wallet)
        immersionBar {
            statusBarDarkFont(true,0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }
        back.setOnClickListener {
            finish()
        }
        var nulsKeyStore: NulsKeyStore = NulsKeyStore()
        if(intent.getBundleExtra("data")!=null){
            naboxAccount = intent.getBundleExtra("data").getSerializable("wallet") as NaboxAccount?
            nulsKeyStore.pubKey = HexUtil.encode(naboxAccount?.compressedPubKey)
            nulsKeyStore.address = naboxAccount?.nulsAddress
            nulsKeyStore.encryptedPrivateKey = HexUtil.encode(naboxAccount?.encryptedPriKey)

            if(intent.getBundleExtra("data").getString("alias")!=null){
                alias = intent.getBundleExtra("data").getString("alias")
                nulsKeyStore.alias =alias
            }
            if(intent.getBundleExtra("data").getInt("colorIndex")!=null){
                colorIndex = intent.getBundleExtra("data").getInt("colorIndex")
            }
            tv_personal_key.text = JSON.toJSONString(nulsKeyStore)
//            tv_personal_key.text = naboxAccount?.decrypt(intent.getBundleExtra("data").getString("password"))
            btn_next.setOnClickListener {
                if(!TextUtils.isEmpty(tv_personal_key.text)){
                    FileUtil.copyToClipboard(this, tv_personal_key.text.toString())
                    showToast(getString(R.string.already_copy))
                }
            }
            btn_success.setOnClickListener {
                //向后台同步钱包
                walletSync()
            }
        }

    }

    override fun initData() {

    }

    companion object{

        fun start(context: Context, colorIndex:Int, password:String, naboxAccount: NaboxAccount, alias:String?=null){
            var intent: Intent = Intent(context,BackupKeystoreActivity().javaClass)
            var bundle: Bundle = Bundle()
            bundle.putString("alias",alias)
            bundle.putString("password",password)
            bundle.putInt("colorIndex",colorIndex)
            bundle.putSerializable("wallet",naboxAccount)
            intent.putExtra("data",bundle)
            context.startActivity(intent)
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
            RecObserver<BaseResponse<Any>>(this,true,false, Api.WALLET_SYNC){
            override fun onSuccess(t: BaseResponse<Any>?) {
                //同步成功。将钱包信息存入本地数据库
                var  newWallet: WalletInfo? = colorIndex?.let {
                    WalletInfo(naboxAccount,
                        it,alias)
                }
                WalletInfoDaoHelper.insertWallet(newWallet)
                UserDao.defaultWallet = newWallet?.nulsAddress.toString()
                startActivity(Intent(this@BackupKeystoreActivity, MainMenuActivity().javaClass))

            }

            override fun onFail(msg: String, code: Int) {
                showToast(msg)
            }
        })
    }
}