package com.nuls.naboxpro.enums;



import com.nuls.naboxpro.common.UserDao;

import java.util.ArrayList;
import java.util.List;

/**
 * created by yange on 2020/3/19 0019
 * 描述：交易类型
 */
public enum TxTypeEnum {
    //这里保证code和后台一直就ok，message可以按照需要自己定义
    COIN_BASE (1, "出块奖励","COIN BASE"),

    TRANSFE(2,"转账","TRANSFE"),

    ACCOUNT_ALIAS (3,"设置账户别名","ACCOUNT ALIAS"),

    REGISTER_AGENT (4,"新建共识接点","REGISTER AGENT"),

    DEPOSIT  (5,"委托参与共识","DEPOSIT"),

    CANCEL_DEPOSIT (6,"取消委托","CANCEL DEPOSIT"),

    YELLOW_PUNISH  (7,"黄牌警告","YELLOW PUNISH"),

    RED_PUNISH  (8,"红牌警告","RED PUNISH"),

    STOP_AGENT   (9,"注销共识节点","STOP AGENT"),

    CROSS_CHAIN  (10,"跨链转账","CROSS CHAIN TRANSFE"),

    REGISTER_CHAIN_AND_ASSET   (11,"注册链","REGISTER CHAIN AND ASSET"),

    DESTROY_CHAIN_AND_ASSET  (12,"注销链","DESTROY CHAIN AND ASSET"),

    ADD_ASSET_TO_CHAIN   (13,"为链新增资产","ADD ASSET TO CHAIN"),

    REMOVE_ASSET_FROM_CHAIN  (14,"删除链上资产","REMOVE ASSET FROM CHAIN"),

    CREATE_CONTRACT (15,"创建智能合约","CREATE CONTRACT"),

    CALL_CONTRACT  (16,"调用智能合约","CALL CONTRACT"),

    DELETE_CONTRACT  (17,"删除智能合约","DELETE CONTRACT"),

    CONTRACT_TRANSFER  (18,"合约内部转账","CONTRACT TRANSFER"),

    CONTRACT_RETURN_GAS (19,"合约执行手续费返还","CONTRACT RETURN GAS"),

    CONTRACT_CREATE_AGENT (20,"合约新建共识节点","CONTRACT CREATE AGENT"),

    CONTRACT_DEPOSIT   (21,"合约委托参与共识","CONTRACT DEPOSIT"),

    CONTRACT_CANCEL_DEPOSIT (22,"合约取消委托共识","CONTRACT CANCEL DEPOSIT"),

    CONTRACT_STOP_AGENT  (23,"合约注销共识节点","CONTRACT STOP AGENT"),

    VERIFIER_CHANGE (24,"验证人变更","VERIFIER CHANGE"),

    VERIFIER_INIT (25,"验证人初始化","VERIFIER INIT"),

    CONTRACT_TOKEN_CROSS_TRANSFER (26,"合约token跨链转账","CONTRACT TOKEN CROSS TRANSFER"),

    LEDGER_ASSET_REG_TRANSFER (27,"账本链内资产注册登记","LEDGER ASSET REG TRANSFER"),

    APPEND_AGENT_DEPOSIT (28,"追加节点保证金","APPEND AGENT DEPOSIT"),

    REDUCE_AGENT_DEPOSIT (29,"撤销节点保证金","REDUCE AGENT DEPOSIT"),

    QUOTATION (30,"喂价交易","QUOTATION"),

    FINAL_QUOTATION (31,"最终喂价交易","FINAL QUOTATION"),

    BATCH_WITHDRAW (32,"批量退出staking交易","BATCH WITHDRAW"),

    BATCH_STAKING_MERGE (33,"合并活期staking记录","BATCH STAKING MERGE"),

    COIN_TRADING (228,"创建交易对","COIN TRADING"),



//    /**
//     * 创建交易对
//     */
//    public static final int COIN_TRADING = 228;
//
//    /**
//     * 挂单委托
//     */
//    public static final int TRADING_ORDER = 229;
//
//    /**
//     * 挂单撤销
//     */
//    public static final int TRADING_ORDER_CANCEL = 230;
//
//    /**
//     * 挂单成交
//     */
//    public static final int TRADING_DEAL = 231;
//
//    /**
//     * 修改交易对
//     */
//    public static final int EDIT_COIN_TRADING = 232;
//
//    /**
//     * 撤单交易确认
//     */
//    public static final int ORDER_CANCEL_CONFIRM = 233;
//
//    /**
//     * 确认 虚拟银行变更交易
//     */
//    public static final int CONFIRM_CHANGE_VIRTUAL_BANK = 40;
//
//    /**
//     * 虚拟银行变更交易
//     */
//    public static final int CHANGE_VIRTUAL_BANK = 41;



    RECHARGE (42,"链内充值交易","RECHARGE"),

    WITHDRAWAL (43,"提现交易","WITHDRAWAL"),

    CONFIRM_WITHDRAWAL (44,"确认提现成功状态交易","CONFIRM WITHDRAWAL"),

    PROPOSAL (45,"发起提案交易","PROPOSAL"),

    VOTE_PROPOSAL (46,"对提案进行投票交易","VOTE PROPOSAL"),

    DISTRIBUTION_FEE (47,"异构链交易手续费补贴","DISTRIBUTION FEE"),

    INITIALIZE_HETEROGENEOUS (48,"虚拟银行初始化异构链","INITIALIZE HETEROGENEOUS"),

    HETEROGENEOUS_CONTRACT_ASSET_REG_PENDING (49,"异构链合约资产注册等待","HETEROGENEOUS CONTRACT ASSET REG PENDING"),

    HETEROGENEOUS_CONTRACT_ASSET_REG_COMPLETE (50,"异构链合约资产注册完成","HETEROGENEOUS CONTRACT ASSET REG COMPLETE"),

    CONFIRM_PROPOSAL (51,"确认提案执行交易","CONFIRM PROPOSAL"),

    RESET_HETEROGENEOUS_VIRTUAL_BANK (52,"重置异构链(合约)虚拟银行","RESET HETEROGENEOUS VIRTUAL BANK"),

    CONFIRM_HETEROGENEOUS_RESET_VIRTUAL_BANK (53,"确认重置异构链(合约)虚拟银行","CONFIRM HETEROGENEOUS RESET VIRTUAL BANK"),

    RECHARGE_UNCONFIRMED (54,"异构链充值待确认交易","RECHARGE UNCONFIRMED"),

    WITHDRAWAL_HETEROGENEOUS_SEND (55,"异构链提现已发布到异构链网络","WITHDRAWAL HETEROGENEOUS SEND"),

    WITHDRAWAL_ADDITIONAL_FEE (56,"追加提现手续费","WITHDRAWAL ADDITIONAL FEE"),

    HETEROGENEOUS_MAIN_ASSET_REG (57,"异构链主资产注册","HETEROGENEOUS MAIN ASSET REG"),

    REGISTERED_CHAIN_CHANGE (60,"已注册跨链的链信息变更","REGISTERED CHAIN CHANGE");



    /**
     * 枚举值
     */
    private final int code;
    /**
     * 枚举描述
     */
    private final String message;

    public String getMessageEn() {
        return messageEn;
    }

    private final String messageEn;

    /**
     * 构造一个<code>WalletType</code>枚举对象
     *
     * @param code
     * @param message
     */
    private TxTypeEnum(int code, String message,String messageEn) {
        this.code = code;
        this.message = message;
        this.messageEn = messageEn;
    }



    /**
     * @return Returns the message.
     */
    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    /**
     * @return Returns the message.
     */
    public String message() {
        return message;
    }



    /**
     * 通过枚举<code>code</code>获得枚举
     *
     * @param code
     * @return WalletType
     */
    public static TxTypeEnum getByCode(String code) {
        for (TxTypeEnum _enum : values()) {
            if (_enum.getCode()==0) {
                return _enum;
            }
        }
        return null;
    }




    /**
     * 获取全部枚举
     *
     * @return List<WalletType>
     */
    public List<TxTypeEnum> getAllEnum() {
        List<TxTypeEnum> list = new ArrayList<TxTypeEnum>();
        for (TxTypeEnum _enum : values()) {
            list.add(_enum);
        }
        return list;
    }


    public static String getTxTypeMessageByCode(int code){
        for (TxTypeEnum _enum : values()) {
            if (_enum.getCode()==code) {
                if(UserDao.INSTANCE.getLanguage().equals(LanguageEnum.CHS.code())){
                    return _enum.message;
                }else{
                    return _enum.messageEn;
                }

            }
        }
        return "未知类型 Unknow Type";
    }






}
