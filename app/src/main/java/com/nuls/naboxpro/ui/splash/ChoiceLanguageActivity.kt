package com.nuls.naboxpro.ui.splash

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.LocaleList
import com.gyf.immersionbar.ktx.immersionBar
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.enums.LanguageEnum
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.utils.showToast
import kotlinx.android.synthetic.main.activity_choice_language.*
import java.util.*

class ChoiceLanguageActivity:BaseActivity() {
    override fun getLayoutId() = R.layout.activity_choice_language

    override fun initView() {
        immersionBar {
            statusBarDarkFont(true,0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }
        if (getSystemLocale(applicationContext) == Locale.ENGLISH || getSystemLocale(
                applicationContext
            ) == Locale.US
        ) {
            rb_english.isChecked = true
            eb_chinese.isChecked = false
            tv_content1.text = "当前检测到您系统语言为：英文"
            tv_content2.text = "English"
        } else if (getSystemLocale(applicationContext) == Locale.CHINA||getSystemLocale(applicationContext) == Locale.CHINESE) {
            //系统语言是中文
            tv_content1.text = "当前检测到您系统语言为：中文"
            tv_content2.text = "Chinese"
            rb_english.isChecked = false
            eb_chinese.isChecked = true
        }
        else{
            //系统语言是中文
            tv_content1.text = "当前检测到您系统语言为：中文"
            tv_content2.text = "Chinese"
            rb_english.isChecked = false
            eb_chinese.isChecked = true
        }

        btn_next.setOnClickListener {
            if(!rb_english.isChecked&&!eb_chinese.isChecked){
                showToast(getString(R.string.choice_language))
                return@setOnClickListener
            }
            if(rb_english.isChecked){
                UserDao.language = LanguageEnum.EN.code
            }else if(eb_chinese.isChecked){
                UserDao.language = LanguageEnum.CHS.code
            }
            startActivity(Intent(this,ChoiceCurrencyActivity().javaClass))
        }


    }

    override fun initData() {

    }


    /**
     * 获取系统的locale
     *
     * @return Locale对象
     */
    fun getSystemLocale(context: Context): Locale? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList.getDefault().get(0)
        } else {
            Locale.getDefault()
        }
    }

}