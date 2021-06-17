package com.nuls.naboxpro.ui.main

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.AppUtils
import com.nuls.naboxpro.R
import com.nuls.naboxpro.base.activity.BaseMvpActivity
import com.nuls.naboxpro.eventbus.TransferSuccess
import com.nuls.naboxpro.ui.fragment.DAppsFragment
import com.nuls.naboxpro.ui.fragment.UserFragment
import com.nuls.naboxpro.ui.fragment.WalletFragment
import com.nuls.naboxpro.utils.showUpdatePopWindow
import kotlinx.android.synthetic.main.activity_mainmenu_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainMenuActivity : BaseMvpActivity<MainMenuPresenter>() {
    override fun getLayoutId() = R.layout.activity_mainmenu_layout

    override fun initView() {

        EventBus.getDefault().register(this)
        getChainConfig()
        replaceFragment(R.id.main_ll, mFragmentList[0] as Fragment, supportFragmentManager)
        main_radiogroup.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.rb_home -> replaceFragment(
                    R.id.main_ll,
                    mFragmentList[0] as Fragment, supportFragmentManager
                )
                R.id.rb_activity -> replaceFragment(
                    R.id.main_ll,
                    mFragmentList[1] as Fragment, supportFragmentManager
                )
                R.id.rb_user -> replaceFragment(
                    R.id.main_ll,
                    mFragmentList[2] as Fragment, supportFragmentManager
                )
            }
        }
        showUpdatePopWindow(this, false)
    }

    override fun initData() {

    }


    private var mFragmentList = mutableListOf(
//        HomeFragment(),
        WalletFragment(),
//        ActivitiesFragment(),
        DAppsFragment(),
        UserFragment()
    )

    private fun replaceFragment(
        contentLayoutId: Int,
        fragment: Fragment,
        fragmentManager: FragmentManager
    ) {
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


    //记录用户首次点击返回键的时间
    private var firstTime: Long = 0


    override fun onBackPressed() {
        val secondTime = System.currentTimeMillis()
        if (secondTime - firstTime > 2000) {
            Toast.makeText(
                this,
                resources.getString(R.string.press_the_exit_procedure_again),
                Toast.LENGTH_SHORT
            ).show()
            firstTime = secondTime
        } else {
            AppUtils.exitApp()
        }
    }

    /**
     * 获取链配置  将部分重要数据存在本地
     */
    private fun getChainConfig() {
        p!!.getChainConfig(this)
//        RetrofitClient.getInstance().invokeGet(this, Api.GET_CHAIN_CONFIG, null)
//            .subscribe(object : RecObserver<BaseResponse<List<ChainConfigBean>>>(this,false,false,Api.GET_CHAIN_CONFIG){
//                override fun onSuccess(t: BaseResponse<List<ChainConfigBean>>?) {
//                    if(t?.data!=null){
//                        //更新chainConfig配置
//                        UserDao.setChainConfig(t.data)
//
//                    }
//                }
//
//                override fun onFail(msg: String, code: Int) {
//                    showToast(msg)
//                }
//            })
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTransferSuccess(event: TransferSuccess) {
        rb_home.isChecked = true
        replaceFragment(
            R.id.main_ll,
            mFragmentList[0] as Fragment, supportFragmentManager
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun newPresenter(): MainMenuPresenter {
        return MainMenuPresenter()
    }

}