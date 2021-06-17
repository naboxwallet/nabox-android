package com.nuls.naboxpro.ui.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nuls.naboxpro.R;
import com.nuls.naboxpro.common.UserDao;
import com.nuls.naboxpro.entity.AssetsEntity;
import com.nuls.naboxpro.enums.MoneyTypeEnum;
import com.nuls.naboxpro.utils.Arith;
import com.nuls.naboxpro.utils.ExtKt;

import org.jetbrains.annotations.NotNull;

public class AssetsAdapter extends BaseQuickAdapter<AssetsEntity, BaseViewHolder> {
    public AssetsAdapter() {
        super(R.layout.item_other_coin_layout);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, AssetsEntity item) {

        if(item.getBalance()!=null){
            if(item.getDecimals()==0){
                helper.setText(R.id.tv_nuls_num,item.getBalance()+"");
            }else{
//                helper.setText(R.id.tv_nuls_num, Arith.divNoGrouping(Double.valueOf(item.getBalance()),addZero(item.getDecimals()),item.getDecimals())+"");
                //这里改为保留六位小数
                 helper.setText(R.id.tv_nuls_num, Arith.divNoGrouping(Double.valueOf(item.getBalance()),addZero(item.getDecimals()),6)+"");
            }
        }else{
            helper.setText(R.id.tv_nuls_num,"0");
        }

        helper.setText(R.id.tv_token_name,item.getSymbol());
        if(!TextUtils.isEmpty(item.getRegisterChain())){
            helper.setText(R.id.tv_register_chain,item.getRegisterChain());

        }else{
            //如果registerChain没有值，就和chain字段是一个值
            if(TextUtils.isEmpty(item.getChain())){
                helper.setText(R.id.tv_register_chain,getContext().getString(R.string.null_content));
            }else{
                helper.setText(R.id.tv_register_chain,item.getChain());
            }
        }
        ImageView imageView = helper.getView(R.id.iv_token);
        if(item.getSymbol()!=null){
            ExtKt.loadCoinIcon(getContext(),imageView,ExtKt.getImageUrl(item.getSymbol()));
        }

        if(!TextUtils.isEmpty(item.getUsdPrice())){
            helper.setText(R.id.tv_money, ExtKt.getAmountByUsd(item.getUsdPrice()));
//            if(UserDao.INSTANCE.getCurrency().equals(MoneyTypeEnum.USD.code())){
//            }else if(UserDao.INSTANCE.getCurrency().equals(MoneyTypeEnum.RMB.code())){
//                helper.setText(R.id.tv_money,item.getUsdPrice());
//            }
        }
    }
    private Double addZero(int number){
        double num = 1.0;
        for(int i=0;i<number;i++){
            num = num*10;
        }
        return num;
    }
}
