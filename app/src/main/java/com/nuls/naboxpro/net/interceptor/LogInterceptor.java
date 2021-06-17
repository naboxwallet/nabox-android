package com.nuls.naboxpro.net.interceptor;




import java.io.IOException;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * 拦截器
 * https://www.jianshu.com/p/c18f2de566b0
 */
public class LogInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        //这个chain里面包含了request和response，所以你要什么都可以从这里拿
        Request oldRequest = chain.request();
//        HttpUrl requestUrl = oldRequest.url();
//        String oldUrl = requestUrl.toString();
//        if (TextUtils.isEmpty(mOriginalBaseUrl)
//                || TextUtils.isEmpty(mNewBaseUrl)
//                || TextUtils.equals(mOriginalBaseUrl, mNewBaseUrl)
//                || !oldUrl.startsWith(mOriginalBaseUrl)) {
//            return chain.proceed(chain.request());
//        }
        Request.Builder builder=oldRequest.newBuilder().addHeader("Connection", "close")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Cache-Control", "no-cache")
                .header("Accept-Encoding", "identity");
        Request newRequest;
        newRequest =builder.build();

        long t1 = System.nanoTime();//请求发起的时间

        String method = newRequest.method();
        if ("POST".equals(method)) {
            StringBuilder sb = new StringBuilder();
            if (newRequest.body() instanceof FormBody) {
                FormBody body = (FormBody) newRequest.body();
                for (int i = 0; i < body.size(); i++) {
                    sb.append(body.encodedName(i) + "=" + body.encodedValue(i) + ",");
                }
                sb.delete(sb.length() - 1, sb.length());
            }
        } else {

        }
        Response response = chain.proceed(newRequest);
        long t2 = System.nanoTime();//收到响应的时间
        //这里不能直接使用response.body().string()的方式输出日志
        //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
        //个新的response给应用层处理
        ResponseBody responseBody = response.peekBody(1024 * 1024);
        String responseStr = responseBody.string();
        return response;
    }
}
