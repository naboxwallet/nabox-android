package com.nuls.naboxpro.ui.fragment


import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.EventLog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.components.SimpleImmersionOwner
import com.gyf.immersionbar.components.SimpleImmersionProxy
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.db.WalletInfoDaoHelper
import com.nuls.naboxpro.entity.WalletInfo
import com.nuls.naboxpro.enums.CoinTypeEnum
import com.nuls.naboxpro.eventbus.RefreshBalanceEvent
import com.nuls.naboxpro.eventbus.SwitchWalletEvent
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.BaseResponse
import com.nuls.naboxpro.net.api.RetrofitClient
import com.nuls.naboxpro.net.observable.RecObserver
import com.nuls.naboxpro.ui.BaseFragment
import com.nuls.naboxpro.ui.activity.ScanActivity
import com.nuls.naboxpro.utils.*
import kotlinx.android.synthetic.main.fragment_home_layout.*
import network.nerve.core.crypto.HexUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomeFragment:BaseFragment(), SimpleImmersionOwner {
    override fun immersionBarEnabled() = true

    override fun initImmersionBar() {
        ImmersionBar.with(this)
            .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
            .statusBarDarkFont(true, 0.2f)
            .statusBarColor(R.color.white)
            .init()
    }

    override fun getLayoutId() = R.layout.fragment_home_layout


    private var nulsAssetsFragment:HomeChildFragment?=null
    private var ethAssetsFragment:HomeChildFragment?=null
    private var nvtAssetsFragment:HomeChildFragment?=null
    private var bscAssetsFragment:HomeChildFragment?=null
    private var hecoAssetsFragment:HomeChildFragment?=null

    override fun initView() {
        EventBus.getDefault().register(this)
        nulsAssetsFragment = HomeChildFragment()
        ethAssetsFragment = HomeChildFragment()
        nvtAssetsFragment = HomeChildFragment()
        bscAssetsFragment = HomeChildFragment()
        hecoAssetsFragment = HomeChildFragment()
        var bundle:Bundle = Bundle()
        initUI()

        tv_amount.setOnClickListener {
            activity?.let { it1 -> showAssetsOverviewPop(it1) }
        }
        iv_scan.setOnClickListener {
            startActivity(Intent(activity ,ScanActivity().javaClass))
        }
        //切换默认钱包
        iv_manage.setOnClickListener {
            activity?.let { it1 -> showSwitchWalletPop(it1) }
        }

        if(WalletInfoDaoHelper.loadDefaultWallet()!=null){

            //nuls
            bundle.putString("chain", CoinTypeEnum.NULS.code)

            nulsAssetsFragment?.arguments = bundle
            //eth
            bundle = Bundle()
            bundle.putString("chain", CoinTypeEnum.ETH.code)

            ethAssetsFragment?.arguments = bundle
            //nerve
            bundle = Bundle()
            bundle.putString("chain", CoinTypeEnum.NERVE.code)

            nvtAssetsFragment?.arguments = bundle
            //bsc
            bundle = Bundle()
            bundle.putString("chain", CoinTypeEnum.BSC.code)

            bscAssetsFragment?.arguments = bundle
            //heco
            bundle = Bundle()
            bundle.putString("chain", CoinTypeEnum.HECO.code)

            hecoAssetsFragment?.arguments = bundle
            replaceFragment(R.id.home_linear, nulsAssetsFragment!!,childFragmentManager)
            radio_group.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rb_coin_1 -> {
                        //首页
                        replaceFragment(
                            R.id.home_linear,
                            nulsAssetsFragment!!,
                            childFragmentManager
                        )
//                        rb_coin_1.isChecked = true
                    }
                    R.id.rb_coin_2 -> {

                        replaceFragment(
                            R.id.home_linear,
                            ethAssetsFragment!!,
                            childFragmentManager
                        )
//                        rb_coin_2.isChecked = true
                    }
                    R.id.rb_coin_3 -> {
                        replaceFragment(
                            R.id.home_linear,
                            nvtAssetsFragment!!,
                            childFragmentManager
                        )
//                        rb_coin_3.isChecked = true
                    }
                    R.id.rb_coin_4 -> {
                        replaceFragment(
                            R.id.home_linear,
                            bscAssetsFragment!!,
                            childFragmentManager
                        )
//                        rb_coin_4.isChecked = true
                    }
                    R.id.rb_coin_5 -> {
                        replaceFragment(
                            R.id.home_linear,
                            hecoAssetsFragment!!,
                            childFragmentManager
                        )
//                        rb_coin_5.isChecked = true
                    }
                }
            }
        }


    }

    override fun initData() {

    }

    private fun initUI(){
        if(WalletInfoDaoHelper.loadDefaultWallet()!=null){
            var walletInfo:WalletInfo = WalletInfoDaoHelper.loadDefaultWallet()
            tv_wallet_name.text = walletInfo.alias
            getWalletPrice()
        }
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


    /**
     * 获取账户资产总额
     */
    private fun getWalletPrice(){
        RetrofitClient.getInstance().invokePostBody(activity,Api.WALLET_PRICE, mapOf(
            "language" to UserDao.language,
            "pubKey" to HexUtil.encode(WalletInfoDaoHelper.loadDefaultWallet().compressedPubKey)
        )).subscribe(object :RecObserver<BaseResponse<String>>(activity,false,false,Api.WALLET_PRICE){
            override fun onSuccess(t: BaseResponse<String>?) {
                if(t!=null&&t.data!=null){
                    tv_amount.text = getAmountByUsd(t.data)
                }
            }
            override fun onFail(msg: String, code: Int) {

            }
        })
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


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        mSimpleImmersionProxy.onDestroy()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: SwitchWalletEvent) {
        initUI()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: RefreshBalanceEvent) {
        getWalletPrice()
    }
}