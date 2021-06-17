package com.nuls.naboxpro.ui.activity

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import com.gyf.immersionbar.ktx.immersionBar
import com.ms.banner.BannerConfig
import com.ms.banner.Transformer
import com.ms.banner.holder.BannerViewHolder
import com.nuls.naboxpro.R
import com.nuls.naboxpro.db.WalletInfoDaoHelper
import com.nuls.naboxpro.entity.WalletInfo
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.ui.splash.ChoiceWalletSkinActivity
import com.nuls.naboxpro.ui.splash.SaveWalletHintActivity
import kotlinx.android.synthetic.main.activity_choice_wallet_skin.*
import kotlinx.android.synthetic.main.base_title_layout.*
import java.util.ArrayList

class ChangeSkinActivity:BaseActivity() {
    internal var colorIndex = 0
    /**
     * 轮播图数据
     */
    internal var mPages: MutableList<Int> = ArrayList()
    override fun getLayoutId() = R.layout.activity_change_skin_layout

    override fun initView() {
        immersionBar {
            statusBarDarkFont(true,0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }
        back.setOnClickListener {
            finish()
        }
        tv_content.text = getString(R.string.skin_name1)
        mPages.add(R.mipmap.png_wallet1)
        mPages.add(R.mipmap.png_wallet2)
        mPages.add(R.mipmap.png_wallet3)
        mPages.add(R.mipmap.png_wallet4)
        mPages.add(R.mipmap.png_wallet5)
        //初始化banner
        //禁止自动滚动
        banner.setAutoPlay(false)
        //不适用系统自带的指示器
        banner.setBannerStyle(BannerConfig.NOT_INDICATOR)
        //动画效果
        banner.setBannerAnimation(Transformer.Scale)
        //禁止循环
        banner.setLoop(false)
        banner.setPages(mPages, CustomViewHolder())
        banner.start()
        banner.setOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {


            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        tv_content.text = getString(R.string.skin_name1)
                        layout_count_bg.setBackgroundResource(R.drawable.shape_choice_skin1)
                    }
                    1 -> {
                        tv_content.text = getString(R.string.skin_name2)
                        layout_count_bg.setBackgroundResource(R.drawable.shape_choice_skin2)
                    }
                    2 -> {
                        tv_content.text = getString(R.string.skin_name3)
                        layout_count_bg.setBackgroundResource(R.drawable.shape_choice_skin3)
                    }
                    3 -> {
                        tv_content.text = getString(R.string.skin_name4)
                        layout_count_bg.setBackgroundResource(R.drawable.shape_choice_skin4)
                    }
                    4 -> {
                        tv_content.text = getString(R.string.skin_name5)
                        layout_count_bg.setBackgroundResource(R.drawable.shape_choice_skin5)
                    }
                }
                colorIndex = position
                tv_card_count.text = (position + 1).toString() + ""
            }

        })
        tv_next.setOnClickListener {
            if(intent!=null&&intent.getSerializableExtra("wallet")!=null){
                var walletInfo:WalletInfo = intent.getSerializableExtra("wallet") as WalletInfo
                walletInfo.color = colorIndex
                WalletInfoDaoHelper.updateWallet(walletInfo)
                finish()
            }
        }
    }

    override fun initData() {

    }

    internal inner class CustomViewHolder : BannerViewHolder<Int> {

        private var mImageView: ImageView? = null

        override fun createView(context: Context): View {
            val view = LayoutInflater.from(context).inflate(R.layout.banner_item, null)
            mImageView = view.findViewById(R.id.iv_banner)
            return view
        }

        override fun onBind(context: Context, position: Int, data: Int?) {
            // 数据绑定
            mImageView!!.setBackgroundResource(data!!)
        }
    }



    companion object{
        fun start(context: Context,walletInfo: WalletInfo){
            var intent:Intent = Intent(context,ChangeSkinActivity().javaClass)
            intent.putExtra("wallet",walletInfo)
            context.startActivity(intent)


        }

    }

}