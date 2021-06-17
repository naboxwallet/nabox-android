package com.nuls.naboxpro.ui.popup

import android.content.Context
import com.lxj.xpopup.core.CenterPopupView
import com.nuls.naboxpro.R
import com.nuls.naboxpro.ui.widget.DYLoadingView


/**
 *
 * @function loading
 * Created by xhw on 2020-1-11 18:16
 */
class LoadingPopup(context: Context) : CenterPopupView(context) {

    private var mCallBack: () -> Unit = {}
    private lateinit var mContext: Context
    private var loadingView: DYLoadingView? = null

    constructor(context: Context, callBack: () -> Unit) : this(context) {
        this.mCallBack = callBack
        this.mContext = context
    }

    override fun getImplLayoutId(): Int {
        return R.layout.layout_loading_popup
    }

    override fun onCreate() {
        super.onCreate()
//        loading_progress.indeterminateDrawable.setColorFilter(mContext.resources.getColor(R.color.colorWhite), PorterDuff.Mode.MULTIPLY);
//        loading_progress.show()
        loadingView = findViewById(R.id.loading_view)
        loadingView?.start()
    }

    override fun dismiss() {
        mCallBack()
        loadingView?.stop()
        super.dismiss()
    }

}