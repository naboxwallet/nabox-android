package com.nuls.naboxpro.ui.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nuls.naboxpro.R;
import com.nuls.naboxpro.entity.AssetsEntity;
import com.nuls.naboxpro.entity.ChainConfigBean;
import com.nuls.naboxpro.utils.ExtKt;

import org.jetbrains.annotations.NotNull;

import javax.annotation.meta.When;

public class AssetManegeAdapter extends BaseQuickAdapter<AssetsEntity, BaseViewHolder> {
    Context context;
    public AssetManegeAdapter(Context context) {
        super(R.layout.item_asset_manage_layout);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, AssetsEntity item) {

        ImageView imageView = holder.getView(R.id.iv_icon);
        ImageView ivstate = holder.getView(R.id.iv_follow);
        ExtKt.loadCoinIcon(context,imageView,ExtKt.getImageUrl(item.getSymbol()));
        holder.setText(R.id.tv_chain,item.getSymbol());
//        holder.setText(R.id.tv_address,item.geta());
        switch (item.getFollowState()){
            case 0://不允许修改
                ivstate.setBackground(context.getResources().getDrawable(R.mipmap.icon_asset_lock,null));
                break;
            case 1://
                ivstate.setBackground(context.getResources().getDrawable(R.mipmap.icon_asset_delete,null));
                break;
            case 2:
                ivstate.setBackground(context.getResources().getDrawable(R.mipmap.icon_asset_add,null));
                break;
        }
//        holder.setText(R.id.tv_address,item.getaddress());
    }


}
