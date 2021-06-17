package com.nuls.naboxpro.ui.fragment

import androidx.recyclerview.widget.LinearLayoutManager
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.entity.DAppBean
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.BaseResponse
import com.nuls.naboxpro.net.api.RetrofitClient
import com.nuls.naboxpro.net.observable.RecObserver
import com.nuls.naboxpro.ui.BaseFragment
import com.nuls.naboxpro.ui.adapter.DAppChildAdapter
import com.nuls.naboxpro.utils.checkDAppNetwork
import kotlinx.android.synthetic.main.fragment_dapp_child_layout.*

class DAppChildFragment:BaseFragment() {

    var myAdapter:DAppChildAdapter?=null

    override fun getLayoutId() = R.layout.fragment_dapp_child_layout

    override fun initView() {
        myAdapter = DAppChildAdapter()
        myAdapter?.setOnItemClickListener { adapter, view, position ->
            var dapp = adapter.getItem(position) as DAppBean
            activity?.let { checkDAppNetwork(it,dapp) }
        }
        data_list.layoutManager = LinearLayoutManager(activity)
        data_list.adapter = myAdapter
        arguments?.getString("chain","")?.let { getDappList(it) }
    }

    override fun initData() {

    }

    private fun getDappList(chain:String){
        RetrofitClient.getInstance().invokePostBody(activity,Api.GET_DAPP_CHAIN, mapOf(
            "language" to UserDao.language,
            "chain" to chain
        )).subscribe(object :RecObserver<BaseResponse<List<DAppBean>>>(activity,false,false,Api.GET_DAPP_CHAIN){
            override fun onSuccess(t: BaseResponse<List<DAppBean>>?) {
                    if(t?.data!=null){
                        myAdapter?.setNewInstance(t.data as MutableList<DAppBean>?)
                    }
            }

            override fun onFail(msg: String, code: Int) {

            }
        })
    }




}