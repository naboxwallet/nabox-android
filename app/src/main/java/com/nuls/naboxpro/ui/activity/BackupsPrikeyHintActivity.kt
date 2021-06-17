package com.nuls.naboxpro.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import com.gyf.immersionbar.ktx.immersionBar
import com.nuls.naboxpro.R
import com.nuls.naboxpro.entity.WalletInfo
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.ui.popup.InputPasswordPop
import com.nuls.naboxpro.utils.showPasswordPop
import com.nuls.naboxpro.utils.showToast
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_backups_personal_key.*
import kotlinx.android.synthetic.main.base_title_layout.*


class BackupsPrikeyHintActivity :BaseActivity(){
    override fun getLayoutId() = R.layout.activity_backups_personal_key

    override fun initView() {
        immersionBar {
            statusBarDarkFont(true,0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }
        back.setOnClickListener {
            finish()
        }
        val rxPermission = RxPermissions(this)
        rxPermission
            .request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribe { granted ->
                if (granted) { // Always true pre-M

                } else {

                }
            }
        var walletInfo:WalletInfo = intent.getSerializableExtra("wallet") as WalletInfo
        if(walletInfo!=null){
            btn_next.setOnClickListener {

                //跳转到备份私钥界面
                showPasswordPop(this,object :InputPasswordPop.InputListener{
                    override fun inputSuccess(password: String?) {
                            if(walletInfo.validatePassword(password)){


                                CopyPrikeyActivity.start(this@BackupsPrikeyHintActivity,walletInfo.decrypt(password))


                            }else{
                                showToast(getString(R.string.password_error))
                            }
                    }
                })

            }
        }

    }

    override fun initData() {

    }

    companion object{
        fun start(context: Context, walletInfo: WalletInfo){
            var intent: Intent = Intent(context,BackupsPrikeyHintActivity().javaClass)
            intent.putExtra("wallet",walletInfo)
            context.startActivity(intent)
        }

    }
}