package com.nuls.naboxpro.ui.activity

import android.content.Intent
import com.blankj.utilcode.util.ActivityUtils
import com.gyf.immersionbar.ktx.immersionBar

import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.db.WalletInfoDaoHelper
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.BaseResponse
import com.nuls.naboxpro.net.api.RetrofitClient
import com.nuls.naboxpro.net.observable.RecObserver
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.ui.main.MainMenuActivity
import com.nuls.naboxpro.ui.popup.EditWalletNamePop
import com.nuls.naboxpro.ui.popup.InputPasswordPop
import com.nuls.naboxpro.ui.splash.CreateWalletActivity
import com.nuls.naboxpro.utils.*
import kotlinx.android.synthetic.main.activity_wallet_edit.*
import kotlinx.android.synthetic.main.base_title_layout.*
import network.nerve.core.crypto.HexUtil


/**
 * 钱包编辑和备份页面
 */
class WalletEditActivity :BaseActivity(){
    override fun getLayoutId() = R.layout.activity_wallet_edit

    override fun initView() {
        back.setOnClickListener {
            finish()
        }
        tv_title.setText(getString(R.string.wallet_manage))
        immersionBar {
            statusBarDarkFont(true,0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }

        if(intent!=null&&intent.getStringExtra("address")!=null){
            //拿到地址 去获取相应钱包和查询余额
            var walletInfo =  WalletInfoDaoHelper.loadWalletByAddress(intent.getStringExtra("address"))
            if(walletInfo!=null){
                //拿到钱包
                tv_wallet_address.text = walletInfo.nulsAddress
                tv_wallet_name_edit.text = walletInfo.alias
                tv_wallet_name.text = walletInfo.alias
                getWalletPrice(HexUtil.encode(walletInfo.compressedPubKey))
            }
            NaboxUtils.initWalletSkin(layout_wallet, walletInfo.color)
            layout_backup_keystore.setOnClickListener {
                BackupsKeystoreHintActivity.start(this,intent.getStringExtra("address"))
            }
            layout_edit_name.setOnClickListener {
                //编辑钱包名称
                showEditNamePop(this,tv_wallet_name.text.toString(),object :
                    EditWalletNamePop.Companion.EditSuccessListener {
                    override fun editSuccess(name: String) {
                        walletInfo.alias = name
                        tv_wallet_name_edit.text = name
                        tv_wallet_name.text = name
                        WalletInfoDaoHelper.updateWallet(walletInfo)
                    }
                })
            }
            //删除钱包
            btn_delete_wallet.setOnClickListener {

                showPasswordPop(this,object :InputPasswordPop.InputListener{
                    override fun inputSuccess(password: String?) {
                        if(walletInfo.validatePassword(password)){
                            //密码正确

                            WalletInfoDaoHelper.deleteWallet(walletInfo)

                            if(WalletInfoDaoHelper.loadAllWallet()!=null&&WalletInfoDaoHelper.loadAllWallet().size>0){
                                //还有其他钱包 那就不用管 退出界面就行
                                //将第一个钱包设置为默认钱包
                                UserDao.defaultWallet = WalletInfoDaoHelper.loadAllWallet()[0].nulsAddress
                                startActivity(Intent(this@WalletEditActivity, MainMenuActivity().javaClass))
                                ActivityUtils.finishOtherActivities(MainMenuActivity().javaClass)

                            }else{
                                //最后一个钱包也被删除了  跳转到创建钱包界面
                                startActivity(Intent(this@WalletEditActivity,CreateWalletActivity().javaClass))
                                ActivityUtils.finishOtherActivities(CreateWalletActivity().javaClass)
                            }
                            showToast(getString(R.string.delete_wallet_success))
                            //干掉其他activity 不然在创建钱包界面返回时会报异常

                        }else{
                            showToast(getString(R.string.password_error))
                        }
                    }

                })

            }
            //修改皮肤
            layout_change_skin.setOnClickListener {
                ChangeSkinActivity.start(this,walletInfo)
            }
            //备份私钥
            layout_backup_personalkey.setOnClickListener {
                BackupsPrikeyHintActivity.start(this,walletInfo)
            }


        }

    }

    override fun initData() {

    }

    /**
     * 获取账户资产总额
     */
    private fun getWalletPrice(compressedPubKey:String){
        RetrofitClient.getInstance().invokePostBody(this, Api.WALLET_PRICE, mapOf(
            "language" to UserDao.language,
            "pubKey" to compressedPubKey
        )).subscribe(object : RecObserver<BaseResponse<String>>(this,false,false, Api.WALLET_PRICE){
            override fun onSuccess(t: BaseResponse<String>?) {
                if(t!=null&&t.data!=null){
                    tv_money.text = getAmountByUsd(t.data)
                }
            }
            override fun onFail(msg: String, code: Int) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        var walletInfo =  WalletInfoDaoHelper.loadWalletByAddress(intent.getStringExtra("address"))
        NaboxUtils.initWalletSkin(layout_wallet, walletInfo.color)

    }


}