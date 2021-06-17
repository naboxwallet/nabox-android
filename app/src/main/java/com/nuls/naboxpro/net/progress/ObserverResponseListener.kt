package com.nuls.naboxpro.net.progress


interface ObserverResponseListener<T> {
    /**
     * 响应成功
     *
     * @param t
     */
    fun onSuccess(t: T)

    /**
     * 响应失败
     *
     * @param e
     */
    fun onFail(msg: String, code: Int)
}
