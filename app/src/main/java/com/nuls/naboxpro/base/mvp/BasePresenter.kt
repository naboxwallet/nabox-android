package com.nuls.naboxpro.base.mvp

open class BasePresenter<V : IView, M : BaseModel> : IPresenter<V> {

    private var v: V? = null

    private var m: M? = null

    open fun createView(v: IView) {
        this.v = v as V
    }

    open fun createModel(m: M) {
        this.m = m
    }

    open fun destroy() {
        v = null
        m = null
    }

    fun getV(): V {
        return v!!
    }

    fun getM(): M {
        return m!!
    }

}