package com.nuls.naboxpro.entity;

import java.util.List;

public class WalletSyncRequestEntity {


    String pubKey;
    String terminal;
    String tag;
    String language;
    String type;
    List<ChainEntity> addressList;

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ChainEntity> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<ChainEntity> addressList) {
        this.addressList = addressList;
    }
}
