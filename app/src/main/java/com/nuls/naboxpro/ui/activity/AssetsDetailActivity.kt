package com.nuls.naboxpro.ui.activity

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gyf.immersionbar.ktx.immersionBar
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.entity.AssetsDetailEntity
import com.nuls.naboxpro.entity.AssetsEntity
import com.nuls.naboxpro.entity.TxDetailBean
import com.nuls.naboxpro.entity.TxListBean
import com.nuls.naboxpro.enums.CoinTypeEnum
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.BaseResponse
import com.nuls.naboxpro.net.api.RetrofitClient
import com.nuls.naboxpro.net.observable.RecObserver
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.ui.widget.EmptyView
import com.nuls.naboxpro.utils.*
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.layout_coin_detail_layout.*
import java.math.BigInteger
import java.text.SimpleDateFormat


/**
 * 资产详情
 */
class AssetsDetailActivity:BaseActivity(){




    var myAdapter:TransferHistoryAdapter?=null
    var pageIndex = 1
    var emptyView:EmptyView?=null
    var assetsEntity:AssetsEntity ?=null
    override fun getLayoutId() = R.layout.layout_coin_detail_layout

    override fun initView() {
        immersionBar {
            statusBarDarkFont(true,0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.main)
        }
        rl_finish.setOnClickListener {
            finish()
        }
        emptyView = EmptyView(this)
        emptyView?.setEmptyImg(R.mipmap.png_no_trans)
        emptyView?.setEmptyText(R.string.no_transcation)
        if(intent!=null){
//            if(intent.getIntExtra("assetId",0)!=null){
//                assetId = intent.getIntExtra("assetId",0)
//            }
//            if(intent.getStringExtra("contractAddress")!=null){
//                contractAddress = intent.getStringExtra("contractAddress")
//            }
            if(intent.getSerializableExtra("assets")!=null){
                assetsEntity = intent.getSerializableExtra("assets") as AssetsEntity?
            }
            getAssetsDetail(assetsEntity?.chain,assetsEntity?.address,assetsEntity?.chainId,assetsEntity?.assetId,assetsEntity?.contractAddress)
            getTransferHistory(assetsEntity?.chain,assetsEntity?.address,assetsEntity?.chainId,assetsEntity?.assetId,assetsEntity?.contractAddress)


            myAdapter = TransferHistoryAdapter()
            list_history.layoutManager = LinearLayoutManager(this)
            list_history.adapter = myAdapter
            myAdapter?.setOnItemClickListener { adapter, view, position ->
                var item:TxListBean.RecordsBean = adapter.data[position] as TxListBean.RecordsBean
//                if(item.){
//
//                }
//                assetsEntity?.chain?.let { TransferDetailActivity().start(this,item.hash,item.id, it) }
                assetsEntity?.chain?.let { getDetail(it,item.hash,item.id) }
            }
            myAdapter?.setEmptyView(emptyView!!)
            refresh_layout.setOnRefreshLoadMoreListener(object :OnRefreshLoadMoreListener{
                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pageIndex++
                    getTransferHistory(assetsEntity?.chain,assetsEntity?.address,assetsEntity?.chainId,assetsEntity?.assetId,assetsEntity?.contractAddress)
                }

                override fun onRefresh(refreshLayout: RefreshLayout) {
                    pageIndex=1
                    getTransferHistory(assetsEntity?.chain,assetsEntity?.address,assetsEntity?.chainId,assetsEntity?.assetId,assetsEntity?.contractAddress)
                }

            })
        }
        //跳转到交易页面
        btn_transfer.setOnClickListener {

            TransferActivity.start(this,intent.getStringExtra("fromChain"),intent.getStringExtra("fromAddress"),assetsEntity)
        }
        //跳转到跨连划转页面
        btn_crossfer.setOnClickListener {
            CrossChainActivity.start(this,intent.getStringExtra("fromAddress"),intent.getStringExtra("fromChain"),assetsEntity)

        }
    }

    override fun initData() {

    }


    /**
     * 获取资产详情
     */
    private fun getAssetsDetail(chain:String?,address:String?,chainId:Int?,assetId: Int?,contractAddress:String?){
        RetrofitClient.getInstance().invokePostBody(this, Api.GET_ASSETS_DETAIL, mapOf(
            "language" to  UserDao.language,
            "chain" to  chain,
            "address" to  address,
            "chainId" to chainId,
            "assetId" to  assetId,
            "contractAddress" to contractAddress,
            "refresh" to false
        )).subscribe(object : RecObserver<BaseResponse<AssetsDetailEntity>>(this,false,false,Api.GET_ASSETS_DETAIL){
            override fun onSuccess(t: BaseResponse<AssetsDetailEntity>?) {
                if(t?.data!=null){
                    tv_nums.text = NaboxUtils.coinValueOf(BigInteger(t.data.total) ,t.data.decimals).toString()
                    tv_frozen_amount.text = NaboxUtils.coinValueOf(BigInteger(t.data?.locked),t.data.decimals).toString()
                    tv_name.text = t.data.symbol
                    tv_available_amount.text = NaboxUtils.coinValueOf(BigInteger(t.data?.balance),t.data.decimals).toString()
                    loadCoinIcon(this@AssetsDetailActivity,iv_icon, getImageUrl(t.data.symbol))
                    tv_money.text = getAmountByUsd(t.data.usdPrice)
                }
            }

            override fun onFail(msg: String, code: Int) {
                showToast(msg)
            }
        })
    }


    /**
     * 获取资产交易记录
     */
    private fun getTransferHistory(chain:String?,address:String?,chainId:Int?,assetId: Int?,contractAddress:String?){
        var map:MutableMap<Any,Any> = mutableMapOf()
        map["language"] = UserDao.language
        chain?.let { map.put("chain", it) }
        address?.let { map.put("address", it) }
        if(chain == CoinTypeEnum.NERVE.code){
            chainId?.let { map.put("chainId", it) }
            assetId?.let { map.put("assetId", it) }
        }else{
            if(TextUtils.isEmpty(contractAddress)){
                chainId?.let { map.put("chainId", it) }
                assetId?.let { map.put("assetId", it) }
            }
            contractAddress?.let { map.put("contractAddress", it) }
        }
        map["pageNumber"] = pageIndex
        map["pageSize"] = 20
//        map["language"] = UserDao.language
//        map["language"] = UserDao.language
//        map["language"] = UserDao.language
//        map["language"] = UserDao.language

//        mapOf(
//            "language" to  UserDao.language,
//            "chain" to  chain,
//            "address" to  address,
//            "chainId" to chainId,
//            "assetId" to  assetId,
//            "contractAddress" to contractAddress,
//            "pageNumber" to pageIndex,
//            "pageSize" to  20
//        )

        RetrofitClient.getInstance().invokePostBody(this, Api.GET_COIN_LIST,map ).subscribe(object : RecObserver<BaseResponse<TxListBean>>(this,false,false){
            override fun onSuccess(t: BaseResponse<TxListBean>?) {
                if(refresh_layout!=null){
                    refresh_layout.finishLoadMore()
                    refresh_layout.finishRefresh()
                }
                if(pageIndex==1){
                    if(t?.data?.records!=null){
                        myAdapter?.setNewInstance(t.data?.records)
                    }

                }else{
                    t?.data?.records?.let { myAdapter?.addData(it) }
                    myAdapter?.notifyDataSetChanged()
                }


            }

            override fun onFail(msg: String, code: Int) {
                if(refresh_layout!=null){
                    refresh_layout.finishLoadMore()
                    refresh_layout.finishRefresh()
                }
                showToast(msg)
            }
        })
    }



    companion object{
        fun start(context: Context,assets:AssetsEntity,fromChain:String,fromAddress:String){
            var intent:Intent = Intent(context,AssetsDetailActivity().javaClass)
            intent.putExtra("assets",assets)
            intent.putExtra("fromChain",fromChain)
            intent.putExtra("fromAddress",fromAddress)
            context.startActivity(intent)
        }
    }


    class TransferHistoryAdapter:BaseQuickAdapter<TxListBean.RecordsBean,BaseViewHolder>(R.layout.item_coin_detail_layout){
        var simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
        override fun convert(holder: BaseViewHolder, item: TxListBean.RecordsBean) {
            when (item.status) {
                0 -> { //状态：0、未确认；1、已确认；-1失败
                    holder.setText(R.id.tv_transtype,R.string.Unconfirmed)
                    holder.setBackgroundResource(R.id.tv_transtype,R.drawable.shape_transaction_in)
                }
                1 -> {
                    holder.setText(R.id.tv_transtype,R.string.Confirmed)
                    holder.setBackgroundResource(R.id.tv_transtype,R.drawable.shape_transaction_out)
                }
                -1 -> {
                    holder.setText(R.id.tv_transtype,R.string.Fail)
                    holder.setBackgroundResource(R.id.tv_transtype,R.drawable.shape_transaction_in)
                }
            }
            if(TextUtils.isEmpty(item.froms)){
                holder.setText(R.id.tv_fromAddress,R.string.null_content)
            }else{
                holder.setText(R.id.tv_fromAddress,item.froms)
            }
            if(TextUtils.isEmpty(item.tos)){
                holder.setText(R.id.tv_toaddress,R.string.null_content)
            }else{
                holder.setText(R.id.tv_toaddress,item.tos)
            }
            if(TextUtils.isEmpty(item.createTime)){
                holder.setText(R.id.tv_date,R.string.null_content)
            }else{
                holder.setText(R.id.tv_date,item.createTime)
            }
            if(TextUtils.isEmpty(item.symbol)){
                holder.setText(R.id.tv_coinName,R.string.null_content)
            }else{
                holder.setText(R.id.tv_coinName,item.symbol)
            }
            if(item.transType==1){//方向：1、转入；-1、转出
                holder.setText(
                    R.id.tv_amount,
                    "+" + NaboxUtils.coinValueOf(BigInteger(item.amount), item.decimals)
                )
                holder.setTextColor(R.id.tv_amount,context.resources.getColor(R.color.main))

            }else if(item.transType==-1){
                holder.setText(
                    R.id.tv_amount,
                    "-" + NaboxUtils.coinValueOf(BigInteger(item.amount), item.decimals)
                )
                holder.setTextColor(R.id.tv_amount,context.resources.getColor(R.color.orange_button_basic))
            }
        }
    }


    fun getDetail(chain:String,txHash:String,transId:Long){
        RetrofitClient.getInstance().invokePostBody(this, Api.TX_INFO, mapOf(
            "language" to UserDao.language,
            "chain" to chain,
            "txHash" to txHash,
            "transCoinId" to transId

        )).subscribe(object :RecObserver<BaseResponse<TxDetailBean>>(this,true,false, Api.TX_INFO){
            override fun onSuccess(t: BaseResponse<TxDetailBean>?) {
                if(t?.data!=null){
                   if(t.data.crossTx!=null){
                        //跨链交易
                       CrossChainDetailActivity.start(this@AssetsDetailActivity,t.data)
                   }else{
                       //本链交易
                       TransferDetailActivity.start(this@AssetsDetailActivity,t.data)

                   }
                }

            }

            override fun onFail(msg: String, code: Int) {
                showToast(msg)
            }

        })

    }

}