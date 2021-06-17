package com.nuls.naboxpro.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.provider.Settings.Global.getString
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.blankj.utilcode.util.AppUtils
import com.bumptech.glide.Glide
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.OnCancelListener
import com.lxj.xpopup.interfaces.OnConfirmListener
import com.nuls.naboxpro.MyApplcation
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.db.AuthDaoHelper
import com.nuls.naboxpro.db.DAppDaoHelper
import com.nuls.naboxpro.db.WalletInfoDaoHelper
import com.nuls.naboxpro.entity.*
import com.nuls.naboxpro.enums.CoinTypeEnum
import com.nuls.naboxpro.enums.MoneyTypeEnum
import com.nuls.naboxpro.eventbus.RefreshEvent
import com.nuls.naboxpro.jsbridge.JsBridge
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.BaseResponse
import com.nuls.naboxpro.net.api.RetrofitClient
import com.nuls.naboxpro.net.observable.RecObserver
import com.nuls.naboxpro.ui.activity.DAppActivity
import com.nuls.naboxpro.ui.popup.*
import com.nuls.naboxpro.wallet.NaboxAccount
import jnr.a64asm.Ext
import org.greenrobot.eventbus.EventBus
import java.math.BigDecimal
import java.util.HashMap

//显示加载动画
fun showLoadingPopup(context: Context, cancel: Boolean, callBack: () -> Unit): BasePopupView {
    return XPopup.Builder(context)
        .hasStatusBarShadow(false)
        .hasShadowBg(false)
        .dismissOnBackPressed(cancel)
        .dismissOnTouchOutside(cancel)
        .asCustom(LoadingPopup(context) {
            callBack()
        })
        .show()
}

/**
 * 弹出toast
 */
fun showToast(toast: String) {
    Toast.makeText(MyApplcation.mContext, toast, Toast.LENGTH_SHORT).show()
}

/**
 * 弹出toast
 */
fun showToast(res: Int) {
    Toast.makeText(MyApplcation.mContext, MyApplcation.mContext.getString(res), Toast.LENGTH_SHORT)
        .show()
}

/**
 * 获取钱包链列表
 */
fun getChainList(naboxAccount: NaboxAccount): MutableList<ChainEntity> {
    val mList: MutableList<ChainEntity> = mutableListOf()
    mList.add(ChainEntity(CoinTypeEnum.NULS.code, naboxAccount?.nulsAddress))
    mList.add(ChainEntity(CoinTypeEnum.NERVE.code, naboxAccount?.nerveAddress))
    mList.add(ChainEntity(CoinTypeEnum.BSC.code, naboxAccount?.bscAddress))
    mList.add(ChainEntity(CoinTypeEnum.ETH.code, naboxAccount?.ethAddress))
    mList.add(ChainEntity(CoinTypeEnum.HECO.code, naboxAccount?.hecoAddress))
    mList.add(ChainEntity(CoinTypeEnum.OKEX.code, naboxAccount?.oktAddress))
    return mList;
}

/**
 * 弹出密码输入框
 */
fun showPasswordPop(context: Context, inputListener: InputPasswordPop.InputListener) {
    XPopup.Builder(context)
        .dismissOnBackPressed(true)
        .dismissOnTouchOutside(true)
        .asCustom(InputPasswordPop(context, inputListener))
        .show()
}


/**
 * 弹出资产选择框
 */
fun showChoiceAssetPop(
    context: Context,
    chain: String,
    address: String,
    tochain: String,
    itemClickListener: ChoiceAssetPop.OnItemClickListener
) {
    XPopup.Builder(context)
        .dismissOnBackPressed(true)
        .dismissOnTouchOutside(true)
        .asCustom(ChoiceAssetPop(context, chain, address, tochain, itemClickListener))
        .show()
}


/**
 * 弹出编辑钱包名称弹窗
 */
fun showEditNamePop(
    context: Context,
    olrName: String,
    itemClickListener: EditWalletNamePop.Companion.EditSuccessListener
) {
    XPopup.Builder(context)
        .dismissOnBackPressed(true)
        .dismissOnTouchOutside(true)
        .asCustom(EditWalletNamePop(context, olrName, itemClickListener))
        .show()
}


/**
 * 弹出资产选择框
 */
fun showSwitchWalletPop(context: Context) {
    XPopup.Builder(context)
        .dismissOnBackPressed(true)
        .dismissOnTouchOutside(true)
        .asCustom(SwitchWalletPop(context))
        .show()
}


/**
 * 弹出chain选择框
 */
fun showChoiceChainPop(
    context: Context,
    choiceChain: String,
    walletInfo: WalletInfo,
    choiceListener: CrossChainPop.ChoiceListener
) {
    XPopup.Builder(context)
        .dismissOnBackPressed(true)
        .dismissOnTouchOutside(true)
        .asCustom(CrossChainPop(context, choiceChain, walletInfo, choiceListener))
        .show()
}


/**
 * 弹出chain和资产选择弹窗
 */
fun showChoiceWalletAndChainPop(
    context: Context,
    chain: String,
    targetChain: String,
    visibilityOther: Boolean,
    choiceListener: ChoiceChainPop.ChoiceListener
) {
    XPopup.Builder(context)
        .dismissOnBackPressed(true)
        .dismissOnTouchOutside(true)
        .asCustom(ChoiceChainPop(context, chain, targetChain, visibilityOther, choiceListener))
        .show()
}


/**
 * 弹出主链选择框
 */
fun showContactChainPop(
    context: Context,
    walletInfo: WalletInfo,
    choiceListener: ContactChainPop.ChoiceListener
) {
    XPopup.Builder(context)
        .dismissOnBackPressed(true)
        .dismissOnTouchOutside(true)
        .asCustom(ContactChainPop(context, choiceListener))
        .show()
}


/**
 * 根据币种类型获取地址
 */
fun getAddressByCoinType(walletInfo: WalletInfo, coinType: String): String {
    var address = ""
    when (coinType) {
        CoinTypeEnum.NULS.code -> {
            address = walletInfo.nulsAddress
        }
        CoinTypeEnum.NERVE.code -> {
            address = walletInfo.nerveAddress
        }
        CoinTypeEnum.ETH.code -> {
            address = walletInfo.ethAddress
        }
        CoinTypeEnum.BSC.code -> {
            address = walletInfo.bscAddress
        }
        CoinTypeEnum.HECO.code -> {
            address = walletInfo.hecoAddress
        }
        CoinTypeEnum.OKEX.code -> {
            address = walletInfo.oktAddress
        }
    }
    return address
}

/**
 * 弹出 资产概览 弹窗
 */
fun showAssetsOverviewPop(context: Context) {
    XPopup.Builder(context)
        .dismissOnBackPressed(true)
        .dismissOnTouchOutside(true)
        .asCustom(AssetsOverviewPop(context))
        .show()
}


/**
 * 弹出通用提示弹窗
 */
fun showCommonHint(context: Context, title: String?, content: String? = "", isSuccess: Boolean?) {
    XPopup.Builder(context)
        .dismissOnBackPressed(true)
        .dismissOnTouchOutside(true)
        .asCustom(CommonHintPop(context, title, content, isSuccess))
        .show()
}


/**
 * 弹出追加手续费弹窗
 */
fun showAddFeeHint(
    context: Context, fromAddress: String, toAddress: String, withdrawalTxHash: String,
    fromChain: String, toChain: String, oldFee: String,
    assetChainId: Int, assetAssetId: Int, assetContractAddress: String
) {
    XPopup.Builder(context)
        .dismissOnBackPressed(true)
        .dismissOnTouchOutside(true)
        .asCustom(
            AddFeePop(
                context,
                fromAddress,
                toAddress,
                withdrawalTxHash,
                fromChain,
                toChain,
                oldFee,
                assetChainId,
                assetAssetId,
                assetContractAddress
            )
        )
        .show()
}

/**
 * 弹出web页面的操作Layout
 */
fun showWebOption(context: Context, clickListener: View.OnClickListener) {
    XPopup.Builder(context)
        .dismissOnBackPressed(true)
        .dismissOnTouchOutside(true)
        .asCustom(WebOptionPop(context, clickListener))
        .show()
}

/**
 * 通过传入的usd数量换算金额
 */
fun getAmountByUsd(usd: String): String? {
    var amount: String? = null
    if (UserDao.currency == MoneyTypeEnum.USD.code) {
        amount = "$ $usd"
    } else if (UserDao.currency == MoneyTypeEnum.RMB.code) {
//        amount = "￥ $usd"
        amount = if (UserDao.rmbExcahnge != null) {
            "￥ " + Arith.mul(UserDao.rmbExcahnge, usd.toDouble()).toString()
        } else {
            //固定一个汇率
            "￥ " + Arith.mul(UserDao.rmbExcahnge, 6.5).toString()
        }
    }
    return amount
}


fun addZero(number: Int): Double? {
    var num = 1.0
    for (i in 0 until number) {
        num *= 10
    }
    return num
}


fun getAmount(amount: String, number: Int): String {

    var doubleString = addZero(number)?.let { Arith.div(amount.toDouble(), it, number) }


    return doubleString.toString()


}

/**
 * 复制到剪贴板
 *
 * @param context 上下文
 * @param text    要复制的内容
 */
fun copyToClipboard(context: Context, text: String) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText("text", text))
    showToast(context.getString(R.string.copy_to_clipboard))

}


fun getCoinType(address: String): String {
    var coinType = ""
    if (address.length < 5) {
        return coinType
    }
    if (Api.ISDEBUG) {
        //取前五位判断
        coinType = when {
            address.startsWith("tNULS") -> {
                CoinTypeEnum.NULS.code
            }
            address.startsWith("TNVT") -> {
                CoinTypeEnum.NERVE.code
            }
            address.startsWith("0x") -> {
                //以太坊系列
                "eths"
            }
            else -> {
                //暂时不支持的地址
                "error"
            }
        }

    } else {
        coinType = when {
            address.startsWith("NULS") -> {
                CoinTypeEnum.NULS.code
            }
            address.startsWith("NVT") -> {
                CoinTypeEnum.NERVE.code
            }
            address.startsWith("0x") -> {
                //以太坊系列
                "eths"
            }
            else -> {
                //暂时不支持的地址
                "error"
            }
        }

    }
    return coinType
}


/**
 * 加载币种头像
 */
fun loadCoinIcon(context: Context, imageView: ImageView, iconUrl: String) {
    Glide.with(context).load(iconUrl).error(R.mipmap.token_logo).into(imageView)
}


//判断被交易资产的资产类型 （因为不能直接通过缩写去判断）  返回该资产的symbol
fun getCoinType(assetsEntity: AssetsEntity, chain: String): String {
    var coinType = CoinTypeEnum.TOKEN.symbol
    var chainList = UserDao.getChainConfig()
////    if(!TextUtils.isEmpty(assetsEntity.contractAddress)){
////        //如果资产的contractAddress 不为空 直接当成合约资产处理
////        return CoinTypeEnum.TOKEN.symbol
////    }
//    return assetsEntity.symbol
    if (chainList != null && chainList?.size!! > 0) {
        for (index in 0 until chainList?.size!!) {
            if (assetsEntity.nerveChainId == chainList[index]?.mainAsset?.chainId
                && assetsEntity.nerveAssetId == chainList[index]?.mainAsset?.assetId
            ) {
                coinType = chainList[index].mainAsset.symbol
            }
        }
    }
    return coinType
}

/**
 * 判断该资产是否该链的主资产
 */
fun isMainAsset(assetsEntity: AssetsEntity, chain: String): Boolean {
    var isMainAsset = false
//    var chainList = UserDao.getChainConfig()
//    if(chainList!=null&& chainList?.size!! >0){
//        for(index in 0 until chainList?.size!!){
//            if(chainList[index].chain ==chain){
//                if(assetsEntity.chainId == chainList[index].mainAsset.chainId
//                    &&assetsEntity.assetId == chainList[index].mainAsset.assetId){
//                    isMainAsset = true
//                }
//            }
//        }
//    }

    if (assetsEntity.configType == 1) {//1:主资产  2：
        isMainAsset = true
    }

    return isMainAsset
}


/**
 * 获取该链的主资产
 */
fun getMainAsset(assets: List<AssetsEntity>, chain: String): AssetsEntity {
    var mainAsset = AssetsEntity()
    if (assets != null && assets?.size!! > 0) {
        for (index in 0 until assets?.size!!) {
            if (isMainAsset(assets[index], chain)) {
                mainAsset = assets[index]
            }
        }
    }
    return mainAsset
}


/**
 * 获取资产在toChain中的配置信息
 */
fun getHeterogeneousInfo(assetsEntity: AssetsEntity, toChain: String): HeterogeneousInfoBean {
    var heterogeneousInfo: HeterogeneousInfoBean? = HeterogeneousInfoBean()
    for (index in 0 until assetsEntity.heterogeneousList.size) {
        if (assetsEntity.heterogeneousList[index].chainName == toChain) {
            heterogeneousInfo = assetsEntity.heterogeneousList[index]
        }
    }

    return heterogeneousInfo!!
}


/**
 *  判断资产是否支持当前交易转账
 */
fun isSupportTransfer(fromChain: String, toChain: String, item: AssetsEntity): Boolean {
    var isSupport = false
    when (fromChain) {
        CoinTypeEnum.NULS.code -> {
            when (toChain) {
                CoinTypeEnum.NERVE.code -> {
                    if (item.isNulsCross) {
                        isSupport = true
                    }
                }
                CoinTypeEnum.NULS.code -> {
                    isSupport = true
                }
                else -> {
                    if (item.isNulsCross && item.heterogeneousList != null && checkHeterogeneous(
                            item.heterogeneousList,
                            toChain
                        )
                    ) {
                        isSupport = true
                    }
                }
            }
        }
        CoinTypeEnum.NERVE.code -> {
            when (toChain) {
                CoinTypeEnum.NERVE.code -> {
                    isSupport = true
                }
                CoinTypeEnum.NULS.code -> {
                    if (item.isNulsCross) {
                        isSupport = true
                    }
                }
                else -> {
                    if (item.heterogeneousList != null && checkHeterogeneous(
                            item.heterogeneousList,
                            toChain
                        )
                    ) {
                        isSupport = true
                    }
                }
            }
        }
        else -> {//bsc heco eth
            if (fromChain == toChain) {
                isSupport = true
            } else {
                when (toChain) {
                    CoinTypeEnum.NULS.code -> {
                        if (item.isNulsCross) {
                            isSupport = true
                        }
                    }
                    CoinTypeEnum.NERVE.code -> {
//                            if(item.heterogeneousList!=null&&checkHeterogeneous(item.heterogeneousList,toChain)){
//                                isSupport = true
//                            }
                        isSupport = true
                    }
                    else -> {
                        if (item.heterogeneousList != null && checkHeterogeneous(
                                item.heterogeneousList,
                                toChain
                            )
                        ) {
                            isSupport = true
                        }
                    }
                }
            }
        }
    }
    return isSupport
}

/**
 * 判断该资产是否支持当前跨链交易
 */
private fun checkHeterogeneous(
    heterogeneousList: List<HeterogeneousInfoBean>,
    chain: String
): Boolean {
    //是否支持该跨链交易
    var isSupport = false
    if (heterogeneousList != null && heterogeneousList.isNotEmpty()) {
        for (index in heterogeneousList.indices) {
            if (chain == heterogeneousList[index].chainName) {
                isSupport = true
            }
        }
    }
    return isSupport
}


fun getImageUrl(symbol: String): String {
    return Api.IMAGE_HOST_URL + symbol + ".png"
}


fun getNumberOfDecimalPlaces(numStr: String): Int {
    val string = BigDecimal(numStr).toPlainString()
    val index = string.indexOf(".")
    return if (index < 0) 0 else string.length - index - 1
}


/**
 * 获取链网络连接状态
 */
fun checkDAppNetwork(context: Context, dapp: DAppBean) {
    if (dapp.chain != UserDao.defaultChain) {
        //dapp不支持当前链 提示用户切换
        XPopup.Builder(context)
            .dismissOnBackPressed(true)
            .dismissOnTouchOutside(true)
            .asConfirm(
                context.getString(R.string.hint),
                "当前DApp只支持:" + dapp.chain + "\n Current DApp only supports:" + dapp.chain,
                context.getString(R.string.cancel),
                context.getString(R.string.choice_wallet),
                OnConfirmListener {
                    showChoiceWalletAndChainPop(context, UserDao.defaultChain!!, dapp.chain, true,
                        ChoiceChainPop.ChoiceListener { walletInfo, assetsEntity -> //设置新的默认钱包
                            UserDao.defaultWallet = walletInfo?.nulsAddress.toString()
                            UserDao.defaultChain = assetsEntity?.chain.toString()

                            EventBus.getDefault().post(RefreshEvent())
                            checkDAppNetwork(context, dapp)
                        })
                },
                OnCancelListener {
                    //取消没有操作
                },
                false
            )
            .show()

        return
    }
    RetrofitClient.getInstance().invokeGet(context, Api.NETWORK_STATUS, mapOf())
        .subscribe(object :
            RecObserver<BaseResponse<HashMap<String, NetWorkStatusBean>>>(context, true, false,Api.NETWORK_STATUS) {
            override fun onSuccess(t: BaseResponse<HashMap<String, NetWorkStatusBean>>?) {
                if (t?.data != null) {
                    if (t.data[UserDao.defaultChain] != null) {
                        var network: NetWorkStatusBean = t.data[UserDao.defaultChain]!!
                        if (network.isRunning) {
                            //网络连接正常 判断是否对改DApp进行过授权，如果没有授权，则需要先授权，再进入
                            var address = getAddressByCoinType(
                                WalletInfoDaoHelper.loadDefaultWallet(),
                                UserDao.defaultChain
                            )
                            if (AuthDaoHelper.checkAuth(dapp.url, UserDao.defaultChain, address)) {
                                //更新最近使用dapp
                                DAppDaoHelper.insertWallet(dapp)
                                DAppActivity.start(context, dapp, address)
                            } else {
                                //未查询到授权信息  弹出授权弹窗弹窗 让用户输入密码确认
                                showDAppAuthPop(
                                    context,
                                    dapp.url,
                                    UserDao.defaultChain,
                                    address,
                                    object : DAppAuthPop.AuthResultListener {
                                        override fun success(longAuth: Boolean) {
                                            //授权成功 存入授权信息 跳转到Dapp页面
                                            AuthDaoHelper.insertAuth(
                                                dapp.url,
                                                UserDao.defaultChain,
                                                address,
                                                longAuth
                                            )
                                            DAppDaoHelper.insertWallet(dapp)
                                            DAppActivity.start(context, dapp, address)
                                        }
                                    })
                            }
                        }
                    }
                }
            }

            override fun onFail(msg: String, code: Int) {
                showToast(msg)
            }
        })

}

/**
 * 弹出通用提示弹窗
 */
fun showDAppAuthPop(
    context: Context,
    url: String,
    chain: String,
    address: String,
    listener: DAppAuthPop.AuthResultListener
) {
    XPopup.Builder(context)
        .dismissOnBackPressed(true)
        .dismissOnTouchOutside(true)
        .asCustom(DAppAuthPop(context, url, chain, address, listener))
        .show()
}


/**
 * 显示版本更新弹窗
 *
 * showHint  是否提示用户 ”您已经是最新版本“  默认不提示
 *
 */
fun showUpdatePopWindow(
    context: Context,
    showHint: Boolean
) {
    RetrofitClient.getInstance()
        .invokeGet(context, Api.CHECK_UPDATE, mapOf("language" to UserDao.language))
        .subscribe(object : RecObserver<BaseResponse<AppVersionBean>>(context, true, false,Api.CHECK_UPDATE) {
            override fun onSuccess(t: BaseResponse<AppVersionBean>?) {
                if (t?.data != null) {
                    if (t.data.versionCode > AppUtils.getAppVersionCode()) {
                        XPopup.Builder(context)
                            .asCustom(CheckUpdatePop(context, t.data))
                            .show()
                    } else {
                        if (showHint) {
                            showToast(context.getString(R.string.now_version))
                        }
                    }
                }
            }

            override fun onFail(msg: String, code: Int) {
                showToast(msg)
            }
        })
}
