package com.nuls.naboxpro.common

import android.text.TextUtils
import com.blankj.utilcode.util.GsonUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nuls.naboxpro.entity.ChainConfigBean
import com.nuls.naboxpro.enums.CoinTypeEnum
import com.tencent.mmkv.MMKV
import kotlin.properties.Delegates

/**
 *
 * @function 用户信息保存
 */
object UserDao {

    private const val permissionExtra = "permissionExtra"


    private const val currencyExtraKey = "currencyExtra"

    private const val languageExtraKey = "languageExtra"


    private const val gestureExtraKey = "gestureExtra"
    private const val feeAddressExtraKey = "feeAddressExtra"
    private const val rmbExchangeKey = "rmbExchangeExtra"
    private const val defaultWalletExtraKey = "defaultWalletExtra"

    private const val defaultChainExtraKey = "defaultChainExtra"
    private const val defaultChainIconExtraKey = "defaultChainIconExtra"
    private const val chainConfigExtraKey = "chainConfigExtra"
    //用户是否阅读过授权说明
    var isReadPermission: Boolean by Delegates.observable(
            MMKV.defaultMMKV().decodeBool(
            permissionExtra,
            false
        )
    ) { _, old, new ->
        if (old != new) {
            MMKV.defaultMMKV().encode(permissionExtra, new)
        }
    }


    //默认钱包  这里是相应钱包的nuls地址
    var defaultWallet: String by Delegates.observable(
        MMKV.defaultMMKV().decodeString(
            defaultWalletExtraKey,
            ""
        )
    ) { _, old, new ->
        if (old != new) {
            MMKV.defaultMMKV().encode(defaultWalletExtraKey, new)
        }
    }



    //默认选中的chain  默认nuls
    var defaultChain: String by Delegates.observable(
        MMKV.defaultMMKV().decodeString(
            defaultChainExtraKey,
            CoinTypeEnum.NULS.code
        )
    ) { _, old, new ->
        if (old != new) {
            MMKV.defaultMMKV().encode(defaultChainExtraKey, new)
        }
    }


    //默认选中的chain图标  默认nuls
    var defaultChainIcon: String by Delegates.observable(
        MMKV.defaultMMKV().decodeString(
            defaultChainIconExtraKey,
           ""
        )
    ) { _, old, new ->
        if (old != new) {
            MMKV.defaultMMKV().encode(defaultChainIconExtraKey, new)
        }
    }


    //手续费中转地址（nerve）
    var feeAddress: String by Delegates.observable(
        MMKV.defaultMMKV().decodeString(
            feeAddressExtraKey,
            ""
        )
    ) { _, old, new ->
        if (old != new) {
            MMKV.defaultMMKV().encode(feeAddressExtraKey, new)
        }
    }


    //用户选择的货币 默认美金
    var currency: String by Delegates.observable(
        MMKV.defaultMMKV().decodeString(
            currencyExtraKey,
            "USD"
        )
    ) { _, old, new ->
        if (old != new) {
            MMKV.defaultMMKV().encode(currencyExtraKey, new)
        }
    }


    //美元兑人民币汇率
    var rmbExcahnge: Double by Delegates.observable(
        MMKV.defaultMMKV().decodeDouble(
            rmbExchangeKey,
            6.5
        )
    ) { _, old, new ->
        if (old != new) {
            MMKV.defaultMMKV().encode(rmbExchangeKey, new)
        }
    }

    //是否开启手势识别
    var isGesture: Boolean by Delegates.observable(
        MMKV.defaultMMKV().decodeBool(
            gestureExtraKey,
            false
        )
    ) { _, old, new ->
        if (old != new) {
            MMKV.defaultMMKV().encode(gestureExtraKey, new)
        }
    }


    //用户选择的语言  默认中文
    var language: String by Delegates.observable(
        MMKV.defaultMMKV().decodeString(
            languageExtraKey,
            "CHN"
        )
    ) { _, old, new ->
        if (old != new) {
            MMKV.defaultMMKV().encode(languageExtraKey, new)
        }
    }


    private var chainConfig: List<ChainConfigBean>? = listOf()
    //链配置信息
    fun getChainConfig(): List<ChainConfigBean>? {
        if (chainConfig == null) {
            if (TextUtils.isEmpty(chainConfigStr)) {
                chainConfig = listOf()
            }
            chainConfig = Gson().fromJson(
                chainConfigStr,
                object :
                    TypeToken<List<ChainConfigBean?>?>() {}.type
            )
        }
        return chainConfig
    }

    fun setChainConfig(chainConfig: List<ChainConfigBean>) {
        this.chainConfig = chainConfig
        chainConfigStr = GsonUtils.toJson(chainConfig)
    }
    //用户信息类
    var chainConfigStr: String? by Delegates.observable(
        MMKV.defaultMMKV().decodeString(
            chainConfigExtraKey,
            ""
        )
    ) { _, old, new ->
        if (old != new) {
            MMKV.defaultMMKV().encode(chainConfigExtraKey, new)
        }
    }

}