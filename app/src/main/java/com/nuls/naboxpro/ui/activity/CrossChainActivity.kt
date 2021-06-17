package com.nuls.naboxpro.ui.activity

import android.content.Context
import android.content.Intent
import android.icu.text.CaseMap
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.db.WalletInfoDaoHelper
import com.nuls.naboxpro.entity.AssetsEntity
import com.nuls.naboxpro.entity.GaslimitEntity
import com.nuls.naboxpro.entity.HeterogeneousInfoBean
import com.nuls.naboxpro.entity.WalletInfo
import com.nuls.naboxpro.enums.CoinTypeEnum
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.BaseResponse
import com.nuls.naboxpro.net.api.RetrofitClient
import com.nuls.naboxpro.net.observable.RecObserver
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.ui.popup.ChoiceAssetPop
import com.nuls.naboxpro.ui.popup.CrossChainPop
import com.nuls.naboxpro.ui.popup.InputPasswordPop
import com.nuls.naboxpro.utils.*


import com.nuls.naboxpro.wallet.transfer.SendBSCTransction
import com.nuls.naboxpro.wallet.transfer.SendETHTransction
import com.nuls.naboxpro.wallet.transfer.SendHECOTransction

import kotlinx.android.synthetic.main.activity_cross_chain_layout.*
import kotlinx.android.synthetic.main.activity_cross_chain_layout.btn_auth
import kotlinx.android.synthetic.main.activity_cross_chain_layout.btn_next
import kotlinx.android.synthetic.main.activity_cross_chain_layout.progress_fee
import kotlinx.android.synthetic.main.activity_cross_chain_layout.tv_all
import kotlinx.android.synthetic.main.activity_cross_chain_layout.tv_fee_low
import kotlinx.android.synthetic.main.activity_cross_chain_layout.tv_network
import kotlinx.android.synthetic.main.activity_cross_chain_layout.tv_num
import kotlinx.android.synthetic.main.activity_cross_chain_layout.tv_use_amount
import kotlinx.android.synthetic.main.activity_trans_layout.*

import kotlinx.android.synthetic.main.base_title_layout.*
import network.nerve.heterogeneous.BSCTool
import network.nerve.heterogeneous.ETHTool
import network.nerve.heterogeneous.HTTool
import network.nerve.heterogeneous.OKTTool
import java.math.BigInteger
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


/**
 * 跨链划转
 */
class CrossChainActivity : BaseActivity() {


    override fun getLayoutId() = R.layout.activity_cross_chain_layout

    //出金 链
    var fromChain = ""

    //出金地址
    var fromAddress = ""

    //入金 链
    var toChain = ""

    //入金 地址
    var toAddress = ""

    var walletInfo: WalletInfo? = null

    //主资产交易时候所需要的的gaslimit
    var mainAssetGasLimit: Long = 35000

    //token 交易所需要的的gaslimit
    var tokenGasLimit: Long = 100000

    /**
     * 被交易的资产
     */
    var assetsEntity: AssetsEntity? = null

    //需要的nvt手续费
    var nvtFee: BigInteger = BigInteger("0")

    var feeLevel = 1.0
    private val mExecutor = Executors.newSingleThreadScheduledExecutor()
    private var mFuture: ScheduledFuture<*>? = null

    /**
     * 记录跨链交易第一笔手续费
     */
    var firstTxPayFee: String = ""

    /**
     * 异构链token授权hash
     */
    var tokenAuthHash = ""

    /**
     * 资产胡列表
     */
    var assetsList: List<AssetsEntity>? = null

    override fun initView() {
        back.setOnClickListener {
            finish()
        }
        immersionBar {
            statusBarDarkFont(true, 0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }
        walletInfo = WalletInfoDaoHelper.loadDefaultWallet()
        if (intent != null) {
            if (intent.getStringExtra("fromAddress") != null) {
                fromAddress = intent.getStringExtra("fromAddress")
                tv_from_adress.text = fromAddress

            }

            if (intent.getStringExtra("fromChain") != null) {
                fromChain = intent.getStringExtra("fromChain")

                if (fromChain == CoinTypeEnum.NERVE.code) {
                    //跨链划转页面，to地址默认选择Nerve网络地址，默认资产选择from链原生资产，如果是从Nerve网络跨链划转，to地址默认选择NULS网络地址
                    tv_to_adress.text = walletInfo?.nulsAddress
                    toChain = CoinTypeEnum.NULS.code()
                    toAddress = walletInfo?.nulsAddress.toString()
                } else {
                    tv_to_adress.text = walletInfo?.nerveAddress
                    toChain = CoinTypeEnum.NERVE.code()
                    toAddress = walletInfo?.nerveAddress.toString()
                }
                initAccelerate()
                tv_from_chain.text = fromChain
                tv_to_chain.text = toChain
                if (intent.getSerializableExtra("assets") != null) {
                    if (isSupportTransfer(
                            fromChain,
                            toChain,
                            intent.getSerializableExtra("assets") as AssetsEntity
                        )
                    ) {
                        choiceAssetSuccess(intent.getSerializableExtra("assets") as AssetsEntity)
                    }
                } else {
                    getAssetsList(fromChain, fromAddress)
                }
            }
        }
        tv_all.setOnClickListener {
            if (assetsEntity != null) {
                tv_num.setText(assetsEntity?.decimals?.let { it1 ->
                    NaboxUtils.coinValueOf(
                        BigInteger(assetsEntity?.balance),
                        it1
                    )
                })
            }
        }
        check_accelerate.setOnClickListener {
            feeLevel = if (check_accelerate.isChecked) {
                1.3
            } else {
                1.0
            }
            if (!TextUtils.isEmpty(fromChain) && assetsEntity != null && !TextUtils.isEmpty(toChain) && !TextUtils.isEmpty(
                    tv_num.text
                )
            ) {
                calculationFee(assetsEntity!!, tv_num.text.toString())
            }
        }
        layout_to.setOnClickListener {
            showChoiceChainPop(
                this,
                fromChain,
                WalletInfoDaoHelper.loadDefaultWallet(),
                object : CrossChainPop.ChoiceListener {
                    override fun itemClick(chain: String, address: String) {
                        if (!TextUtils.isEmpty(fromChain) && fromChain == chain) {
                            showToast(getString(R.string.chain_cant_same))

                        } else {
                            tv_to_chain.text = chain
                            tv_to_adress.text = address
                            toChain = chain
                            toAddress = address

                            //清空所选资产和输入的金额 让用户重新选择再走一次判断流程
                            clearChoiceAsset()
                            initAccelerate()

                        }
                    }
                })
        }
        tv_network.setOnClickListener {
            if (TextUtils.isEmpty(fromChain) || TextUtils.isEmpty(fromAddress)) {
                showToast(getString(R.string.choice_fromchain))
            } else if (TextUtils.isEmpty(toChain) || TextUtils.isEmpty(toAddress)) {
                showToast(getString(R.string.choice_tochain))
            } else {
                showChoiceAssetPop(
                    this,
                    fromChain,
                    fromAddress,
                    toChain,
                    object : ChoiceAssetPop.OnItemClickListener {
                        override fun itemClick(item: AssetsEntity, items: List<AssetsEntity>) {
                            assetsList = items
                            if (isSupportTransfer(fromChain, toChain, item)) {
                                choiceAssetSuccess(item)
                            } else {
                                showToast("该资产不支持当前交易")
                            }
                        }
                    })
            }
        }
        tv_num.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (amountDelayRun != null) {
                    //每次editText有变化的时候，则移除上次发出的延迟线程
                    handler.removeCallbacks(amountDelayRun)
                }
                if (TextUtils.isEmpty(p0)) {
                    amountString = ""
                    tv_payfee.text = ""
                } else {
                    amountString = p0.toString()
                    handler.postDelayed(amountDelayRun, 800)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })


        //确认
        btn_next.setOnClickListener {
            when {
                TextUtils.isEmpty(fromChain) -> {
                    showToast(getString(R.string.choice_fromchain))
                }
                TextUtils.isEmpty(toChain) -> {
                    showToast(getString(R.string.choice_tochain))
                }
                assetsEntity == null -> {
                    showToast(getString(R.string.choice_transfer_asset))
                }
                TextUtils.isEmpty(tv_num.text) -> {
                    showToast(getString(R.string.please_enter_the_amount_of_transfer))
                }
                TextUtils.isEmpty(tv_payfee.text) -> {
                    showToast(getString(R.string.fee_calculation_faild))
                }
                else -> {
                    var payFee = ""
                    if (!TextUtils.isEmpty(tv_payfee.text)) {
                        payFee = tv_payfee.text.toString()
                    }
                    if (!checkUserBalance()) {
                        showToast(R.string.balance_not_enough)
                        return@setOnClickListener
                    }
                    ConfirmTransferActivity.start(
                        this@CrossChainActivity,
                        fromAddress,
                        toAddress,
                        fromChain,
                        toChain,
                        tv_num.text.toString().trim(),
                        assetsEntity!!,
                        payFee,
                        "",
                        feeLevel
                    )
                }
            }
        }

        btn_auth.setOnClickListener {
            when {
//                TextUtils.isEmpty(edit_address.text) -> showToast(getString(R.string.please_enter_the_transfer_address))
                assetsEntity == null -> showToast(getString(R.string.choice_transfer_asset))
                else -> {
                    if (mFuture != null) {
                        //说明是授权中，不处理点击事件，给用户一种无法点击的错觉
                        return@setOnClickListener
                    }
                    //输入密码获得私钥
                    showPasswordPop(this, object : InputPasswordPop.InputListener {
                        override fun inputSuccess(password: String?) {
                            var walletInfo = WalletInfoDaoHelper.loadDefaultWallet()
                            if (walletInfo.validatePassword(password)) {
                                var heterogeneousInfo =
                                    getHeterogeneousInfo(assetsEntity!!, fromChain!!)
                                tokenAuthHash = ""
                                Thread(Runnable {
                                    //发起授权 并且拿到授权交易的hash
                                    try {
                                        when (fromChain) {
                                            CoinTypeEnum.ETH.code -> {
                                                tokenAuthHash = ETHTool.authorization(
                                                    intent.getStringExtra("fromAddress"),
                                                    walletInfo.decrypt(password),
                                                    heterogeneousInfo.heterogeneousChainMultySignAddress,
                                                    heterogeneousInfo.contractAddress
                                                )
                                            }
                                            CoinTypeEnum.BSC.code -> {
                                                tokenAuthHash = BSCTool.authorization(
                                                    intent.getStringExtra("fromAddress"),
                                                    walletInfo.decrypt(password),
                                                    heterogeneousInfo.heterogeneousChainMultySignAddress,
                                                    heterogeneousInfo.contractAddress
                                                )
                                            }
                                            CoinTypeEnum.HECO.code -> {
                                                tokenAuthHash = HTTool.authorization(
                                                    intent.getStringExtra("fromAddress"),
                                                    walletInfo.decrypt(password),
                                                    heterogeneousInfo.heterogeneousChainMultySignAddress,
                                                    heterogeneousInfo.contractAddress
                                                )

                                            }
                                            CoinTypeEnum.OKEX.code -> {
                                                tokenAuthHash = OKTTool.authorization(
                                                    intent.getStringExtra("fromAddress"),
                                                    walletInfo.decrypt(password),
                                                    heterogeneousInfo.heterogeneousChainMultySignAddress,
                                                    heterogeneousInfo.contractAddress
                                                )
                                            }
                                        }
                                    } catch (e: Exception) {
                                       runOnUiThread {
                                           var s = e.message
                                           if (s!!.contains("insufficient funds")) {
                                               showToast(getString(R.string.balance_not_enough))
                                           } else {
                                               showToast(s)
                                           }
                                       }
                                    }
                                    if (!TextUtils.isEmpty(tokenAuthHash)) {
                                        runOnUiThread {
                                            btn_auth.text = getString(R.string.authing)
                                        }
                                        //轮询当前授权的结果
                                        authQuery()
                                    }

                                }).start()
                            } else {
                                showToast(getString(R.string.password_error))
                            }
                        }
                    })

                }
            }
        }

    }

    var amountString = ""

    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private val amountDelayRun = Runnable {
        if (assetsEntity == null) {

            showToast(getString(R.string.choice_transfer_asset))
            tv_num.text = null
            return@Runnable
        }
        if (!TextUtils.isEmpty(tv_num.text) && tv_num.text.startsWith(".")) {
            tv_num.text = null
            showToast(getString(R.string.amount_format_error))
            return@Runnable
        }

        if (assetsEntity != null && !TextUtils.isEmpty(tv_num.text)) {
            if (BigInteger(assetsEntity?.balance) < NaboxUtils.getAmount(
                    tv_num.text.toString(),
                    assetsEntity?.decimals!!
                )
            ) {
                //可用余额小于输入金额 弹出提示  把输入金额设置Wie空
                tv_num.text = null
                showToast(
                    "最多可转" + NaboxUtils.coinValueOf(
                        BigInteger(assetsEntity?.balance),
                        assetsEntity?.decimals!!
                    )
                )
                return@Runnable
            }
        }
        if (getNumberOfDecimalPlaces(tv_num.text.toString()) > assetsEntity?.decimals!!) {
            showToast(assetsEntity?.symbol + "最多支持" + assetsEntity?.decimals + "位小数")
            tv_num.text = null
            return@Runnable
        }
        //判断输入地址类型
        if (!TextUtils.isEmpty(fromChain) && assetsEntity != null && !TextUtils.isEmpty(toChain) && !TextUtils.isEmpty(
                tv_num.text
            )
        ) {
            calculationFee(assetsEntity!!, tv_num.text.toString())
        }
    }
    private val handler = Handler()

    /**
     * 检测余额是否足够发起交易
     * 返回true表示余额足够 false表示余额不够
     */
    private fun checkUserBalance(): Boolean {

        return calculationPayFee()
    }


    /**
     * 根据字符串获取有效胡字段
     */
    private fun getEffectiveFree(content: String): String {
        var length = content.length
        var index: Int
        for (index in 0 until length) {
            if (content[index].toInt() == 46 || (content[index].toInt() in 48..57)) {
            } else {
                return content.substring(0, index)
            }
        }
        return "0"
    }


    /**
     * 清空所选资产
     */
    fun clearChoiceAsset() {
        assetsEntity = null
        tv_network.text = ""
        tv_payfee.text = getString(R.string.null_content)
        tv_num.text = null
        tv_use_amount.text = null
    }

    /**
     * 判断该资产是否支持当前跨链交易
     */
    private fun checkHeterogeneous(heterogeneousList: List<HeterogeneousInfoBean>): Boolean {
        //是否支持该跨链交易
        var isSupport = false
        if (heterogeneousList != null && heterogeneousList.isNotEmpty()) {
            for (index in heterogeneousList.indices) {
                if (toChain == heterogeneousList[index].chainName) {
                    isSupport = true
                }
            }
        }
        return isSupport
    }


    private fun choiceAssetSuccess(item: AssetsEntity) {
        assetsEntity = item
        tv_num.text = null
        tv_network.text = item.symbol
        if (!TextUtils.isEmpty(tv_num.text) && !TextUtils.isEmpty(fromChain) && !TextUtils.isEmpty(
                toChain
            )
        ) {
            calculationFee(item, tv_num.text.toString().trim())
        }

        tv_use_amount.text = NaboxUtils.coinValueOf(BigInteger(item.balance), item.decimals)


        //选择资产完成 判断是否需要进行异构链授权
        if (fromChain != CoinTypeEnum.NULS.code && fromChain != CoinTypeEnum.NERVE.code) {
            if (item.configType != 1) {//1 为主资产   其他为合约资产
                //token才需要判断是否授权
                Thread(Runnable {
                    var heterogeneousInfo = getHeterogeneousInfo(item!!, fromChain!!)
                    when (fromChain) {
                        CoinTypeEnum.ETH.code -> {
                            if (!ETHTool.isAuthorized(
                                    intent.getStringExtra("fromAddress"),
                                    heterogeneousInfo.heterogeneousChainMultySignAddress,
                                    heterogeneousInfo.contractAddress
                                )
                            ) {
                                notAuth()
                            } else {
                                alreadyAuth()
                            }
                        }
                        CoinTypeEnum.BSC.code -> {
                            if (!BSCTool.isAuthorized(
                                    intent.getStringExtra("fromAddress"),
                                    heterogeneousInfo.heterogeneousChainMultySignAddress,
                                    heterogeneousInfo.contractAddress
                                )
                            ) {
                                notAuth()
                            } else {
                                alreadyAuth()
                            }
                        }
                        CoinTypeEnum.HECO.code -> {
                            if (!HTTool.isAuthorized(
                                    intent.getStringExtra("fromAddress"),
                                    heterogeneousInfo.heterogeneousChainMultySignAddress,
                                    heterogeneousInfo.contractAddress
                                )
                            ) {
                                notAuth()
                            } else {
                                alreadyAuth()
                            }
                        }
                        CoinTypeEnum.OKEX.code -> {
                            if (!OKTTool.isAuthorized(
                                    intent.getStringExtra("fromAddress"),
                                    heterogeneousInfo.heterogeneousChainMultySignAddress,
                                    heterogeneousInfo.contractAddress
                                )
                            ) {
                                notAuth()
                            } else {
                                alreadyAuth()
                            }
                        }
                    }
                }).start()
            }
        }

    }


    /**
     * 计算手续费  跨链划转需要组装两笔交易的手续费（涉及到异构链）
     */
    private fun calculationFee(assetsEntity: AssetsEntity, amount: String) {
        showFeeLoading()
        firstTxPayFee = ""
        //还是先判断出入金所属链  在判断转移资产的类型
        //第一笔交易的手续费
        var payFeeFirst: String = getString(R.string.null_content)
        //先将手续费文本置为空
        tv_payfee.text = null
        when (fromChain) {
            CoinTypeEnum.NULS.code -> {
                when (toChain) {
                    CoinTypeEnum.NERVE.code -> {
                        if (TextUtils.isEmpty(assetsEntity.contractAddress)) {
                            hideFeeLoading()
                            tv_payfee.text = "0.01NULS"
                        } else {
                            getGasLimit(
                                fromChain,
                                getAddressByCoinType(
                                    WalletInfoDaoHelper.loadDefaultWallet(),
                                    fromChain
                                ),
                                assetsEntity.contractAddress,
                                NaboxUtils.getAmount(amount, assetsEntity.decimals)
                            )
                        }
                    }
                    else -> {
                        if (TextUtils.isEmpty(assetsEntity.contractAddress)) {
                            //非合约资产
                            payFeeFirst = "0.01NULS"
                            getFeeByNvtCross(toChain, payFeeFirst)
                        } else {
                            getGasLimit(
                                fromChain,
                                getAddressByCoinType(
                                    WalletInfoDaoHelper.loadDefaultWallet(),
                                    fromChain
                                ),
                                assetsEntity.contractAddress,
                                NaboxUtils.getAmount(amount, assetsEntity.decimals)
                            )
                        }
                    }
                }
            }
            CoinTypeEnum.NERVE.code -> {
                when (toChain) {
                    CoinTypeEnum.NULS.code -> {
                        tv_payfee.text = "0.01NULS + 0.01NVT"
                        hideFeeLoading()
                    }
                    else -> {
                        getFeeByNvtCross(toChain)
                    }
                }

            }
            else -> {
                if (isMainAsset(assetsEntity, fromChain)) {
                    getGasPrice(mainAssetGasLimit, fromChain, toChain)
                } else {
                    getGasPrice(tokenGasLimit, fromChain, toChain)
                }
            }
        }
    }

    override fun initData() {

    }

    /**
     * 显示手续费计算动画
     */
    fun showFeeLoading() {
        if (progress_fee.visibility == View.GONE) {
            progress_fee.visibility = View.VISIBLE
        }

    }

    /**
     * 隐藏手续费计算动画
     */
    fun hideFeeLoading() {
        if (progress_fee.visibility == View.VISIBLE) {
            progress_fee.visibility = View.GONE
        }
    }


    /**
     * languagestringY语言：EN/CHSchainstringY地址所属链：NULS、NERVE、Ethereum、BSCaddressstringY合约调用者地址valuebigIntegerY调用者向合约地址转入的主网资产金额，没有此业务时填0contractAddressstringY合约地址methodNamestringY合约方法methodDescstringN合约方法描述，若合约内方法没有重载，参数可以为空
     */
    private fun getGasLimit(
        chain: String,
        address: String,
        contractAddress: String? = "",
        amount: BigInteger
    ) {


        var array = arrayOf(
            getAddressByCoinType(
                WalletInfoDaoHelper.loadDefaultWallet(),
                CoinTypeEnum.NERVE.code
            ), amount
        )


//        var transfer:String = if(fromChain==toChain){
//            "transfer"
//        }else{
//            "transferCrossChain"
//        }
//        var value = if(fromChain==toChain){
//            0
//        }else{
//            10000000
//        }
        RetrofitClient.getInstance().invokePostBody(
            this, Api.CALL_GASLIMIT, mapOf(
                "language" to UserDao.language,
                "chain" to chain,
                "address" to address,
                "value" to 10000000,
                "contractAddress" to contractAddress,
                "methodName" to "transferCrossChain",
                "args" to array
            )
        ).subscribe(object : RecObserver<BaseResponse<GaslimitEntity>>(this, false, false,Api.CALL_GASLIMIT) {
            override fun onSuccess(t: BaseResponse<GaslimitEntity>?) {
Log.e("invokePostBody",Api.CALL_GASLIMIT+"="+System.currentTimeMillis())
                if (t?.data != null) {

                    if (fromChain == CoinTypeEnum.NULS.code && toChain != CoinTypeEnum.NERVE.code) {
                        getFeeByNvtCross(
                            toChain,
                            NaboxUtils.coinValueOf(
                                BigInteger.valueOf(t.data.gasLimit * 25 + 1000000),
                                8
                            )
                        )
                    } else {
                        hideFeeLoading()
                        tv_payfee.text = NaboxUtils.coinValueOf(
                            BigInteger.valueOf(t.data.gasLimit * 25 + 1000000),
                            8
                        ) + "NULS"
                    }
                }
            }

            override fun onFail(msg: String, code: Int) {
                Log.e("invokePostBody",Api.CALL_GASLIMIT+"="+System.currentTimeMillis())
                hideFeeLoading()
                showToast(msg)
            }
        })
    }


    //查询异构跨链手续费  oldFee 需要拼接的其他手续费 可以为空
    private fun getFeeByNvtCross(chain: String, firstFee: String? = "") {
        RetrofitClient.getInstance().invokePostBody(
            this, Api.NVT_CROSS_FEE, mapOf(
                "language" to UserDao.language,
                "chain" to chain
            )
        ).subscribe(object : RecObserver<BaseResponse<Number>>(this, false, false,Api.NVT_CROSS_FEE) {
            override fun onSuccess(t: BaseResponse<Number>?) {
                Log.e("invokePostBody",Api.NVT_CROSS_FEE+"="+System.currentTimeMillis())
                hideFeeLoading()
                //小数点八位
                if (t?.data != null) {
                    if (TextUtils.isEmpty(firstFee)) {
                        firstTxPayFee = firstFee.toString()
//                        tv_payfee.text  =  NaboxUtils.coinValueOf(BigInteger.valueOf(t.data.toLong()),8)+ "NVT"
                        tv_payfee.text = NaboxUtils.coinValueOf(
                            BigInteger(
                                Arith.mulNorGrouping(
                                    t.data.toDouble(),
                                    feeLevel,
                                    8
                                )
                            ), 8
                        ) + " NVT"
                    } else {
                        tv_payfee.text = firstFee + "+" + NaboxUtils.coinValueOf(
                            BigInteger(
                                Arith.mulNorGrouping(
                                    t.data.toDouble(),
                                    feeLevel,
                                    8
                                )
                            ), 8
                        ) + " NVT"
//                        tv_payfee.text  = firstFee+"+"+ NaboxUtils.coinValueOf(BigInteger.valueOf(t.data.toLong()),8)+ "NVT"
                    }

                }
            }

            override fun onFail(msg: String, code: Int) {
                Log.e("invokePostBody",Api.NVT_CROSS_FEE+"="+System.currentTimeMillis())
                hideFeeLoading()
                showToast(msg)
            }
        })
    }

    //查询gas价格
    private fun getGasPrice(gasLimit: Long, chain: String, toChain: String) {
        var fistPayFee = ""
        RetrofitClient.getInstance().invokePostBody(
            this, Api.GAS_PRICE, mapOf(
                "language" to UserDao.language,
                "chain" to chain
            )
        ).subscribe(object : RecObserver<BaseResponse<Number>>(this, false, false,Api.GAS_PRICE) {
            override fun onSuccess(t: BaseResponse<Number>?) {
                Log.e("invokePostBody",Api.GAS_PRICE+"="+System.currentTimeMillis())
                if (t?.data != null) {
//                        fistPayFee = NaboxUtils.coinValueOf(BigInteger.valueOf(gasLimit*t.data.toLong()),CoinTypeEnum.getDecimalByCode(chain))+CoinTypeEnum.getByCode(chain).symbol()
                    fistPayFee = NaboxUtils.coinValueOf(
                        BigInteger(
                            Arith.mulNorGrouping(
                                feeLevel,
                                gasLimit * t.data.toDouble(),
                                CoinTypeEnum.getDecimalByCode(chain)
                            )
                        ), CoinTypeEnum.getDecimalByCode(chain)
                    ) + CoinTypeEnum.getByCode(chain).symbol()

                    when (toChain) {
                        CoinTypeEnum.NULS.code -> {
                            hideFeeLoading()
                            tv_payfee.text = fistPayFee + "+" + "0.01NULS + 0.01NVT"
                        }
                        CoinTypeEnum.NERVE.code -> {
                            hideFeeLoading()
                            tv_payfee.text = fistPayFee
                        }
                        else -> {
                            getFeeByNvtCross(toChain, fistPayFee)
                        }
                    }
                }
            }

            override fun onFail(msg: String, code: Int) {
                Log.e("invokePostBody",Api.GAS_PRICE+"="+System.currentTimeMillis())
                //获取gasprice失败
                hideFeeLoading()
            }
        })

    }


    fun calculationPayFee(): Boolean {

        //手续费
        var fee = getEffectiveFree(tv_payfee.text.toString()).toDouble()
        //输入的金额
        var amount = tv_num.text.toString().trim().toDouble()
        //可用余额
        var totalBalance = NaboxUtils.coinValueOf(
            BigInteger(assetsEntity?.balance),
            assetsEntity?.decimals!!
        ).toDouble()

        var isSuccess = false
        when (fromChain) {
            CoinTypeEnum.NULS.code -> {
                //手续费 0.01 nuls
                if (assetsEntity?.configType == 1) {//1 主链资产   2 token   被交易资产是nuls
                    //直接相加去和可用资产判断
                    return amount + fee <= totalBalance
                } else {
                    //判断当前账户的NULS余额是否有0.01
                    return (amount <= totalBalance) && (findFeeFromCode(CoinTypeEnum.NULS.code) >= 0.01)
                }
            }
            CoinTypeEnum.NERVE.code -> {
                when (toChain) {
                    CoinTypeEnum.NULS.code -> {
//                        手续费是"0.01NULS+0.01NVT"
                        if (assetsEntity?.configType == 1) {//1 主链资产   2 token   被交易资产是NVT
                            //直接相加去和可用资产判断
                            //注意，这里的手续费有两种！！！！！！！！
                            return (amount + fee <= totalBalance) && (findFeeFromCode(CoinTypeEnum.NERVE.code) >= 0.01)
                        } else {
                            //非主链资产里面有一个特殊情况 就是NULS 这里需要单独判断
                            //NULS 和非NULS 的逻辑不一样
                            if (assetsEntity!!.chain.equals(CoinTypeEnum.NULS.code)) {
                                return (amount + 0.01 <= totalBalance) && (findFeeFromCode(
                                    CoinTypeEnum.NERVE.code
                                ) >= 0.01)
                            } else {
                                return (amount <= totalBalance) && (findFeeFromCode(
                                    CoinTypeEnum.NERVE.code
                                ) >= 0.01) && (findFeeFromCode(
                                    CoinTypeEnum.NULS.code
                                ) >= 0.01)
                            }
                        }
                    }
                    else -> {
                        //nvt 跨链到异构链
                        //手续费为 firstTxPayFee的值  单位NVT
                        //判断被交易的资产是不是NVT
                        if (assetsEntity?.configType == 1) {//主链资产  这里就是nvt
                            //判断输入金额 + 手续费 和可用余额的大小关系
                            return amount + fee <= totalBalance


                        } else {
                            //被交易资产不是nvt
                            //判断当前账户是否有足够多的nvt余额用来支付手续费
                            return (amount <= totalBalance) && (findFeeFromCode(CoinTypeEnum.NERVE.code) >= fee)

                        }


                    }
                }
            }
            else -> {
                //手续费为 firstTxPayFee的值  单位fromChain的主链资产
                if (assetsEntity?.configType == 1) {//交易的是主链资产
                    //判断输入金额 + 手续费 和可用余额的大小关系
                    return amount + fee <= totalBalance

                } else {
                    //判断当前账户是否有足够多的主链资产余额用来支付手续费
                    var boo: Boolean = amount <= totalBalance
                    var mainAmount = findFeeFromCode(fromChain)
                    return boo && (mainAmount >= fee)

                }
            }
        }
        return isSuccess;
    }

    private fun findFeeFromCode(code: String): Double {
        var size: Int? = assetsList?.size
        for (i in 0 until size!!) {
            if (assetsList!![i]?.chain.equals(code)) {
                return assetsList!![i]?.balance.toDouble()
            }
        }
        return 0.0
    }


    companion object {

        fun start(
            context: Context,
            fromAddress: String,
            fromChain: String,
            assets: AssetsEntity? = null
        ) {
            var intent: Intent = Intent(context, CrossChainActivity().javaClass)
            intent.putExtra("fromAddress", fromAddress)
            intent.putExtra("fromChain", fromChain)
            intent.putExtra("assets", assets)
            context.startActivity(intent)
        }


    }


    /**
     * 获取当前链资产列表，并且将
     */
    private fun getAssetsList(chain: String, address: String) {
        RetrofitClient.getInstance().invokePostBody(
            this, Api.GET_ASSETS, mapOf(
                "language" to UserDao.language,
                "chain" to chain,
                "address" to address
            )
        ).subscribe(object : RecObserver<BaseResponse<List<AssetsEntity>>>(this, false, false, Api.GET_ASSETS) {
            override fun onSuccess(t: BaseResponse<List<AssetsEntity>>?) {
                Log.e("invokePostBody",Api.GET_ASSETS+"="+System.currentTimeMillis())
                if (t?.data != null) {
                    //获取成功后 默认选中该链的主资产
                    var assetsEntity = getMainAsset(t.data, chain)
                    if (assetsEntity != null) {
                        //获取主资产成功
                        if (isSupportTransfer(fromChain, toChain, assetsEntity)) {
                            choiceAssetSuccess(assetsEntity)
                        }
                    }
                }
            }

            override fun onFail(msg: String, code: Int) {
                Log.e("invokePostBody",Api.GET_ASSETS+"="+System.currentTimeMillis())
                showToast(msg)
            }
        })
    }


    private fun initAccelerate() {
        if (fromChain == CoinTypeEnum.ETH.code
            || fromChain == CoinTypeEnum.BSC.code
            || fromChain == CoinTypeEnum.HECO.code
            || fromChain == CoinTypeEnum.OKEX.code
            || toChain == CoinTypeEnum.ETH.code
            || toChain == CoinTypeEnum.BSC.code
            || toChain == CoinTypeEnum.HECO.code
            || toChain == CoinTypeEnum.OKEX.code
        ) {
            layout_accelerate.visibility = View.VISIBLE
        } else {
            layout_accelerate.visibility = View.GONE
            feeLevel = 1.0
            check_accelerate.isChecked = false
        }
        if (!TextUtils.isEmpty(fromChain) && assetsEntity != null && !TextUtils.isEmpty(toChain) && !TextUtils.isEmpty(
                tv_num.text
            )
        ) {
            calculationFee(assetsEntity!!, tv_num.text.toString())
        }

    }


    fun authQuery() {
        //如果有轮询在进行 先关闭正在运行定时任务
        mFuture?.run {
            if (!isCancelled) cancel(true)
            null
        }

        //创建并开启定时任务
        mFuture = mExecutor.scheduleWithFixedDelay({
            Log.i("naboxpro", "轮询异构链token转账授权情况")
            var heterogeneousInfo = getHeterogeneousInfo(assetsEntity!!, fromChain!!)
            when (fromChain) {
                CoinTypeEnum.ETH.code -> {
                    if (!ETHTool.isAuthorized(
                            intent.getStringExtra("fromAddress"),
                            heterogeneousInfo.heterogeneousChainMultySignAddress,
                            heterogeneousInfo.contractAddress
                        )
                    ) {
                        notAuth()
                    } else {
                        alreadyAuth()
                    }
                }
                CoinTypeEnum.BSC.code -> {
                    if (!BSCTool.isAuthorized(
                            intent.getStringExtra("fromAddress"),
                            heterogeneousInfo.heterogeneousChainMultySignAddress,
                            heterogeneousInfo.contractAddress
                        )
                    ) {
                        notAuth()
                    } else {
                        alreadyAuth()
                    }
                }
                CoinTypeEnum.HECO.code -> {
                    if (!HTTool.isAuthorized(
                            intent.getStringExtra("fromAddress"),
                            heterogeneousInfo.heterogeneousChainMultySignAddress,
                            heterogeneousInfo.contractAddress
                        )
                    ) {
                        notAuth()
                    } else {
                        alreadyAuth()
                    }
                }
                CoinTypeEnum.OKEX.code -> {
                    if (!OKTTool.isAuthorized(
                            intent.getStringExtra("fromAddress"),
                            heterogeneousInfo.heterogeneousChainMultySignAddress,
                            heterogeneousInfo.contractAddress
                        )
                    ) {
                        notAuth()
                    } else {
                        alreadyAuth()
                    }
                }
            }

        }, 1000, 1000, TimeUnit.MILLISECONDS)
    }

    private fun notAuth() {
        runOnUiThread {
            Log.i("naboxpro", "token未授权")
            if (btn_auth.visibility == View.GONE) {
                btn_auth.visibility = View.VISIBLE
            }
            if (btn_next.visibility == View.VISIBLE) {
                btn_next.visibility = View.GONE
            }
        }

    }


    private fun alreadyAuth() {

        runOnUiThread {
            Log.i("naboxpro", "token已经授权")
            //如果已经授权 看一下是否有授权轮询，有就把轮询关了
            if (mFuture != null) {
                mFuture?.run {
                    if (!isCancelled) cancel(true)
                    null
                }
            }
            if (btn_auth.visibility == View.VISIBLE) {
                btn_auth.visibility = View.GONE
                btn_auth.text = getString(R.string.authorize)
            }
            if (btn_next.visibility == View.GONE) {
                btn_next.visibility = View.VISIBLE
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mFuture?.run {
            if (!isCancelled) cancel(true)
            null
        }
    }

}