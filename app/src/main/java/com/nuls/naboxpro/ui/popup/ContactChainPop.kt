package com.nuls.naboxpro.ui.popup

import android.content.Context
import com.lxj.xpopup.core.BottomPopupView
import com.nuls.naboxpro.R
import com.nuls.naboxpro.entity.WalletInfo
import com.nuls.naboxpro.enums.CoinTypeEnum
import kotlinx.android.synthetic.main.pop_cross_chain_layout.view.*

class ContactChainPop(context: Context): BottomPopupView(context) {


    var choiceListener: ChoiceListener?=null
    override fun getImplLayoutId() = R.layout.pop_cross_chain_layout
    constructor(context: Context, choiceListener: ChoiceListener):this(context){
        this.choiceListener = choiceListener
    }


    override fun onCreate() {
        super.onCreate()

        iv_close.setOnClickListener {

            dismiss()

        }
        layout1.setOnClickListener {
            choiceListener?.itemClick(CoinTypeEnum.NULS.code)
            dismiss()
        }
        layout2.setOnClickListener {
            choiceListener?.itemClick(CoinTypeEnum.NERVE.code)
            dismiss()
        }
        layout3.setOnClickListener {
            choiceListener?.itemClick(CoinTypeEnum.ETH.code)
            dismiss()
        }
        layout4.setOnClickListener {
            choiceListener?.itemClick(CoinTypeEnum.BSC.code)
            dismiss()
        }
        layout5.setOnClickListener {
            choiceListener?.itemClick(CoinTypeEnum.HECO.code)
            dismiss()
        }

    }


    interface  ChoiceListener{

        fun itemClick(chain: String)
    }



}