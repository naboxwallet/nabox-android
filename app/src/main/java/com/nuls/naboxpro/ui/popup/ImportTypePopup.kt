package com.nuls.naboxpro.ui.popup

import android.content.Context
import android.content.Intent
import com.lxj.xpopup.core.BottomPopupView
import com.nuls.naboxpro.R
import com.nuls.naboxpro.ui.splash.ImportByKeystoreActivity
import com.nuls.naboxpro.ui.splash.ImportByPriKeyActivity
import kotlinx.android.synthetic.main.pop_import_type_layout.view.*

class ImportTypePopup(context: Context):BottomPopupView(context) {


    override fun getImplLayoutId() = R.layout.pop_import_type_layout

    override fun onCreate() {
        super.onCreate()
        tv_keystore.setOnClickListener {
            context.startActivity(Intent(context,ImportByKeystoreActivity().javaClass))
            dismiss()
        }
        iv_cancel.setOnClickListener {
            dismiss()
        }
        tv_mnemonic.setOnClickListener {
            dismiss()
        }
        tv_personal_key.setOnClickListener {
            context.startActivity(Intent(context,ImportByPriKeyActivity().javaClass))
            dismiss()
        }
    }


}