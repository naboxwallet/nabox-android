package com.nuls.naboxpro.jsbridge;


import com.alibaba.fastjson.JSONArray;

public class JsonRpc {

    public static class JsonRpcError {

        int code;
        String message;
        Object data;

        public JsonRpcError() {
        }

        public JsonRpcError(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public JsonRpcError(int code, String message, Object data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }

    String id;

    String jsonrpc = "2.0";

    JSONArray params;

    Object result;

    JsonRpcError error;

    public JsonRpc(String id, Object result) {
        this.id = id;
        this.result = result;
    }

    public JsonRpc(String id,JsonRpcError error){
        this.error = error;
        this.id = id;
    }

    public static JsonRpc success(String id, Object result) {
        return new JsonRpc(id, result);
    }

    public static JsonRpc fail(String id,JsonRpcError error){
        return new JsonRpc(id,error);
    }

    public JsonRpc() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public JsonRpcError getError() {
        return error;
    }

    public void setError(JsonRpcError error) {
        this.error = error;
    }

    public JSONArray getParams() {
        return params;
    }

    public void setParams(JSONArray params) {
        this.params = params;
    }
}
