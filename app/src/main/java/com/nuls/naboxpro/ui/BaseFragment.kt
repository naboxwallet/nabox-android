package com.nuls.naboxpro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment:Fragment() {

     var myview:View?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myview = layoutInflater.inflate( getLayoutId(),container,false)
        return  myview
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }


    /**
     * 设置layout
     */
    abstract fun getLayoutId():Int

    /**
     * 设置初始化view
     */
    abstract fun initView()

    /**
     * 数据操作
     */
    abstract fun initData()



}