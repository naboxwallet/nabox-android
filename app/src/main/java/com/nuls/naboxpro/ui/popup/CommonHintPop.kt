package com.nuls.naboxpro.ui.popup

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import com.lxj.xpopup.core.BottomPopupView
import com.nuls.naboxpro.R
import kotlinx.android.synthetic.main.pop_common_hint.view.*
import org.web3j.abi.datatypes.Int

class CommonHintPop(context: Context): BottomPopupView(context) {


    var title:String ?=""
    var content:String ?=""
    var isSuccess:Boolean ?=true
    constructor(context: Context,title:String?,content:String?,isSuccess:Boolean?):this(context){
        this.title = title
        this.content = content
        this.isSuccess = isSuccess
    }


    override fun getImplLayoutId() = R.layout.pop_common_hint

    override fun onCreate() {
        super.onCreate()
        if(isSuccess!!){
            tv_icon.setBackgroundResource(R.mipmap.icon_succ)
        }else{
            tv_icon.setBackgroundResource(R.mipmap.icon_fail)
        }
        iv_close.setOnClickListener {
            dismiss()
            (context as Activity).finish()
        }
        if(!TextUtils.isEmpty(title)){
            tv_title.text = title
        }
        if(!TextUtils.isEmpty(content)){
            tv_message.text = content
        }

    }

}