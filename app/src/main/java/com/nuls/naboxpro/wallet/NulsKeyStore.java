package com.nuls.naboxpro.wallet;

import java.io.Serializable;

/**
 * nuls的keystore
 */
public class NulsKeyStore  implements  Serializable{
    /**
     * 钱包地址
     */
    private String address;
    /**
     * 加密私钥
     */
    private String encryptedPrivateKey;
    /**
     * 钱包别名
     */
    private String alias ="uuuuuuu";

    /**
     * 公钥
     */
    private String pubKey;
    /**
     * TODO 不会赋值，只是用于保持和以前的Nuls keyStore保持一致
     *
     */
    private String prikey;

    public NulsKeyStore() {
    }

    public NulsKeyStore(String address, String encryptedPrivateKey) {
        this.address = address;
        this.encryptedPrivateKey = encryptedPrivateKey;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEncryptedPrivateKey() {
        return encryptedPrivateKey;
    }

    public void setEncryptedPrivateKey(String encryptedPrivateKey) {
        this.encryptedPrivateKey = encryptedPrivateKey;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public String getPrikey() {
        return prikey;
    }

    public void setPrikey(String prikey) {
        this.prikey = prikey;
    }
}
