package com.nuls.naboxpro.ui.activity

import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.ActivityUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.nuls.naboxpro.R
import com.nuls.naboxpro.entity.WalletInfo
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.utils.copyToClipboard
import kotlinx.android.synthetic.main.activity_copy_personal_key.*
import kotlinx.android.synthetic.main.base_title_layout.*

/**
 * 复制私钥
 */
class CopyPrikeyActivity:BaseActivity() {
    override fun getLayoutId() = R.layout.activity_copy_personal_key

    override fun initView() {
        immersionBar {
            statusBarDarkFont(true,0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }
        back.setOnClickListener {
            finish()
        }
        if(intent!=null&&intent.getStringExtra("prikey")!=null){
            tv_personal_key.text = intent.getStringExtra("prikey")
            btn_next.setOnClickListener {
                copyToClipboard(this,tv_personal_key.text.toString().trim())
                ActivityUtils.finishActivity(BackupsPrikeyHintActivity().javaClass)
                finish()
            }
        }

    }

    override fun initData() {

    }

    companion object{
        fun start(context: Context, prikey: String){
            var intent: Intent = Intent(context,CopyPrikeyActivity().javaClass)
            intent.putExtra("prikey",prikey)
            context.startActivity(intent)
        }

    }

}