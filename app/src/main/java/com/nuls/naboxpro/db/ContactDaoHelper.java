package com.nuls.naboxpro.db;

import com.nuls.naboxpro.MyApplcation;
import com.nuls.naboxpro.entity.ContactsDaoEntity;
import com.nuls.naboxpro.entity.WalletInfo;


import java.util.List;

public class ContactDaoHelper {

    /**
     * 插入联系人
     * @param
     */
    public static void insertContact(ContactsDaoEntity contactsDaoEntity){
        ContactsDaoEntity contactInfo = MyApplcation.getDaoSession().getContactsDaoEntityDao().queryBuilder().where(ContactsDaoEntityDao.Properties.Address.eq(contactsDaoEntity.getAddress())).unique();
        if(contactInfo!=null){//已经存在相同地址的联系人
            contactInfo.setName(contactsDaoEntity.getName());
            MyApplcation.getDaoSession().getContactsDaoEntityDao().update(contactInfo);
        }else{
            MyApplcation.getDaoSession().getContactsDaoEntityDao().insert(contactsDaoEntity);
        }
    }

    /**
     * 删除联系人
     * @param
     */
    public static void deleteContact(ContactsDaoEntity contactsDaoEntity){
        MyApplcation.getDaoSession().getContactsDaoEntityDao().delete(contactsDaoEntity);
    }


    /**
     * 查询所有联系人
     */
    public static List<ContactsDaoEntity> loadAllWallet(){
        return MyApplcation.getDaoSession().getContactsDaoEntityDao().loadAll();
    }




}
