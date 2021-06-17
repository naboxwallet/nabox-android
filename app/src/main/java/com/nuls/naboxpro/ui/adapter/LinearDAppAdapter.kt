package com.nuls.naboxpro.ui.adapter

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.entity.DAppBean
import com.nuls.naboxpro.enums.LanguageEnum
import com.nuls.naboxpro.utils.DensityUtil
import com.zhuangku.app.utils.image.loadImg
import com.zhuangku.app.utils.image.loadImgRadius

class LinearDAppAdapter : BaseQuickAdapter<DAppBean, BaseViewHolder>(R.layout.item_dapp_layout) {

    var or: Int? = null

    var screenWidth: Int? =null

    override fun convert(holder: BaseViewHolder, item: DAppBean) {
        setItemMinWidth(holder)
        var imageView = holder.getView<ImageView>(R.id.iv_icon)
        context.loadImgRadius(imageView, item.icon, 6)
        if (UserDao.language == LanguageEnum.CHS.code) {
            holder.setText(R.id.tv_name, item.fileName)
        } else {
            holder.setText(R.id.tv_name, item.fileNameEn)
        }

    }

    private fun setItemMinWidth(holder: BaseViewHolder) {
        if(screenWidth==null) screenWidth = DensityUtil.getScreenWidth(context)
        var tvName: TextView = holder.getView(R.id.tv_name)
        var lp: ViewGroup.LayoutParams = tvName.layoutParams
        lp.width = (screenWidth!! - DensityUtil.dip2px(context, 80f)-DensityUtil.dip2px(context, 10f)*4)/5
        tvName.layoutParams = lp
    }

}