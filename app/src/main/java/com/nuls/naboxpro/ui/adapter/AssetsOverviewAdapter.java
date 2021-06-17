package com.nuls.naboxpro.ui.adapter;


import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nuls.naboxpro.R;
import com.nuls.naboxpro.entity.AssetsOverviewEntity;
import com.nuls.naboxpro.utils.ExtKt;

import org.jetbrains.annotations.NotNull;

/**
 * 资产概览
 */
public class AssetsOverviewAdapter extends BaseQuickAdapter<AssetsOverviewEntity, BaseViewHolder> {
    Context context;
    public AssetsOverviewAdapter(Context context) {
        super(R.layout.item_assets_overview_layout);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, AssetsOverviewEntity item) {
        ImageView imageView  = holder.getView(R.id.iv_icon);
                holder.setText(R.id.tv_name,item.getChain());
                holder.setText(R.id.tv_amount, ExtKt.getAmountByUsd(item.getPrice()));
                Glide.with(context).load(item.getIcon()).into(imageView);

    }
}
