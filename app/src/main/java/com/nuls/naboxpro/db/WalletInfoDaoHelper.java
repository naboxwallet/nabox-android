package com.nuls.naboxpro.db;

import com.nuls.naboxpro.MyApplcation;
import com.nuls.naboxpro.common.UserDao;
import com.nuls.naboxpro.entity.WalletInfo;

import java.util.ArrayList;
import java.util.List;

import network.nerve.core.crypto.HexUtil;


public class WalletInfoDaoHelper {


    /**
     * 像数据库插入钱包
     * @param walletInfo
     */
    public static void insertWallet(WalletInfo walletInfo){
        WalletInfo wallinfoOld = MyApplcation.getDaoSession().getWalletInfoDao().queryBuilder().where(WalletInfoDao.Properties.NulsAddress.eq(walletInfo.getNulsAddress())).unique();
        if(wallinfoOld!=null){//说明已经存在相同钱包
            //更新这两条配置就行
            wallinfoOld.setAlias(walletInfo.getAlias());
            wallinfoOld.setColor(walletInfo.getColor());
            //更新已经存在的钱包
            MyApplcation.getDaoSession().getWalletInfoDao().update(wallinfoOld);
        }else{//插入新钱包
            MyApplcation.getDaoSession().getWalletInfoDao().insert(walletInfo);
        }

    }

    /**
     * 删除钱包
     * @param walletInfo
     */
    public static void deleteWallet(WalletInfo walletInfo){
        MyApplcation.getDaoSession().getWalletInfoDao().delete(walletInfo);
    }


    /**
     * 查询所有钱包
     */
    public static List<WalletInfo> loadAllWallet(){
        return MyApplcation.getDaoSession().getWalletInfoDao().loadAll();
    }


    /**
     * 更新钱包
     */
    public static void updateWallet(WalletInfo walletInfo){
        MyApplcation.getDaoSession().getWalletInfoDao().update(walletInfo);
    }


    /**
     * 查询所有钱包公钥
     */
    public static List<String> loadAllWalletPubKey(){
        List<String> pubKeyList = new ArrayList<String>();
        List<WalletInfo> walletList = MyApplcation.getDaoSession().getWalletInfoDao().loadAll();
        if(walletList!=null&&!walletList.isEmpty()){
            for(int i = 0;i<walletList.size();i++){
                pubKeyList.add(HexUtil.encode(walletList.get(i).getCompressedPubKey()));
            }
        }
        return pubKeyList;
    }


    /**
     * 查询指定钱包
     */
    public static WalletInfo loadWalletByAddress(String nulsAddress){
        WalletInfo walletInfo = MyApplcation.getDaoSession().getWalletInfoDao().queryBuilder().where(WalletInfoDao.Properties.NulsAddress.eq(nulsAddress)).unique();
        return walletInfo;
    }


    /**
     * 查询默认钱包
     */
    public static WalletInfo loadDefaultWallet(){
        WalletInfo walletInfo = MyApplcation.getDaoSession().getWalletInfoDao().queryBuilder().where(WalletInfoDao.Properties.NulsAddress.eq(UserDao.INSTANCE.getDefaultWallet())).unique();
        if(walletInfo!=null){
            return walletInfo;
        }else{
            return  MyApplcation.getDaoSession().getWalletInfoDao().loadAll().get(0);
        }

    }
}
