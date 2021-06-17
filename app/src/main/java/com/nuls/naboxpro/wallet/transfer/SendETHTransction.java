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
public class SendETHTransction {

    public static void initTest() {
        ETHTool.init("https://ropsten.infura.io/v3/e51e9f10a4f647af81d5f083873f27a5");
    }

    /**
     * ETH网络 转账ETH
     *
     * @param fromAddress 转出地址
     * @param prikey      转出地址私钥
     * @param toAddress   转入地址
     * @param amount      金额
     * @return String 交易 hex
     * @throws Exception
     */
    public static String transferEth(String fromAddress, String prikey, String toAddress, BigDecimal amount, BigInteger gasLimit, BigInteger gasPrice) throws Exception {
        // amount 为未经过换算的原始值
        String hex = ETHTool.createTransferEth(fromAddress, prikey, toAddress, amount, gasLimit, gasPrice).getTxHex();
        return hex;
    }

    /**
     * ETH网络 转账ERC20
     *
     * @param fromAddress     转出地址
     * @param prikey          转出地址私钥
     * @param toAddress       转入地址
     * @param amount          金额
     * @param contractAddress ERC20 token合约地址
     * @return String 交易hex
     * @throws Exception
     */
    public static String transferErc20(String fromAddress, String prikey, String toAddress, BigInteger amount, String contractAddress, BigInteger gasLimit, BigInteger gasPrice) throws Exception {
        String hex = ETHTool.createTransferErc20(
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
     * ETH网络 根据地址查询ETH余额
     * 可直接调用 ETHTool.getEthBalance(String address) 来获取未换算的余额
     *
     * @param address 需要查询余额的地址
     * @return BigDecimal 返回实际余额
     * @throws Exception
     */
    public static BigDecimal getEthBalance(String address) throws Exception {
        BigDecimal ethBalance = ETHTool.getEthBalance(address);
        if (null == ethBalance) {
            throw new RuntimeException(AccountErrorCode.RPC_REQUEST_FAILD.getMsg());
        }
        BigDecimal balance = ethBalance.divide(BigDecimal.TEN.pow(18));
        return balance;
    }

    /**
     * ETH网络 根据地址查询ERC20余额(将自动获取token小数位数,来计算实际余额)
     * 可直接调用 ETHTool.getErc20Balance(String address) 来获取未换算的余额
     *
     * @param address         需要查询余额的地址
     * @param contractAddress BEP20 token合约地址
     * @return
     * @throws Exception
     */
    public static BigDecimal getErc20Balance(String address, String contractAddress) throws Exception {
        BigInteger bep20Balance = ETHTool.getErc20Balance(address, contractAddress);
        if (null == bep20Balance) {
            throw new RuntimeException(AccountErrorCode.RPC_REQUEST_FAILD.getMsg());
        }
        int decimal = ETHTool.getContractTokenDecimals(contractAddress);
        BigDecimal balance = new BigDecimal(bep20Balance).divide(BigDecimal.TEN.pow(decimal));
        return balance;
    }


    /**
     * 预估提现手续费 NVT
     *
     * @param isETHToken 提现资产是否是ETH  是:true ,其他资产:false
     * @return BigInteger 大约需支付的NVT手续费
     * @throws Exception
     */
    public static BigInteger estimatedNerveWithdrawalFee(boolean isETHToken) throws Exception {
        BigInteger currentGasPrice = ETHTool.getCurrentGasPrice();
        BigDecimal nvtUSD = NerveSDKTool.getUsdPrice(9, 1);
        BigDecimal ethUSD = NerveSDKTool.getUsdPrice(9, 2);
        return ETHTool.calNVTOfWithdraw(nvtUSD, new BigDecimal(currentGasPrice), ethUSD, isETHToken).toBigInteger();
    }

    /**
     * 充值ETH，直接调用ETHTool工具
     * ETHTool.createRechargeEth(String fromAddress, String privateKey, BigInteger value, String toAddress, String multySignContractAddress)
     */

    /**
     * 查询是否已授权使用ERC20
     * ETHTool.isAuthorized(String fromAddress, String multySignContractAddress, String erc20ContractAddress)
     */

    /**
     * 授权使用ERC20
     * ETHTool.authorization(String fromAddress, String privateKey, String multySignContractAddress, String erc20ContractAddress)
     */

    /**
     * 充值ERC20，直接调用ETHTool工具
     * ETHTool.createRechargeErc20(String fromAddress, String privateKey, BigInteger value, String toAddress, String multySignContractAddress, String erc20ContractAddress)
     */

    /**
     * 查询交易是否成功（通过返回的状态来判断,  TransactionReceipt.isStatusOK()）
     * ETHTool.getTxReceipt(String authHash)
     */

}
