package com.nuls.naboxpro.wallet;

import com.nuls.naboxpro.MyApplcation;
import com.nuls.naboxpro.R;
import com.nuls.naboxpro.net.Api;
import com.nuls.naboxpro.utils.ExtKt;

import org.web3j.crypto.Credentials;

import java.math.BigInteger;
import java.util.Arrays;

import io.nuls.core.model.FormatValidUtils;
import network.nerve.base.data.Address;
import network.nerve.core.crypto.ECKey;
import network.nerve.core.crypto.HexUtil;
import network.nerve.core.exception.NulsException;
import network.nerve.core.exception.NulsRuntimeException;
import network.nerve.core.model.StringUtils;
import network.nerve.kit.error.AccountErrorCode;
import network.nerve.kit.util.AccountTool;

/**
 * @author: Charlie
 * @date: 2020-12-15
 */
public class NaboxAccountUtil {

    public static final int NULS_CHAIN_ID = Api.NULS_CHAIN_ID;
    public static final int NERVE_CHAIN_ID = Api.NERVE_CHAIN_ID;
//    public static final int NULS_CHAIN_ID = 1;
//    public static final int NERVE_CHAIN_ID = 9;
//测试  链id

//    public static final int NULS_CHAIN_ID = 2;
//    public static final int NERVE_CHAIN_ID = 5;


    public static final int ETH_CHAIN_ID = 101;
    public static final int BSC_CHAIN_ID = 102;

    /**
     * 生成一个新的账户(私钥不加密)
     *
     * @return
     * @throws NulsException
     */
    public static NaboxAccount createAccount() throws NulsException {
        return generatorAccount(null);
    }

    /**
     * 生成一个新的账户
     *
     * @param password 设置密码
     * @return
     * @throws NulsException
     */
    public static NaboxAccount createAccount(String password) throws NulsException {
//        if (StringUtils.isNotBlank(password) && !FormatValidUtils.validPassword(password)) {
//            throw new NulsRuntimeException(AccountErrorCode.PASSWORD_IS_WRONG);
//        }
        if (StringUtils.isBlank(password)) {
            throw new NulsRuntimeException(AccountErrorCode.PASSWORD_IS_WRONG);
        }
        NaboxAccount naboxAccount = generatorAccount();
        if (StringUtils.isNotBlank(password)) {
            naboxAccount.encrypt(password);
        }
        return naboxAccount;
    }


    /**
     * 初始化钱包地址
     *
     * @param
     * @return
     */
    public static String initAddress(String prikey) throws NulsException {
        ECKey ecKey = null;
        if (StringUtils.isBlank(prikey)) {
            ecKey = new ECKey();
        } else {
            try {
                ecKey = ECKey.fromPrivate(new BigInteger(1, HexUtil.decode(prikey)));
            } catch (Exception var6) {
                throw new NulsException(AccountErrorCode.PRIVATE_KEY_WRONG, var6);
            }
        }
        Address nulsAddress = AccountTool.newAddress(Api.NULS_CHAIN_ID, ecKey.getPubKey());
        return nulsAddress.getBase58();
    }

    /**
     * 根据私钥创建(导入)一个账户(不加密)
     *
     * @param prikey 私钥
     * @return
     */
    public static NaboxAccount importAccount(String prikey) {
        return importAccount(prikey, null);
    }

    /**
     * 根据私钥创建(导入)一个账户
     *
     * @param prikey   私钥
     * @param password 密码
     * @return
     */
    public static NaboxAccount importAccount(String prikey, String password) {
        //去掉前缀0X 然后补0  64位
        if (prikey.startsWith("0x") || prikey.startsWith("0X")) {
            prikey = prikey.substring(2);
            int length = prikey.length();
            if (length <= 64) {
                length = 64 - length;
            }
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < length; i++) {
                sb.append("0");
            }
            sb.append(prikey);
            prikey = sb.toString();
        }


        if (!ECKey.isValidPrivteHex(prikey)) {
            ExtKt.showToast(R.string.error_privateKey);
            return null;
//            throw new NulsRuntimeException(AccountErrorCode.PRIVATE_KEY_WRONG);
        }
//        if (StringUtils.isNotBlank(password) && !FormatValidUtils.validPassword(password)) {
//            throw new NulsRuntimeException(AccountErrorCode.PASSWORD_IS_WRONG);
//        }
        byte[] prikeyByte = HexUtil.decode(prikey);
        // 如果私钥长度大于32位, 则取后32位
        if (prikeyByte.length > 32) {
            int start = prikeyByte.length - 32;
            prikeyByte = Arrays.copyOfRange(prikeyByte, start, 32);
        }
/*        if (prikeyByte[0] == (byte) 0) {
            // 32位私钥首位是0, 无效私钥
            System.out.println(HexUtil.encode(prikeyByte));
            throw new NulsRuntimeException(AccountErrorCode.PRIVATE_KEY_WRONG);
        }*/

        NaboxAccount naboxAccount;
        try {
            naboxAccount = generatorAccount(prikey);
        } catch (NulsException e) {
            throw new NulsRuntimeException(AccountErrorCode.PRIVATE_KEY_WRONG);
        }
        if (StringUtils.isNotBlank(password)) {
            naboxAccount.encrypt(password);
        }
        return naboxAccount;
    }

    /**
     * 生成新账户(未加密)
     *
     * @return
     * @throws NulsException
     */
    public static NaboxAccount generatorAccount() throws NulsException {
        return generatorAccount(null);
    }

    /**
     * 根据合法私钥生成账户(未加密)
     *
     * @param prikey
     * @return
     * @throws NulsException
     */
    private static NaboxAccount generatorAccount(String prikey) throws NulsException {
        ECKey ecKey = null;
        if (StringUtils.isBlank(prikey)) {
            ecKey = new ECKey();
        } else {
            try {
                ecKey = ECKey.fromPrivate(new BigInteger(1, HexUtil.decode(prikey)));
            } catch (Exception var6) {
                throw new NulsException(AccountErrorCode.PRIVATE_KEY_WRONG, var6);
            }
        }
        ECKey decompressECkey = ecKey.decompress();
        NaboxAccount naboxAccount = new NaboxAccount();
        naboxAccount.setCompressedPubKey(ecKey.getPubKey());
        naboxAccount.setDecompressPubKey(decompressECkey.getPubKey());
        naboxAccount.setPriKey(ecKey.getPrivKeyBytes());

        // 生成NULS地址
        Address nulsAddress = AccountTool.newAddress(Api.NULS_CHAIN_ID, naboxAccount.getCompressedPubKey());
        naboxAccount.setNulsAddress(nulsAddress.getBase58());

        // 生成NERVE地址
        Address nerveAddress = AccountTool.newAddress(Api.NERVE_CHAIN_ID, naboxAccount.getCompressedPubKey());
        naboxAccount.setNerveAddress(nerveAddress.getBase58());

        // 生成BSC/ETH地址(相同)
        Credentials credentials = Credentials.create(HexUtil.encode(naboxAccount.getPriKey()));
        String address = credentials.getAddress();
        naboxAccount.setBscAddress(address);
        naboxAccount.setEthAddress(address);
        naboxAccount.setHecoAddress(address);
        naboxAccount.setOktAddress(address);
        return naboxAccount;
    }

}
