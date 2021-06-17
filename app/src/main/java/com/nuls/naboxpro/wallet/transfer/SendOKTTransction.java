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

import java.math.BigDecimal;
import java.math.BigInteger;

import network.nerve.heterogeneous.OKTTool;
import network.nerve.kit.error.AccountErrorCode;
import network.nerve.kit.util.NerveSDKTool;

/**
 * @author: Loki
 * @date: 2021/04/27
 */
public class SendOKTTransction {

    public static void initTest() {
        OKTTool.init("https://exchaintestrpc.okex.org/");
    }

    /**
     * OKT网络 转账BNB
     *
     * @param fromAddress 转出地址
     * @param prikey      转出地址私钥
     * @param toAddress   转入地址
     * @param amount      金额
     * @return String 交易hex
     * @throws Exception
     */
    public static String transferOkt(String fromAddress, String prikey, String toAddress, BigDecimal amount, BigInteger gasLimit, BigInteger gasPrice) throws Exception {

//        BigInteger gasLimit = Constant.GAS_LIMIT_OF_MAIN;
//        BigInteger gasPrice = OKTTool.getCurrentGasPrice();
        String hex = OKTTool.createTransferOkt(fromAddress, prikey, toAddress, amount, gasLimit, gasPrice).getTxHex();
        return hex;
    }

    /**
     * OKT网络 转账KIP20
     *
     * @param fromAddress     转出地址
     * @param prikey          转出地址私钥
     * @param toAddress       转入地址
     * @param amount          金额
     * @param contractAddress KIP20 token合约地址
     * @return String 交易hex
     * @throws Exception
     */
    public static String transferKip20(String fromAddress, String prikey, String toAddress, BigInteger amount, String contractAddress, BigInteger gasLimit, BigInteger gasPrice) throws Exception {
        String hex = OKTTool.createTransferKip20(
                fromAddress,
                prikey,
                toAddress,
                amount,
                contractAddress,
                gasLimit,
                gasPrice).getTxHex();
        return hex;
    }

    /**
     * OKT网络 根据地址查询BNB余额
     * 可直接调用 OKTTool.getOktBalance(String address) 来获取未换算的余额
     *
     * @param address 需要查询余额的地址
     * @return BigDecimal 返回实际余额
     * @throws Exception
     */
    public static BigDecimal getOktBalance(String address) throws Exception {
        BigDecimal bnbBalance = OKTTool.getOktBalance(address);
        if (null == bnbBalance) {
            throw new RuntimeException(AccountErrorCode.RPC_REQUEST_FAILD.getMsg());
        }
        BigDecimal balance = bnbBalance.divide(BigDecimal.TEN.pow(18));
        return balance;
    }

    /**
     * OKT网络 根据地址查询KIP20余额(将自动获取token小数位数,来计算实际余额)
     * 可直接调用 OKTTool.getOktBalance(String address) 来获取未换算的余额
     *
     * @param address         需要查询余额的地址
     * @param contractAddress KIP20 token合约地址
     * @return
     * @throws Exception
     */
    public static BigDecimal getKip20Balance(String address, String contractAddress) throws Exception {
        BigInteger bep20Balance = OKTTool.getKip20Balance(address, contractAddress);
        if (null == bep20Balance) {
            throw new RuntimeException(AccountErrorCode.RPC_REQUEST_FAILD.getMsg());
        }
        int decimal = OKTTool.getContractTokenDecimals(contractAddress);
        BigDecimal balance = new BigDecimal(bep20Balance).divide(BigDecimal.TEN.pow(decimal));
        return balance;
    }

    /**
     * 预估提现手续费 NVT
     *
     * @param isOktToken 提现资产是否是BNB  是:true ,其他资产:false
     * @return BigInteger 大约需支付的NVT手续费
     * @throws Exception
     */
    public static BigInteger estimatedNerveWithdrawalFee(boolean isOktToken) throws Exception {
        BigInteger currentGasPrice = OKTTool.getCurrentGasPrice();
        BigDecimal nvtUSD = NerveSDKTool.getUsdPrice(9, 1);
        BigDecimal bnbUSD = NerveSDKTool.getUsdPrice(9, 25);
        return OKTTool.calNVTOfWithdraw(nvtUSD, new BigDecimal(currentGasPrice), bnbUSD, isOktToken).toBigInteger();
    }
    /**
     * 充值BNB，直接调用OKTTool工具
     * OKTTool.createRechargeOkt(String fromAddress, String privateKey, BigInteger value, String toAddress, String multySignContractAddress)
     */

    /**
     * 查询是否已授权使用KIP20
     * OKTTool.isAuthorized(String fromAddress, String multySignContractAddress, String erc20ContractAddress)
     */

    /**
     * 授权使用KIP20
     * OKTTool.authorization(String fromAddress, String privateKey, String multySignContractAddress, String erc20ContractAddress)
     */

    /**
     * 充值KIP20，直接调用OKTTool工具
     * OKTTool.createRechargeKip20(String fromAddress, String privateKey, BigInteger value, String toAddress, String multySignContractAddress, String erc20ContractAddress)
     */

    /**
     * 查询交易是否成功（通过返回的状态来判断,  TransactionReceipt.isStatusOK()）
     * OKTTool.getTxReceipt(String authHash)
     */
}
