package com.nuls.naboxpro.entity;

import java.io.Serializable;

public class HeterogeneousInfoBean implements Serializable {


    private int heterogeneousChainId;
    private String heterogeneousChainMultySignAddress;
    private String contractAddress;
    private String chainName;
    private boolean token;

    public int getHeterogeneousChainId() {
        return heterogeneousChainId;
    }

    public void setHeterogeneousChainId(int heterogeneousChainId) {
        this.heterogeneousChainId = heterogeneousChainId;
    }

    public String getHeterogeneousChainMultySignAddress() {
        return heterogeneousChainMultySignAddress;
    }

    public void setHeterogeneousChainMultySignAddress(String heterogeneousChainMultySignAddress) {
        this.heterogeneousChainMultySignAddress = heterogeneousChainMultySignAddress;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getChainName() {
        return chainName;
    }

    public void setChainName(String chainName) {
        this.chainName = chainName;
    }

    public boolean isToken() {
        return token;
    }

    public void setToken(boolean token) {
        this.token = token;
    }
}
