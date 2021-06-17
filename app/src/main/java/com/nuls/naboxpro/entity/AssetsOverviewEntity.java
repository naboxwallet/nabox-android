package com.nuls.naboxpro.entity;

public class AssetsOverviewEntity {


    /**
     *  链名
     */
    String chain;

    /**
     * 链资产数量
     */
    String price;
    /**
     * 链图标
     */
    String icon;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getChain() {
        return chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
