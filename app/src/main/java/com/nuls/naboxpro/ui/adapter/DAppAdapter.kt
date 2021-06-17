package com.nuls.naboxpro.ui.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.entity.DAppBean
import com.nuls.naboxpro.enums.LanguageEnum
import com.zhuangku.app.utils.image.loadImgRadius

class DAppAdapter : BaseQuickAdapter<DAppBean, BaseViewHolder>(R.layout.item_dapp_layout) {

    override fun convert(holder: BaseViewHolder, item: DAppBean) {
        var imageView = holder.getView<ImageView>(R.id.iv_icon)
        context.loadImgRadius(imageView, item.icon, 6)
        if (UserDao.language == LanguageEnum.CHS.code) {
            holder.setText(R.id.tv_name, item.fileName)
        } else {
            holder.setText(R.id.tv_name, item.fileNameEn)
        }

    }

}