package com.nuls.naboxpro.ui.fragment

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.db.WalletInfoDaoHelper
import com.nuls.naboxpro.entity.AssetsDetailEntity
import com.nuls.naboxpro.entity.AssetsEntity
import com.nuls.naboxpro.entity.AssetsOverviewEntity
import com.nuls.naboxpro.enums.CoinTypeEnum
import com.nuls.naboxpro.eventbus.RefreshBalanceEvent
import com.nuls.naboxpro.eventbus.RefreshEvent
import com.nuls.naboxpro.eventbus.SwitchWalletEvent
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.BaseResponse
import com.nuls.naboxpro.net.api.RetrofitClient
import com.nuls.naboxpro.net.observable.RecObserver
import com.nuls.naboxpro.ui.BaseFragment
import com.nuls.naboxpro.ui.activity.*
import com.nuls.naboxpro.ui.adapter.AssetsAdapter
import com.nuls.naboxpro.utils.*
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.activity_wallet_edit.*
import kotlinx.android.synthetic.main.fragment_home_child_layout.*
import kotlinx.android.synthetic.main.fragment_home_child_layout.layout_wallet
import kotlinx.android.synthetic.main.fragment_home_child_layout.tv_money
import kotlinx.android.synthetic.main.fragment_home_child_layout.tv_wallet_address
import network.nerve.core.crypto.HexUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.math.BigInteger


/**
 * 在这里去查余额 显示历史记录
 */
class HomeChildFragment :BaseFragment() {

    var assetsAdapter:AssetsAdapter?=null

    var assetsList:ArrayList<AssetsEntity>?=null
    var chain:String?=null
    var address:String?=null
    override fun getLayoutId() = R.layout.fragment_home_child_layout

    override fun initView() {
        EventBus.getDefault().register(this)
        assetsList = arrayListOf()
        assetsAdapter = AssetsAdapter()
        list_assets.layoutManager = LinearLayoutManager(activity)
        list_assets.adapter = assetsAdapter

        if(arguments?.getString("chain")!=null){
            chain =arguments?.getString("chain")
            address = getAddressByCoinType(WalletInfoDaoHelper.loadDefaultWallet(),
                arguments?.getString("chain")!!
            )
            initUI()
        }
        tv_wallet_address.setOnClickListener {
            tv_wallet_address.text?.toString()?.let { it1 -> activity?.let { it2 ->
                copyToClipboard(
                    it2, it1)
            } }
        }
        asset_add.setOnClickListener {
            //显示资产关注弹窗
//            activity?.let { it1 -> arguments?.getString("chain")?.let { it2 ->
//                AssetsManageActivity.start(it1, assetsList!!,
//                    it2
//                )
//            } }
            activity?.let { it1 -> arguments?.getString("chain")?.let { it2 ->
                AssetsManageActivity.start(it1,
                    it2
                )
            } }
//            startActivity(Intent(activity,AssetsManageActivity().javaClass))
        }
        refresh_layout.setEnableLoadMore(false)
        refresh_layout.setOnRefreshLoadMoreListener(object :OnRefreshLoadMoreListener{
            override fun onLoadMore(refreshLayout: RefreshLayout) {

            }

            override fun onRefresh(refreshLayout: RefreshLayout) {
                EventBus.getDefault().post(RefreshEvent())
                initUI()
            }
        })
        assetsAdapter?.setOnItemClickListener { adapter, view, position ->
//            //跳转到币种交易详情
            var  assetsEntity:AssetsEntity = adapter.data[position] as AssetsEntity
            var contractAddress:String?=""
            if(assetsEntity.contractAddress!=null){
                contractAddress = assetsEntity.contractAddress
            }
            activity?.let { chain?.let { it1 ->
                address?.let { it2 ->
                    AssetsDetailActivity.start(it,assetsEntity,
                        it1, it2
                    )
                }
            } }

        }

        tv_trans.setOnClickListener {
            activity?.let { it1 -> arguments?.getString("chain")?.let { it2 ->
                address?.let { it3 ->
                    TransferActivity.start(it1,
                        it2, it3
                    )
                }
            } }
        }
        tv_cross_chain.setOnClickListener {

            address?.let { it1 -> activity?.let { it2 -> chain?.let { it3 ->
                CrossChainActivity.start(it2, it1,
                    it3
                )
            } } }

//            startActivity(Intent(activity,CrossChainActivity().javaClass))
        }
        iv_zxing.setOnClickListener {
            activity?.let { it1 -> arguments?.getString("chain")?.let { it2 ->
                address?.let { it3 ->
                    AddressZxingActivity.start(it1, it3,
                        it2
                    )
                }
            } }
        }
    }

    override fun initData() {

    }




    fun initUI(){
        getAssetsList(
            arguments?.getString("chain")!!,getAddressByCoinType(WalletInfoDaoHelper.loadDefaultWallet(),
                arguments?.getString("chain")!!
            ))
        getChainPrice()
        NaboxUtils.initWalletSkin(layout_wallet, WalletInfoDaoHelper.loadDefaultWallet().color)
        tv_wallet_address.text = getAddressByCoinType(WalletInfoDaoHelper.loadDefaultWallet(),
            arguments?.getString("chain")!!
        )

    }


    /**
     * 获取资产列表
     *  chain 地址所属链
     *  address 地址
     */
    private fun getAssetsList(chain:String,address:String){
        RetrofitClient.getInstance().invokePostBody(activity,Api.GET_ASSETS, mapOf(
            "language" to  UserDao.language,
            "chain" to  chain,
            "address" to  address
        )).subscribe(object :RecObserver<BaseResponse<List<AssetsEntity>>>(activity,false,false,Api.GET_ASSETS){
            override fun onSuccess(t: BaseResponse<List<AssetsEntity>>?) {
                if(refresh_layout!=null){
                    refresh_layout.finishRefresh()
                    refresh_layout.finishRefresh()
                }
                if(t?.data!=null){
                    assetsList?.clear()
                    assetsList?.addAll(t.data)
                    assetsAdapter?.setNewInstance(t.data as MutableList<AssetsEntity>?)

                }
            }

            override fun onFail(msg: String, code: Int) {
                if(refresh_layout!=null){
                    refresh_layout.finishRefresh()
                    refresh_layout.finishRefresh()
                }
                showToast(msg)
            }
        })
    }








//    /**
//     * 获取资产列表
//     */
//    private  fun getAssetsList(){
//
//        RetrofitClient.getInstance().invokePostBody(context,Api.CHAIN_PRICE, mapOf(
//            "language" to UserDao.language,
//            "pubKey" to HexUtil.encode(WalletInfoDaoHelper.loadDefaultWallet().compressedPubKey)
//        )).subscribe(object :RecObserver<BaseResponse<List<AssetsOverviewEntity>>>(context,false,false){
//            override fun onSuccess(t: BaseResponse<List<AssetsOverviewEntity>>?) {
//                if(t!=null&&t.data!=null){
//                    myAdapter?.setNewInstance(t.data as MutableList<AssetsOverviewEntity>?)
//                }
//            }
//
//            override fun onFail(msg: String, code: Int) {
//                showToast(msg)
//            }
//        })
//    }




    /**
     * 获取对应链的资产总额（美元）
     */
    private  fun getChainPrice(){

        RetrofitClient.getInstance().invokePostBody(context,Api.CHAIN_PRICE, mapOf(
            "language" to UserDao.language,
            "pubKey" to HexUtil.encode(WalletInfoDaoHelper.loadDefaultWallet().compressedPubKey)
        )).subscribe(object :RecObserver<BaseResponse<List<AssetsOverviewEntity>>>(context,false,false,Api.CHAIN_PRICE){
            override fun onSuccess(t: BaseResponse<List<AssetsOverviewEntity>>?) {
                if(t!=null&&t.data!=null){
                    for(index in t.data.indices){
                        if(t.data[index].chain == arguments?.getString("chain")){
                            tv_money.text = getAmountByUsd(t.data[index].price)
                        }
                    }
                }
            }

            override fun onFail(msg: String, code: Int) {
                showToast(msg)
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: SwitchWalletEvent) {
        initUI()
    }


    override fun onResume() {
        super.onResume()
        //关注等页面回来需要刷新
        initUI()
    }

}