package com.nuls.naboxpro.ui.popup

import android.content.Context
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.core.BottomPopupView
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.entity.AssetsEntity
import com.nuls.naboxpro.entity.WalletInfo
import com.nuls.naboxpro.enums.CoinTypeEnum
import kotlinx.android.synthetic.main.pop_cross_chain_layout.view.*

class CrossChainPop (context: Context): BottomPopupView(context){


    var walletInfo:WalletInfo?=null
    //已经选中的链  弹窗中不再显示
    var choiceChain:String?=null
    var choiceListener:ChoiceListener?=null
    override fun getImplLayoutId() = R.layout.pop_cross_chain_layout


    /**
     *
     */
    constructor(context: Context,choiceChain:String?,walletInfo: WalletInfo,choiceListener:ChoiceListener):this(context){
        this.walletInfo = walletInfo
        this.choiceChain = choiceChain
        this.choiceListener = choiceListener
    }

    override fun onCreate() {
        super.onCreate()
        iv_close.setOnClickListener {
            dismiss()
        }
        if(!TextUtils.isEmpty(choiceChain)){

            when(choiceChain){
                CoinTypeEnum.NULS.code -> layout1.visibility = View.GONE
                CoinTypeEnum.NERVE.code -> layout2.visibility = View.GONE
                CoinTypeEnum.ETH.code -> layout3.visibility = View.GONE
                CoinTypeEnum.BSC.code -> layout4.visibility = View.GONE
                CoinTypeEnum.HECO.code -> layout5.visibility = View.GONE
                CoinTypeEnum.OKEX.code -> layout6.visibility = View.GONE
            }
        }
        layout1.setOnClickListener {
            walletInfo?.nulsAddress?.let { it1 ->
                choiceListener?.itemClick(CoinTypeEnum.NULS.code,
                    it1
                )
            }
            dismiss()
        }
        layout2.setOnClickListener {
            walletInfo?.nerveAddress?.let { it1 ->
                choiceListener?.itemClick(CoinTypeEnum.NERVE.code,
                    it1
                )
            }
            dismiss()
        }
        layout3.setOnClickListener {
            walletInfo?.ethAddress?.let { it1 ->
                choiceListener?.itemClick(CoinTypeEnum.ETH.code,
                    it1
                )
            }
            dismiss()
        }
        layout4.setOnClickListener {
            walletInfo?.bscAddress?.let { it1 ->
                choiceListener?.itemClick(CoinTypeEnum.BSC.code,
                    it1
                )
            }
            dismiss()
        }
        layout5.setOnClickListener {
            walletInfo?.hecoAddress?.let { it1 ->
                choiceListener?.itemClick(CoinTypeEnum.HECO.code,
                    it1
                )
            }
            dismiss()
        }

        layout6.setOnClickListener {
            walletInfo?.oktAddress?.let { it1 ->
                choiceListener?.itemClick(CoinTypeEnum.OKEX.code,
                    it1
                )
            }
            dismiss()
        }

//        list_assets.layoutManager = LinearLayoutManager(context)

    }



//
//    /**
//     * 获取资产列表
//     *  chain 地址所属链
//     *  address 地址
//     */
//    private fun getAssetsList(chain:String,address:String){
//        RetrofitClient.getInstance().invokePostBody(context, Api.GET_ASSETS, mapOf(
//            "language" to  UserDao.language,
//            "chain" to  chain,
//            "address" to  address
//        )).subscribe(object : RecObserver<BaseResponse<List<AssetsEntity>>>(context,false,false){
//            override fun onSuccess(t: BaseResponse<List<AssetsEntity>>?) {
//                if(t?.data!=null){
//                    myAdapter?.setNewInstance(t.data as MutableList<AssetsEntity>?)
//                }
//            }
//
//            override fun onFail(msg: String, code: Int) {
//                showToast(msg)
//            }
//        })
//    }
    interface  ChoiceListener{

        fun itemClick(chain: String,address:String)
    }
//
//
//    class MyAdapter: BaseQuickAdapter<AssetsEntity, BaseViewHolder>(R.layout.item_cross_chain_layout){
//        override fun convert(holder: BaseViewHolder, item: AssetsEntity) {
//            holder.setText(R.id.tv_assetName,item.symbol)
//            holder.setText(R.id.tv_amount, NaboxUtils.coinValueOf(BigInteger(item.balance) ,item.decimals))
//
//        }
//    }


}