package com.nuls.naboxpro.ui.popup

import android.content.Context
import android.view.View
import com.lxj.xpopup.core.BottomPopupView
import com.nuls.naboxpro.R
import kotlinx.android.synthetic.main.layout_pop_web_option.view.*

/**
 * web页面的专用弹出操作菜单布局
 */
class WebOptionPop(context: Context) : BottomPopupView(context) {

    private var clickListener: OnClickListener? = null

    constructor(context: Context, clickListener: OnClickListener) : this(context) {
        this.clickListener = clickListener
    }

    override fun getImplLayoutId(): Int {
        return R.layout.layout_pop_web_option
    }

    override fun onCreate() {
        super.onCreate()
        tvRefresh.setOnClickListener {
            clickListener?.onClick(it)
            dismiss()
        }
        tvCopyUrl.setOnClickListener {
            clickListener?.onClick(it)
            dismiss()
        }
        tvCancel.setOnClickListener {
            clickListener?.onClick(it)
            dismiss()
        }

    }

}