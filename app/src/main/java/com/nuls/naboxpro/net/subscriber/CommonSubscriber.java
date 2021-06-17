package com.nuls.naboxpro.net.subscriber;
import android.util.Log;

import com.google.gson.Gson;
import com.nuls.naboxpro.net.BaseResponse;
import com.nuls.naboxpro.net.ErrorHandle;
import com.nuls.naboxpro.net.ExceptionHandle;


import org.json.JSONException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;


/**
 * Created on 2018/12/18.
 * <p>
 * 默认的Subscriber
 */

public abstract class CommonSubscriber<T> implements Observer<ResponseBody> {

    @Override
    public void onSubscribe(Disposable d) {

    }


    @Override
    public void onError(Throwable e) {
        if (e instanceof ExceptionHandle.ResponeThrowable) {
            onError((ExceptionHandle.ResponeThrowable) e);
        } else {
            onError(ExceptionHandle.handleException(e));
        }

    }

    /**
     * 剥离数据，去壳，并处理异常情况
     *
     * @param value
     */
    @Override
    public void onNext(ResponseBody value) {
//        if (!NetworkUtil.isNetworkAvailable(BaseApplication.getInstance())) {
//            ExceptionHandle.ResponeThrowable ex = new ExceptionHandle.ResponeThrowable(new Exception(), ExceptionHandle.ERROR.NETWORD_ERROR);
//            ex.message = "无可用网络";
//            onError(ex);
//        }

        String response = null;
        try {
            response = value.string();
            BaseResponse result = new Gson().fromJson(response,BaseResponse.class);
            Log.i("yange-httpresult",response+"");
//            OpenApiResponse result = GsonUtils.get(response, OpenApiResponse.class);
            if (result == null) {
                throw new JSONException("解析异常");
            }
            if (result.getCode()==1000) {
                Type type = getType();
                String parseData = getParseDataFromResponse(response);
                T data = new Gson().fromJson(parseData, type);
                if (data == null) {
                    throw new RuntimeException("数据解析异常");
                }
                onSuccess(data);
            } else {
                onError(result.getMsg(), String.valueOf(result.getCode()));
                ErrorHandle.handleException(result.getMsg(), String.valueOf(result.getCode()));
            }
            onComplete();

        } catch (Exception e) {
            e.printStackTrace();
            onError(e);
        } finally {
            value.close();
        }

    }

    /**
     * 获取泛型类型
     *
     * @return
     */
    private Type getType() {
        Type superclass = getClass().getGenericSuperclass(); //getGenericSuperclass()获得带有泛型的父类
        if (superclass instanceof Class) {
            throw new RuntimeException("须传入指定类型");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        Type type = parameterized.getActualTypeArguments()[0];
        return type;
    }


    /**
     * 将要解析的data数据
     *
     * @param response
     * @return
     * @throws JSONException
     */
//    public abstract String getParseDataFromResponse(String response) throws JSONException;
    String getParseDataFromResponse(String response) {
        return response;
    }

    @Override
    public void onComplete() {

    }

    /**
     * 成功回调
     *
     * @param data
     */
    public abstract void onSuccess(T data);

    /**
     * 错误回调
     *
     * @param msg
     * @param code
     */
    public abstract void onError(String msg, String code);

    /**
     * 错误回调
     *
     * @param e
     */
    public void onError(ExceptionHandle.ResponeThrowable e) {
        if (e != null) {
                    onError(e.message, e.code + "");

        } else {
            onError("未知错误", ExceptionHandle.ERROR.UNKNOWN + "");
        }

    }

}
