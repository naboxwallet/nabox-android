package com.nuls.naboxpro.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import androidx.annotation.RequiresApi
import com.gyf.immersionbar.ktx.immersionBar

import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.entity.DAppBean
import com.nuls.naboxpro.enums.LanguageEnum
import com.nuls.naboxpro.jsbridge.JsBridge
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.utils.copyToClipboard
import com.nuls.naboxpro.utils.showWebOption
import kotlinx.android.synthetic.main.activity_dapp_web_layout.*
import kotlinx.android.synthetic.main.fragment_activities_layout.*
import kotlin.math.log

class DAppActivity : BaseActivity() {
    override fun getLayoutId() = R.layout.activity_dapp_web_layout

    override fun initView() {

        immersionBar {
            statusBarDarkFont(true, 0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }
        //        webView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);

        iv_close.setOnClickListener {
            finish()
        }
        ivBtnMore.setOnClickListener {
            showWebOption(this, View.OnClickListener {
                var viewId = it.id
                webOption(viewId)
            })
        }

        webView.webViewClient = object : WebViewClient() {
//            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//                return false // 返回false
//            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                handler?.proceed()
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(
                view: WebView,
                newProgress: Int
            ) {

                if (newProgress == 100) {
                    progressBar.visibility = View.GONE//加载完网页进度条消失
                } else {
                    if (progressBar.visibility == View.GONE) {
                        progressBar.visibility = View.VISIBLE//开始加载网页时显示进度条
                    }
                    progressBar.progress = newProgress//设置进度值
                }
                super.onProgressChanged(view, newProgress)
            }
        }
        val webSetting: WebSettings = webView.settings
        webSetting.domStorageEnabled = true
        webSetting.javaScriptEnabled = true
        webSetting.javaScriptCanOpenWindowsAutomatically = true
        webSetting.blockNetworkImage = false
        webSetting.cacheMode = WebSettings.LOAD_NO_CACHE //不加载缓存

        webSetting.defaultTextEncodingName = "utf-8"
        webSetting.setSupportZoom(true)
        // 设置出现缩放工具
        // 设置出现缩放工具
        webSetting.builtInZoomControls = true
        //扩大比例的缩放
        //扩大比例的缩放
        webSetting.useWideViewPort = true
        webSetting.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        webSetting.loadWithOverviewMode = true
        if (intent != null
            && intent.getSerializableExtra("dapp") != null &&
            intent.getStringExtra("address") != null
        ) {

            var dapp: DAppBean = intent.getSerializableExtra("dapp") as DAppBean
            if (UserDao.language == LanguageEnum.CHS.code) {
                tv_title.text = dapp.fileName
            } else {
                tv_title.text = dapp.fileNameEn
            }
            JsBridge(this, supportFragmentManager, webView, intent.getStringExtra("address"))
            webView.loadUrl(dapp.url)
        }
    }

    override fun initData() {

    }


    companion object {
        fun start(context: Context, dapp: DAppBean, address: String) {
            var intent = Intent(context, DAppActivity().javaClass)
            intent.putExtra("dapp", dapp)
            intent.putExtra("address", address)
            context.startActivity(intent)

        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    override fun onDestroy() {
        super.onDestroy()
        webview?.removeAllViews()
        webView.destroy()
    }

    private fun webOption(id: Int) {
        if (id == R.id.tvRefresh) {
            webView.reload()
        } else if (id == R.id.tvCopyUrl) {
            copyToClipboard(this, webView.url)
        } else if (id == R.id.tvCancel) {

        }
    }


}