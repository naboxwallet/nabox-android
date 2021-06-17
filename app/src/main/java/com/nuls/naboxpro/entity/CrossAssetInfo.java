package com.nuls.naboxpro.entity;

public class CrossAssetInfo {



    String symbol;
    Number chainId;
    Number assetId;
    Number decimals;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Number getChainId() {
        return chainId;
    }

    public void setChainId(Number chainId) {
        this.chainId = chainId;
    }

    public Number getAssetId() {
        return assetId;
    }

    public void setAssetId(Number assetId) {
        this.assetId = assetId;
    }

    public Number getDecimals() {
        return decimals;
    }

    public void setDecimals(Number decimals) {
        this.decimals = decimals;
    }
}
