package com.nuls.naboxpro.net;

/**
 * created by yange
 */
public interface Api {

    /**
     * 新版本Nabox 测试环境
     */

    String IMEI = "test";


    /**
     * 是否debug
     * 如果为true测试环境
     * false为正式环境
     */
    boolean ISDEBUG = false;

    int NULS_CHAIN_ID = ISDEBUG ? 2 : 1;
    int NERVE_CHAIN_ID = ISDEBUG ? 5 : 9;

    //    String BASE_URL = "http://nabox_api.zhoulijun.top/nabox-api/";//测试地址
//    String BASE_URL = "http://api.v2.nabox.io/nabox-api/";//正式地址

    String BASE_URL = ISDEBUG?"http://nabox_api.zhoulijun.top/nabox-api/":"http://api.v2.nabox.io/nabox-api/";

    String PUBLIC_SERVICE = "https://public1.nuls.io/";


    /**
     * 密码匹配数字
     */
    public static final String MATCH_NUMBER = ".*[0-9]+.*";
    /**
     * 密码匹配小写英文
     */
    public static final String MATCH_LOWERCASE_WORD = ".*[a-z]+.*";
    /**
     * 密码强度大写英文
     */
    public static final String MATCH_UPERCASE_WORD = ".*[A-Z]+.*";

    public static final String MATCH_SPECIAL_WORD = ".*[^0-9a-zA-Z]+.*";


    String IMAGE_HOST_URL = "https://nuls-cf.oss-us-west-1.aliyuncs.com/icon/";


    /**
     * 同步钱包
     */
    String WALLET_SYNC = "wallet/sync";

    /**
     * 获取汇率
     */
    String GET_EXCHANGE = "api/usd/exchange";
    /**
     * 同步钱包
     */
    String CHAIN_CONFIG = "chain/config";


    /**
     * 查询账户USD总价格
     */
    String WALLET_PRICE = "wallet/price";

    /**
     * 查询地址资产列表/wallet/address/assets
     */
    String GET_ASSETS = "wallet/address/assets";


    /**
     * 查询地址链主资产
     */
    String GET_MAIN_ASSETS = "wallet/chain/main";

    /**
     * 查询账户每条链USD总价格列
     */
    String CHAIN_PRICE = "wallet/chain/price";


    /**
     * 获取资产详情
     */
    String GET_ASSETS_DETAIL = "wallet/address/asset";


    /**
     * 获取交易列表
     */
    String GET_COIN_LIST = "tx/coin/list";

    String GET_CHAIN_CONFIG = "api/chain/config";


    /**
     * 广播链内交易
     */
    String TX_TRANSFER = "tx/transfer";

    /**
     * 交易详情
     */
    String TX_INFO = "tx/coin/info";

    /**
     * nvt异构跨链手续费计算
     */
    String NVT_CROSS_FEE = "asset/nvt/cross/price";


    /**
     * 估算gas消耗
     */
    String CALL_GASLIMIT = "contract/imputed/call/gas";

    /**
     * gas价格
     */
    String GAS_PRICE = "asset/gasprice";

    /**
     * 跨链交易授权
     */
    String CROSS_AUTHER = "tx/cross/author";


    /**
     * 跨链交易授权
     */
    String CROSS_TRANSFER = "tx/cross/transfer";


    /**
     * 关注/取消关注资产
     */
    String FACUS_ASSETS = "wallet/address/asset/focus";


    /**
     * 查询异构链资产在nerve链上对应资产的信息
     */
    String CROSS_CHAIN_INFO = "asset/nerve/chain/info";

    /**
     * 搜索资产
     */
    String SEARCH_ASSET = "asset/query";


    /**
     * 获取多个账户usd总价格
     */
    String GET_WALLET_LIST_PRICE = "wallet/prices";


    /**
     * 获取推荐的dapp
     */
    String GET_RECOMMEND_DAPP = "dapp/recommend";

    /**
     * 根据chain获取dapp
     */
    String GET_DAPP_CHAIN = "dapp/chain";


    /**
     * 搜索Dapp
     */
    String SEARCH_DAPP = "dapp/name";
    /**
     * 获取dapp页面banner
     */
    String GET_BANNER = "dapp/banner";


    /**
     * 获取网络状态
     */
    String NETWORK_STATUS = "api/sync/info";

    /**
     * 刷新钱包
     */
    String WALLET_REFRESH = "wallet/refresh";


    /**
     * 以太坊接口透传
     */
    String ETH_CALL = "api/ethCall";

    /**
     * 版本更新
     */
    String  CHECK_UPDATE = "api/version/best";

}
