package com.nuls.naboxpro.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.nuls.naboxpro.R
import kotlinx.android.synthetic.main.layout_empty_view.view.*


/**
 * @function 空页面
 * Created by xhw on 2020-1-15 15:50
 */
class EmptyView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.layout_empty_view, this)
    }

    //设置描述图片
    fun setEmptyImg(res: Int) {
        img_empty.setImageResource(res)
    }

    //设置描述文字
    fun setEmptyText(content: String) {
        tv_empty.text = content
    }

    fun setEmptyText(res: Int) {
        tv_empty.setText(res)
    }

}
