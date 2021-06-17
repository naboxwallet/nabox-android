package com.nuls.naboxpro.entity;

import java.util.List;

public class TxListBean {


    /**
     * records : [{"id":440,"hash":"10bd3c3df0731d7d3c74beef2843022adb32a148409e9e1c00f9ea8969b42a07","address":null,"chainId":2,"assetId":1,"contractAddress":null,"amount":"100000000","transType":1,"symbol":"NULS","decimals":8,"locked":0,"froms":"tNULSeBaMtyRv6wa4Pzg44DrhmLSEoLWgMXudn","tos":"tNULSeBaMtyRv6wa4Pzg44DrhmLSEoLWgMXudn","status":1,"createTime":"1981-10-07 10:59:52"},{"id":439,"hash":"10bd3c3df0731d7d3c74beef2843022adb32a148409e9e1c00f9ea8969b42a07","address":null,"chainId":2,"assetId":1,"contractAddress":null,"amount":"100100000","transType":-1,"symbol":"NULS","decimals":8,"locked":0,"froms":"tNULSeBaMtyRv6wa4Pzg44DrhmLSEoLWgMXudn","tos":"tNULSeBaMtyRv6wa4Pzg44DrhmLSEoLWgMXudn","status":1,"createTime":"1981-10-07 10:59:52"}]
     * total : 2
     * size : 20
     * current : 1
     * searchCount : true
     * pages : 1
     */

    private int total;
    private int size;
    private int current;
    private boolean searchCount;
    private int pages;
    private List<RecordsBean> records;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public boolean isSearchCount() {
        return searchCount;
    }

    public void setSearchCount(boolean searchCount) {
        this.searchCount = searchCount;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<RecordsBean> getRecords() {
        return records;
    }

    public void setRecords(List<RecordsBean> records) {
        this.records = records;
    }

    public static class RecordsBean {
        /**
         * id : 440
         * hash : 10bd3c3df0731d7d3c74beef2843022adb32a148409e9e1c00f9ea8969b42a07
         * address : null
         * chainId : 2
         * assetId : 1
         * contractAddress : null
         * amount : 100000000
         * transType : 1
         * symbol : NULS
         * decimals : 8
         * locked : 0
         * froms : tNULSeBaMtyRv6wa4Pzg44DrhmLSEoLWgMXudn
         * tos : tNULSeBaMtyRv6wa4Pzg44DrhmLSEoLWgMXudn
         * status : 1
         * createTime : 1981-10-07 10:59:52
         */

        private Long id;
        private String hash;
        private Object address;
        private int chainId;
        private int assetId;
        private Object contractAddress;
        private String amount;
        private int transType;
        private String symbol;
        private int decimals;
        private int locked;
        private String froms;
        private String tos;
        private int status;
        private String createTime;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public Object getAddress() {
            return address;
        }

        public void setAddress(Object address) {
            this.address = address;
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

        public int getLocked() {
            return locked;
        }

        public void setLocked(int locked) {
            this.locked = locked;
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
    }
}
