package com.nuls.naboxpro.ui.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.nuls.naboxpro.R
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.utils.showToast
import com.nuls.naboxpro.wallet.NaboxAccount
import kotlinx.android.synthetic.main.activity_create_wallet_first.*
import kotlinx.android.synthetic.main.activity_edit_wallet_name.*
import kotlinx.android.synthetic.main.base_title_layout.*

class InputWalletNameActivity :BaseActivity(){
    override fun getLayoutId() = R.layout.activity_edit_wallet_name
    var naboxAccount:NaboxAccount?=null
    var alias:String?=null
    override fun initView() {
        immersionBar {
            statusBarDarkFont(true,0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }
        back.setOnClickListener {
            finish()
        }
        if(intent.getBundleExtra("data")!=null){
            naboxAccount = intent.getBundleExtra("data").getSerializable("wallet") as NaboxAccount?
//            if(intent.getBundleExtra("data").getString("alias")!=null){
//                alias = intent.getBundleExtra("data").getString("alias")
//            }
            btn_next.setOnClickListener {
                if(TextUtils.isEmpty(edit_wallet_name.text)){
                    showToast(getString(R.string.input_wallet_name))
                }else{
//                    naboxAccount?.let { it1 ->
//                        ChoiceWalletSkinActivity.start(this,
//                            it1,edit_wallet_name.text.toString().trim())
//                    }
                    naboxAccount?.let { it1 ->
                        SaveWalletHintActivity.start(this,0,
                            it1,edit_wallet_name.text.toString().trim())
                    }

                }
            }
        }
    }

    override fun initData() {

    }


    companion object{

        fun start(context: Context, naboxAccount: NaboxAccount, alias:String?=null){
            var intent: Intent = Intent(context,InputWalletNameActivity().javaClass)
            var bundle: Bundle = Bundle()
            bundle.putSerializable("wallet",naboxAccount)
            intent.putExtra("data",bundle)
            context.startActivity(intent)
        }


    }



}