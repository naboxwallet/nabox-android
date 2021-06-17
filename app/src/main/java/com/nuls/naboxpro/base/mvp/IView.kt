package com.nuls.naboxpro.base.mvp

import androidx.appcompat.app.AppCompatActivity

interface IView {

    fun showProgressDialog()

    fun dismissProgressDialog()

    fun show(msg: String)

    fun showSuccess(msg: String)

    fun showWarm(msg: String)

    fun showError(msg: String)

}