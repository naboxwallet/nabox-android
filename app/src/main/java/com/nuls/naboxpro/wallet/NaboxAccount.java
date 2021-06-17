/**
 * MIT License
 * <p>
 * Copyright (c) 2019-2020 nerve.network
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

package com.nuls.naboxpro.wallet;

import android.text.TextUtils;

import io.nuls.core.crypto.AESEncrypt;
import io.nuls.core.crypto.ECKey;
import io.nuls.core.crypto.EncryptedData;
import io.nuls.core.crypto.Sha256Hash;
import io.nuls.core.exception.CryptoException;
import io.nuls.core.exception.NulsException;
import io.nuls.core.model.FormatValidUtils;
import io.nuls.v2.error.AccountErrorCode;
import network.nerve.core.crypto.HexUtil;

import org.bouncycastle.crypto.params.KeyParameter;
import org.web3j.crypto.Credentials;
import org.web3j.utils.Numeric;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * 异构链账户类
 *
 * @author: Mimi
 * @date: 2020-02-26
 */
public class NaboxAccount implements Serializable {

    /**
     * NULS链地址
     */
    private String nulsAddress;

    /**
     * NERVE链地址
     */
    private String nerveAddress;

    /**
     * ETH链地址
     */
    private String ethAddress;

    /**
     * BSC链地址
     */
    private String bscAddress;

    /**
     * HECO链地址
     */
    private String hecoAddress;
    /**
     * OKExChain 地址
     */
    private String oktAddress;
    /**
     * 原始公钥(未压缩, 包含压缩位)
     * 注意:ETH/BSC链使用该公钥
     *
     */
    private byte[] decompressPubKey;

    public String getOktAddress() {
        return oktAddress;
    }

    public void setOktAddress(String oktAddress) {
        this.oktAddress = oktAddress;
    }

    /**
     * 压缩公钥
     * 注意:NULS/NERVE链使用该公钥
     */
    private byte[] compressedPubKey;

    /**
     * 加密私钥(设置密码后有值)
     */
    private byte[] encryptedPriKey;

    /**
     * 明文私钥(设置密码后置空)
     */
    private byte[] priKey;

    /**
     * 账户是否被加密(是否设置过密码)
     * Whether the account is encrypted (Whether the password is set)
     */
    public boolean isEncrypted() {
        return getEncryptedPriKey() != null && getEncryptedPriKey().length > 0;
    }

    /**
     * 验证账户密码是否正确
     * Verify that the account password is correct
     */
    public boolean validatePassword(String password) {
//        boolean result = FormatValidUtils.validPassword(password);
//        if (!result) {
//            return false;
//        }
        if(TextUtils.isEmpty(password)){
            return  false;
        }
        byte[] unencryptedPrivateKey;
        try {
            unencryptedPrivateKey = AESEncrypt.decrypt(this.getEncryptedPriKey(), password);
        } catch (CryptoException e) {
            return false;
        }
        BigInteger newPriv = new BigInteger(1, unencryptedPrivateKey);
        ECKey key = ECKey.fromPrivate(newPriv);

        return Arrays.equals(key.getPubKey(), getCompressedPubKey());
    }


    /**
     * 根据密码加密账户(给账户设置密码)
     * Password-encrypted account (set password for account)
     */
    public void encrypt(String password) {
        if (this.isEncrypted()) {
            return;
        }
        EncryptedData encryptedPrivateKey = AESEncrypt.encrypt(this.priKey, EncryptedData.DEFAULT_IV, new KeyParameter(Sha256Hash.hash(password.getBytes())));
        this.setPriKey(new byte[0]);
        this.setEncryptedPriKey(encryptedPrivateKey.getEncryptedBytes());
    }

    /**
     * 根据密码获取私钥, 如果是未加密账户则抛异常
     * @param  password
     * @return
     * @throws NulsException
     */
    public String decrypt(String password) throws NulsException {
        try {
            if(!isEncrypted()){
                throw new NulsException(AccountErrorCode.ACCOUNT_UNENCRYPTED);
            }
            byte[] unencryptedPrivateKey = AESEncrypt.decrypt(this.getEncryptedPriKey(), password);
            BigInteger newPriv = new BigInteger(1, unencryptedPrivateKey);
            ECKey key = ECKey.fromPrivate(newPriv);

            // 解密后的明文生成公钥，与原公钥校验
            if (!Arrays.equals(key.getPubKey(), getCompressedPubKey())) {
                throw new NulsException(AccountErrorCode.PASSWORD_IS_WRONG);
            }
            return HexUtil.encode(unencryptedPrivateKey);
        } catch (Exception e) {
            throw new NulsException(AccountErrorCode.PASSWORD_IS_WRONG);
        }
    }



    public String getNulsAddress() {
        return nulsAddress;
    }

    public void setNulsAddress(String nulsAddress) {
        this.nulsAddress = nulsAddress;
    }

    public String getNerveAddress() {
        return nerveAddress;
    }

    public void setNerveAddress(String nerveAddress) {
        this.nerveAddress = nerveAddress;
    }

    public String getEthAddress() {
        return ethAddress;
    }

    public void setEthAddress(String ethAddress) {
        this.ethAddress = ethAddress;
    }

    public String getBscAddress() {
        return bscAddress;
    }

    public void setBscAddress(String bscAddress) {
        this.bscAddress = bscAddress;
    }

    public byte[] getCompressedPubKey() {
        return compressedPubKey;
    }

    public void setCompressedPubKey(byte[] compressedPubKey) {
        this.compressedPubKey = compressedPubKey;
    }

    public byte[] getDecompressPubKey() {
        return decompressPubKey;
    }

    public void setDecompressPubKey(byte[] decompressPubKey) {
        this.decompressPubKey = decompressPubKey;
    }

    public byte[] getEncryptedPriKey() {
        return encryptedPriKey;
    }

    public void setEncryptedPriKey(byte[] encryptedPriKey) {
        this.encryptedPriKey = encryptedPriKey;
    }

    public byte[] getPriKey() {
        return priKey;
    }

    public void setPriKey(byte[] priKey) {
        this.priKey = priKey;
    }

    public String getHecoAddress() {
        return hecoAddress;
    }

    public void setHecoAddress(String hecoAddress) {
        this.hecoAddress = hecoAddress;
    }
}
