package com.nuls.naboxpro.ui.popup

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.CenterPopupView
import com.nuls.naboxpro.R
import com.nuls.naboxpro.utils.showToast
import kotlinx.android.synthetic.main.dialog_password_layout.view.*


/**
 * 输入密码弹窗
 */
class InputPasswordPop(context: Context):CenterPopupView(context) {

    override fun getImplLayoutId() = R.layout.dialog_password_layout
    var inputListener:InputListener?=null
    private lateinit var mContext: Context
    constructor(context: Context,inputListener: InputListener):this(context){
        this.inputListener = inputListener
        this.mContext = context
    }


    override fun onCreate() {
        super.onCreate()
        popupInfo.isMoveUpToKeyboard = false
        btn_sure.setOnClickListener {
            if(TextUtils.isEmpty(edit_password.text)){
                showToast(resources.getString(R.string.input_password))
                return@setOnClickListener
            }
            inputListener?.inputSuccess(edit_password.text.toString().trim())
            dismiss()
        }
        btn_cancel.setOnClickListener {
            dismiss()
        }
    }

    interface InputListener {
        fun inputSuccess(password: String?)
    }


    override fun onDismiss() {
        super.onDismiss()
        KeyboardUtils.hideSoftInput(edit_password)

    }
}