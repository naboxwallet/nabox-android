package com.nuls.naboxpro.enums;

import android.text.TextUtils;

public enum LanguageEnum {

    EN("EN","English"),

    CHS("CHS","中文"),
    ;

    /**
     * 枚举值
     */
    private final String code;



    /**
     * 枚举描述
     */
    private final String message;


    /**
     * 构造一个<code>CoinType</code>枚举对象
     *
     * @param code
     * @param message
     */
    private LanguageEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * @return Returns the code.
     */
    public String code() {
        return code;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
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
    public static LanguageEnum getByCode(String code) {
        if (TextUtils.isEmpty(code)) {
            return CHS;
        }
        for (LanguageEnum _enum : values()) {
            if (_enum.getCode().equals(code)) {
                return _enum;
            }
        }
        return CHS;
    }

}
