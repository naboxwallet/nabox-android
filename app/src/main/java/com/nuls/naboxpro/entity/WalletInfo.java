package com.nuls.naboxpro.entity;


import com.nuls.naboxpro.wallet.NaboxAccount;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;

import io.nuls.core.crypto.AESEncrypt;
import io.nuls.core.crypto.ECKey;
import io.nuls.core.exception.CryptoException;
import io.nuls.core.exception.NulsException;
import io.nuls.v2.error.AccountErrorCode;
import network.nerve.core.crypto.HexUtil;

@Entity
public class WalletInfo implements Serializable {

    private static final long serialVersionUID = -4621715087305128172L;

    @Id(autoincrement=true)
    Long id;

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

    String hecoAddress;

    public String getHecoAddress() {
        return hecoAddress;
    }

    public void setHecoAddress(String hecoAddress) {
        this.hecoAddress = hecoAddress;
    }

    /**
     * 原始公钥(未压缩, 包含压缩位)
     * 注意:ETH/BSC链使用该公钥
     *
     */
    private byte[] decompressPubKey;

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
     * OKExChain 地址
     */
    private String oktAddress;
    /**
     * 钱包别名
     */
    String alias;


    /**
     * 皮肤颜色
     */
    int color;


    /**
     * 账户总资产 美元
     */
    String usdPrice;


    public WalletInfo(NaboxAccount naboxAccount,int color, String alias){
        this.nulsAddress = naboxAccount.getNulsAddress();
        this.nerveAddress = naboxAccount.getNerveAddress();
        this.ethAddress = naboxAccount.getEthAddress();
        this.bscAddress = naboxAccount.getBscAddress();
        this.hecoAddress = naboxAccount.getHecoAddress();
        this.decompressPubKey = naboxAccount.getDecompressPubKey();
        this.compressedPubKey = naboxAccount.getCompressedPubKey();
        this.encryptedPriKey = naboxAccount.getEncryptedPriKey();
        this.priKey = naboxAccount.getPriKey();
        this.oktAddress = naboxAccount.getOktAddress();
        this.alias = alias;
        this.color = color;
    }




    @Generated(hash = 1835945074)
    public WalletInfo(Long id, String nulsAddress, String nerveAddress, String ethAddress,
            String bscAddress, String hecoAddress, byte[] decompressPubKey, byte[] compressedPubKey,
            byte[] encryptedPriKey, byte[] priKey, String oktAddress, String alias, int color,
            String usdPrice) {
        this.id = id;
        this.nulsAddress = nulsAddress;
        this.nerveAddress = nerveAddress;
        this.ethAddress = ethAddress;
        this.bscAddress = bscAddress;
        this.hecoAddress = hecoAddress;
        this.decompressPubKey = decompressPubKey;
        this.compressedPubKey = compressedPubKey;
        this.encryptedPriKey = encryptedPriKey;
        this.priKey = priKey;
        this.oktAddress = oktAddress;
        this.alias = alias;
        this.color = color;
        this.usdPrice = usdPrice;
    }

    public boolean validatePassword(String password) {
//        boolean result = FormatValidUtils.validPassword(password);
//        if (!result) {
//            return false;
//        }
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
     * 根据密码获取私钥, 如果是未加密账户则抛异常
     * @param password
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

    /**
     * 账户是否被加密(是否设置过密码)
     * Whether the account is encrypted (Whether the password is set)
     */
    public boolean isEncrypted() {
        return getEncryptedPriKey() != null && getEncryptedPriKey().length > 0;
    }


    @Generated(hash = 1144910350)
    public WalletInfo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNulsAddress() {
        return this.nulsAddress;
    }

    public void setNulsAddress(String nulsAddress) {
        this.nulsAddress = nulsAddress;
    }

    public String getNerveAddress() {
        return this.nerveAddress;
    }

    public void setNerveAddress(String nerveAddress) {
        this.nerveAddress = nerveAddress;
    }

    public String getEthAddress() {
        return this.ethAddress;
    }

    public void setEthAddress(String ethAddress) {
        this.ethAddress = ethAddress;
    }

    public String getBscAddress() {
        return this.bscAddress;
    }

    public void setBscAddress(String bscAddress) {
        this.bscAddress = bscAddress;
    }

    public byte[] getDecompressPubKey() {
        return this.decompressPubKey;
    }

    public void setDecompressPubKey(byte[] decompressPubKey) {
        this.decompressPubKey = decompressPubKey;
    }

    public byte[] getCompressedPubKey() {
        return this.compressedPubKey;
    }

    public void setCompressedPubKey(byte[] compressedPubKey) {
        this.compressedPubKey = compressedPubKey;
    }

    public byte[] getEncryptedPriKey() {
        return this.encryptedPriKey;
    }

    public void setEncryptedPriKey(byte[] encryptedPriKey) {
        this.encryptedPriKey = encryptedPriKey;
    }

    public byte[] getPriKey() {
        return this.priKey;
    }

    public void setPriKey(byte[] priKey) {
        this.priKey = priKey;
    }




    public int getColor() {
        return this.color;
    }




    public void setColor(int color) {
        this.color = color;
    }




    public String getAlias() {
        return this.alias;
    }




    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getUsdPrice() {
        return this.usdPrice;
    }

    public void setUsdPrice(String usdPrice) {
        this.usdPrice = usdPrice;
    }

    public String getOktAddress() {
        return this.oktAddress;
    }

    public void setOktAddress(String oktAddress) {
        this.oktAddress = oktAddress;
    }



}
