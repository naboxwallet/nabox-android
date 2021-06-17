package com.nuls.naboxpro.ui

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.enums.LanguageEnum
import com.nuls.naboxpro.utils.LanguageConstants
import com.nuls.naboxpro.utils.LanguageUtil
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity

abstract  class BaseActivity :FragmentActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
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


    override fun getResources(): Resources {
       val res = super.getResources()
        val config = res.configuration
        config.fontScale = 1f
        res.updateConfiguration(config, res.displayMetrics)
        return res
    }


    override fun attachBaseContext(newBase: Context?) {
        //默认不选择，返回系统首选语言
        var newLanguage = ""
        if (UserDao.language != null) {
            if (UserDao.language == LanguageEnum.CHS.code()
            ) {
                newLanguage = LanguageConstants.SIMPLIFIED_CHINESE
            } else if (UserDao.language == LanguageEnum.EN.code()
            ) {
                newLanguage = LanguageConstants.ENGLISH
            }
        }
        super.attachBaseContext(LanguageUtil.attachBaseContext(newBase, newLanguage))
    }



}