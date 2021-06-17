package com.nuls.naboxpro.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.cardview.widget.CardView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nuls.naboxpro.R;
import com.nuls.naboxpro.entity.WalletInfo;
import com.nuls.naboxpro.utils.ExtKt;
import com.nuls.naboxpro.utils.FileUtil;
import com.nuls.naboxpro.utils.NaboxUtils;

/**
 * 钱包列表 adapter
 */
public class WalletListAdapter extends BaseQuickAdapter<WalletInfo, BaseViewHolder> {
    Context context;
    public WalletListAdapter(Context context) {
        super(R.layout.item_wallet_list_layout);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, WalletInfo item) {

        CardView cardView = helper.getView(R.id.layout_wallet);
        if(TextUtils.isEmpty(item.getAlias())){
            helper.setText(R.id.tv_wallet_name,"Nabox Wallet");
        }else{
            helper.setText(R.id.tv_wallet_name,item.getAlias());
        }
//        if(item.getWalletType()!=null){
//            helper.setText(R.id.tv_wallet_type,"("+WalletTypeEnum.getMessageByCode(item.getWalletType().code())+")");
//        }else{
//            helper.setText(R.id.tv_wallet_type,"--");
//        }
        if(TextUtils.isEmpty(item.getNulsAddress())){
            helper.setText(R.id.tv_wallet_address,"-- --");
        }else{
            helper.setText(R.id.tv_wallet_address,item.getNulsAddress());
        }
//        if(item.getBalanceInfo()!=null&&item.getBalanceInfo().getTotalBalance()!=null){
//                helper.setText(R.id.tv_money,WalletUtils.getMoney(item.getBalanceInfo().getTotalBalance()));
//        }else{
//            helper.setText(R.id.tv_money,"0.00");
//        }
        RelativeLayout relativeLayout = helper.getView(R.id.layout_bg);
        NaboxUtils.initWalletSkin(relativeLayout,item.getColor());
        //复制到剪贴板
        helper.getView(R.id.tv_wallet_address)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FileUtil.copyToClipboard(context,item.getNulsAddress());
                        ExtKt.showToast(context.getString(R.string.copy_to_clipboard));

                    }
                });
        helper.getView(R.id.iv_zxing)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        WalletInfo walletInfo = new WalletInfo();
//                        walletInfo.setNaboxWallet(item);
//                        Intent intent = new Intent(context, ReceivablesActivity.class);
//                        intent.putExtra("nabox", walletInfo);
//                        ArmsUtils.startActivity(intent);
                    }
                });
    }



}
