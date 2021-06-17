package com.nuls.naboxpro;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.liulishuo.filedownloader.FileDownloader;
import com.nuls.naboxpro.common.UserDao;
import com.nuls.naboxpro.db.DaoMaster;
import com.nuls.naboxpro.db.DaoSession;
import com.nuls.naboxpro.db.MySQLiteOpenHelper;
import com.nuls.naboxpro.db.WalletInfoDaoHelper;
import com.nuls.naboxpro.net.Api;
import com.nuls.naboxpro.net.BaseResponse;
import com.nuls.naboxpro.net.api.RetrofitClient;
import com.nuls.naboxpro.net.observable.RecObserver;
import com.nuls.naboxpro.utils.NeverCrash;
import com.nuls.naboxpro.wallet.transfer.SendBSCTransction;
import com.nuls.naboxpro.wallet.transfer.SendETHTransction;
import com.nuls.naboxpro.wallet.transfer.SendHECOTransction;
import com.nuls.naboxpro.wallet.transfer.SendOKTTransction;
import com.nuls.naboxpro.wallet.transfer.SendTransctions;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mmkv.MMKV;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import network.nerve.core.crypto.HexUtil;

public class MyApplcation extends Application {

    public static Context mContext;
    public static DaoSession mDaoSession;
    DaoMaster mDaoMaster;
    MySQLiteOpenHelper mHelper;

    int activityCount;

    public static DaoSession getDaoSession() {
        return mDaoSession;
    }


    public Context getInstance() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        init();
        initDB();
        if (Api.ISDEBUG) {
            SendETHTransction.initTest();
            SendBSCTransction.initTest();
            SendTransctions.initTest();
            SendHECOTransction.initTest();
            SendOKTTransction.initTest();
        }
        CrashReport.initCrashReport(getApplicationContext(), "9fe1cf03a5", Api.ISDEBUG);
//        NeverCrash.init(new NeverCrash.CrashHandler() {
//            @Override
//            public void uncaughtException(Thread t, Throwable e) {
//
////                e.printStackTrace();
//                showToast(e.getMessage());//这里打印日志
//            }
//        });
        //下载框架初始化
        FileDownloader.setupOnApplicationOnCreate(this);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                activityCount++;
                if (activityCount == 1) {
                    //切换到前台  刷新账户
                    Map<String, Object> map = new HashMap<>();
                    if(WalletInfoDaoHelper.loadAllWallet()!=null
                    &&!WalletInfoDaoHelper.loadAllWallet().isEmpty()){
                        map.put("pubKey", HexUtil.encode(WalletInfoDaoHelper.loadDefaultWallet().getCompressedPubKey()));
                        map.put("language", UserDao.INSTANCE.getLanguage());
                        RetrofitClient.getInstance().invokePostBody(getApplicationContext(), Api.WALLET_REFRESH, map
                        ).subscribe(new RecObserver<BaseResponse<Object>>(getApplicationContext(),false,false,Api.WALLET_REFRESH) {
                            @Override
                            public void onSuccess(BaseResponse<Object> data) {

                            }

                            @Override
                            public void onFail(@NotNull String msg, int code) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                activityCount--;
                if (activityCount == 0) {
//                    ArmsUtils.makeText(MyApplication.this,"应用切换到后台");
                    Log.d("viclee", ">>>>>>>>>>>>>>>>>>>切到后台  lifecycle");
                }
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });

    }


    private void init() {

        MMKV.initialize(this);

    }


    void initDB() {
        //1.创建数据库
        mHelper = new MySQLiteOpenHelper(this, "nabox.db");
//        mHelper = DevOpenHelper(this, "mydb", null);
        //3.获取管理器类
        //3.获取管理器类
        mDaoMaster = new DaoMaster(mHelper.getWritableDb());
        //4.获取表对象
        //4.获取表对象
        mDaoSession = mDaoMaster.newSession();
    }
}
