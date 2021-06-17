package com.nuls.naboxpro.ui.popup
import android.app.Activity
import android.content.Context
import android.text.TextUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.core.CenterPopupView
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.BaseResponse
import com.nuls.naboxpro.net.api.RetrofitClient
import com.nuls.naboxpro.net.observable.RecObserver
import com.nuls.naboxpro.utils.NaboxUtils
import com.nuls.naboxpro.utils.showToast
import com.nuls.naboxpro.wallet.transfer.SendTransctions
import kotlinx.android.synthetic.main.pop_addfee_layout.view.*
import java.math.BigInteger

class AddFeePop(context: Context) :CenterPopupView(context) {
    private var loadingPopup: BasePopupView? = null
    var fromChain:String?=null
    var oldFee:String?=null
    var fromAddress:String ?=null
    var toAddress:String ?=null
    var withdrawalTxHash:String ?= null
    var toChain:String?=null
    var assetChainId:Int = 0
    var assetAssetId:Int = 0
    var assetContractAddress:String ?= null
    override fun getImplLayoutId() = R.layout.pop_addfee_layout


    constructor(context: Context,fromAddress:String,toAddress:String,withdrawalTxHash:String,
                fromChain:String,toChain:String,oldFee:String,
                assetChainId:Int,assetAssetId:Int,assetContractAddress:String):this(context){

        this.fromChain = fromChain
        this.oldFee = oldFee
        this.toChain= toChain
        this.assetAssetId = assetAssetId
        this.assetChainId = assetChainId
        this.toAddress = toAddress
        this.assetContractAddress = assetContractAddress
        this.fromAddress = fromAddress
        this.withdrawalTxHash = withdrawalTxHash

    }


    override fun onCreate() {
        super.onCreate()
        loadingPopup = XPopup.Builder(context)
            .hasStatusBarShadow(false)
            .hasShadowBg(false)
            .dismissOnBackPressed(false)
            .dismissOnTouchOutside(false)
            .asCustom(LoadingPopup(context) {

            })
        if(fromChain!=null){
            getFeeByNvtCross(fromChain!!)
        }
        btn_cancel.setOnClickListener {
            dismiss()
        }
        tv_alread_pay.text = oldFee
        btn_sure.setOnClickListener {
            if(TextUtils.isEmpty(tv_add_fee.text)){
                showToast(context.getString(R.string.input_add_fee_amount))
                return@setOnClickListener
            }
            loadingPopup?.show()
            Thread(
                Runnable {
//                    var txhash =   SendTransctions.createNerveWithdrawalAdditionalFeeTx()
//                    (context as Activity).runOnUiThread {
//                        loadingPopup?.dismiss()
//                        if(!TextUtils.isEmpty(txhash)){
//                            fromChain?.let { it1 -> crossAuther(it1,txhash) }
//                        }
//
//                    }
                }
            ).start()

        }
    }




    //查询异构跨链手续费
    private fun getFeeByNvtCross(chain:String){
        RetrofitClient.getInstance().invokePostBody(context, Api.NVT_CROSS_FEE, mapOf(
            "language" to UserDao.language,
            "chain" to chain
        )).subscribe(object : RecObserver<BaseResponse<Number>>(context,false,false,Api.NVT_CROSS_FEE){
            override fun onSuccess(t: BaseResponse<Number>?) {
                //小数点八位
                if(t?.data!=null){
                    tv_recommend_fee.text  =  NaboxUtils.coinValueOf(BigInteger.valueOf(t.data.toLong()),8)+ "NVT"
                }
            }

            override fun onFail(msg: String, code: Int) {
                showToast(msg)
            }
        })
    }


    /**
     * 异构链资产在跨链转出到nerve链上之前，需要在本链广播授权交易
     */
    private fun crossAuther(crossChain:String,txHex:String){
        RetrofitClient.getInstance().invokePostBody(context,Api.CROSS_AUTHER, mapOf(
            "language" to UserDao.language,
            "chain"  to crossChain,
            "txHex" to txHex
        )).subscribe(object :RecObserver<BaseResponse<Any>>(context,true,false,Api.CROSS_AUTHER){
            override fun onSuccess(t: BaseResponse<Any>?) {

                crossTransfer(txHex)

            }

            override fun onFail(msg: String, code: Int) {
                showToast(msg)
            }
        })
    }


    private fun crossTransfer(txHex:String){

        RetrofitClient.getInstance().invokePostBody(context,Api.CROSS_TRANSFER, mapOf(
            "language" to UserDao.language,
            "fromChain"  to fromChain,
            "toChain" to toChain,
            "fromAddress" to fromAddress,
            "toAddress" to  toAddress,
            "txHex" to  txHex,
            "chainId" to assetChainId,
            "assetId" to assetAssetId,
            "contractAddress" to assetContractAddress,
            "crossTxHex" to ""

        )).subscribe(object :RecObserver<BaseResponse<Any>>(context,true,false,Api.CROSS_TRANSFER){
            override fun onSuccess(t: BaseResponse<Any>?) {
                    dismiss()
            }

            override fun onFail(msg: String, code: Int) {
                    showToast(msg)
            }
        })
    }


    override fun onDismiss() {
        super.onDismiss()
        if(loadingPopup!=null){
            loadingPopup?.dismiss()
        }
    }
}