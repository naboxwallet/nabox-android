package com.nuls.naboxpro.ui.splash

import android.content.Intent
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.gyf.immersionbar.ktx.immersionBar
import com.nuls.naboxpro.R
import com.nuls.naboxpro.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_welcome_second.*

class WelcomeSecondActivity :BaseActivity(){
    override fun getLayoutId() = R.layout.activity_welcome_second

    override fun initView() {
        immersionBar {
            statusBarDarkFont(true,0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }
        val alphaAnimation = AlphaAnimation(0f, 1f)
        alphaAnimation.duration = 2000
        alphaAnimation.fillAfter = true//停止
        val alphaAnimation2 = AlphaAnimation(0f, 1f)
        alphaAnimation2.duration = 2000
        alphaAnimation2.fillAfter = true//停止
        tv_content.animation = alphaAnimation
        alphaAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                //需要先清空Animation  不然setvisibility会无效
                tv_content.animation = null
                tv_content.visibility = View.GONE
                tv_content2.visibility = View.VISIBLE
                tv_content2.animation = alphaAnimation2
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        alphaAnimation2.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                startActivity(Intent(this@WelcomeSecondActivity,ChoiceLanguageActivity().javaClass))
                finish()
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
    }

    override fun initData() {

    }
}