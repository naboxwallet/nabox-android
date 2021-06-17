package com.nuls.naboxpro.net;

public class BaseResponse<T> {

//错误示例{    "code": 1002,    "msg": "语言类型错误",    "data": null}
    /**
     * //返回码，1000表示成功，其余表示失败
     */
    int code;

    /**
     * //返回信息
     */
    String msg;


    /**
     * //返回数据
     */
    T  data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
