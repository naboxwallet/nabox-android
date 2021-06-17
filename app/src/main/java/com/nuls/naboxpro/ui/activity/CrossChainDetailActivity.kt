package com.nuls.naboxpro.ui.activity

import android.content.Context
import android.content.Intent
import android.view.View
import com.gyf.immersionbar.ktx.immersionBar
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.db.WalletInfoDaoHelper
import com.nuls.naboxpro.entity.TxDetailBean
import com.nuls.naboxpro.enums.CoinTypeEnum
import com.nuls.naboxpro.enums.TxTypeEnum
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.BaseResponse
import com.nuls.naboxpro.net.api.RetrofitClient
import com.nuls.naboxpro.net.observable.RecObserver
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.ui.popup.InputPasswordPop
import com.nuls.naboxpro.utils.*
import com.nuls.naboxpro.wallet.transfer.SendETHTransction
import com.nuls.naboxpro.wallet.transfer.SendTransctions
import kotlinx.android.synthetic.main.activity_crosschain_transfer_layout.*
import kotlinx.android.synthetic.main.base_title_layout.*
import network.nerve.heterogeneous.ETHTool
import java.math.BigInteger


/**
 * 跨链交易详情  因为ui差异较大，所以不再与交易详情共用一个activity
 */
class CrossChainDetailActivity :BaseActivity(){
    override fun getLayoutId() = R.layout.activity_crosschain_transfer_layout

    override fun initView() {
        immersionBar {
            statusBarDarkFont(true,0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }

        if(intent!=null){
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




    fun setData2View(bean: TxDetailBean){
        tv_amount.text = NaboxUtils.coinValueOf(BigInteger(bean.tx.amount),bean.tx.decimals)+"  "+bean.tx.symbol
        tv_fee.text = bean.tx?.fee
        tv_date.text = bean.tx?.createTime
        tv_type.text = TxTypeEnum.getTxTypeMessageByCode(bean.tx.type)
        tv_chain1.text = bean?.crossTx?.fromChain
        tv_address1.text = bean?.crossTx?.fromAddress
        tv_address1.setOnClickListener {
            copyToClipboard(this,tv_address1.text.toString())
        }
        tv_txid1.text = bean?.crossTx?.txHash
        tv_txid1.setOnClickListener {
            copyToClipboard(this,tv_txid1.text.toString())
        }
        tv_chain2.text = bean?.crossTx?.toChain
        tv_address2.text = bean?.crossTx?.toAddress
        tv_address2.setOnClickListener {
            copyToClipboard(this,tv_address2.text.toString())
        }
        tv_txid2.text = bean?.crossTx.crossTxHash
        tv_txid2.setOnClickListener {
            copyToClipboard(this,tv_txid2.text.toString())
        }
        when(bean.crossTx.status){

            0 ->{
                tv_status.text = "跨链交易本链未确认"
            }
            1 ->{
                tv_status.text = "跨链交易本链已确认"
            }
            2 ->{
                tv_status.text ="跨链交易NERVE已广播交易待确认"
                add_fee.visibility = View.VISIBLE
                add_fee.setOnClickListener {
                    //追加异构链跨链手续费
                    var txhex = ""
                    txhex = if(bean?.crossTx?.fromChain==CoinTypeEnum.NERVE.code){
                        bean?.crossTx?.txHash.toString()
                    }else{
                        bean?.crossTx?.crossTxHash.toString()
                    }
                    showPasswordPop(this,object :InputPasswordPop.InputListener{
                        override fun inputSuccess(password: String?) {
                            if(WalletInfoDaoHelper.loadDefaultWallet().validatePassword(password)){
//                                SendTransctions.createNerveWithdrawalAdditionalFeeTx(bean?.crossTx?.fromAddress,WalletInfoDaoHelper.loadDefaultWallet().decrypt(password),)

//                                showAddFeeHint(this@CrossChainDetailActivity,bean?.crossTx.fromAddress,bean?.crossTx.toAddress,)
                            }else{
                                showToast(getString(R.string.password_error))
                            }

                        }
                    })
                }
            }
            3 ->{
                tv_status.text ="跨链交易NERVE广播失败"
            }
            4 ->{
                tv_status.text ="跨链交易目标链已确认"
            }
            5 ->{
                tv_status.text ="跨链交易失败"
            }
        }
    }

    companion object{
        fun start(context: Context,txData:TxDetailBean){

            var intent = Intent(context,CrossChainDetailActivity().javaClass)
            intent.putExtra("txData",txData)
            context.startActivity(intent)

        }
    }
}