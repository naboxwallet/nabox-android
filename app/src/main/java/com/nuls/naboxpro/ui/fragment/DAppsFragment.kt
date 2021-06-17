package com.nuls.naboxpro.ui.fragment

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.components.SimpleImmersionOwner
import com.gyf.immersionbar.components.SimpleImmersionProxy
import com.ms.banner.BannerConfig
import com.ms.banner.Transformer
import com.ms.banner.holder.BannerViewHolder
import com.ms.banner.listener.OnBannerClickListener
import com.nuls.naboxpro.R
import com.nuls.naboxpro.db.DAppDaoHelper
import com.nuls.naboxpro.entity.DAppBannerEntity
import com.nuls.naboxpro.entity.DAppBean
import com.nuls.naboxpro.enums.CoinTypeEnum
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.BaseResponse
import com.nuls.naboxpro.net.api.RetrofitClient
import com.nuls.naboxpro.net.observable.RecObserver
import com.nuls.naboxpro.ui.BaseFragment
import com.nuls.naboxpro.ui.activity.ChangeSkinActivity
import com.nuls.naboxpro.ui.activity.DAppSearchActivity
import com.nuls.naboxpro.ui.adapter.DAppAdapter
import com.nuls.naboxpro.ui.adapter.LinearDAppAdapter
import com.nuls.naboxpro.ui.widget.BaseFragmentPagerAdapter
import com.nuls.naboxpro.utils.checkDAppNetwork
import com.zhuangku.app.utils.image.loadImg
import kotlinx.android.synthetic.main.activity_choice_wallet_skin.*
import kotlinx.android.synthetic.main.fragment_dapps_layout.*
import kotlinx.android.synthetic.main.fragment_dapps_layout.banner
import java.util.ArrayList

class DAppsFragment :BaseFragment() , SimpleImmersionOwner {
    private var mTitleList = mutableListOf("BSC","Heco","OKEXChain","Ethereum")
    var recommendAdapter:DAppAdapter?=null
    var historyAdapter:LinearDAppAdapter?=null
    var pagerAdapter: BaseFragmentPagerAdapter? = null


    /**
     * 轮播图数据
     */
    internal var mPages: MutableList<DAppBean> = ArrayList()

    override fun getLayoutId()=R.layout.fragment_dapps_layout

    override fun initView() {
        recommendAdapter = DAppAdapter()
        list_recommend.layoutManager = GridLayoutManager(activity,5)
        list_recommend.adapter = recommendAdapter
        val fragmentList = mutableListOf<Fragment>()
        var  bscFragment = DAppChildFragment()
        var  hecoFragment = DAppChildFragment()
        var  okexFragment = DAppChildFragment()
        var  ethFragment = DAppChildFragment()
        var bundle = Bundle()
        bundle.putString("chain",CoinTypeEnum.BSC.code)
        bscFragment.arguments = bundle
        fragmentList.add(bscFragment)
        bundle = Bundle()
        bundle.putString("chain",CoinTypeEnum.HECO.code)
        hecoFragment.arguments = bundle
        fragmentList.add(hecoFragment)
        bundle = Bundle()
        bundle.putString("chain",CoinTypeEnum.OKEX.code)
        okexFragment.arguments = bundle
        fragmentList.add(okexFragment)
        bundle = Bundle()
        bundle.putString("chain",CoinTypeEnum.ETH.code)
        ethFragment.arguments = bundle
        fragmentList.add(ethFragment)
        pagerAdapter = BaseFragmentPagerAdapter(fragmentList, mTitleList, childFragmentManager)
        viewpager.adapter = pagerAdapter
        tablayout.setupWithViewPager(viewpager)
        tv_search.setOnClickListener {
            //跳转到dapp搜索
            startActivity(Intent(activity,DAppSearchActivity().javaClass))
        }
        recommendAdapter?.setOnItemClickListener { adapter, view, position ->
            var dapp = adapter.getItem(position) as DAppBean
            activity?.let { checkDAppNetwork(it,dapp) }
        }
//        list_history.layoutManager = GridLayoutManager(activity,5)
//        var historyManager = LinearLayoutManager(activity)
//        historyManager.orientation = LinearLayoutManager.HORIZONTAL
//        list_history.layoutManager = historyManager
        list_history.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)

        historyAdapter = LinearDAppAdapter()
        historyAdapter?.setOnItemClickListener { adapter, view, position ->
            var dapp = adapter.getItem(position) as DAppBean
            activity?.let { checkDAppNetwork(it,dapp) }
        }
        list_history.adapter = historyAdapter
        getBanner()
        refresh_layout.setOnRefreshListener {
            getBanner()
            getRecommendDApps()

        }
    }

    override fun initData() {

    }

    override fun immersionBarEnabled()= true

    override fun initImmersionBar() {
        ImmersionBar.with(this)
            .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
            .statusBarDarkFont(true, 0.2f)
            .statusBarColor(R.color.white)
            .init()
    }


    /**
     * 获取推荐的dapp
     */
    private fun getRecommendDApps(){
        RetrofitClient.getInstance().invokeGet(activity,Api.GET_RECOMMEND_DAPP, mapOf())
            .subscribe(object :RecObserver<BaseResponse<List<DAppBean>>>(activity,false,false,Api.GET_RECOMMEND_DAPP){
                override fun onSuccess(t: BaseResponse<List<DAppBean>>?) {
                    refresh_layout?.finishRefresh()
                    if(t?.data!=null){
                        recommendAdapter?.setNewInstance(t.data as MutableList<DAppBean>?)
                    }
                }
                override fun onFail(msg: String, code: Int) {
                    refresh_layout?.finishRefresh()
                }
            })
    }


    /**
     * 获取banner图
     */
    private fun getBanner(){
        RetrofitClient.getInstance().invokeGet(activity,Api.GET_BANNER,
            mapOf()).subscribe(object :RecObserver<BaseResponse<List<DAppBean>>>(activity,false,false,Api.GET_BANNER){
            override fun onSuccess(t: BaseResponse<List<DAppBean>>?) {
                  if(t?.data!=null){
                      mPages.addAll(t?.data)
                      initBanner()
                  }
            }

            override fun onFail(msg: String, code: Int) {

            }
        })


    }

    internal inner class CustomViewHolder : BannerViewHolder<DAppBean> {

        private var mImageView: ImageView? = null

        override fun createView(context: Context): View {
            val view = LayoutInflater.from(context).inflate(R.layout.dapp_banner_item, null)
            mImageView = view.findViewById(R.id.iv_banner)
            return view
        }

        override fun onBind(context: Context, position: Int, data: DAppBean?) {
            // 数据绑定
            mImageView?.let { context.loadImg(it,data?.icon) }
            mImageView?.setOnClickListener {
                activity?.let { data?.let { it1 -> checkDAppNetwork(it, it1) } }
            }
//            mImageView!!.setBackgroundResource(data.icon!!)
        }
    }


    fun initBanner(){
        banner.setAutoPlay(false)
        //动画效果
        banner.setBannerAnimation(Transformer.Default)
        //禁止循环
        banner.setLoop(false)
        banner.setPages(mPages, CustomViewHolder())
        banner.start()
    }


    override fun onResume() {
        super.onResume()
        if(DAppDaoHelper.loadAllDApp()!=null&&DAppDaoHelper.loadAllDApp().isNotEmpty()){
            if(layout_history.visibility==View.GONE){
                layout_history.visibility = View.VISIBLE
            }
            historyAdapter?.setNewInstance(DAppDaoHelper.loadAllDApp())
        }else{
            historyAdapter?.setNewInstance(null)
        }
        getRecommendDApps()

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
}