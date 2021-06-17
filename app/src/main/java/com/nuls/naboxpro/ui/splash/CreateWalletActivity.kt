package com.nuls.naboxpro.ui.splash

import android.Manifest
import android.content.Intent
import com.lxj.xpopup.XPopup
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.ui.popup.PermissionPop

import io.reactivex.functions.Consumer
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import com.gyf.immersionbar.ktx.immersionBar
import com.nuls.naboxpro.R
import com.nuls.naboxpro.ui.popup.ImportTypePopup
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_create_wallet.*
import kotlinx.android.synthetic.main.base_title_layout.*


class CreateWalletActivity:BaseActivity() {
    override fun getLayoutId() = R.layout.activity_create_wallet

    override fun initView() {
        back.setOnClickListener {
            finish()
        }
        immersionBar {
            statusBarDarkFont(true,0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }
        if (!UserDao.isReadPermission) {
            XPopup.Builder(this)
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(false)
                .asCustom(PermissionPop(this, PermissionPop.CloseCallback {
                    UserDao.isReadPermission = true
                    requestPermissions()
                }))
                .show()
        } else {
            requestPermissions()
        }

        iv_import.setOnClickListener {
            XPopup.Builder(this)
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(false)
                .asCustom(ImportTypePopup(this))
                .show()

        }
        iv_create.setOnClickListener {
            startActivity(Intent(this,InitWalletActivity().javaClass))
        }


    }

    override fun initData() {

    }


    /**
     * 统一先请求需要的危险权限
     */
    private fun requestPermissions() {
        val rxPermission = RxPermissions(this)
        rxPermission
            .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.READ_PHONE_STATE)
            .subscribe { granted ->
                if (granted) { // Always true pre-M
                    // I can control the camera now
                } else {
                    // Oups permission denied
                }
            }

//        val rxPermission = RxPermissions(this)
//        rxPermission.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
//        rxPermission.request(
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.CAMERA,
//            Manifest.permission.READ_PHONE_STATE
//        ).subscribe(Consumer<Boolean> {
//                //这里只需要申请统一授权就行了，不需要就权限问题做具体处理。后续具体调用的时候再具体申请
//            })

    }

}