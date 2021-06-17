package com.nuls.naboxpro.enums;

import android.text.TextUtils;

public enum  CoinTypeEnum {
    //nuls
    NULS("NULS", "NULS","NULS",8),
    //以太坊
    ETH("Ethereum", "ETH","ETH",18),
    //币安
    BSC("BSC", "BNB","BNB",18),
    //nvt
    NERVE("NERVE","NVT","NVT",8),
    //火币
    HECO("Heco","HT","HT",18),
    //okex
    OKEX("OKExChain","OKT","OKT",18),
    //合约资产  不关心具体是哪个的合约  这里的token symbol decimal都是随便写的
    TOKEN("Token","Token","Token",8);





    String code;
    String message;
    String symbol;
    int decimal;
    public String getCode() {
        return code;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getDecimal() {
        return decimal;
    }

    public void setDecimal(int decimal) {
        this.decimal = decimal;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private CoinTypeEnum(String code, String message,String symbol,int decimal) {
        this.code = code;
        this.message = message;
        this.symbol = symbol;
        this.decimal =decimal;
    }
    /**
     * @return Returns the code.
     */
    public String code() {
        return code;
    }


    /**
     * @return Returns the symbol.
     */
    public String symbol() {
        return symbol;
    }

    /**
     * @return Returns the message.
     */
    public String message() {
        return message;
    }
    /**
     * 通过枚举<code>code</code>获得枚举
     *
     * @param code
     * @return CoinType
     */
    public static CoinTypeEnum getByCode(String code) {
        for (CoinTypeEnum _enum : values()) {
            if (_enum.getCode().equals(code)) {
                return _enum;
            }
        }
        return CoinTypeEnum.NULS;
    }


    /**
     * 通过枚举<code>code</code>message （币种缩写）
     *
     * @param code
     * @return CoinType
     */
    public static String getMessageByCode(String code) {
        for (CoinTypeEnum _enum : values()) {
            if (_enum.getCode().equals(code)) {
                return _enum.symbol;
            }
        }
        return CoinTypeEnum.NULS.symbol;
    }


    /**
     *
     *
     * @param code
     * @return CoinType
     */
    public static int getDecimalByCode(String code) {
        for (CoinTypeEnum _enum : values()) {
            if (_enum.getCode().equals(code)) {
                return _enum.decimal;
            }
        }
        return 8;
    }
}
