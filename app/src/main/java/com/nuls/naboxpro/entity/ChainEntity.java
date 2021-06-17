package com.nuls.naboxpro.entity;

public class ChainEntity {

    /**
     * 所属链：NULS、NERVE、Ethereum、BSC
     */
    String chain;
    /**
     * 地址
     */
    String address;

    public String getChain() {
        return chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ChainEntity(String chain,String address){
        this.chain = chain;
        this.address = address;
    }

}
