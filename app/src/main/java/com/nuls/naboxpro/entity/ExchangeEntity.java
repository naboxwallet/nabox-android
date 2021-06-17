package com.nuls.naboxpro.entity;

public class ExchangeEntity {


    /**
     * 美元兑btc汇率
     */
    double BTC;
    /**
     * 美元兑人民币汇率
     */
    double CNY;

    public double getBTC() {
        return BTC;
    }

    public void setBTC(double BTC) {
        this.BTC = BTC;
    }

    public double getCNY() {
        return CNY;
    }

    public void setCNY(double CNY) {
        this.CNY = CNY;
    }
}
