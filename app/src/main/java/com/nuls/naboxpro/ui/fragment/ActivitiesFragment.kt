package com.nuls.naboxpro.ui.fragment

import android.content.res.Configuration
import android.net.http.SslError
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.webkit.*
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.components.SimpleImmersionOwner
import com.gyf.immersionbar.components.SimpleImmersionProxy
import com.nuls.naboxpro.R
import com.nuls.naboxpro.ui.BaseFragment
import com.nuls.naboxpro.utils.showToast
import kotlinx.android.synthetic.main.fragment_activities_layout.*

/**
 * 生态页
 */
class ActivitiesFragment:BaseFragment(), SimpleImmersionOwner {
    override fun getLayoutId() = R.layout.fragment_activities_layout

    override fun initView() {

        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return false // 返回false
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                handler?.proceed()
            }
        }
        webview.webChromeClient = object : WebChromeClient(){
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
//                if (newProgress == 100) {
//                    pro_percent.visibility = View.GONE//加载完网页进度条消失
//                } else {
//                    pro_percent.visibility = View.VISIBLE//开始加载网页时显示进度条
//                    pro_percent.progress = newProgress//设置进度值
//                }
                super.onProgressChanged(view, newProgress)
            }

        }

        var webSettings: WebSettings =webview.settings
        webSettings.javaScriptEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        // 设置缓存路径
//        webSettings.setAppCachePath("");
        // 支持缩放(适配到当前屏幕)
        webSettings.setSupportZoom(true)
        webSettings.javaScriptEnabled = true
        // 将图片调整到合适的大小
        webSettings.useWideViewPort = true
        // 支持内容重新布局,一共有四种方式
        // 默认的是NARROW_COLUMNS
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        // 设置可以被显示的屏幕控制
        webSettings.displayZoomControls = true
        // 设置默认字体大小
        webSettings.defaultFontSize = 12

        btn.setOnClickListener {
            if(TextUtils.isEmpty(edit_address.text)){
                showToast("先输入网址")
                return@setOnClickListener
            }
            webview.loadUrl(edit_address.text.toString())

        }


    }

    override fun initData() {

    }

    override fun immersionBarEnabled() = true

    override fun initImmersionBar() {
        ImmersionBar.with(this)
            .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
            .statusBarDarkFont(true, 0.2f)
            .statusBarColor(R.color.white)
            .init()
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

    override fun onDestroy() {
        super.onDestroy()
        mSimpleImmersionProxy.onDestroy()
    }

}