package com.nuls.naboxpro.net.api;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.nuls.naboxpro.net.TrustAllCerts;
import com.nuls.naboxpro.net.interceptor.LogInterceptor;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import org.json.JSONObject;

import java.io.File;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.X509TrustManager;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.nuls.naboxpro.net.Api.BASE_URL;
import static com.nuls.naboxpro.net.observable.CommonObservable.schedulersTransformer;
import static com.nuls.naboxpro.net.observable.CommonObservable.transformer;

/**
 * 基本脱胎于RetrofitClient  只是用来处理访问nuls openapi的请求
 */
public class NulsClient {
    /*默认超时时间*/
    private static final int DEFAULT_TIMEOUT = 10;
    /*ApiService 接口类*/
    private ApiService apiService;
    /*OkHttpClient*/
    private OkHttpClient okHttpClient;
    /*baseurl  接口地址*/
    private static String baseUrl = BASE_URL;

    private Retrofit retrofit;

    private static class SingletonHolder {
        private static NulsClient INSTANCE = new NulsClient();
    }

    private NulsClient() {
        okHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(TrustAllCerts.createSSLSocketFactory(),new X509TrustManager(){

                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                })
                .hostnameVerifier(new TrustAllCerts.TrustAllHostnameVerifier())
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .addNetworkInterceptor(
                        new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))//记录应用中的网络请求的信息
                .addInterceptor(new LogInterceptor())//日志拦截
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
        /*创建Retrofit*/
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();
        /*创建ApiService 接口类*/
        apiService = retrofit.create(ApiService.class);
    }





    public static NulsClient getInstance() {
        return NulsClient.SingletonHolder.INSTANCE;
    }

    public ApiService getApiService() {
        return apiService;
    }

    /**
     * create you ApiService
     * Create an implementation of the API endpoints defined by the {@code service} interface.
     */
    public <T> T create(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("RecClient service is null!");
        }
        return retrofit.create(service);
    }

    /**
     * 上传图片
     * @param mContext
     * @param path
     * @param file
     * @return
     */
    public Observable uploadPic(Context mContext, String path, File file) {
        Map<String, String> headers = new HashMap<>();
//        if(UserDao.INSTANCE.getUserToken()!=null){
//            headers.put("Authorization", "Bearer "+UserDao.INSTANCE.getUserToken());
//        }
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("item",file.getName(),fileBody);
        return apiService
                .uploadFile(path,headers, body)
                .compose(schedulersTransformer())
                .compose(transformer());

    }


    /**
     * POST  body形式
     *
     * @param path
     * @param param
     * @return
     */
    public Observable invokePostBody(Context mContext, String path, Map<String, Object> param) {
        Map<String, String> headers = new HashMap<>();
        JSONObject json;
        if(param!=null){
            json = new JSONObject(param);
        }else{
            json = new JSONObject(new HashMap());
        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON,json.toString());
        Log.i("http-post",path+json.toString());
//        if(UserDao.INSTANCE.getUserToken()!=null){
//            headers.put("Authorization", "Bearer "+UserDao.INSTANCE.getUserToken());
//        }

        return apiService
                .invokePost(path, headers, body)
//                .compose(((RxAppCompatActivity) mContext).bindUntilEvent(ActivityEvent.DESTROY))
                .compose(schedulersTransformer())
                .compose(transformer());

    }


    /**
     * POST  body形式
     *
     * @param path
     * @return
     */
    public Observable invokePostBody(Context mContext, String path,Object object) {
        Map<String, String> headers = new HashMap<>();
        MediaType JsonMedia = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JsonMedia,  JSON.toJSONString(object));
        Log.i("http-post",path+JSON.toJSONString(object));
//        if(UserDao.INSTANCE.getUserToken()!=null){
//            headers.put("Authorization", "Bearer "+UserDao.INSTANCE.getUserToken());
//        }

        return apiService
                .invokePost(path, headers, body)
//                .compose(((RxAppCompatActivity) mContext).bindUntilEvent(ActivityEvent.DESTROY))
                .compose(schedulersTransformer())
                .compose(transformer());

    }



    public Observable invokePostBodyNoToken(Context mContext, String path, Map<String, Object> param) {
        Map<String, String> headers = new HashMap<>();
        JSONObject json;
        if(param!=null){
            json = new JSONObject(param);
        }else{
            json = new JSONObject(new HashMap());
        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Log.i("http-post",path+json.toString());
        RequestBody body = RequestBody.create(JSON,json.toString());
        return apiService
                .invokePost(path, headers, body)
//                .compose(((RxAppCompatActivity) mContext).bindUntilEvent(ActivityEvent.DESTROY))
                .compose(schedulersTransformer())
                .compose(transformer());

    }





    /**
     * post 请求 表单形式
     *  content 先传进来 后续可能会把请求和生命周期绑定起来
     * @param path
     * @param param
     * @return
     */
    public Observable invokePost(Context mContext, String path, Map<String, String> param) {
        Map<String, String> headers = new HashMap<>();
//        headers.put("userToken", UserDao.INSTANCE.getUserToken());
        if(param==null){
            param = new HashMap<>();
        }
        Log.i("http-post",path+param.toString());
        return apiService
                .invokePost(path, headers, param)
                .compose(((RxAppCompatActivity) mContext).bindUntilEvent(ActivityEvent.DESTROY))
                .compose(schedulersTransformer())
                .compose(transformer());

    }

    public Observable invokeGetNoToken(Context mContext, String path, Map<String, String> param) {
        if (param == null) {
            param = new HashMap<>();
        }
        return apiService
                .invokeGet(path, param)
                .compose(((RxAppCompatActivity) mContext).bindUntilEvent(ActivityEvent.DESTROY))
                .compose(schedulersTransformer())
                .compose(transformer());

    }
    /**
     * get请求 带token
     *
     * @param path
     * @param param
     * @return
     */
    public Observable invokeGet(Context mContext, String path, Map<String, Object> param) {
        Map<String, String> headers = new HashMap<>();
//        if(UserDao.INSTANCE.getUserToken()!=null){
//            headers.put("Authorization", "Bearer "+UserDao.INSTANCE.getUserToken());
//        }
        if (param == null) {
            param = new HashMap<>();
        }
        Log.i("http-post",path+param.toString());
        return apiService
                .invokeGet(path, headers, param)
//                .compose(((RxAppCompatActivity) mContext).bindUntilEvent(ActivityEvent.DESTROY))
                .compose(schedulersTransformer())
                .compose(transformer());

    }

}
