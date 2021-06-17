package com.nuls.naboxpro.net.api;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * 基于retrofit的注解接口
 */

public interface ApiService {



    /* 文件上传 */
    @Multipart
    @POST("{path}")
    Observable<ResponseBody> uploadFile(@Path(value = "path", encoded = true) String path, @HeaderMap Map<String, String> headers, @Part MultipartBody.Part file);


    /**
     *
     * @param requestBody
     * @return
     */
    @POST("{url}")
    Observable<ResponseBody> postBody(@Body RequestBody requestBody);


    /**
     * post 请求 body
     * @param path
     * @param headers
     * @param requestBody
     * @return
     */
    @POST("{path}")
    Observable<ResponseBody> invokePost(@Path(value = "path", encoded = true) String path, @HeaderMap Map<String, String> headers, @Body RequestBody requestBody);



    /**
     * post请求 表单
     * @param path
     * @param headers
     * @param param
     * @return
     */
    @FormUrlEncoded
    @POST("{path}")
    Observable<ResponseBody> invokePost(@Path(value = "path", encoded = true) String path, @HeaderMap Map<String, String> headers, @FieldMap Map<String, String> param);

    /**
     * get 请求 带header
     * @param path
     * @param headers
     * @param param
     * @return
     */
    @GET("{path}")
    Observable<ResponseBody> invokeGet(@Path(value = "path", encoded = true) String path, @HeaderMap Map<String, String> headers, @QueryMap Map<String, Object> param);
    /**
     * get 请求 不带header
     * @param path
     * @param param
     * @return
     */
    @GET("{path}")
    Observable<ResponseBody> invokeGet(@Path(value = "path", encoded = true) String path, @QueryMap Map<String, String> param);

}
