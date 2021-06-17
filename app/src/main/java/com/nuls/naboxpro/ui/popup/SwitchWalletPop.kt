package com.nuls.naboxpro.ui.popup

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.db.WalletInfoDaoHelper
import com.nuls.naboxpro.entity.WalletInfo
import com.nuls.naboxpro.eventbus.SwitchWalletEvent
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.BaseResponse
import com.nuls.naboxpro.net.api.RetrofitClient
import com.nuls.naboxpro.net.observable.RecObserver
import com.nuls.naboxpro.ui.splash.CreateWalletActivity
import com.nuls.naboxpro.ui.splash.InitWalletActivity
import com.nuls.naboxpro.utils.getAmountByUsd
import io.nuls.core.crypto.HexUtil
import kotlinx.android.synthetic.main.pop_wallet_list_layout.view.*
import org.greenrobot.eventbus.EventBus

class SwitchWalletPop(context: Context): BottomPopupView(context) {


    var adapter:WalletListAdapter?=null

    override fun getImplLayoutId() = R.layout.pop_wallet_list_layout


    override fun onCreate() {
        super.onCreate()
        adapter = WalletListAdapter()
        wallet_list.layoutManager = LinearLayoutManager(context)
        wallet_list.adapter = adapter
        adapter?.setOnItemClickListener { adapter, view, position ->
            //切换选中 并刷新数据
            var walletInfo:WalletInfo = adapter.data[position] as WalletInfo
            //重新设置默认钱包
            UserDao.defaultWallet =walletInfo.nulsAddress
            EventBus.getDefault().post(SwitchWalletEvent())
            dismiss()
        }
        close.setOnClickListener {
            dismiss()
        }
        btn_create.setOnClickListener {
            context.startActivity(Intent(context,InitWalletActivity().javaClass))
            dismiss()
        }
        btn_import.setOnClickListener {
            XPopup.Builder(context)
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(false)
                .asCustom(ImportTypePopup(context))
                .show()
            dismiss()
        }
        var walletList:MutableList<WalletInfo>  = WalletInfoDaoHelper.loadAllWallet()
        if(walletList!=null){
            adapter?.setNewInstance(walletList)
            loadAllWalletMoney(walletList)
        }
    }



    //adapter
    class WalletListAdapter:BaseQuickAdapter<WalletInfo,BaseViewHolder>(R.layout.item_switch_wallet_layout){
        override fun convert(holder: BaseViewHolder, item: WalletInfo) {
            holder.setText(R.id.tv_name,item.alias)
            if(TextUtils.isEmpty(item.usdPrice)){
                holder.setText(R.id.tv_price, R.string.null_content)
            }else{
                holder.setText(R.id.tv_price, getAmountByUsd(item.usdPrice))
            }
            if(item.nulsAddress==UserDao.defaultWallet){
                holder.setGone(R.id.tv_check,false)
            }else{
                holder.setGone(R.id.tv_check,true)
            }
        }
    }


    private fun loadAllWalletMoney(walletList:MutableList<WalletInfo>){
        WalletInfoDaoHelper.loadAllWallet()
        RetrofitClient.getInstance().invokePostBody(context, Api.GET_WALLET_LIST_PRICE, mapOf(
            "language" to UserDao.language,
            "pubKeyList" to WalletInfoDaoHelper.loadAllWalletPubKey()

        )).subscribe(object : RecObserver<BaseResponse<HashMap<String, String>>>(context,false,false,Api.GET_WALLET_LIST_PRICE){
            override fun onSuccess(t: BaseResponse<HashMap<String, String>>?) {
                for(index in walletList.indices){
                    walletList[index].usdPrice = t?.data?.get(HexUtil.encode(walletList[index].compressedPubKey))
                }
                adapter?.setNewInstance(walletList)
                adapter?.notifyDataSetChanged()

            }

            override fun onFail(msg: String, code: Int) {

            }
        })
    }






}