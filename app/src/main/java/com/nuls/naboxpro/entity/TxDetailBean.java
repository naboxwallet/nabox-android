package com.nuls.naboxpro.entity;

import java.io.Serializable;

public class TxDetailBean implements Serializable {


    /**
     * tx : {"id":"10bd3c3df0731d7d3c74beef2843022adb32a148409e9e1c00f9ea8969b42a07","chain":"NULS","height":425287,"type":2,"froms":"tNULSeBaMtyRv6wa4Pzg44DrhmLSEoLWgMXudn","tos":"tNULSeBaMtyRv6wa4Pzg44DrhmLSEoLWgMXudn","fee":"0.001 NULS","remark":null,"status":1,"createTime":"1981-10-07 10:59:52","amount":"100000000","transType":1,"symbol":"NULS","decimals":8}
     * crossTx : null
     */

    private TxBean tx;
    private CrossTxBean crossTx;

    public TxBean getTx() {
        return tx;
    }

    public void setTx(TxBean tx) {
        this.tx = tx;
    }

    public CrossTxBean getCrossTx() {
        return crossTx;
    }

    public void setCrossTx(CrossTxBean crossTx) {
        this.crossTx = crossTx;
    }


    public static class  CrossTxBean implements  Serializable{
        /**
         *   //当交易未跨链交易时，crossTx有值  "txHash": "3e77f6270709026e271f9d7",
         *   //转出链交易hash  "fromChain": "NULS",   //转出链  "toChain": "NERVE",    //转入链  "nerveTxHash": null,
         *   //只有通过nabox发起的异构跨链交易，crossTxHex不为空时，后台再本链交易确认后，自动在nerve链广播crossTxHex成功后，nerveTxHash才会赋值。 
         *    "crossTxHash": "3e77f9d79c204ffc5fee05c2",  //转入链交易hash  "fromAddress": "tNULSeBaMt9Tf6VvfYfvUFGVqdiyPqFLfQg9La", //转出地址  
         *   "toAddress": "TNVTdTSPTXQudD2FBSefpQRkXTyhhtSjyEVAF",   //转入地址  "status": 4   //跨链交易状态                  0. 跨链交易本链未确认
         *   1. 跨链交易本链已确认
         *   2. 跨链交易NERVE链已广播交易待确认
         *   3. 跨链交易NERVE链广播失败
         *   4. 跨链交易目标链已确认                  5. 跨链交易失败
         */

        String txHash;
        String fromChain;
        String toChain;
        String nerveTxHash;
        String crossTxHash;
        String fromAddress;
        String toAddress;
        int status;

        public String getTxHash() {
            return txHash;
        }

        public void setTxHash(String txHash) {
            this.txHash = txHash;
        }

        public String getFromChain() {
            return fromChain;
        }

        public void setFromChain(String fromChain) {
            this.fromChain = fromChain;
        }

        public String getToChain() {
            return toChain;
        }

        public void setToChain(String toChain) {
            this.toChain = toChain;
        }

        public String getNerveTxHash() {
            return nerveTxHash;
        }

        public void setNerveTxHash(String nerveTxHash) {
            this.nerveTxHash = nerveTxHash;
        }

        public String getCrossTxHash() {
            return crossTxHash;
        }

        public void setCrossTxHash(String crossTxHash) {
            this.crossTxHash = crossTxHash;
        }

        public String getFromAddress() {
            return fromAddress;
        }

        public void setFromAddress(String fromAddress) {
            this.fromAddress = fromAddress;
        }

        public String getToAddress() {
            return toAddress;
        }

        public void setToAddress(String toAddress) {
            this.toAddress = toAddress;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }


    public static class TxBean implements  Serializable{
        /**
         * id : 10bd3c3df0731d7d3c74beef2843022adb32a148409e9e1c00f9ea8969b42a07
         * chain : NULS
         * height : 425287
         * type : 2
         * froms : tNULSeBaMtyRv6wa4Pzg44DrhmLSEoLWgMXudn
         * tos : tNULSeBaMtyRv6wa4Pzg44DrhmLSEoLWgMXudn
         * fee : 0.001 NULS
         * remark : null
         * status : 1
         * createTime : 1981-10-07 10:59:52
         * amount : 100000000
         * transType : 1
         * symbol : NULS
         * decimals : 8
         */

        private String id;
        private String chain;
        private int height;
        private int type;
        private String froms;
        private String tos;
        private String fee;
        private String remark;
        private int status;
        private String createTime;
        private String amount;
        private int transType;
        private String symbol;
        private int decimals;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getChain() {
            return chain;
        }

        public void setChain(String chain) {
            this.chain = chain;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getFroms() {
            return froms;
        }

        public void setFroms(String froms) {
            this.froms = froms;
        }

        public String getTos() {
            return tos;
        }

        public void setTos(String tos) {
            this.tos = tos;
        }

        public String getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = fee;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public int getTransType() {
            return transType;
        }

        public void setTransType(int transType) {
            this.transType = transType;
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
    }
}
