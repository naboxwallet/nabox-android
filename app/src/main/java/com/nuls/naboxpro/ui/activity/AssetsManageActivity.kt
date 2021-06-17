package com.nuls.naboxpro.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.annotations.Until
import com.gyf.immersionbar.ktx.immersionBar
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.db.WalletInfoDaoHelper
import com.nuls.naboxpro.entity.AssetsEntity
import com.nuls.naboxpro.entity.ChainConfigBean
import com.nuls.naboxpro.enums.CoinTypeEnum
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.BaseResponse
import com.nuls.naboxpro.net.api.RetrofitClient
import com.nuls.naboxpro.net.observable.RecObserver
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.ui.adapter.AssetManegeAdapter
import com.nuls.naboxpro.utils.getAddressByCoinType
import com.nuls.naboxpro.utils.showToast
import com.trello.navi2.model.BundleBundle
import kotlinx.android.synthetic.main.activity_follow_assets_layout.*
import java.util.stream.Collector
import java.util.stream.Collectors

class AssetsManageActivity:BaseActivity() {
    override fun getLayoutId() = R.layout.activity_follow_assets_layout

    var chain:String = ""
    var address:String = ""
    var editString = ""
    var assetsList:ArrayList<AssetsEntity>?= arrayListOf()
    var adapter:AssetManegeAdapter?=null
    override fun initView() {
        immersionBar {
            statusBarDarkFont(true,0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }
        back.setOnClickListener {
            finish()
        }
        adapter = AssetManegeAdapter(this)
        recycler_data.layoutManager = LinearLayoutManager(this)
        recycler_data.adapter = adapter
        if(intent!=null){
//            assetsList = intent.getSerializableExtra("assetsList") as ArrayList<AssetsEntity>?
            getAssetsList()

        }
        tv_search.setOnEditorActionListener { textView, i, keyEvent ->

            if (i == EditorInfo.IME_ACTION_SEARCH) {
                if(!TextUtils.isEmpty(tv_search.text)){
                    searchCoin(intent.getStringExtra("chain"),tv_search.text.toString())
                }
                true
            }
            false


        }
        tv_search.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                if(delayRun!=null){
                    //每次editText有变化的时候，则移除上次发出的延迟线程
                    handler.removeCallbacks(delayRun)
                }
                editString = p0.toString()
//                if(editString.length>2){//长度大于2才发送
//                    //延迟1000ms，如果不再输入字符，则执行该线程的run方法
//                    handler.postDelayed(delayRun, 1000)
//                }
                //延迟1000ms，如果不再输入字符，则执行该线程的run方法
                handler.postDelayed(delayRun, 1000)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
        adapter?.addChildClickViewIds(R.id.iv_follow)
        adapter?.setOnItemChildClickListener { adapter, view, position ->

            var item :AssetsEntity= adapter.getItem(position) as AssetsEntity
            var contractAddress:String = ""
            if(item.contractAddress!=null){
                contractAddress = item.contractAddress
            }
            when(item.followState){
                1->{//已经关注 调用取消关注
                    focusAssets(position,intent.getStringExtra("chain"), getAddressByCoinType(WalletInfoDaoHelper.loadDefaultWallet(),intent.getStringExtra("chain")),item.chainId,item.assetId,contractAddress,false)
                }
                2->{//未关注 调用关注接口

                    focusAssets(position,intent.getStringExtra("chain"), getAddressByCoinType(WalletInfoDaoHelper.loadDefaultWallet(),intent.getStringExtra("chain")),item.chainId,item.assetId,contractAddress,true)
                }
            }
        }
    }


    /**
     * 获取资产列表
     *  chain 地址所属链
     *  address 地址
     */
    private fun getAssetsList(){
        RetrofitClient.getInstance().invokePostBody(this, Api.GET_ASSETS, mapOf(
            "language" to  UserDao.language,
            "chain" to  intent.getStringExtra("chain"),
            "address" to  getAddressByCoinType(WalletInfoDaoHelper.loadDefaultWallet(), intent.getStringExtra("chain"))
        )).subscribe(object : RecObserver<BaseResponse<List<AssetsEntity>>>(this,true,false, Api.GET_ASSETS){
            override fun onSuccess(t: BaseResponse<List<AssetsEntity>>?) {
                if(t?.data!=null){
                    assetsList?.clear()
                    assetsList?.addAll(t.data)
                    getChainConfig()

                }
            }

            override fun onFail(msg: String, code: Int) {
                showToast(msg)
            }
        })
    }


    override fun initData() {

    }
    private val handler = Handler()

    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private val delayRun = Runnable {
        //判断输入地址类型
        if(!TextUtils.isEmpty(editString)){
            searchCoin(intent.getStringExtra("chain"),tv_search.text.toString())
        }
    }



    companion object{
        fun start(context: Context,chain:String ){
            var intent:Intent = Intent(context,AssetsManageActivity().javaClass)
            //当前资产列表
//            intent.putExtra("assetsList",assetsList)
            //链
            intent.putExtra("chain",chain)
            context.startActivity(intent)
        }
    }


    /**
     * 获取链配置
     */
    private fun getChainConfig(){
        RetrofitClient.getInstance().invokeGet(this,Api.GET_CHAIN_CONFIG, null)
            .subscribe(object :RecObserver<BaseResponse<List<ChainConfigBean>>>(this,true,false,Api.GET_CHAIN_CONFIG){
                override fun onSuccess(t: BaseResponse<List<ChainConfigBean>>?) {
                        if(t?.data!=null){
                            adapter?.setNewInstance(assetsList?.let { checkAsset(it, t.data as MutableList<ChainConfigBean>) })

//                            adapter.setNewInstance(checkAsset(assetsList,addAssetList(assetsList)))

                        }
                }

                override fun onFail(msg: String, code: Int) {
                    showToast(msg)
                }
            })
   }

    /**
     * 合并资产列表
      */
    fun addAssetList(asssetList:MutableList<AssetsEntity>, configAssetList:MutableList<AssetsEntity>):MutableList<AssetsEntity>{
        for(index in 0 until  configAssetList.size){
            if(!asssetList.contains(configAssetList[index])){
                asssetList.add(configAssetList[index])
            }
        }
        return  asssetList
  }







    /**
     * 处理列表内容
     */
    private fun checkAsset(assetsList:MutableList<AssetsEntity>,chainConfigList:MutableList<ChainConfigBean>):MutableList<AssetsEntity>{

        var datalist:MutableList<AssetsEntity> = arrayListOf()
        for(index in 0 until chainConfigList.size){
            if(chainConfigList[index].chain==intent.getStringExtra("chain")){//只展示当前链信息
                var assets = chainConfigList[index].assets
//                assets.addAll(assetsList)
                assets = addAssetList(assetsList,assets)
                for(index in 0 until assets.size){
                    //显示所有小于等于3的资产
//                    if(assets[index].configType<=3){
                        var item  =assets[index]
                        //在具体判断config的值
                        item.followState = 2

                    //在循环匹配传进来的资产列表，没匹配到的就说明没关注
                        when(item.configType){
                            0 -> {
                                for(assetsIndex in 0 until assetsList.size){
                                    if(item.assetId == assetsList[assetsIndex].assetId
                                        &&item.chainId == assetsList[assetsIndex].chainId
                                        &&item.contractAddress == assetsList[assetsIndex].contractAddress){
                                        item.followState = 1

                                    }
                                }
                            }
                            1->{//不允许用户取消关注
                                item.followState = 0
                            }
                            2->{//不允许用户取消关注
                                item.followState = 0
                            }
                            3 ->{
                                item.followState = 2
                                for(assetsIndex in 0 until assetsList.size){
                                    if(item.assetId == assetsList[assetsIndex].assetId){
                                        item.followState = 1
                                    }
                                }
                            }
                            else ->{
                                for(assetsIndex in 0 until assetsList.size){
                                    if(item.assetId == assetsList[assetsIndex].assetId
                                        &&item.chainId == assetsList[assetsIndex].chainId
                                        &&item.contractAddress == assetsList[assetsIndex].contractAddress){
                                        item.followState = 1
                                    }
                                }

                            }
                        }
                        datalist.add(item)
//                    }
                }
            }
        }
        return datalist
    }





    private fun focusAssets(position:Int, chain:String, address: String, chainId:Int, assetId:Int, contractAddress:String, focus:Boolean){
        RetrofitClient.getInstance().invokePostBody(this,Api.FACUS_ASSETS, mapOf(
            "language" to  UserDao.language,
            "chain" to chain,
            "address" to address,
            "chainId" to chainId,
            "assetId" to assetId,
            "contractAddress" to contractAddress,
            "focus" to focus
        )).subscribe(object :RecObserver<BaseResponse<Any>>(this,true,true,Api.FACUS_ASSETS){
            override fun onSuccess(t: BaseResponse<Any>?) {
                //刷新状态
                var item:AssetsEntity= adapter?.getItem(position) as AssetsEntity
                if(focus){//关注成功
                    item.followState = 1
                }else{//取消关注成功
                    item.followState = 2
                }
                adapter?.notifyDataSetChanged()

            }
            override fun onFail(msg: String, code: Int) {
                showToast(msg)
            }
        })
    }


    fun searchCoin(chain:String,searchKey:String){
        RetrofitClient.getInstance().invokePostBody(this,Api.SEARCH_ASSET, mapOf(
            "language" to  UserDao.language,
            "chain" to chain,
            "searchKey" to searchKey
        )).subscribe(object :RecObserver<BaseResponse<List<AssetsEntity>>>(this,true,true,Api.SEARCH_ASSET){
            override fun onSuccess(t: BaseResponse<List<AssetsEntity>>?) {
                assetsList?.let { notifiAdapter(it, t?.data as MutableList<AssetsEntity>) }
            }
            override fun onFail(msg: String, code: Int) {
                showToast(msg)
            }
        })
    }



    fun notifiAdapter(assetsList:MutableList<AssetsEntity>,dataList:MutableList<AssetsEntity>){
        var newDatalist:MutableList<AssetsEntity> = arrayListOf()
        for(index in 0 until dataList.size){
            //显示所有小于等于3的资产
//            if(dataList[index].configType<=3){
                var item  =dataList[index]
                //在具体判断config的值
                item.followState = 2
                when(item.configType){
                    0 ->
                    {
                        //在循环匹配传进来的资产列表，没匹配到的就说明没关注
                        for(assetsIndex in 0 until assetsList.size){
                            if(item.assetId == assetsList[assetsIndex].assetId
                                &&item.chainId == assetsList[assetsIndex].chainId
                                &&item.contractAddress == assetsList[assetsIndex].contractAddress){
                                item.followState = 1
                            }
                        }
                    }
                    1->{//不允许用户取消关注
                        item.followState = 0
                    }
                    2->{//不允许用户取消关注
                        item.followState = 0
                    }
                    3 ->{
                        item.followState = 2
                        for(assetsIndex in 0 until assetsList.size){
                            if(item.assetId == assetsList[assetsIndex].assetId
                                &&item.chainId == assetsList[assetsIndex].chainId){
                                item.followState = 1
                            }
                        }
                    }
                    else ->{
                        //在循环匹配传进来的资产列表，没匹配到的就说明没关注
                        for(assetsIndex in 0 until assetsList.size){
                            if(item.assetId == assetsList[assetsIndex].assetId
                                &&item.chainId == assetsList[assetsIndex].chainId
                                &&item.contractAddress == assetsList[assetsIndex].contractAddress){
                                item.followState = 1
                            }
                        }
                    }
                }
                newDatalist.add(item)
//            }
        }
        if(adapter!=null){
            adapter?.setNewInstance(newDatalist)
        }

    }



}