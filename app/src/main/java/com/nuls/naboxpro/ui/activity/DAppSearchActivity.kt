package com.nuls.naboxpro.ui.activity

import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.google.android.flexbox.FlexboxLayout
import com.google.android.flexbox.FlexboxLayoutManager
import com.gyf.immersionbar.ktx.immersionBar
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.db.DAppDaoHelper
import com.nuls.naboxpro.entity.DAppBean
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.BaseResponse
import com.nuls.naboxpro.net.api.RetrofitClient
import com.nuls.naboxpro.net.observable.RecObserver
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.ui.adapter.DAppChildAdapter
import com.nuls.naboxpro.ui.adapter.DAppChildHistoryAdapter
import com.nuls.naboxpro.utils.checkDAppNetwork
import com.nuls.naboxpro.utils.showToast
import kotlinx.android.synthetic.main.activity_dapp_search_layout.*
import kotlinx.android.synthetic.main.fragment_dapps_layout.*
import org.apache.http.util.TextUtils

class DAppSearchActivity : BaseActivity() {

    var myAdapter: DAppChildAdapter? = null
    var historyAdapter: DAppChildHistoryAdapter? = null
    override fun getLayoutId() = R.layout.activity_dapp_search_layout

    override fun initView() {
        immersionBar {
            statusBarDarkFont(true, 0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }
        edit_search.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                if (!TextUtils.isEmpty(edit_search.text)) {
                    edit_search.clearFocus()
                    searchDapp(edit_search.text.toString().trim())
                }
                true
            }
            false
        }
        tv_cancel.setOnClickListener {
            finish()
        }
        ivDeleteHistory.setOnClickListener {
            DAppDaoHelper.deleteAllDApp()
            refreshHistory()
        }
        myAdapter = DAppChildAdapter()
        myAdapter?.setOnItemClickListener { adapter, view, position ->
            var dapp = adapter.getItem(position) as DAppBean
            checkDAppNetwork(this, dapp)
        }
        list_result.layoutManager = LinearLayoutManager(this)
        list_result.adapter = myAdapter

        historyAdapter = DAppChildHistoryAdapter()
        historyAdapter?.setOnItemClickListener { adapter, view, position ->
            var dapp = adapter.getItem(position) as DAppBean
            checkDAppNetwork(this, dapp)
        }
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        refreshHistory()
        flexLayout.removeAllViews()
        addFlestItemView("Nerve", 0)
        addFlestItemView("Uniswap", 1)
        addFlestItemView("JustSwap", 2)
        addFlestItemView("波卡", 3)
        addFlestItemView("NerveBridge", 4)
        addFlestItemView("1inch.exchange", 5)
    }

    private fun refreshHistory() {
        if (DAppDaoHelper.loadAllDApp() != null && DAppDaoHelper.loadAllDApp().isNotEmpty()) {
            historyAdapter?.setNewInstance(DAppDaoHelper.loadAllDApp())
        } else {
            historyAdapter?.setNewInstance(null)
        }
    }


    private fun searchDapp(searchKey: String) {
        RetrofitClient.getInstance().invokePostBody(
            this, Api.SEARCH_DAPP, mapOf(
                "language" to UserDao.language,
                "dappName" to searchKey
            )
        ).subscribe(object : RecObserver<BaseResponse<List<DAppBean>>>(this, true, false,Api.SEARCH_DAPP) {
            override fun onSuccess(t: BaseResponse<List<DAppBean>>?) {
                if (t?.data != null) {
                    myAdapter?.setNewInstance(t?.data as MutableList<DAppBean>?)
                }
                if (list_result != null) list_result.visibility = View.VISIBLE
                if (nscSV != null) nscSV.visibility = View.GONE

            }

            override fun onFail(msg: String, code: Int) {
                showToast(msg)
                //失败后清空搜索列表
                myAdapter?.setNewInstance(mutableListOf())
            }

        })


    }

    private fun addFlestItemView(desc: String, index: Int) {
        var tv: TextView = layoutInflater.inflate(R.layout.item_dapp_hot_search, null) as TextView
        tv.tag = index
        tv.text = desc
        flexLayout.addView(tv)
        var lp: ViewGroup.MarginLayoutParams = tv.layoutParams as ViewGroup.MarginLayoutParams
        lp.topMargin = ConvertUtils.dp2px(10f)
        lp.leftMargin = ConvertUtils.dp2px(15f)
        tv.layoutParams = lp
    }

}