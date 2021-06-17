package com.nuls.naboxpro.ui.activity

import android.content.Context
import android.content.Intent
import android.view.View
import com.gyf.immersionbar.ktx.immersionBar
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.entity.TxDetailBean
import com.nuls.naboxpro.enums.TxTypeEnum
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.BaseResponse
import com.nuls.naboxpro.net.api.RetrofitClient
import com.nuls.naboxpro.net.observable.RecObserver
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.utils.NaboxUtils
import com.nuls.naboxpro.utils.copyToClipboard
import com.nuls.naboxpro.utils.showToast
import kotlinx.android.synthetic.main.activity_transfer_detail_layout.*
import kotlinx.android.synthetic.main.base_title_layout.*
import java.math.BigInteger


/**
 * 交易详情
 */
class TransferDetailActivity :BaseActivity(){
    override fun getLayoutId() = R.layout.activity_transfer_detail_layout

    override fun initView() {
        immersionBar {
            statusBarDarkFont(true,0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }

        if(intent!=null){
//            if(intent.getStringExtra("txHash")!=null
//                &&intent.getLongExtra("transId",0)!=null
//                &&intent.getStringExtra("chain")!=null){
//                getDetail()
//            }
            if(intent.getSerializableExtra("txData")!=null){
                setData2View(intent.getSerializableExtra("txData") as TxDetailBean)
            }
        }
        back.setOnClickListener {
            finish()
        }
    }

    override fun initData() {

    }




    fun setData2View(bean:TxDetailBean){
        tv_amount.text = NaboxUtils.coinValueOf(BigInteger(bean.tx.amount),bean.tx.decimals)+"  "+bean.tx.symbol
        if(bean.tx.status==0){ //状态：0、未确认；1、已确认；-1失败
            tv_status.text = getString(R.string.Unconfirmed)
        }else if(bean.tx.status==1){
            tv_status.text = getString(R.string.Confirmed)
        }else if(bean.tx.status==-1){
            tv_status.text = getString(R.string.Fail)
        }
        tv_txid.text = bean.tx?.id
        if(bean.tx.froms!=null){
            var addressList:List<String>  ?=  bean.tx?.froms?.split(",")
            if(addressList!=null&&addressList.size>1){
                List_fromAddress.text = addressList[1]
            }else{
                List_fromAddress.text = bean.tx?.froms
            }
        }

        tv_toAddress.text = bean.tx?.tos
        tv_trans_date.text = bean.tx?.createTime
        tv_payfee.text = bean.tx?.fee
        tv_block_height.text = bean.tx.height.toString()
        tv_remark.text = bean.tx?.remark
        tv_trans_type.text = TxTypeEnum.getTxTypeMessageByCode(bean.tx.type)
        tv_txid.setOnClickListener {
            copyToClipboard(this,tv_txid.text.toString())
        }
        tv_toAddress.setOnClickListener {
            copyToClipboard(this,tv_toAddress.text.toString())
        }
        List_fromAddress.setOnClickListener {
            copyToClipboard(this,List_fromAddress.text.toString())
        }

    }



    companion object{
        fun start(context: Context,txData:TxDetailBean){

            var intent = Intent(context,TransferDetailActivity().javaClass)
            intent.putExtra("txData",txData)
            context.startActivity(intent)

        }
    }





}