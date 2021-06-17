package com.nuls.naboxpro.ui.main

import android.content.Context
import com.nuls.naboxpro.base.mvp.BaseModel
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.entity.ChainConfigBean
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.BaseResponse
import com.nuls.naboxpro.net.api.RetrofitClient
import com.nuls.naboxpro.net.observable.RecObserver
import com.nuls.naboxpro.utils.showToast

class MainMenuModel : BaseModel() {


    /**
     * 获取链配置  将部分重要数据存在本地
     */
    open fun getChainConfig(context: Context,callback:RecObserver<BaseResponse<List<ChainConfigBean>>>){
        RetrofitClient.getInstance().invokeGet(context, Api.GET_CHAIN_CONFIG, null)
            .subscribe(callback)
    }

}