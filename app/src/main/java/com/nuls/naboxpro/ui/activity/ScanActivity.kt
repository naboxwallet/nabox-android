package com.nuls.naboxpro.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.hardware.Camera
import android.os.Bundle
import android.view.WindowManager
import cn.bingoogolapple.qrcode.core.QRCodeView
import com.nuls.naboxpro.R
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.utils.showToast
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_scan_layout.*
import org.json.JSONObject

/**
 * 扫码
 */
class ScanActivity:BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    override fun getLayoutId() = R.layout.activity_scan_layout

    override fun initView() {
        back.setOnClickListener {
            finish()
        }
        val rxPermission = RxPermissions(this)
        rxPermission
            .request(
                Manifest.permission.CAMERA
           )
            .subscribe { granted ->
                if (granted) { // Always true pre-M

                    zxingview.setDelegate(object :QRCodeView.Delegate{
                        override fun onScanQRCodeSuccess(result: String?) {
//                            var jsonObject: JSONObject? = null
//                            result?.let { showToast(it) }
//                            try {
//                                jsonObject = JSONObject(result)
//                                if (jsonObject.has("url")
//                                    && jsonObject.has("send")
//                                ) {
//                                    //同时包含url和send参数，说明是扫码导入钱包 到 网页轻钱包
//                                    val intent = Intent(
//                                        this@ScanActivity,
//                                        LoginConfirmActivity::class.java
//                                    )
//                                    intent.putExtra("key", jsonObject["send"].toString())
//                                    startActivity(intent)
//                                    finish()
//                                    return
//                                }
//
//
//
//                            }catch (e:Exception){
//
//                            }
                            //目前只存在扫码直接获取二维码内容
                            var intent = Intent().putExtra("zxing",result)
                            setResult(Activity.RESULT_OK,intent)
                            finish()


                        }

                        override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {

                        }

                        override fun onScanQRCodeOpenCameraError() {
                            showToast(getString(R.string.failed_to_turn_on_the_camera))
                        }

                    })
                    zxingview.startSpotAndShowRect()
                } else {
                    showToast(getString(R.string.failed_to_obtain_permission))
                    finish()
                }
            }


    }

    override fun onStart() {
        super.onStart()

        zxingview.startCamera()
        zxingview.showScanRect()
    }

    override fun initData() {

    }

    override fun onDestroy() {
        super.onDestroy()
        if(zxingview!=null){
            zxingview.onDestroy()
        }
    }

    override fun onStop() {
        super.onStop()
        if(zxingview!=null){
            zxingview.stopCamera()
        }
    }
}