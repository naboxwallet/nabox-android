package com.nuls.naboxpro.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {
    public MySQLiteOpenHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        //在这里处理升级逻辑
//        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
//            @Override
//            public void onCreateAllTables(Database db, boolean ifNotExists) {
//                DaoMaster.createAllTables(db, true);
//            }
//
//            @Override
//            public void onDropAllTables(Database db, boolean ifExists) {
//                DaoMaster.dropAllTables(db, true);
//            }
//            //注意此处的参数StudentDao.class，很重要（一开始没注意，给坑了一下），它就是需要升级的table的Dao,
//            //不填的话数据丢失，
//            // 这里可以放多个Dao.class，也就是可以做到很多table的安全升级
//        }, SearchHistoryBeanDao.class,UserAccountBeanDao.class);

    }
}
