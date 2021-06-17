package com.nuls.naboxpro.enums;

public enum MoneyTypeEnum {


    USD("USD", "USD"),
    RMB("RMB","人民币");







    String code;
    String message;

    public String getCode() {
        return code;
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

    private MoneyTypeEnum(String code, String message) {
        this.code = code;

        this.message = message;
    }
    /**
     * @return Returns the code.
     */
    public String code() {
        return code;
    }

    /**
     * @return Returns the message.
     */
    public String message() {
        return message;
    }

}
