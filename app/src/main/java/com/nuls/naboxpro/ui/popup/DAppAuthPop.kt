package com.nuls.naboxpro.ui.popup

import android.content.Context
import android.text.TextUtils
import com.lxj.xpopup.core.CenterPopupView
import com.nuls.naboxpro.R
import com.nuls.naboxpro.db.WalletInfoDaoHelper
import com.nuls.naboxpro.utils.showToast
import kotlinx.android.synthetic.main.pop_dapp_auth_layout.view.*

class DAppAuthPop(context: Context) : CenterPopupView(context) {

    var url: String? = ""
    var chain: String? = ""
    var address: String? = ""

    var listener:AuthResultListener?=null
    override fun getImplLayoutId() = R.layout.pop_dapp_auth_layout

    constructor(context: Context, url: String, chain: String, address: String,listener:AuthResultListener) : this(context) {
        this.url = url
        this.chain = chain
        this.address = address
        this.listener = listener
    }


    override fun onCreate() {
        super.onCreate()
        popupInfo.isMoveUpToKeyboard = false
        btn_cancel.setOnClickListener {
            dismiss()
        }
        btn_sure.setOnClickListener {
            if(TextUtils.isEmpty(edit_password.text)){
                showToast(context.getString(R.string.input_password))
            }else{
                var walletInfo = WalletInfoDaoHelper.loadDefaultWallet()
                if(walletInfo.validatePassword(edit_password.text.toString().trim())){
                    //密码正确，授权成功，向数据库添加授权信息
                    listener?.success(check_longAuth.isChecked)
                    dismiss()
                }else{
                    showToast(context.getString(R.string.password_error))
                }

            }

        }





    }




    interface AuthResultListener{
        fun success(longAuth:Boolean)
    }


}