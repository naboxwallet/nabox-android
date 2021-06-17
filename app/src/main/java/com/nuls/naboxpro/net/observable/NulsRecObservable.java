package com.nuls.naboxpro.net.observable;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.nuls.naboxpro.net.ErrorHandle;
import com.nuls.naboxpro.net.ExceptionHandle;
import com.nuls.naboxpro.net.NulsBaseResponse;
import com.nuls.naboxpro.net.progress.ObserverResponseListener;
import com.nuls.naboxpro.net.progress.ProgressCancelListener;
import com.nuls.naboxpro.net.progress.ProgressDialogHandler;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;


/**
 * 单独处理和nuls主网的请求
 */
public abstract class NulsRecObservable<T> implements Observer<ResponseBody>, ObserverResponseListener<T>, ProgressCancelListener {


    private ProgressDialogHandler mProgressDialogHandler;
    private Disposable d;
    Context context;

    public NulsRecObservable(Context context) {
        this(context, true, true);
    }

    public NulsRecObservable(Context context, boolean isDialog, boolean cancelable) {
        this.context = context;
        if (isDialog) {
            mProgressDialogHandler = new ProgressDialogHandler(context, this, cancelable);
        }
    }


    private void showProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            mProgressDialogHandler = null;
        }
    }

    @Override
    public void onSubscribe(Disposable d) {
        this.d = d;
        showProgressDialog();
    }

    @Override
    public void onNext(ResponseBody t) {

        String response;
        try {
            response = t.string();
            NulsBaseResponse result = new Gson().fromJson(response,NulsBaseResponse.class);

            if (result == null) {
                throw new JSONException("解析异常");
            }
            Log.i("http-result",response+"");
            if (result.getError()==null&&result.getResult()!=null) {
                Type type = getType();
                String parseData = getParseDataFromResponse(response);
                T data = new Gson().fromJson(parseData, type);
                if (data == null) {
                    throw new RuntimeException("数据解析异常");
                }
                onSuccess(data);
            } else {
                onFail(result.getError().getMessage(), result.getError().getCode());
                ErrorHandle.handleException(result.getError().getMessage(), String.valueOf(result.getError().getCode()));

            }
            onComplete();

        } catch (Exception e) {
            e.printStackTrace();
            onError(e);
        } finally {
            t.close();
        }
    }

    private Type getType() {
        Type superclass = getClass().getGenericSuperclass(); //getGenericSuperclass()获得带有泛型的父类
        if (superclass instanceof Class) {
            throw new RuntimeException("须传入指定类型");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        Type type = parameterized.getActualTypeArguments()[0];
        return type;
    }

    String getParseDataFromResponse(String response) {
        return response;
    }

    @Override
    public void onError(Throwable e) {
        Log.e("error", "Throwable-onError: " + e.getMessage());
        dismissProgressDialog();
        onFail(((ExceptionHandle.ResponeThrowable) e).message,((ExceptionHandle.ResponeThrowable) e).code);
    }

    @Override
    public void onComplete() {
        dismissProgressDialog();
    }

    @Override
    public void onCancelProgress() {
        //如果处于订阅状态，则取消订阅
        if (!d.isDisposed()) {
            d.dispose();
        }
    }

    public abstract void onSuccess(T data);

    public abstract void onFail(@NotNull String msg, int code);

}
