package com.nuls.naboxpro.enums;

public enum  TransferType {
    NULS_TO_NULS("NULS_TO_NULS", "NULS转账到NULS"),
    NULS_TO_NERVE("NULS_TO_NERVE", "NULS转账到NULS"),
    NVT_TO_NULS("NVT_TO_NULS", "NVT转账到NULS"),
    NVT_TO_NVT("NVT_TO_NVT", "NVT转账到NVT"),
    NVT_TO_ETHS("NVT_TO_ETHS", "NVT转账到以太坊系"),
    ETHS_TO_NVT("ETHS_TO_NVT", "以太坊系转账到NVT"),
    ETHS_TO_ETHS("ETHS_TO_ETHS", "以太坊系转账到以太坊系");



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

    private TransferType(String code, String message) {
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
