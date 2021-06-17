package com.nuls.naboxpro.ui.popup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import com.blankj.utilcode.util.AppUtils
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloader
import com.lxj.xpopup.core.CenterPopupView
import com.nuls.naboxpro.R
import com.nuls.naboxpro.entity.AppVersionBean
import com.nuls.naboxpro.utils.BaseDownloaderListener
import kotlinx.android.synthetic.main.pop_version_layout.view.*

/**
 * created by yange on 2020/3/19 0019
 * 描述：检查更新
 */
class CheckUpdatePop(context: Context) : CenterPopupView(context) {

    /**
     * apk是否下载完成 默认false 未下载完成
     */
    var uploadSuccess:Boolean = false


    /**
     * apk路径
     */
    var targetFilePath:String?=null

    /**
     * 更新接口返回的数据
     */
    var appVersionBean: AppVersionBean? = null

    constructor(
        context: Context,
        appVersionBean: AppVersionBean
    ) : this(context) {
        this.appVersionBean = appVersionBean
    }


    override fun getImplLayoutId(): Int {
        return R.layout.pop_version_layout
    }

    override fun onCreate() {
        super.onCreate()
        popupInfo.isDismissOnBackPressed = false
        popupInfo.isDismissOnTouchOutside = false
        //强制更新
        if(!appVersionBean?.forceUpdate!!){
            btn_cancel.visibility = View.GONE
        }
        tv_content.text = appVersionBean?.markDownContext
        btn_sure.setOnClickListener {
            if(uploadSuccess){
                AppUtils.installApp(targetFilePath)
            }else{
                checkUpdate(appVersionBean?.downUrl)
            }
        }
        btn_cancel.setOnClickListener {
            dismiss()
        }
    }

    //检查更新
    private fun checkUpdate(url: String?) {
        // TODO: 2021/6/10 0010  如果url不是直接下载地址，也是一个下载页面的网址，就直接用浏览器打开    具体选择方式，和@豆奶对接
         //
//        val intent = Intent()
//        intent.data = Uri.parse(url)
//        intent.action = Intent.ACTION_VIEW
//        context.startActivity(intent)
        // TODO: 2021/6/10 0010  颜格：  如果url是apk的下载地址，则用本地下载的方式 ！！！！！   这里没有判断权限问题，需要额外判断一下  ！！！！！！！！！
        progress_version.visibility = View.VISIBLE
        btn_sure.visibility = View.GONE
        val path = context.externalCacheDir?.absolutePath + "/nabox.apk"
        FileDownloader.getImpl()
            .create(url)
            .setPath(path)
            .setForceReDownload(true)
            .setWifiRequired(false)
            .setAutoRetryTimes(3).setListener(object : BaseDownloaderListener() {
                override fun completed(task: BaseDownloadTask?) {
                    super.completed(task)
                    progress_version.visibility = View.GONE
                    btn_sure.visibility = View.VISIBLE
                    btn_sure.text = context.getString(R.string.install)
                    uploadSuccess = true
                    targetFilePath  = task?.targetFilePath
                    AppUtils.installApp(task?.targetFilePath)

                }

                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    super.error(task, e)
                    (context as Activity).runOnUiThread {
                        progress_version.visibility = View.GONE
                        btn_sure.visibility = View.VISIBLE
                        btn_sure.text = context.getString(R.string.update_error)
                        btn_cancel.visibility = View.VISIBLE
                    }
                }

                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    super.progress(task, soFarBytes, totalBytes)
                    (context as Activity).runOnUiThread {
                        val progress = (soFarBytes.toLong() * 100 / totalBytes.toLong()).toInt()
                        progress_version.visibility = View.VISIBLE
                        btn_sure.visibility = View.GONE
                        progress_version.progress = progress
                    }
                }
            }).start()
    }

}