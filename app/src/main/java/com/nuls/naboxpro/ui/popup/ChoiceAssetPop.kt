package com.nuls.naboxpro.ui.popup

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.core.BottomPopupView
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.entity.AssetsEntity
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.BaseResponse
import com.nuls.naboxpro.net.api.RetrofitClient
import com.nuls.naboxpro.net.observable.RecObserver
import com.nuls.naboxpro.utils.NaboxUtils
import com.nuls.naboxpro.utils.isSupportTransfer
import com.nuls.naboxpro.utils.showToast
import kotlinx.android.synthetic.main.pop_assets_overview_layout.view.*
import java.math.BigInteger

class ChoiceAssetPop(context: Context): BottomPopupView(context) {

    var chain:String = ""
    var address:String = ""

    /**
     * 出金链
     */
    var toChain:String = ""
    var itemClickListener:OnItemClickListener?=null
    constructor(context: Context,chain:String,address:String,toChain:String,itemClickListener:OnItemClickListener):this(context){
        this.chain = chain
        this.address = address
        this.toChain = toChain
        this.itemClickListener = itemClickListener
    }



    var myAdapter:AssetAdapter?=null

    override fun getImplLayoutId() = R.layout.pop_choice_asset


    override fun onCreate() {
        super.onCreate()
        list_assets.layoutManager = LinearLayoutManager(context) as RecyclerView.LayoutManager?
        myAdapter = AssetAdapter()
        list_assets.adapter = myAdapter
        myAdapter?.setOnItemClickListener { adapter, view, position ->
            itemClickListener?.itemClick(adapter.data[position] as AssetsEntity,getData() as List<AssetsEntity>)
            dismiss()
        }
        iv_close.setOnClickListener {
            dismiss()
        }
        getAssetsList(chain,address)
    }


     class AssetAdapter:BaseQuickAdapter<AssetsEntity, BaseViewHolder>(R.layout.item_pop_asset_list){
         override fun convert(holder: BaseViewHolder, item: AssetsEntity) {
             holder.setText(R.id.tv_assetName,item.symbol)
             if("0" == item.balance){
                 holder.setText(R.id.tv_amount,"0")
             }else{
                 holder.setText(R.id.tv_amount,NaboxUtils.coinValueOf(BigInteger(item.balance) ,item.decimals))
             }


         }
     }



    fun checkAsset(assetList:List<AssetsEntity>): MutableList<AssetsEntity>? {
        var supportList:MutableList<AssetsEntity> = mutableListOf()
        for(index in assetList.indices){
            if(isSupportTransfer(chain,toChain,assetList[index])){
                supportList.add(assetList[index])
            }
        }
        return  supportList
    }



    /**
     * 获取资产列表
     *  chain 地址所属链
     *  address 地址
     */
    private fun getAssetsList(chain:String,address:String){
        RetrofitClient.getInstance().invokePostBody(context, Api.GET_ASSETS, mapOf(
            "language" to  UserDao.language,
            "chain" to  chain,
            "address" to  address
        )).subscribe(object : RecObserver<BaseResponse<List<AssetsEntity>>>(context,false,false, Api.GET_ASSETS){
            override fun onSuccess(t: BaseResponse<List<AssetsEntity>>?) {
                if(t?.data!=null){
//                    myAdapter?.setNewInstance(t.data as MutableList<AssetsEntity>?)
                    myAdapter?.setNewInstance(checkAsset(t.data))
                }
            }

            override fun onFail(msg: String, code: Int) {
                showToast(msg)
            }
        })
    }

    open fun getData(): List<AssetsEntity>? {
        return if(myAdapter==null) null
        else myAdapter!!.data
    }

    interface  OnItemClickListener{

        fun itemClick(item:AssetsEntity,items:List<AssetsEntity>)
    }





}