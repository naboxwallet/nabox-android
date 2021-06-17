package com.nuls.naboxpro.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.alibaba.fastjson.JSON
import com.duke.dfileselector.util.FileUtils.SDCARD_PATH_NABOX
import com.google.gson.Gson
import com.gyf.immersionbar.ktx.immersionBar
import com.nuls.naboxpro.R
import com.nuls.naboxpro.db.WalletInfoDaoHelper
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.ui.popup.InputPasswordPop
import com.nuls.naboxpro.utils.FileUtil
import com.nuls.naboxpro.utils.showCommonHint
import com.nuls.naboxpro.utils.showPasswordPop
import com.nuls.naboxpro.utils.showToast
import com.nuls.naboxpro.wallet.NaboxAccount
import com.nuls.naboxpro.wallet.NulsKeyStore
import com.tbruyelle.rxpermissions2.RxPermissions
import io.nuls.core.crypto.HexUtil
import kotlinx.android.synthetic.main.activity_backups_keystore.*
import kotlinx.android.synthetic.main.base_title_layout.*

import java.io.File

class BackupsKeystoreHintActivity:BaseActivity() {
    override fun getLayoutId() = R.layout.activity_backups_keystore

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
            //
            val rxPermission = RxPermissions(this)
            rxPermission
                .request(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe { granted ->
                    if (granted) { // Always true pre-M
                        //必须有权限再操作
                        //弹出密码框
                        showPasswordPop(this,object :InputPasswordPop.InputListener{
                            override fun inputSuccess(password: String?) {
                                var walletInfo = WalletInfoDaoHelper.loadWalletByAddress(intent.getStringExtra("address"))
                                if(walletInfo.validatePassword(password)){
                                    //密码正确 备份keystore
                                    var nulsKeyStore: NulsKeyStore = NulsKeyStore()

                                        nulsKeyStore.pubKey = HexUtil.encode(walletInfo?.compressedPubKey)
                                        nulsKeyStore.address = walletInfo?.nulsAddress
                                        nulsKeyStore.encryptedPrivateKey = HexUtil.encode(walletInfo?.encryptedPriKey)
                                        if(walletInfo.alias!=null){
                                            nulsKeyStore.alias = walletInfo?.alias
                                        }
                                        if (nulsKeyStore != null) {
                                            val content = Gson().toJson(nulsKeyStore)
                                            val file = File(SDCARD_PATH_NABOX)
                                            if (!file.exists()) {
                                                file.mkdir()
                                            }
                                            if (TextUtils.isEmpty(nulsKeyStore.alias)) {
                                                FileUtil.writeString(
                                                    SDCARD_PATH_NABOX + nulsKeyStore.address + ".keystore",
                                                    content,
                                                    null
                                                )
                                            } else {
                                                FileUtil.writeString(
                                                    SDCARD_PATH_NABOX + nulsKeyStore.alias + "_" + nulsKeyStore.address + ".keystore",
                                                    content,
                                                    null
                                                )
                                            }
                                            showCommonHint(this@BackupsKeystoreHintActivity,getString(R.string.backup_success),getString(R.string.keystore_save_hint),true)

                                        }
                                }else{
                                    showToast(getString(R.string.password_error))
                                }

                            }

                        })

                    } else {
                        showToast(getString(R.string.get_permission_fail))
                    }
                }

        }
    }

    override fun initData() {

    }

    companion object{

        fun start(context: Context,address:String){

            var intent:Intent = Intent(context,BackupsKeystoreHintActivity().javaClass)
            intent.putExtra("address",address)
            context.startActivity(intent)
        }

    }
}