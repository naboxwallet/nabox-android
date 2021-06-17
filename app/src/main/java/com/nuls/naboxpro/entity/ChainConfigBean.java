package com.nuls.naboxpro.entity;

import java.util.List;

public class ChainConfigBean {


    /**
     * id : 52
     * chain : NULS
     * prefix : tNULS
     * chainId : 2
     * tokenFlag : true
     * nulsFlag : true
     * intro : null
     * icon : null
     * browseUrl : null
     * apiUrl : http://beta.api.nuls.io/jsonrpc
     * psUrl : http://beta.public1.nuls.io/jsonrpc
     * configs : {"feeAddress":"tNULSeBaMomrBpDYJrfm49LcJ2nJKrNT5TEdam","destroyAddress":"tNULSeBaMhZnRteniCy3UZqPjTbnWKBPHX1a5d"}
     * mainAsset : {"id":15719,"chain":"NULS","chainId":2,"assetId":1,"decimals":8,"assetName":"NULS","symbol":"NULS","configType":1,"contractAddress":null,"source":7,"icon":"https://nuls-cf.oss-us-west-1.aliyuncs.com/icon/NULS.png","nulsCross":true,"heterogeneousList":null}
     * assets : [{"id":15719,"chain":"NULS","chainId":2,"assetId":1,"decimals":8,"assetName":"NULS","symbol":"NULS","configType":1,"contractAddress":null,"source":7,"icon":"https://nuls-cf.oss-us-west-1.aliyuncs.com/icon/NULS.png","nulsCross":true,"heterogeneousList":[{"heterogeneousChainId":101,"heterogeneousChainMultySignAddress":"0x7d759a3330cec9b766aa4c889715535eed3c0484","contractAddress":"0xae7fccff7ec3cf126cd96678adae83a2b303791c","chainName":"Ethereum","token":true},{"heterogeneousChainId":102,"heterogeneousChainMultySignAddress":"0xf7915d4de86b856f3e51b894134816680bf09eee","contractAddress":"0x72755f739b56ef98bda25e2622c63add229dec01","chainName":"BSC","token":true},{"heterogeneousChainId":103,"heterogeneousChainMultySignAddress":"0xb339211438dcbf3d00d7999ad009637472fc72b3","contractAddress":"0x74a163fcd791ec7aab2204ffabf1a1dfb8854883","chainName":"Heco","token":true}]}]
     */

    private int id;
    private String chain;
    private String prefix;
    private int chainId;
    private boolean tokenFlag;
    private boolean nulsFlag;
    private Object intro;
    private Object icon;
    private Object browseUrl;
    private String apiUrl;
    private String psUrl;
    private ConfigsBean configs;
    private MainAssetBean mainAsset;
    private List<AssetsEntity> assets;
    private int nativeId;

    public int getNativeId() {
        return nativeId;
    }

    public void setNativeId(int nativeId) {
        this.nativeId = nativeId;
    }

    public List<AssetsEntity> getAssets() {
        return assets;
    }

    public void setAssets(List<AssetsEntity> assets) {
        this.assets = assets;
    }

    /**
     * 关注状态 默认 0  锁定，不允许用户关注或取关   1 已关注（可取消关注）  2 未关注
     */
    int fellowState = 0;

    public int getFellowState() {
        return fellowState;
    }

    public void setFellowState(int fellowState) {
        this.fellowState = fellowState;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChain() {
        return chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getChainId() {
        return chainId;
    }

    public void setChainId(int chainId) {
        this.chainId = chainId;
    }

    public boolean isTokenFlag() {
        return tokenFlag;
    }

    public void setTokenFlag(boolean tokenFlag) {
        this.tokenFlag = tokenFlag;
    }

    public boolean isNulsFlag() {
        return nulsFlag;
    }

    public void setNulsFlag(boolean nulsFlag) {
        this.nulsFlag = nulsFlag;
    }

    public Object getIntro() {
        return intro;
    }

    public void setIntro(Object intro) {
        this.intro = intro;
    }

    public Object getIcon() {
        return icon;
    }

    public void setIcon(Object icon) {
        this.icon = icon;
    }

    public Object getBrowseUrl() {
        return browseUrl;
    }

    public void setBrowseUrl(Object browseUrl) {
        this.browseUrl = browseUrl;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getPsUrl() {
        return psUrl;
    }

    public void setPsUrl(String psUrl) {
        this.psUrl = psUrl;
    }

    public ConfigsBean getConfigs() {
        return configs;
    }

    public void setConfigs(ConfigsBean configs) {
        this.configs = configs;
    }

    public MainAssetBean getMainAsset() {
        return mainAsset;
    }

    public void setMainAsset(MainAssetBean mainAsset) {
        this.mainAsset = mainAsset;
    }



    public static class ConfigsBean {
        /**
         * feeAddress : tNULSeBaMomrBpDYJrfm49LcJ2nJKrNT5TEdam
         * destroyAddress : tNULSeBaMhZnRteniCy3UZqPjTbnWKBPHX1a5d
         */

        private String feeAddress;
        private String destroyAddress;

        public String getFeeAddress() {
            return feeAddress;
        }

        public void setFeeAddress(String feeAddress) {
            this.feeAddress = feeAddress;
        }

        public String getDestroyAddress() {
            return destroyAddress;
        }

        public void setDestroyAddress(String destroyAddress) {
            this.destroyAddress = destroyAddress;
        }
    }

    public static class MainAssetBean {
        /**
         * id : 15719
         * chain : NULS
         * chainId : 2
         * assetId : 1
         * decimals : 8
         * assetName : NULS
         * symbol : NULS
         * configType : 1
         * contractAddress : null
         * source : 7
         * icon : https://nuls-cf.oss-us-west-1.aliyuncs.com/icon/NULS.png
         * nulsCross : true
         * heterogeneousList : null
         */

        private int id;
        private String chain;
        private int chainId;
        private int assetId;
        private int decimals;
        private String assetName;
        private String symbol;
        private int configType;
        private Object contractAddress;
        private int source;
        private String icon;
        private boolean nulsCross;
        private Object heterogeneousList;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public int getDecimals() {
            return decimals;
        }

        public void setDecimals(int decimals) {
            this.decimals = decimals;
        }

        public String getAssetName() {
            return assetName;
        }

        public void setAssetName(String assetName) {
            this.assetName = assetName;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public int getConfigType() {
            return configType;
        }

        public void setConfigType(int configType) {
            this.configType = configType;
        }

        public Object getContractAddress() {
            return contractAddress;
        }

        public void setContractAddress(Object contractAddress) {
            this.contractAddress = contractAddress;
        }

        public int getSource() {
            return source;
        }

        public void setSource(int source) {
            this.source = source;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public boolean isNulsCross() {
            return nulsCross;
        }

        public void setNulsCross(boolean nulsCross) {
            this.nulsCross = nulsCross;
        }

        public Object getHeterogeneousList() {
            return heterogeneousList;
        }

        public void setHeterogeneousList(Object heterogeneousList) {
            this.heterogeneousList = heterogeneousList;
        }
    }

//    public static class AssetsBean {
//        /**
//         * id : 15719
//         * chain : NULS
//         * chainId : 2
//         * assetId : 1
//         * decimals : 8
//         * assetName : NULS
//         * symbol : NULS
//         * configType : 1
//         * contractAddress : null
//         * source : 7
//         * icon : https://nuls-cf.oss-us-west-1.aliyuncs.com/icon/NULS.png
//         * nulsCross : true
//         * heterogeneousList : [{"heterogeneousChainId":101,"heterogeneousChainMultySignAddress":"0x7d759a3330cec9b766aa4c889715535eed3c0484","contractAddress":"0xae7fccff7ec3cf126cd96678adae83a2b303791c","chainName":"Ethereum","token":true},{"heterogeneousChainId":102,"heterogeneousChainMultySignAddress":"0xf7915d4de86b856f3e51b894134816680bf09eee","contractAddress":"0x72755f739b56ef98bda25e2622c63add229dec01","chainName":"BSC","token":true},{"heterogeneousChainId":103,"heterogeneousChainMultySignAddress":"0xb339211438dcbf3d00d7999ad009637472fc72b3","contractAddress":"0x74a163fcd791ec7aab2204ffabf1a1dfb8854883","chainName":"Heco","token":true}]
//         */
//
//        private int id;
//        private String chain;
//        private int chainId;
//
//        public int getFollowState() {
//            return followState;
//        }
//
//        public void setFollowState(int followState) {
//            this.followState = followState;
//        }
//
//
//        private int assetId;
//        private int decimals;
//        private String assetName;
//        private String symbol;
//        private int configType;
//        private String contractAddress;
//        private int source;
//        private String icon;
//        private boolean nulsCross;
//        private List<HeterogeneousListBean> heterogeneousList;
//
//        public int getId() {
//            return id;
//        }
//
//        public void setId(int id) {
//            this.id = id;
//        }
//
//        public String getChain() {
//            return chain;
//        }
//
//        public void setChain(String chain) {
//            this.chain = chain;
//        }
//
//        public int getChainId() {
//            return chainId;
//        }
//
//        public void setChainId(int chainId) {
//            this.chainId = chainId;
//        }
//
//        public int getAssetId() {
//            return assetId;
//        }
//
//        public void setAssetId(int assetId) {
//            this.assetId = assetId;
//        }
//
//        public int getDecimals() {
//            return decimals;
//        }
//
//        public void setDecimals(int decimals) {
//            this.decimals = decimals;
//        }
//
//        public String getAssetName() {
//            return assetName;
//        }
//
//        public void setAssetName(String assetName) {
//            this.assetName = assetName;
//        }
//
//        public String getSymbol() {
//            return symbol;
//        }
//
//        public void setSymbol(String symbol) {
//            this.symbol = symbol;
//        }
//
//        public int getConfigType() {
//            return configType;
//        }
//
//        public void setConfigType(int configType) {
//            this.configType = configType;
//        }
//
//        public String getContractAddress() {
//            return contractAddress;
//        }
//
//        public void setContractAddress(String contractAddress) {
//            this.contractAddress = contractAddress;
//        }
//
//        public int getSource() {
//            return source;
//        }
//
//        public void setSource(int source) {
//            this.source = source;
//        }
//
//        public String getIcon() {
//            return icon;
//        }
//
//        public void setIcon(String icon) {
//            this.icon = icon;
//        }
//
//        public boolean isNulsCross() {
//            return nulsCross;
//        }
//
//        public void setNulsCross(boolean nulsCross) {
//            this.nulsCross = nulsCross;
//        }
//
//        public List<HeterogeneousListBean> getHeterogeneousList() {
//            return heterogeneousList;
//        }
//
//        public void setHeterogeneousList(List<HeterogeneousListBean> heterogeneousList) {
//            this.heterogeneousList = heterogeneousList;
//        }
//
//        public static class HeterogeneousListBean {
//            /**
//             * heterogeneousChainId : 101
//             * heterogeneousChainMultySignAddress : 0x7d759a3330cec9b766aa4c889715535eed3c0484
//             * contractAddress : 0xae7fccff7ec3cf126cd96678adae83a2b303791c
//             * chainName : Ethereum
//             * token : true
//             */
//
//            private int heterogeneousChainId;
//            private String heterogeneousChainMultySignAddress;
//            private String contractAddress;
//            private String chainName;
//            private boolean token;
//
//            public int getHeterogeneousChainId() {
//                return heterogeneousChainId;
//            }
//
//            public void setHeterogeneousChainId(int heterogeneousChainId) {
//                this.heterogeneousChainId = heterogeneousChainId;
//            }
//
//            public String getHeterogeneousChainMultySignAddress() {
//                return heterogeneousChainMultySignAddress;
//            }
//
//            public void setHeterogeneousChainMultySignAddress(String heterogeneousChainMultySignAddress) {
//                this.heterogeneousChainMultySignAddress = heterogeneousChainMultySignAddress;
//            }
//
//            public String getContractAddress() {
//                return contractAddress;
//            }
//
//            public void setContractAddress(String contractAddress) {
//                this.contractAddress = contractAddress;
//            }
//
//            public String getChainName() {
//                return chainName;
//            }
//
//            public void setChainName(String chainName) {
//                this.chainName = chainName;
//            }
//
//            public boolean isToken() {
//                return token;
//            }
//
//            public void setToken(boolean token) {
//                this.token = token;
//            }
//        }
//    }
}
