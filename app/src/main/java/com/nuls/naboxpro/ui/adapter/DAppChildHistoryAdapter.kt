package com.nuls.naboxpro.ui.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.entity.DAppBean
import com.nuls.naboxpro.enums.LanguageEnum
import com.zhuangku.app.utils.image.loadImgRadius
import org.bouncycastle.asn1.x509.Holder

class DAppChildHistoryAdapter:BaseQuickAdapter<DAppBean,BaseViewHolder> (R.layout.item_dapp_child_histroy_layout){
    override fun convert(holder: BaseViewHolder, item: DAppBean) {

        var imageView = holder.getView<ImageView>(R.id.iv_icon)
        context.loadImgRadius(imageView,item.icon,6)
        if(UserDao.language==LanguageEnum.CHS.code){
            holder.setText(R.id.tv_dapp_title,item.fileName)
            holder.setText(R.id.tv_dapp_desc,item.url)
        }else{
            holder.setText(R.id.tv_dapp_title,item.fileNameEn)
            holder.setText(R.id.tv_dapp_desc,item.url)
        }

    }
}