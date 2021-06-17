package com.nuls.naboxpro.ui.fragment

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.components.SimpleImmersionOwner
import com.gyf.immersionbar.components.SimpleImmersionProxy
import com.nuls.naboxpro.R
import com.nuls.naboxpro.ui.BaseFragment
import com.nuls.naboxpro.ui.activity.AboutUsActivity
import com.nuls.naboxpro.ui.activity.ContactsListActivity
import com.nuls.naboxpro.ui.activity.SettingActivity
import com.nuls.naboxpro.ui.activity.WalletManageActivity
import kotlinx.android.synthetic.main.fragment_user.*

class UserFragment:BaseFragment(), SimpleImmersionOwner {
    override fun immersionBarEnabled() = true

    override fun initImmersionBar() {
        ImmersionBar.with(this)
            .titleBarMarginTop(emptyview)  //可以为任意view
            .init()
    }

    override fun getLayoutId() = R.layout.fragment_user

    override fun initView() {
        layout_about.setOnClickListener {
            startActivity(Intent(activity,AboutUsActivity().javaClass))
        }
        layout_setting.setOnClickListener {
            startActivity(Intent(activity,SettingActivity().javaClass))
        }
        layout_manage_wallet.setOnClickListener {
            startActivity(Intent(activity,WalletManageActivity().javaClass))
        }
        layout_contacts.setOnClickListener {
            startActivity(Intent(activity,ContactsListActivity().javaClass))
        }


    }

    override fun initData() {

    }

    override fun onDestroy() {
        super.onDestroy()
        mSimpleImmersionProxy.onDestroy()
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