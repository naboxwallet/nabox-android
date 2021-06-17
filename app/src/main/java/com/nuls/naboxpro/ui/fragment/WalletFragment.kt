package com.nuls.naboxpro.ui.fragment

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.components.SimpleImmersionOwner
import com.gyf.immersionbar.components.SimpleImmersionProxy
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.db.WalletInfoDaoHelper
import com.nuls.naboxpro.entity.AssetsEntity
import com.nuls.naboxpro.entity.AssetsOverviewEntity
import com.nuls.naboxpro.enums.CoinTypeEnum
import com.nuls.naboxpro.eventbus.RefreshBalanceEvent
import com.nuls.naboxpro.eventbus.RefreshEvent
import com.nuls.naboxpro.eventbus.SwitchWalletEvent
import com.nuls.naboxpro.eventbus.TransferSuccess
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.BaseResponse
import com.nuls.naboxpro.net.api.RetrofitClient
import com.nuls.naboxpro.net.observable.RecObserver
import com.nuls.naboxpro.ui.BaseFragment
import com.nuls.naboxpro.ui.activity.*
import com.nuls.naboxpro.ui.adapter.AssetsAdapter
import com.nuls.naboxpro.ui.popup.ChoiceChainPop
import com.nuls.naboxpro.utils.*
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.fragment_wallet_layout.*
import network.nerve.core.crypto.HexUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class WalletFragment :BaseFragment() , SimpleImmersionOwner {
    override fun immersionBarEnabled() = true

    override fun initImmersionBar() {
        ImmersionBar.with(this)
            .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
            .statusBarDarkFont(true, 0.2f)
            .statusBarColor(R.color.white)
            .init()
    }


    /**
     * 当前选中的链 默认nuls
     */
    var chain:String?=CoinTypeEnum.NULS.code


    /**
     * 选中链的图标
     */
    var chainIcon:String?=""
    var assetsList:ArrayList<AssetsEntity>?=null
    var address:String?=null

    var assetsAdapter: AssetsAdapter?=null
    var assetFragment:AssetListFragment?=null
    var transferFragment:TransferHistoryFragment?=null
    override fun getLayoutId() = R.layout.fragment_wallet_layout

    override fun initView() {
        EventBus.getDefault().register(this)
        assetFragment = AssetListFragment()
        transferFragment = TransferHistoryFragment()
        refresh_layout.setEnableLoadMore(false)
        initUI()
        refresh_layout.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onLoadMore(refreshLayout: RefreshLayout) {

            }

            override fun onRefresh(refreshLayout: RefreshLayout) {
//                EventBus.getDefault().post(RefreshBalanceEvent())
                EventBus.getDefault().post(RefreshEvent())
//                initUI()
            }
        })
        chain = UserDao.defaultChain
        assetsAdapter?.setOnItemClickListener { adapter, view, position ->
            //            //跳转到币种交易详情
            var  assetsEntity:AssetsEntity = adapter.data[position] as AssetsEntity
            var contractAddress:String?=""
            if(assetsEntity.contractAddress!=null){
                contractAddress = assetsEntity.contractAddress
            }
            activity?.let { address?.let { it1 ->
                AssetsDetailActivity.start(it,assetsEntity, chain!!,
                    it1
                )
            } }
        }
        //切换钱包和资产
        layout_chain.setOnClickListener {

            activity?.let { it1 ->
                showChoiceWalletAndChainPop(it1,chain!!,"",false,
                ChoiceChainPop.ChoiceListener { walletInfo, assetsEntity -> //设置新的默认钱包
                    UserDao.defaultWallet = walletInfo?.nulsAddress.toString()
                    UserDao.defaultChain = assetsEntity?.chain.toString()
                    chain = assetsEntity?.chain
                    //刷新ui
//                    initUI()
                    EventBus.getDefault().post(RefreshEvent())
                })
            }
        }
        tv_trans.setOnClickListener {
            activity?.let { it1 -> chain?.let { it2 ->
                address?.let { it3 ->
                    TransferActivity.start(it1,
                        it2, it3
                    )
                }
            } }
        }
        tv_wallet_address.setOnClickListener {
            activity?.let { it1 -> copyToClipboard(it1,tv_wallet_address.text.toString()) }
        }
        tv_cross_chain.setOnClickListener {

            address?.let { it1 -> activity?.let { it2 -> chain?.let { it3 ->
                CrossChainActivity.start(it2, it1,
                    it3
                )
            } } }

        }
        iv_scan.setOnClickListener {
            startActivity(Intent(activity ,ScanActivity().javaClass))
        }
        asset_add.setOnClickListener {
            //显示资产关注弹窗

            activity?.let { it1 -> AssetsManageActivity.start(it1, chain!!) }
        }
        tv_detail.setOnClickListener {

            //编辑钱包
            val intent = Intent(activity, WalletEditActivity::class.java)
            val bundle = activity?.let { it1 ->
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    it1,
                    home_cardview,
                    "share_cardview"
                ).toBundle()
            }
            intent.putExtra("address", UserDao.defaultWallet)
            startActivity(intent, bundle)
        }
        iv_zxing.setOnClickListener {
            activity?.let { it1 -> chain?.let { it2 ->
                address?.let { it3 ->
                    AddressZxingActivity.start(it1, it3,
                        it2
                    )
                }
            } }
        }
        choiceAssetFragment()
        tv_tab_asset.setOnClickListener {
            choiceAssetFragment()
        }
        tv_tab_history.setOnClickListener {
            choiceHistoryFragment()
        }
//        replaceFragment(R.id.layout_ll, assetFragment!!,childFragmentManager)
    }

    override fun initData() {

    }


    private fun initUI(){
        chain = UserDao.defaultChain

        address = getAddressByCoinType(WalletInfoDaoHelper.loadDefaultWallet(),
            chain!!
        )
        tv_cointype.text =WalletInfoDaoHelper.loadDefaultWallet().alias
        getChainPrice()
        NaboxUtils.initWalletSkin(layout_wallet, WalletInfoDaoHelper.loadDefaultWallet().color)
        tv_wallet_address.text = getAddressByCoinType(
            WalletInfoDaoHelper.loadDefaultWallet(),
            chain!!
        )

        activity?.let { loadCoinIcon(it,iv_manage, getImageUrl(CoinTypeEnum.getMessageByCode(chain!!))) }
    }




    /**
     * 获取对应链的资产总额（美元）
     */
    private  fun getChainPrice(){

        RetrofitClient.getInstance().invokePostBody(context,Api.CHAIN_PRICE, mapOf(
            "language" to UserDao.language,
            "pubKey" to HexUtil.encode(WalletInfoDaoHelper.loadDefaultWallet().compressedPubKey)
        )).subscribe(object :RecObserver<BaseResponse<List<AssetsOverviewEntity>>>(context,false,false,Api.CHAIN_PRICE){
            override fun onSuccess(t: BaseResponse<List<AssetsOverviewEntity>>?) {
                refresh_layout?.finishRefresh()
                if(t!=null&&t.data!=null){
                    for(index in t.data.indices){
                        if(t.data[index].chain == chain){
                            tv_money.text = getAmountByUsd(t.data[index].price)

                        }
                    }
                }
            }

            override fun onFail(msg: String, code: Int) {
                refresh_layout?.finishRefresh()
                showToast(msg)
            }
        })
    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        mSimpleImmersionProxy.onHiddenChanged(hidden)
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mSimpleImmersionProxy.onConfigurationChanged(newConfig)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        mSimpleImmersionProxy.isUserVisibleHint = isVisibleToUser
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mSimpleImmersionProxy.onActivityCreated(savedInstanceState)
    }
    /**
     * ImmersionBar代理类
     */
    private val mSimpleImmersionProxy = SimpleImmersionProxy(this)


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTransferSuccess(event: TransferSuccess) {
        choiceHistoryFragment()
        EventBus.getDefault().post(RefreshEvent())
    }


    override fun onResume() {
        super.onResume()
        //关注等页面回来需要刷新
        initUI()
    }

    //dapp页面可能会触发这个eventbus  ----------------------
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: RefreshEvent) {
        initUI()
    }




    fun replaceFragment(contentLayoutId: Int, fragment: Fragment, fragmentManager: FragmentManager) {
        val transaction = fragmentManager.beginTransaction()
        val fragments = fragmentManager.fragments
        for (mFragment in fragments) {
            transaction.hide(mFragment)
        }
        if (!fragment.isAdded) {
            transaction.add(contentLayoutId, fragment).commitAllowingStateLoss()
        } else {
            transaction.show(fragment).commitAllowingStateLoss()
        }
    }



    private fun choiceAssetFragment(){
        replaceFragment(R.id.layout_ll, assetFragment!!,childFragmentManager)
        tv_tab_asset.setTextColor(Color.parseColor("#333333"))
        tv_tab_asset.paint.isFakeBoldText = true
        tv_tab_asset.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18.0F)
        tv_tab_history.setTextColor(Color.parseColor("#616262"))
        tv_tab_history.paint.isFakeBoldText = false
        tv_tab_history.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.0F)
    }

    private fun choiceHistoryFragment(){
        replaceFragment(R.id.layout_ll, transferFragment!!,childFragmentManager)
        tv_tab_asset.setTextColor(Color.parseColor("#616262"))
        tv_tab_asset.paint.isFakeBoldText = false
        tv_tab_asset.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.0F)
        tv_tab_history.setTextColor(Color.parseColor("#333333"))
        tv_tab_history.paint.isFakeBoldText = true
        tv_tab_history.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18.0F)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}