package com.nuls.naboxpro.utils

import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener

 open class BaseDownloaderListener: FileDownloadListener() {
    override fun warn(task: BaseDownloadTask?) {

    }

    override fun completed(task: BaseDownloadTask?) {

    }

    override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {

    }

    override fun error(task: BaseDownloadTask?, e: Throwable?) {

    }

    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {

    }

    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {

    }
}