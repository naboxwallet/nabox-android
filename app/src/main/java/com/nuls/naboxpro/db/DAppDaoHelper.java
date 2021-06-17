package com.nuls.naboxpro.db;

import com.nuls.naboxpro.MyApplcation;
import com.nuls.naboxpro.entity.DAppBean;

import java.util.Date;
import java.util.List;

public class DAppDaoHelper {


    /**
     * 像数据库插入Dapp信息
     *
     * @param
     */
    public static void insertWallet(DAppBean dAppBean) {

        DAppBean dapp = MyApplcation.getDaoSession().getDAppBeanDao().queryBuilder().where(DAppBeanDao.Properties.Id.eq(dAppBean.getId())).unique();
        if (dapp != null) {//说明已经存在相同钱包
//            //更新这两条配置就行
//            dapp.setUpdateTime(new Date().getTime());
            //更新已经存在的钱包
            dapp.setUpdateTime(new Date().getTime());
            MyApplcation.getDaoSession().getDAppBeanDao().update(dapp);
        } else {//插入新钱包
            dAppBean.setUpdateTime(new Date().getTime());
            MyApplcation.getDaoSession().getDAppBeanDao().insert(dAppBean);
        }

    }

    /**
     * 查询所有dapp 时间最新排序
     */
    public static List<DAppBean> loadAllDApp() {
        return MyApplcation.getDaoSession().getDAppBeanDao().queryBuilder().orderDesc(DAppBeanDao.Properties.UpdateTime).list();
    }

    /**
     * 删除所有的dapp记录
     */
    public static void deleteAllDApp() {
        MyApplcation.getDaoSession().getDAppBeanDao().deleteAll();
    }


}
