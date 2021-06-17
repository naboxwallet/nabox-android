package com.nuls.naboxpro.ui.main

import android.content.Context
import com.nuls.naboxpro.base.mvp.BasePresenter
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.entity.ChainConfigBean
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.BaseResponse
import com.nuls.naboxpro.net.observable.RecObserver

class MainMenuPresenter: BasePresenter<MainMenuActivity, MainMenuModel> {
    constructor() : super(){
        createModel(MainMenuModel())
    }


    /**
     * 获取链配置  将部分重要数据存在本地
     */
     fun getChainConfig(context: Context){
        getM().getChainConfig(context,object : RecObserver<BaseResponse<List<ChainConfigBean>>>(context,false,false,
            Api.GET_CHAIN_CONFIG){
            override fun onSuccess(t: BaseResponse<List<ChainConfigBean>>?) {
                if(t?.data!=null){
                    //更新chainConfig配置
                    UserDao.setChainConfig(t.data)
                }
            }

            override fun onFail(msg: String, code: Int) {
                getV().showError(msg)
            }
        })
    }

}