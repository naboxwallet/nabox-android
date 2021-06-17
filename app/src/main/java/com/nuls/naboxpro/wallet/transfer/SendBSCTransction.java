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

import network.nerve.heterogeneous.BSCTool;
import network.nerve.heterogeneous.ETHTool;
import network.nerve.heterogeneous.constant.Constant;
import network.nerve.kit.error.AccountErrorCode;
import network.nerve.kit.util.NerveSDKTool;

import org.web3j.protocol.core.methods.response.EthSendTransaction;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author: Loki
 * @date: 2020/11/30
 */
public class SendBSCTransction {

    public static void initTest() {
        BSCTool.init("https://data-seed-prebsc-1-s1.binance.org:8545/");
    }

    /**
     * BSC网络 转账BNB
     *
     * @param fromAddress 转出地址
     * @param prikey      转出地址私钥
     * @param toAddress   转入地址
     * @param amount      金额
     * @return String 交易hex
     * @throws Exception
     */
    public static String transferBnb(String fromAddress, String prikey, String toAddress, BigDecimal amount, BigInteger gasLimit, BigInteger gasPrice) throws Exception {

//        BigInteger gasLimit = Constant.GAS_LIMIT_OF_MAIN;
//        BigInteger gasPrice = BSCTool.getCurrentGasPrice();
        String hex = BSCTool.createTransferBnb(fromAddress, prikey, toAddress, amount, gasLimit, gasPrice).getTxHex();
        return hex;
    }

    /**
     * BSC网络 转账BEP20
     *
     * @param fromAddress     转出地址
     * @param prikey          转出地址私钥
     * @param toAddress       转入地址
     * @param amount          金额
     * @param contractAddress BEP20 token合约地址
     * @return String 交易hex
     * @throws Exception
     */
    public static String transferBep20(String fromAddress, String prikey, String toAddress, BigInteger amount, String contractAddress, BigInteger gasLimit, BigInteger gasPrice) throws Exception {
        String hex = BSCTool.createTransferBep20(
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
     * BSC网络 根据地址查询BNB余额
     * 可直接调用 BSCTool.getBnbBalance(String address) 来获取未换算的余额
     *
     * @param address 需要查询余额的地址
     * @return BigDecimal 返回实际余额
     * @throws Exception
     */
    public static BigDecimal getBnbBalance(String address) throws Exception {
        BigDecimal bnbBalance = BSCTool.getBnbBalance(address);
        if (null == bnbBalance) {
            throw new RuntimeException(AccountErrorCode.RPC_REQUEST_FAILD.getMsg());
        }
        BigDecimal balance = bnbBalance.divide(BigDecimal.TEN.pow(18));
        return balance;
    }

    /**
     * BSC网络 根据地址查询BEP20余额(将自动获取token小数位数,来计算实际余额)
     * 可直接调用 BSCTool.getBnbBalance(String address) 来获取未换算的余额
     *
     * @param address         需要查询余额的地址
     * @param contractAddress BEP20 token合约地址
     * @return
     * @throws Exception
     */
    public static BigDecimal getBep20Balance(String address, String contractAddress) throws Exception {
        BigInteger bep20Balance = BSCTool.getBep20Balance(address, contractAddress);
        if (null == bep20Balance) {
            throw new RuntimeException(AccountErrorCode.RPC_REQUEST_FAILD.getMsg());
        }
        int decimal = BSCTool.getContractTokenDecimals(contractAddress);
        BigDecimal balance = new BigDecimal(bep20Balance).divide(BigDecimal.TEN.pow(decimal));
        return balance;
    }

    /**
     * 预估提现手续费 NVT
     *
     * @param isBnbToken 提现资产是否是BNB  是:true ,其他资产:false
     * @return BigInteger 大约需支付的NVT手续费
     * @throws Exception
     */
    public static BigInteger estimatedNerveWithdrawalFee(boolean isBnbToken) throws Exception {
        BigInteger currentGasPrice = BSCTool.getCurrentGasPrice();
        BigDecimal nvtUSD = NerveSDKTool.getUsdPrice(9, 1);
        BigDecimal bnbUSD = NerveSDKTool.getUsdPrice(9, 25);
        return BSCTool.calNVTOfWithdraw(nvtUSD, new BigDecimal(currentGasPrice), bnbUSD, isBnbToken).toBigInteger();
    }
    /**
     * 充值BNB，直接调用BSCTool工具
     * BSCTool.createRechargeBnb(String fromAddress, String privateKey, BigInteger value, String toAddress, String multySignContractAddress)
     */

    /**
     * 查询是否已授权使用BEP20
     * BSCTool.isAuthorized(String fromAddress, String multySignContractAddress, String erc20ContractAddress)
     */

    /**
     * 授权使用BEP20
     * BSCTool.authorization(String fromAddress, String privateKey, String multySignContractAddress, String erc20ContractAddress)
     */
//    BSCTool.authorization(String fromAddress, String privateKey, String multySignContractAddress, String erc20ContractAddress)

    /**
     * 充值BEP20，直接调用BSCTool工具
     * BSCTool.createRechargeBep20(String fromAddress, String privateKey, BigInteger value, String toAddress, String multySignContractAddress, String erc20ContractAddress)
     */

    /**
     * 查询交易是否成功（通过返回的状态来判断,  TransactionReceipt.isStatusOK()）
     * BSCTool.getTxReceipt(String authHash)
     */
}
