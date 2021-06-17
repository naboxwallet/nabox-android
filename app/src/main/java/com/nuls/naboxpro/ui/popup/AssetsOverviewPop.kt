package com.nuls.naboxpro.ui.popup

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.lxj.xpopup.core.BottomPopupView
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.db.WalletInfoDaoHelper
import com.nuls.naboxpro.entity.AssetsOverviewEntity
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.BaseResponse
import com.nuls.naboxpro.net.api.RetrofitClient
import com.nuls.naboxpro.net.observable.RecObserver
import com.nuls.naboxpro.ui.adapter.AssetsOverviewAdapter
import com.nuls.naboxpro.utils.showAssetsOverviewPop
import com.nuls.naboxpro.utils.showToast
import kotlinx.android.synthetic.main.pop_assets_overview_layout.view.*
import network.nerve.core.crypto.HexUtil

/**
 * 资产概览
 */
class AssetsOverviewPop(context: Context): BottomPopupView(context){

    var myAdapter:AssetsOverviewAdapter?=null
    override fun getImplLayoutId() = R.layout.pop_assets_overview_layout


    override fun onCreate() {
        super.onCreate()
        iv_close.setOnClickListener {
            dismiss()
        }
        myAdapter = AssetsOverviewAdapter(context)
        list_assets.layoutManager = LinearLayoutManager(context)
        list_assets.adapter = myAdapter
        getAssetsList()
    }


    /**
     * 获取资产列表
     */
    private  fun getAssetsList(){

        RetrofitClient.getInstance().invokePostBody(context,Api.CHAIN_PRICE, mapOf(
            "language" to UserDao.language,
            "pubKey" to HexUtil.encode(WalletInfoDaoHelper.loadDefaultWallet().compressedPubKey)
        )).subscribe(object :RecObserver<BaseResponse<List<AssetsOverviewEntity>>>(context,false,false,Api.CHAIN_PRICE){
            override fun onSuccess(t: BaseResponse<List<AssetsOverviewEntity>>?) {
                    if(t!=null&&t.data!=null){
                        myAdapter?.setNewInstance(t.data as MutableList<AssetsOverviewEntity>?)
                    }
            }

            override fun onFail(msg: String, code: Int) {
                showToast(msg)
            }
        })
    }
}