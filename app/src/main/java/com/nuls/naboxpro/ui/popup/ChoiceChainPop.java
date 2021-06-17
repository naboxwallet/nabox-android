package com.nuls.naboxpro.ui.popup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lxj.xpopup.core.BottomPopupView;
import com.nuls.naboxpro.R;
import com.nuls.naboxpro.common.UserDao;
import com.nuls.naboxpro.db.WalletInfoDaoHelper;
import com.nuls.naboxpro.entity.AssetsEntity;
import com.nuls.naboxpro.entity.AssetsOverviewEntity;
import com.nuls.naboxpro.entity.WalletInfo;
import com.nuls.naboxpro.net.Api;
import com.nuls.naboxpro.net.BaseResponse;
import com.nuls.naboxpro.net.api.RetrofitClient;
import com.nuls.naboxpro.net.observable.RecObserver;
import com.nuls.naboxpro.ui.splash.CreateWalletActivity;
import com.nuls.naboxpro.utils.ExtKt;
import com.nuls.naboxpro.utils.NaboxUtils;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import network.nerve.core.crypto.HexUtil;

import static com.nuls.naboxpro.utils.ExtKt.getAmountByUsd;
import static com.nuls.naboxpro.utils.ExtKt.getImageUrl;
import static com.nuls.naboxpro.utils.ExtKt.loadCoinIcon;
import static com.nuls.naboxpro.utils.ExtKt.showToast;

public class ChoiceChainPop extends BottomPopupView {

    RecyclerView listWallet,listAssets;

    ImageButton close;

    ImageView ivAdd;

    AssetAdapter assetAdapter;
    WalletAdapter walletAdapter;

    ChoiceListener choiceListener;


    boolean visibilityOther ;
    /**
     * 当前钱包是否被选中钱包  如果是，那么需要对他的资产列表进行判断  标记当前显示的资产
     */
    boolean isChoice = true;

    /**
     * 当前显示的链
     */
    String chain;

    /**
     * 当前选中的钱包
     */
    WalletInfo walletInfo;


    String targetChain;

    /**
     *
     * @param context
     * @param chain 当前链
     * @param visibilityOther 是否非当前链资产 默认false
     * @param  targetChain 目标链 用来筛选
     * @param choiceListener
     */
    public ChoiceChainPop(@NonNull Context context,String chain,String targetChain,boolean visibilityOther,ChoiceListener choiceListener) {
        super(context);
        this.chain = chain;
        this.visibilityOther = visibilityOther;
        this.choiceListener =choiceListener;
        this.targetChain = targetChain;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_choice_wallet;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        listWallet = findViewById(R.id.wallet_list);
        listAssets = findViewById(R.id.chain_list);
        close  = findViewById(R.id.close);
        ivAdd = findViewById(R.id.iv_add);
        ivAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().startActivity(new Intent(getContext(), CreateWalletActivity.class));
                dismiss();
            }
        });
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        listWallet.setLayoutManager(new LinearLayoutManager(getContext()));
        listAssets.setLayoutManager(new LinearLayoutManager(getContext()));
        walletInfo = WalletInfoDaoHelper.loadDefaultWallet();
        assetAdapter = new AssetAdapter();
        listAssets.setAdapter(assetAdapter);
        walletAdapter = new WalletAdapter();
        listWallet.setAdapter(walletAdapter);

        if(WalletInfoDaoHelper.loadAllWallet()!=null){

            walletAdapter.setNewInstance(WalletInfoDaoHelper.loadAllWallet());
            loadAllWalletMoney(WalletInfoDaoHelper.loadAllWallet());
        }
//        getAssetsList(HexUtil.encode(walletInfo.getCompressedPubKey()));
//
        getChainPrice(HexUtil.encode(walletInfo.getCompressedPubKey()));
        walletAdapter.setOnItemClickListener((adapter, view, position) -> {
            //切换选中钱包 刷新资产列表
            walletInfo = (WalletInfo) adapter.getData().get(position);
            walletAdapter.notifyDataSetChanged();
            if(walletInfo.getNulsAddress().equals(UserDao.INSTANCE.getDefaultWallet())){
                isChoice = true;
            }else{
                isChoice = false;
            }
//            getAssetsList(HexUtil.encode(walletInfo.getCompressedPubKey()));
            getChainPrice(HexUtil.encode(walletInfo.getCompressedPubKey()));

        });
        assetAdapter.setOnItemClickListener((adapter, view, position) -> {
            if(choiceListener!=null){
                choiceListener.success(walletInfo, (AssetsOverviewEntity) adapter.getData().get(position));
            }
            dismiss();
        });

    }





    class WalletAdapter extends BaseQuickAdapter<WalletInfo, BaseViewHolder>{

        public WalletAdapter() {
            super(R.layout.item_pop_wallet);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder holder, WalletInfo item) {
            if(TextUtils.isEmpty(item.getAlias())){
                holder.setText(R.id.tv_walletName,getContext().getString(R.string.null_content));
            }else{
                holder.setText(R.id.tv_walletName,item.getAlias());
            }
            if(TextUtils.isEmpty(item.getUsdPrice())){
                holder.setText(R.id.tv_amount,getContext().getString(R.string.null_content));
            }else{
                holder.setText(R.id.tv_amount, getAmountByUsd(item.getUsdPrice()));
            }
            RelativeLayout relativeLayout = holder.findView(R.id.layout_wallet);
            TextView title = holder.getView(R.id.tv_walletName);
            TextPaint paint = title.getPaint();

            if(walletInfo.getNulsAddress().equals(item.getNulsAddress())){
                //说明这个钱包被选中，设置一个单独的背景色
                relativeLayout.setBackgroundColor(0);
                relativeLayout.setBackgroundColor(getResources().getColor(R.color.fff9fafc,null));
                title.setTextColor(Color.parseColor("#333333"));
                paint.setFakeBoldText(true);
//                holder.setBackgroundColor(R.id.layout_wallet,R.color.red);
            }else{
                relativeLayout.setBackgroundColor(0);
                title.setTextColor(Color.parseColor("#8F95A8"));
                paint.setFakeBoldText(false);
                relativeLayout.setBackgroundColor(getResources().getColor(R.color.fff2f2f4,null));
            }
        }
    }




    class AssetAdapter extends BaseQuickAdapter<AssetsOverviewEntity,BaseViewHolder>{

        public AssetAdapter() {
            super(R.layout.item_choicechain_asset_layout);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder holder, AssetsOverviewEntity item) {
            if(TextUtils.isEmpty(item.getChain())){
                holder.setText(R.id.tv_chainName,getContext().getString(R.string.null_content));
                holder.setText(R.id.tv_chainAddress,getContext().getString(R.string.null_content));
            }else{
                holder.setText(R.id.tv_chainName,item.getChain());
                if(walletInfo!=null){
                    holder.setText(R.id.tv_chainAddress,ExtKt.getAddressByCoinType(walletInfo,item.getChain()));
                }

            }
//            if(TextUtils.isEmpty(item.getAddress())){
//                holder.setText(R.id.tv_chainAddress,getContext().getString(R.string.null_content));
//            }else{
//                holder.setText(R.id.tv_chainAddress, item.getAddress());
//            }

            holder.getView(R.id.tv_chainAddress).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExtKt.copyToClipboard(getContext(),ExtKt.getAddressByCoinType(walletInfo,item.getChain()));
                }
            });
            holder.getView(R.id.iv_copy).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExtKt.copyToClipboard(getContext(),ExtKt.getAddressByCoinType(walletInfo,item.getChain()));
                }
            });


            if(TextUtils.isEmpty(item.getPrice())){
                holder.setText(R.id.tv_amount,getContext().getString(R.string.null_content));
            }else{
                holder.setText(R.id.tv_amount, getAmountByUsd(item.getPrice()));
            }
            ImageView imageView = holder.getView(R.id.iv_pic);
            if(!TextUtils.isEmpty(item.getIcon())){
                loadCoinIcon(getContext(), imageView, item.getIcon());
            }
            if(isChoice){
                if(chain.equals(item.getChain())){
                    holder.setGone(R.id.flag,false);
                }else{
                    holder.setGone(R.id.flag,true);
                }

            }else{
                holder.setGone(R.id.flag,true);
            }
        }
    }




//    /**
//     * 获取资产列表
//     */
//    private void getAssetsList(String pubKey){
//        Map<String, Object> map = new HashMap<>();
//        map.put("language",UserDao.INSTANCE.getLanguage());
//        map.put("pubKey",pubKey);
//        RetrofitClient.getInstance().invokePostBody(getContext(),Api.GET_MAIN_ASSETS,map)
//                .subscribe(new RecObserver<BaseResponse<List<AssetsEntity>>>(getContext(),false,false) {
//                    @Override
//                    public void onSuccess(BaseResponse<List<AssetsEntity>> data) {
//                        if(data.getData()!=null){
//                            assetAdapter.setNewInstance(data.getData());
//                        }
//                    }
//                    @Override
//                    public void onFail(@NotNull String msg, int code) {
//
//                    }
//                });
//    }

    private void loadAllWalletMoney(List<WalletInfo> walletList){

        Map<String, Object> map = new HashMap<>();
        map.put("language", UserDao.INSTANCE.getLanguage());
        map.put("pubKeyList", WalletInfoDaoHelper.loadAllWalletPubKey());
        RetrofitClient.getInstance().invokePostBody(getContext(), Api.GET_WALLET_LIST_PRICE,map)
                .subscribe(new RecObserver<BaseResponse<HashMap<String, String>>>(getContext(),false,false, Api.GET_WALLET_LIST_PRICE) {
                    @Override
                    public void onSuccess(BaseResponse<HashMap<String, String>> data) {
                        for(int i =0;i<walletList.size();i++){
                            walletList.get(i).setUsdPrice(data.getData().get(HexUtil.encode(walletList.get(i).getCompressedPubKey())));
                        }
                        walletAdapter.setNewInstance(walletList);
                        walletAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onFail(@NotNull String msg, int code) {
                        showToast(msg);
                    }
                });

    }



    public  interface ChoiceListener{

        /**
         * 返回选中的钱包信息和资产信息
         * @param walletInfo
         * @param assetsEntity
         */
        void  success(WalletInfo walletInfo,AssetsOverviewEntity assetsEntity);

    }



    /**
     * 获取对应链的资产总额（美元）
     */
    private  void getChainPrice(String pubkey){
        Map<String, Object> map = new HashMap<>();
        map.put("language", UserDao.INSTANCE.getLanguage());
        map.put("pubKey", pubkey);
        RetrofitClient.getInstance().invokePostBody(getContext(),Api.CHAIN_PRICE,map)
                .subscribe(new RecObserver<BaseResponse<List<AssetsOverviewEntity>>>(getContext(),false,false,Api.CHAIN_PRICE) {
                    @Override
                    public void onSuccess(BaseResponse<List<AssetsOverviewEntity>> data) {
                        if(visibilityOther){
                            assetAdapter.setNewInstance( fileterAsset(data.getData()));
                        }else{
                            assetAdapter.setNewInstance(data.getData());
                        }

                    }

                    @Override
                    public void onFail(@NotNull String msg, int code) {
                        showToast(msg);
                    }
                });

    }


    private  List<AssetsOverviewEntity> fileterAsset(List<AssetsOverviewEntity> oldList){
        List<AssetsOverviewEntity> newList = new ArrayList<>();
       for(int i = 0;i<oldList.size();i++){
           if(oldList.get(i).getChain().contains(targetChain)){
               newList.add(oldList.get(i));
           }
       }
       return  newList;
    }


}
