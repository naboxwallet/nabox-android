package com.nuls.naboxpro.jsbridge;

import com.alibaba.fastjson.JSON;

public enum JsonRpcErrorEnum {
    _4001(4001, "User Rejected Request"),
    _4200(4200, "Unsupported Method"),
    _4100(4100, "Unauthorized"),
    _4900(4900,"Disconnected"),
    _4901(4901,"Chain Disconnected"),
    _32700(-32700, "Parse error"),
    _32600(-32600, "Invalid request"),
    _32601(-32601, "Method not found"),
    _32602(-32602, "Invalid params"),
    _32603(-32603, "Internal error"),
    _32000(-32000, "Invalid input"),
    _32001(-32001, "Resource not found"),
    _32002(-32002, "Resource unavailable"),
    _32003(-32003, "Transaction rejected"),
    _32004(-32004, "Method not supported"),
    _32005(-32005, "Limit exceeded"),
    _32006(-32006, "JSON-RPC version not supported"),
    _32007(-32000, "Invalid input");

    public final int code;

    public final String message;

    JsonRpcErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public JsonRpc.JsonRpcError getError(){
        return new JsonRpc.JsonRpcError(code,message);
    }

    public JsonRpc.JsonRpcError getError(Object data){
        return new JsonRpc.JsonRpcError(code,message, JSON.toJSONString(data));
    }

}
