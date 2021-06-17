package com.nuls.naboxpro.base.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nuls.naboxpro.base.mvp.BaseModel
import com.nuls.naboxpro.base.mvp.BasePresenter
import com.nuls.naboxpro.base.mvp.IPresenter
import com.nuls.naboxpro.base.mvp.IView
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.utils.showToast
import kotlinx.android.synthetic.main.activity_mainmenu_layout.*

abstract class BaseMvpActivity<P : BasePresenter<*, *>> : BaseActivity(), IView {

    protected var p: P? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        p = newPresenter()
        p?.createView(this)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        p!!.destroy()
        super.onDestroy()
    }

    abstract fun newPresenter(): P?

    override fun showProgressDialog() {
        TODO("Not yet implemented")
    }

    override fun dismissProgressDialog() {
        TODO("Not yet implemented")
    }

    override fun show(msg: String) {
        showToast(msg)
    }

    override fun showSuccess(msg: String) {
        show(msg)
    }

    override fun showWarm(msg: String) {
        show(msg)
    }

    override fun showError(msg: String) {
        show(msg)
    }

}