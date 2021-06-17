package com.nuls.naboxpro.net.observable;






import com.nuls.naboxpro.net.ExceptionHandle;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created on 2018/12/18.
 */

public class CommonObservable {


    /**
     * 统一线程切换
     * 默认
     * @return
     */
    public static ObservableTransformer schedulersTransformer() {
        return new ObservableTransformer() {

            @Override
            public ObservableSource apply(Observable upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 统一线程切换
     * 下载
     *
     * @return
     */
    public static ObservableTransformer downLoadTransformer() {
        return new ObservableTransformer() {

            @Override
            public ObservableSource apply(Observable upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io());
            }
        };
    }

    /**
     * 处理错误
     *
     * @return
     */
    public static ObservableTransformer transformer() {
        return new ObservableTransformer() {

            @Override
            public ObservableSource apply(Observable upstream) {
                return upstream.onErrorResumeNext(new HttpResponseFunc());
            }

        };
    }

    /**
     * 服务器错误转换
     */
    private static class HttpResponseFunc implements Function<Throwable, Observable> {
        @Override
        public Observable apply(Throwable throwable) throws Exception {
            return Observable.error(ExceptionHandle.handleException(throwable));
        }
    }






}
