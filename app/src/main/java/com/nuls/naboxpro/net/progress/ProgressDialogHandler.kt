package com.nuls.naboxpro.net.progress

import android.content.Context
import android.os.Handler
import android.os.Message
import com.lxj.xpopup.core.BasePopupView
import com.nuls.naboxpro.utils.showLoadingPopup


class ProgressDialogHandler(private val context: Context, private val mProgressCancelListener: ProgressCancelListener, private val cancelable: Boolean) : Handler() {

    private var loadingPopup: BasePopupView? = null

    private fun initProgressDialog() {
        if (loadingPopup == null) {
            loadingPopup = showLoadingPopup(context, cancelable) {
                if (cancelable) {
                    mProgressCancelListener.onCancelProgress()
                }
            }
        }
    }

    private fun dismissProgressDialog() {
        if (loadingPopup != null) {
            loadingPopup!!.dismiss()
            loadingPopup = null
        }
    }

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            SHOW_PROGRESS_DIALOG -> initProgressDialog()
            DISMISS_PROGRESS_DIALOG -> dismissProgressDialog()
        }
    }

    companion object {
        const val SHOW_PROGRESS_DIALOG = 1
        const val DISMISS_PROGRESS_DIALOG = 2
    }
}
