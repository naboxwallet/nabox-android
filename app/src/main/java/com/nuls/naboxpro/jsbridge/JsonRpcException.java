package com.nuls.naboxpro.jsbridge;

public class JsonRpcException extends Exception{

    public JsonRpc.JsonRpcError error;

    public JsonRpcException(JsonRpc.JsonRpcError error) {
        this.error = error;
    }

    public JsonRpcException(JsonRpcErrorEnum error) {
        this.error = new JsonRpc.JsonRpcError(error.code,error.message);
    }
}
