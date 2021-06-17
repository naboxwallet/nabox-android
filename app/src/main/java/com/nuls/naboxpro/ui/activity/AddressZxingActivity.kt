package com.nuls.naboxpro.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder
import com.nuls.naboxpro.R
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.utils.NaboxUtils
import com.nuls.naboxpro.utils.copyToClipboard
import kotlinx.android.synthetic.main.activity_receivables.*
import kotlinx.android.synthetic.main.base_title_maincolor_layout.*


/**
 * 钱包地址二维码
 */
class AddressZxingActivity:BaseActivity(){
    override fun getLayoutId() = R.layout.activity_receivables
        var bitmap:Bitmap?=null
    override fun initView() {
        rl_finish.setOnClickListener {
            finish()
        }
       if(intent!=null){
           if(intent.getStringExtra("address")!=null){
               bitmap = QRCodeEncoder.syncEncodeQRCode(intent.getStringExtra("address"), 200)
               iv_zxing.setImageBitmap(bitmap)
               tv_address.text = intent.getStringExtra("address")
               tv_address.setOnClickListener {
                   copyToClipboard(this, intent.getStringExtra("address"))
               }
           }
           if(intent.getStringExtra("symbol")!=null){
               tv_name.text = intent.getStringExtra("symbol")
           }
       }
    }

    override fun initData() {

    }




    companion object{

        fun start(context: Context,address:String,symbol:String){
            var intent:Intent = Intent(context,AddressZxingActivity().javaClass)
            intent.putExtra("address",address)
            intent.putExtra("symbol",symbol)
            context.startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bitmap?.recycle()
        bitmap=null
    }
}