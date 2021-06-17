package com.nuls.naboxpro.entity;

import java.util.List;

public class AssetsDetailEntity {


    /**
     * address : tNULSeBaMrTBVU55fgFSBKTVk9Tgu2qsuNZxj2
     * chain : NULS
     * chainId : 2
     * assetId : 1
     * contractAddress : null
     * symbol : NULS
     * decimals : 8
     * total : 0
     * locked : 0
     * balance : 0
     * nonce : null
     * usdPrice : 0
     * icon : https://nuls-cf.oss-us-west-1.aliyuncs.com/icon/NULS.png
     * configType : 1
     * source : 7
     * nulsCross : false
     * heterogeneousList : [{"heterogeneousChainId":101,"heterogeneousChainMultySignAddress":"0x7d759a3330cec9b766aa4c889715535eed3c0484","contractAddress":"0xae7fccff7ec3cf126cd96678adae83a2b303791c","chainName":"Ethereum","token":true},{"heterogeneousChainId":102,"heterogeneousChainMultySignAddress":"0xf7915d4de86b856f3e51b894134816680bf09eee","contractAddress":"0x72755f739b56ef98bda25e2622c63add229dec01","chainName":"BSC","token":true},{"heterogeneousChainId":103,"heterogeneousChainMultySignAddress":"0xb339211438dcbf3d00d7999ad009637472fc72b3","contractAddress":"0x74a163fcd791ec7aab2204ffabf1a1dfb8854883","chainName":"Heco","token":true}]
     */

    private String address;
    private String chain;
    private int chainId;
    private int assetId;
    private Object contractAddress;
    private String symbol;
    private int decimals;
    private String total;
    private String locked;
    private String balance;
    private Object nonce;
    private String usdPrice;
    private String icon;
    private int configType;
    private int source;
    private boolean nulsCross;
    private List<HeterogeneousInfoBean> heterogeneousList;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getChain() {
        return chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }

    public int getChainId() {
        return chainId;
    }

    public void setChainId(int chainId) {
        this.chainId = chainId;
    }

    public int getAssetId() {
        return assetId;
    }

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

    public Object getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(Object contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public Object getNonce() {
        return nonce;
    }

    public void setNonce(Object nonce) {
        this.nonce = nonce;
    }

    public String getUsdPrice() {
        return usdPrice;
    }

    public void setUsdPrice(String usdPrice) {
        this.usdPrice = usdPrice;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getConfigType() {
        return configType;
    }

    public void setConfigType(int configType) {
        this.configType = configType;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public boolean isNulsCross() {
        return nulsCross;
    }

    public void setNulsCross(boolean nulsCross) {
        this.nulsCross = nulsCross;
    }

    public List<HeterogeneousInfoBean> getHeterogeneousList() {
        return heterogeneousList;
    }

    public void setHeterogeneousList(List<HeterogeneousInfoBean> heterogeneousList) {
        this.heterogeneousList = heterogeneousList;
    }
}
