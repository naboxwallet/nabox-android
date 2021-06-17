package com.nuls.naboxpro.ui.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.db.WalletInfoDaoHelper
import com.nuls.naboxpro.entity.AssetsEntity
import com.nuls.naboxpro.eventbus.RefreshEvent
import com.nuls.naboxpro.eventbus.SwitchWalletEvent
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.BaseResponse
import com.nuls.naboxpro.net.api.RetrofitClient
import com.nuls.naboxpro.net.observable.RecObserver
import com.nuls.naboxpro.ui.BaseFragment
import com.nuls.naboxpro.ui.activity.AssetsDetailActivity
import com.nuls.naboxpro.ui.adapter.AssetsAdapter
import com.nuls.naboxpro.utils.NaboxUtils
import com.nuls.naboxpro.utils.getAddressByCoinType
import com.nuls.naboxpro.utils.showToast
import kotlinx.android.synthetic.main.fragment_asset_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * 资产列表fragment
 */
class AssetListFragment :BaseFragment(){
    override fun getLayoutId() = R.layout.fragment_asset_layout
    var assetsAdapter: AssetsAdapter?=null
    var assetsList:ArrayList<AssetsEntity>?=null


    override fun initView() {
        EventBus.getDefault().register(this)
        assetsList = arrayListOf()
        assetsAdapter = AssetsAdapter()
        assetsAdapter?.setOnItemClickListener { adapter, view, position ->
            //            //跳转到币种交易详情
            var  assetsEntity:AssetsEntity = adapter.data[position] as AssetsEntity
            activity?.let {
                AssetsDetailActivity.start(
                    it,assetsEntity,UserDao.defaultChain,
                    getAddressByCoinType(WalletInfoDaoHelper.loadDefaultWallet(),UserDao.defaultChain))
            }
        }
        smartlayout.setEnableLoadMore(false)
        list_data.layoutManager = LinearLayoutManager(activity)
        list_data.adapter = assetsAdapter
        getAssetsList()
    }

    override fun initData() {

    }


    /**
     * 获取资产列表
     *  chain 地址所属链
     *  address 地址
     */
    private fun getAssetsList(){
        RetrofitClient.getInstance().invokePostBody(activity, Api.GET_ASSETS, mapOf(
            "language" to  UserDao.language,
            "chain" to  UserDao.defaultChain,
            "address" to  getAddressByCoinType(WalletInfoDaoHelper.loadDefaultWallet(), UserDao.defaultChain)
        )).subscribe(object : RecObserver<BaseResponse<List<AssetsEntity>>>(activity,false,false,Api.GET_ASSETS){
            override fun onSuccess(t: BaseResponse<List<AssetsEntity>>?) {
                if(t?.data!=null){
                    assetsList?.clear()
                    assetsList?.addAll(t.data)
                    assetsAdapter?.setNewInstance(t.data as MutableList<AssetsEntity>?)

                }
            }

            override fun onFail(msg: String, code: Int) {
                showToast(msg)
            }
        })
    }


    fun initUI(){
        getAssetsList()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: RefreshEvent) {
        initUI()
    }


    override fun onResume() {
        super.onResume()
        //关注等页面回来需要刷新
        initUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}