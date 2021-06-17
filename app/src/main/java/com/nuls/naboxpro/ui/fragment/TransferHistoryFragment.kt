package com.nuls.naboxpro.ui.fragment

import android.text.TextUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.db.WalletInfoDaoHelper
import com.nuls.naboxpro.entity.TxDetailBean
import com.nuls.naboxpro.entity.TxListBean
import com.nuls.naboxpro.eventbus.RefreshEvent
import com.nuls.naboxpro.eventbus.SwitchWalletEvent
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.BaseResponse
import com.nuls.naboxpro.net.api.RetrofitClient
import com.nuls.naboxpro.net.observable.RecObserver
import com.nuls.naboxpro.ui.BaseFragment
import com.nuls.naboxpro.ui.activity.AssetsDetailActivity
import com.nuls.naboxpro.ui.activity.CrossChainDetailActivity
import com.nuls.naboxpro.ui.activity.TransferDetailActivity
import com.nuls.naboxpro.utils.NaboxUtils
import com.nuls.naboxpro.utils.getAddressByCoinType
import com.nuls.naboxpro.utils.showToast
import kotlinx.android.synthetic.main.fragment_asset_layout.*
import kotlinx.android.synthetic.main.layout_coin_detail_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.math.BigInteger
import java.text.SimpleDateFormat

class TransferHistoryFragment:BaseFragment() {


    var myAdapter: TransferHistoryAdapter?=null
    var pageIndex:Int = 1
    override fun getLayoutId() = R.layout.fragment_asset_layout

    override fun initView() {
        EventBus.getDefault().register(this)
        myAdapter = TransferHistoryAdapter()
        list_data.layoutManager = LinearLayoutManager(activity)
        list_data.adapter = myAdapter
        getTransferHistory(UserDao.defaultChain, getAddressByCoinType(WalletInfoDaoHelper.loadDefaultWallet(),UserDao.defaultChain))
        smartlayout.setOnLoadMoreListener {
            pageIndex++
            getTransferHistory(UserDao.defaultChain, getAddressByCoinType(WalletInfoDaoHelper.loadDefaultWallet(),UserDao.defaultChain))
        }
        myAdapter?.setOnItemClickListener { adapter, view, position ->
            var item:TxListBean.RecordsBean = adapter.data[position] as TxListBean.RecordsBean
            getDetail(UserDao.defaultChain,item.hash,item.id)
        }
    }

    override fun initData() {

    }


    /**
     * 获取资产交易记录
     */
    private fun getTransferHistory(chain:String?,address:String?){
        var map:MutableMap<Any,Any> = mutableMapOf()
        map["language"] = UserDao.language
        chain?.let { map.put("chain", it) }
        address?.let { map.put("address", it) }
        map["pageNumber"] = pageIndex
        map["pageSize"] = 20
        RetrofitClient.getInstance().invokePostBody(activity, Api.GET_COIN_LIST,map ).subscribe(object : RecObserver<BaseResponse<TxListBean>>(activity,false,false,Api.GET_COIN_LIST){
            override fun onSuccess(t: BaseResponse<TxListBean>?) {
                smartlayout?.finishLoadMore()
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
                smartlayout?.finishLoadMore()
                showToast(msg)
            }
        })
    }
    fun getDetail(chain:String,txHash:String,transId:Long){
        RetrofitClient.getInstance().invokePostBody(activity, Api.TX_INFO, mapOf(
            "language" to UserDao.language,
            "chain" to chain,
            "txHash" to txHash,
            "transCoinId" to transId

        )).subscribe(object :RecObserver<BaseResponse<TxDetailBean>>(activity,true,false,Api.TX_INFO){
            override fun onSuccess(t: BaseResponse<TxDetailBean>?) {
                if(t?.data!=null){
                    if(t.data.crossTx!=null){
                        //跨链交易
                        activity?.let { CrossChainDetailActivity.start(it,t.data) }
                    }else{
                        //本链交易
                        activity?.let { TransferDetailActivity.start(it,t.data) }

                    }
                }

            }

            override fun onFail(msg: String, code: Int) {
                showToast(msg)
            }

        })

    }



    class TransferHistoryAdapter: BaseQuickAdapter<TxListBean.RecordsBean, BaseViewHolder>(R.layout.item_coin_detail_layout){
        var simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
        override fun convert(holder: BaseViewHolder, item: TxListBean.RecordsBean) {
            when {
                item.status==0 -> { //状态：0、未确认；1、已确认；-1失败
                    holder.setText(R.id.tv_transtype,R.string.Unconfirmed)
                    holder.setBackgroundResource(R.id.tv_transtype,R.drawable.shape_transaction_in)
                }
                item.status==1 -> {
                    holder.setText(R.id.tv_transtype,R.string.Confirmed)
                    holder.setBackgroundResource(R.id.tv_transtype,R.drawable.shape_transaction_out)
                }
                item.status==-1 -> {
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

    fun initUI(){
        pageIndex = 1
        getTransferHistory(UserDao.defaultChain, getAddressByCoinType(WalletInfoDaoHelper.loadDefaultWallet(),UserDao.defaultChain))

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: RefreshEvent) {
        initUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}