package com.nuls.naboxpro.ui.activity

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gyf.immersionbar.ktx.immersionBar
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.db.WalletInfoDaoHelper
import com.nuls.naboxpro.entity.WalletInfo
import com.nuls.naboxpro.enums.CoinTypeEnum
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.BaseResponse
import com.nuls.naboxpro.net.api.RetrofitClient
import com.nuls.naboxpro.net.observable.RecObserver
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.ui.splash.CreateWalletActivity
import com.nuls.naboxpro.utils.NaboxUtils
import com.nuls.naboxpro.utils.copyToClipboard
import com.nuls.naboxpro.utils.getAmountByUsd
import com.nuls.naboxpro.utils.showToast
import kotlinx.android.synthetic.main.activity_wallet_list.*
import kotlinx.android.synthetic.main.activity_wallet_list.back
import kotlinx.android.synthetic.main.activity_wallet_list.tv_title
import kotlinx.android.synthetic.main.base_title_layout.*
import network.nerve.core.crypto.HexUtil
import org.web3j.crypto.WalletUtils


/**
 * 钱包管理
 */
class WalletManageActivity:BaseActivity(){

    override fun getLayoutId() = R.layout.activity_wallet_list


    var adapter:WalletAdapter?=null

    override fun initView() {
        back.setOnClickListener {
            finish()
        }
        immersionBar {
            statusBarDarkFont(true,0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }
        tv_title.text = getString(R.string.wallet_manage)
        recycle_wallet.layoutManager = LinearLayoutManager(this)
        adapter = WalletAdapter()
        recycle_wallet.adapter = adapter

        tv_add.setOnClickListener {
            startActivity(Intent(this,CreateWalletActivity().javaClass))
        }

        adapter?.setOnItemClickListener { adapter, view, position ->
            var walletInfo:WalletInfo = adapter.data[position] as WalletInfo
            val cardView = view.findViewById<View>(R.id.layout_wallet)
            //编辑钱包
            val intent = Intent(this, WalletEditActivity::class.java)
            val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this as Activity,
                cardView,
                "share_cardview"
            ).toBundle()
            intent.putExtra("address", walletInfo.nulsAddress)
            startActivity(intent, bundle)
        }
    }

    override fun initData() {

    }


    override fun onResume() {
        super.onResume()
        if(WalletInfoDaoHelper.loadAllWallet()!=null&&WalletInfoDaoHelper.loadAllWallet().size>0){
            adapter?.setNewInstance(WalletInfoDaoHelper.loadAllWallet())
            adapter?.notifyDataSetChanged()
            loadAllWalletMoney()
        }
    }


    class WalletAdapter:BaseQuickAdapter<WalletInfo,BaseViewHolder>(R.layout.item_wallet_list_layout){
        override fun convert(helper: BaseViewHolder, item: WalletInfo) {

            val cardView = helper.getView<CardView>(R.id.layout_wallet)
            if (TextUtils.isEmpty(item.alias)) {
                helper.setText(R.id.tv_wallet_name,"Nabox Wallet")
            } else {
                helper.setText(R.id.tv_wallet_name, item.alias)
            }
//        if(item.getWalletType()!=null){
//            helper.setText(R.id.tv_wallet_type,"("+WalletTypeEnum.getMessageByCode(item.getWalletType().code())+")");
//        }else{
//            helper.setText(R.id.tv_wallet_type,"--");
//        }
            if (TextUtils.isEmpty(item.nulsAddress)) {
                helper.setText(R.id.tv_wallet_address, "-- --")
            } else {
                helper.setText(R.id.tv_wallet_address, item.nulsAddress)
            }
            if (!TextUtils.isEmpty(item.usdPrice)) {
                helper.setText(
                    R.id.tv_money,
                    getAmountByUsd(item.usdPrice)
                )
            } else {
                helper.setText(R.id.tv_money, "0.00")
            }
            val relativeLayout = helper.getView<RelativeLayout>(R.id.layout_bg)
            NaboxUtils.initWalletSkin(relativeLayout, item.color)
            //复制到剪贴板
            helper.getView<TextView>(R.id.tv_wallet_address)
                .setOnClickListener(View.OnClickListener {
                    copyToClipboard(context, item.nulsAddress)
                    showToast(context.getString(R.string.copy_to_clipboard))
                })
            helper.getView<ImageView>(R.id.iv_zxing)
                .setOnClickListener(View.OnClickListener {
//                    showToast("开发中")

                     AddressZxingActivity.start(context,item.nulsAddress,CoinTypeEnum.NULS.code)
//                    val walletInfo = WalletInfo()
//                    walletInfo.setNaboxWallet(item)
//                    val intent = Intent(context, ReceivablesActivity::class.java)
//                    intent.putExtra("nabox", walletInfo)
//                    ArmsUtils.startActivity(intent)
                })
        }
    }



    private fun loadAllWalletMoney(){
        var walletList:MutableList<WalletInfo>  = WalletInfoDaoHelper.loadAllWallet()
        WalletInfoDaoHelper.loadAllWallet()
        RetrofitClient.getInstance().invokePostBody(this,Api.GET_WALLET_LIST_PRICE, mapOf(
            "language" to UserDao.language,
            "pubKeyList" to WalletInfoDaoHelper.loadAllWalletPubKey()

        )).subscribe(object :RecObserver<BaseResponse<HashMap<String,String>>>(this,false,false,Api.GET_WALLET_LIST_PRICE){
            override fun onSuccess(t: BaseResponse<HashMap<String,String>>?) {
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