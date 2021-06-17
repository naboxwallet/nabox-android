package com.nuls.naboxpro.db;

import com.nuls.naboxpro.MyApplcation;
import com.nuls.naboxpro.entity.AuthorizationBean;

public class AuthDaoHelper {


    /**
     * 插入一条授权信息
     */
    public static void insertAuth(String url, String chain, String address, boolean isLongAuth) {
//        AuthorizationBean auth = new AuthorizationBean(url,chain,address,true,isLongAuth);
        AuthorizationBean auth = MyApplcation.getDaoSession().getAuthorizationBeanDao().queryBuilder().where(AuthorizationBeanDao.Properties.Url.eq(url), AuthorizationBeanDao.Properties.Chain.eq(chain), AuthorizationBeanDao.Properties.Address.eq(address)).unique();
        if (auth != null) {
            auth.setIsLongAuth(isLongAuth);
            MyApplcation.getDaoSession().getAuthorizationBeanDao().update(auth);
        } else {
            MyApplcation.getDaoSession().getAuthorizationBeanDao().insert(new AuthorizationBean(url, chain, address, true, isLongAuth));
        }
//        WalletInfo wallinfoOld = MyApplcation.getDaoSession().getWalletInfoDao().queryBuilder().where(WalletInfoDao.Properties.NulsAddress.eq(walletInfo.getNulsAddress())).unique();
//        if(wallinfoOld!=null){//说明已经存在相同钱包
//            //更新这两条配置就行
//            wallinfoOld.setAlias(walletInfo.getAlias());
//            wallinfoOld.setColor(walletInfo.getColor());
//            //更新已经存在的钱包
//            MyApplcation.getDaoSession().getWalletInfoDao().update(wallinfoOld);
//        }else{//插入新钱包
//            MyApplcation.getDaoSession().getWalletInfoDao().insert(walletInfo);
//        }


    }

    /**
     * 查询是否授权
     */
    public static boolean checkAuth(String url, String chain, String address) {
        AuthorizationBean auth = MyApplcation.getDaoSession().getAuthorizationBeanDao().queryBuilder().where(AuthorizationBeanDao.Properties.Url.eq(url), AuthorizationBeanDao.Properties.Chain.eq(chain), AuthorizationBeanDao.Properties.Address.eq(address)).unique();
        if (auth != null) {

            return auth.getIsLongAuth();
        }
        return false;
    }


}
