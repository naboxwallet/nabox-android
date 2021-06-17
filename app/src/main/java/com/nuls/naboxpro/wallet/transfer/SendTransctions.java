/**
 * MIT License
 * <p>
 * Copyright (c) 2017-2018 nuls.io
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.nuls.naboxpro.wallet.transfer;

import android.os.Looper;

import com.nuls.naboxpro.utils.ExtKt;

import org.junit.Assert;

import io.nuls.core.basic.Result;
import io.nuls.core.parse.JSONUtils;
import io.nuls.v2.NulsSDKBootStrap;
import io.nuls.v2.SDKContext;
import io.nuls.v2.model.dto.ContractValidateCallForm;
import io.nuls.v2.model.dto.ImputedGasContractCallForm;
import io.nuls.v2.util.NulsSDKTool;
import network.nerve.NerveSDKBootStrap;
import network.nerve.kit.error.AccountErrorCode;
import network.nerve.kit.model.dto.WithdrawalTxDto;
import network.nerve.kit.util.NerveSDKTool;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import static io.nuls.v2.constant.Constant.CONTRACT_MINIMUM_PRICE;
import static io.nuls.v2.constant.Constant.MAX_GASLIMIT;

/**
 * Nabox 转账工具类
 * 所有创建交易的方法均包含创建交易，签名交易，广播交易
 *
 * @author: Charlie
 * @date: 2020/11/27
 */
public class SendTransctions {

    /**
     * 初始化测试网 api地址，测试时使用。
     * 默认为主网api地址(无需初始化)
     */
    public static void initTest() {
        NerveSDKBootStrap.initTest("http://beta.api.nerve.network/");
        NulsSDKBootStrap.initTest("http://beta.api.nuls.io/");
    }

    /* 从NULS链发起交易 */

    /**
     * NULS链内 单账户对单账户转账NULS资产(不能用于转其他资产)
     *
     * @param fromAddress 转出地址
     * @param prikey      转出地址私钥
     * @param toAddress   转入地址
     * @param amount      金额
     * @param time        交易时间
     * @param remark      交易备注
     * @return String 交易hex
     */
    public static String createNulsTxSimpleTransferOfNuls(String fromAddress, String prikey, String toAddress, BigInteger amount, long time, String remark) {

        Result<Map> result = NulsSDKTool.createTxSimpleTransferOfNuls(fromAddress, toAddress, amount, time, remark);
        if (result.isFailed()) {
            throw new RuntimeException(result.getErrorCode().getMsg());
        }
        String txHex = (String) result.getData().get("txHex");
        //签名
        result = NulsSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");
        return txHex;
      /*  String txHash = (String) result.getData().get("hash");
        //广播
        result = NulsSDKTool.broadcast(txHex);
        if (result.isFailed()) {
            throw new RuntimeException(result.getErrorCode().getMsg());
        } else {
            return txHash;
        }*/
    }

    /**
     * NULS链内 单账户对单账户转账其他资产(非NULS，非合约资产)
     *
     * @param fromAddress  转出地址
     * @param prikey       转出地址私钥
     * @param toAddress    转入地址
     * @param assetChainId 转账资产 链ID
     * @param assetId      转账资产ID
     * @param amount       金额
     * @param time         交易时间
     * @param remark       备注
     * @return String 交易hex
     */
    public static String createNulsTxSimpleTransferOfNonNuls(String fromAddress, String prikey, String toAddress, int assetChainId, int assetId, BigInteger amount, long time, String remark) {
        //组装交易
        Result<Map> result = NulsSDKTool.createTxSimpleTransferOfNonNuls(fromAddress, toAddress, assetChainId, assetId, amount, time, remark);
        if (result.isFailed()) {
            throw new RuntimeException(result.getErrorCode().getMsg());
        }
        String txHex = (String) result.getData().get("txHex");
        //签名
        result = NulsSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");
        return txHex;
    }

    /**
     * NULS链内 单账户对单账户合约资产转账(NRC20)
     *
     * @param fromAddress     转出地址
     * @param prikey          转出地址私钥
     * @param toAddress       转入地址
     * @param amount          金额
     * @param contractAddress NRC20合约地址
     * @param time            交易时间
     * @param remark          交易备注
     * @return String 交易 hex
     */
    public static String createNulsNrc20Transfer(String fromAddress, String prikey, String toAddress, BigInteger amount, String contractAddress, long time, String remark) {
        // 在线接口(不可跳过，一定要调用的接口) - 获取账户余额信息
        Result accountBalanceR = NulsSDKTool.getAccountBalance(fromAddress, SDKContext.main_chain_id, SDKContext.main_asset_id);
        if (accountBalanceR.isFailed()) {
            throw new RuntimeException(accountBalanceR.getErrorCode().getMsg());
        }
        Map balance = (Map) accountBalanceR.getData();
        BigInteger senderFeeBalance = new BigInteger(balance.get("available").toString());
        String nonce = balance.get("nonce").toString();

        String methodName = "transfer";
        String methodDesc = "";
        Object[] args = new Object[]{toAddress, amount};

        ImputedGasContractCallForm iForm = new ImputedGasContractCallForm();
        iForm.setSender(fromAddress);
        iForm.setContractAddress(contractAddress);
        iForm.setMethodName(methodName);
        iForm.setMethodDesc(methodDesc);
        iForm.setArgs(args);

        Result iResult = NulsSDKTool.imputedContractCallGas(iForm);
        if (iResult.isFailed()) {
            throw new RuntimeException(iResult.getErrorCode().getMsg());
        }
        Map result = (Map) iResult.getData();
        Long gasLimit = Long.valueOf(result.get("gasLimit").toString());

        Result<Map> rs = NulsSDKTool.tokenTransferTxOffline(fromAddress, senderFeeBalance, nonce, toAddress, contractAddress, gasLimit, amount, time, remark);
        if (rs.isFailed()) {
            throw new RuntimeException(rs.getErrorCode().getMsg());
        }
        String txHex = rs.getData().get("txHex").toString();
        // 签名
        Result res = NulsSDKTool.sign(txHex, fromAddress, prikey);
        Map signMap = (Map) res.getData();

        return signMap.get("txHex").toString();
        // 在线接口 - 广播交易
/*        Result<Map> broadcaseTxR = NulsSDKTool.broadcast(signMap.get("txHex").toString());
        if (broadcaseTxR.isFailed()) {
            throw new RuntimeException(broadcaseTxR.getErrorCode().getMsg());
        } else {
            Map data = broadcaseTxR.getData();
            String hash = (String) data.get("hash");
            return hash;
        }*/
    }

    /**
     * NULS 跨链-> NERVE
     * 从NULS链发起 单账户对单账户合约资产跨链转账(NRC20转到NERVE)
     *
     * @param fromAddress     转出地址
     * @param prikey          转出地址私钥
     * @param toNerveAddress  转入地址(NERVE地址)
     * @param amount          金额
     * @param contractAddress NRC20合约地址
     * @param time            交易时间
     * @param remark          备注
     * @return String 交易 hex
     */
    public static String createNulsNrc20CrossTx(String fromAddress, String prikey, String toNerveAddress, BigInteger amount, String contractAddress, long time, String remark) {
        int chainId = SDKContext.main_chain_id;
        // 调用的跨链转账函数
        String methodName = "transferCrossChain";
        String methodDesc = "";
        Object[] args = new Object[]{toNerveAddress, amount};

        // 在线接口(不可跳过，一定要调用的接口) - 获取账户余额信息
        Result accountBalanceR = NulsSDKTool.getAccountBalance(fromAddress, chainId, SDKContext.main_asset_id);
        if (accountBalanceR.isFailed()) {
            throw new RuntimeException(accountBalanceR.getErrorCode().getMsg());
        }
        Map balance = (Map) accountBalanceR.getData();
        BigInteger senderBalance = new BigInteger(balance.get("available").toString());
        String nonce = balance.get("nonce").toString();

        BigInteger value = BigInteger.valueOf(1000_0000L);
        // 在线接口(可跳过) - 验证调用合约的合法性，可不验证
        ContractValidateCallForm validateCallForm = new ContractValidateCallForm();
        validateCallForm.setSender(fromAddress);
        validateCallForm.setValue(value.longValue());
        validateCallForm.setGasLimit(MAX_GASLIMIT);
        validateCallForm.setPrice(CONTRACT_MINIMUM_PRICE);
        validateCallForm.setContractAddress(contractAddress);
        validateCallForm.setMethodName(methodName);
        validateCallForm.setMethodDesc(methodDesc);
        validateCallForm.setArgs(args);
        Result vResult = NulsSDKTool.validateContractCall(validateCallForm);
        if (vResult.isFailed()) {
            throw new RuntimeException(vResult.getErrorCode().getMsg());
        }
        Map mapValidate = (Map) vResult.getData();
        boolean success = (boolean) mapValidate.get("success");
        if(!success){
            throw new RuntimeException((String) mapValidate.get("msg"));
        }

        // 在线接口(可跳过) - 估算调用合约需要的GAS，可不估算，离线写一个合理的值
        ImputedGasContractCallForm iForm = new ImputedGasContractCallForm();
        iForm.setSender(fromAddress);
        iForm.setValue(value);
        iForm.setContractAddress(contractAddress);
        iForm.setMethodName(methodName);
        iForm.setMethodDesc(methodDesc);
        iForm.setArgs(args);

        Result iResult = NulsSDKTool.imputedContractCallGas(iForm);
        if (iResult.isFailed()) {
            throw new RuntimeException(iResult.getErrorCode().getMsg());
        }
        Map result = (Map) iResult.getData();
        Long gasLimit = Long.valueOf(result.get("gasLimit").toString());

        Result<Map> rs = NulsSDKTool.nrc20CrossChainTxOffline(fromAddress, senderBalance, nonce, toNerveAddress, contractAddress, gasLimit, amount, time, remark);
        if (rs.isFailed()) {
            throw new RuntimeException(rs.getErrorCode().getMsg());
        }
        String txHex = rs.getData().get("txHex").toString();
        // 签名
        Result res = NulsSDKTool.sign(txHex, fromAddress, prikey);
        Map signMap = (Map) res.getData();
        return signMap.get("txHex").toString();
      /*
        // 在线接口 - 广播交易
        Result<Map> broadcaseTxR = NulsSDKTool.broadcast(signMap.get("txHex").toString());
        if (broadcaseTxR.isFailed()) {
            throw new RuntimeException(broadcaseTxR.getErrorCode().getMsg());
        } else {
            Map data = broadcaseTxR.getData();
            String hash = (String) data.get("hash");
            return hash;
        }*/
    }


    /**
     * NULS 跨链-> NERVE
     * 从NULS链发起 跨链转账NULS资产(不能用于转其他资产)
     *
     * @param fromAddress    转出地址
     * @param prikey         转出地址私钥
     * @param toNerveAddress 转入地址(NERVE地址)
     * @param amount         金额
     * @param time           交易时间
     * @param remark         备注
     * @return String 交易 hex
     */
    public static String createNulsCrossTxSimpleTransferOfNuls(String fromAddress, String prikey, String toNerveAddress, BigInteger amount, long time, String remark) {

        Result<Map> result = NulsSDKTool.createCrossTxSimpleTransferOfNuls(fromAddress, toNerveAddress, amount, time, remark);
        if (result.isFailed()) {
            throw new RuntimeException(result.getErrorCode().getMsg());
        }
        String txHex = (String) result.getData().get("txHex");
        //签名
        result = NulsSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");
        return txHex;
    }

    /**
     * NULS 跨链-> NERVE
     * 从NULS链发起 跨链转账 其他资产（非NULS，非合约资产）
     *
     * @param fromAddress    转出地址
     * @param prikey         转出地址私钥
     * @param toNerveAddress 转入地址(NERVE地址)
     * @param assetChainId   转账资产 链ID
     * @param assetId        转账资产ID
     * @param amount         金额
     * @param time           交易时间
     * @param remark         备注
     * @return String 交易 hex
     */
    public static String createNulsCrossTxSimpleTransferOfNonNuls(String fromAddress, String prikey, String toNerveAddress, int assetChainId, int assetId, BigInteger amount, long time, String remark) {
        Result<Map> result = NulsSDKTool.createCrossTxSimpleTransferOfNonNuls(fromAddress, toNerveAddress, assetChainId, assetId, amount, time, remark);
        if (result.isFailed()) {
            throw new RuntimeException(result.getErrorCode().getMsg());
        }
        String txHex = (String) result.getData().get("txHex");
        //签名
        result = NulsSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");
        return txHex;
    }


    /* 从NERVE链发起交易 */

    /**
     * NERVE链内 单账户对单账户转账NVT资产(不能用于转其他资产)
     *
     * @param fromAddress 转出地址
     * @param prikey      转出地址私钥
     * @param toAddress   转入地址
     * @param amount      金额
     * @param time        交易时间
     * @param remark      交易备注
     * @return String 交易 hex
     */
    public static String createNerveTxSimpleTransferOfNvt(String fromAddress, String prikey, String toAddress, BigInteger amount, long time, String remark) {

        network.nerve.core.basic.Result<Map> result = NerveSDKTool.createTxSimpleTransferOfNvt(fromAddress, toAddress, amount, time, remark);
        if (result.isFailed()) {
            throw new RuntimeException(result.getErrorCode().getMsg());
        }
        String txHex = (String) result.getData().get("txHex");
        //签名
        result = NerveSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");
        return txHex;
    }

    /**
     * NERVE链内 单账户对单账户转账非NVT资产(不能用于转NVT资产)
     *
     * @param fromAddress  转出地址
     * @param prikey       转出地址私钥
     * @param toAddress    转入地址
     * @param assetChainId 转账资产 链ID
     * @param assetId      转账资产ID
     * @param amount       金额
     * @param time         交易时间
     * @param remark       备注
     * @return String 交易 hex
     */
    public static String createNerveTxSimpleTransferOfNonNvt(String fromAddress, String prikey, String toAddress, int assetChainId, int assetId, BigInteger amount, long time, String remark) {
        network.nerve.core.basic.Result<Map> result =
                NerveSDKTool.createTxSimpleTransferOfNonNvt(fromAddress, toAddress, assetChainId, assetId, amount, time, remark);
        if (result.isFailed()) {
            throw new RuntimeException(result.getErrorCode().getMsg());
        }
        String txHex = (String) result.getData().get("txHex");
        //签名
        result = NerveSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");
        return txHex;
    }

    /**
     * NERVE 跨链-> NULS
     * 从NERVE链发起 跨链转账其他资产（非NULS，非NVT资产）
     * 不能用于转账NULS, NVT资产
     *
     * @param fromAddress   转出地址
     * @param prikey        转出地址私钥
     * @param toNulsAddress 转入地址
     * @param assetChainId  转账资产 链ID
     * @param assetId       转账资产ID
     * @param amount        金额
     * @param time          交易时间
     * @param remark        备注
     * @return String 交易 hex
     */
    public static String createNerveCrossTxSimpleTransferOfNonNvtNuls(String fromAddress, String prikey, String toNulsAddress, int assetChainId, int assetId, BigInteger amount, long time, String remark) {
        network.nerve.core.basic.Result<Map> result =
                NerveSDKTool.createCrossTxSimpleTransferOfNonNvtNuls(fromAddress, toNulsAddress, assetChainId, assetId, amount, time, remark);
        if (result.isFailed()) {
            throw new RuntimeException(result.getErrorCode().getMsg());
        }
        String txHex = (String) result.getData().get("txHex");
        //签名
        result = NerveSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");
        return txHex;
    }

    /**
     * NERVE 跨链-> NULS
     * 从NERVE链发起 跨链转账NULS资产（只能用于跨链转账NULS资产）
     *
     * @param fromAddress   转出地址
     * @param prikey        转出地址私钥
     * @param toNulsAddress 转入地址
     * @param amount        金额
     * @param time          交易时间
     * @param remark        交易备注
     * @return String 交易 hex
     */
    public static String createNerveCrossTxSimpleTransferOfNuls(String fromAddress, String prikey, String toNulsAddress, BigInteger amount, long time, String remark) {
        network.nerve.core.basic.Result<Map> result =
                NerveSDKTool.createCrossTxSimpleTransferOfNuls(fromAddress, toNulsAddress, amount, time, remark);
        if (result.isFailed()) {
            throw new RuntimeException(result.getErrorCode().getMsg());
        }
        String txHex = (String) result.getData().get("txHex");
        //签名
        result = NerveSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");
        return txHex;
    }

    /**
     * NERVE 跨链-> NULS
     * 从NERVE链发起 跨链转账NVT资产（只能用于跨链转账NVT资产）
     *
     * @param fromAddress   转出地址
     * @param prikey        转出地址私钥
     * @param toNulsAddress 转入地址
     * @param amount        金额
     * @param time          交易时间
     * @param remark        交易备注
     * @return String 交易 hex
     */
    public static String createNerveCrossTxSimpleTransferOfNvt(String fromAddress, String prikey, String toNulsAddress, BigInteger amount, long time, String remark) {
        network.nerve.core.basic.Result<Map> result =
                NerveSDKTool.createCrossTxSimpleTransferOfNvt(fromAddress, toNulsAddress, amount, time, remark);
        if (result.isFailed()) {
            throw new RuntimeException(result.getErrorCode().getMsg());
        }
        String txHex = (String) result.getData().get("txHex");
        //签名
        result = NerveSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");
        return txHex;
    }

    /**
     * NERVE 跨链(提现)-> ETH/BSC(以太坊,币安智能链等异构链网络)
     * 从NERVE链 转账到异构链网络, 支持的资产都可以交易
     *
     * @param fromAddress          转出地址
     * @param prikey               转出地址私钥
     * @param heterogeneousChainId 转入网络的链ID
     * @param toAddress            对应网络的转入地址
     * @param assetChainId         转账资产 链ID
     * @param assetId              转账资产ID
     * @param amount               金额
     * @param fee                  手续费(异构链提现交易的手续费, 如果手续费不够后续可以追加)
     * @param time                 交易时间
     * @param remark               备注
     * @return String 交易 hex
     * @throws Exception
     */
    public static String createNerveWithdrawalTx(String fromAddress,
                                                 String prikey,
                                                 int heterogeneousChainId,
                                                 String toAddress,
                                                 int assetChainId,
                                                 int assetId,
                                                 BigInteger amount,
                                                 BigInteger fee,
                                                 long time,
                                                 String remark) throws Exception {
        WithdrawalTxDto withdrawalTxDto = new WithdrawalTxDto();
        withdrawalTxDto.setFromAddress(fromAddress);
        withdrawalTxDto.setAssetChainId(assetChainId);
        withdrawalTxDto.setAssetId(assetId);
        withdrawalTxDto.setHeterogeneousChainId(heterogeneousChainId);
        withdrawalTxDto.setHeterogeneousAddress(toAddress);
        // 提现金额
        withdrawalTxDto.setAmount(amount);

        // 手续费
        if (null == fee) {
            Looper.prepare();
            ExtKt.showToast(AccountErrorCode.INSUFFICIENT_FEE.getMsg());
            Looper.loop();
            return null;
//            throw new RuntimeException(AccountErrorCode.INSUFFICIENT_FEE.getMsg());
        }
        withdrawalTxDto.setDistributionFee(fee);
        withdrawalTxDto.setTime(time);
        withdrawalTxDto.setRemark(remark);
        network.nerve.core.basic.Result<Map> result = NerveSDKTool.createWithdrawalTx(withdrawalTxDto);
        if (result.isFailed()) {

//            throw new RuntimeException(result.getErrorCode().getMsg());
            Looper.prepare();
            ExtKt.showToast(result.getErrorCode().getMsg());
            Looper.loop();
            return null;
        }
        String txHex = (String) result.getData().get("txHex");
        //签名
        result = NerveSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");
        return txHex;
    }

    /**
     * NERVE链 追加异构链提现手续费
     *
     * @param fromAddress      转出地址
     * @param prikey           转出地址私钥
     * @param withdrawalTxHash 需追加手续费的提现交易hash
     * @param amount           追加手续费的金额
     * @param time             交易时间
     * @param remark           交易备注
     * @return String 交易 hex
     */
    public static String createNerveWithdrawalAdditionalFeeTx(String fromAddress, String prikey, String withdrawalTxHash, BigInteger amount, long time, String remark) {
        network.nerve.core.basic.Result<Map> result =
                NerveSDKTool.withdrawalAdditionalFeeTx(fromAddress, withdrawalTxHash, amount, time, remark);
        if (result.isFailed()) {
            throw new RuntimeException(result.getErrorCode().getMsg());
        }
        String txHex = (String) result.getData().get("txHex");
        //签名
        result = NerveSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");
        return txHex;
    }

}
