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
 * ????????????
 */
class CrossChainActivity : BaseActivity() {


    override fun getLayoutId() = R.layout.activity_cross_chain_layout

    //?????? ???
    var fromChain = ""

    //????????????
    var fromAddress = ""

    //?????? ???
    var toChain = ""

    //?????? ??????
    var toAddress = ""

    var walletInfo: WalletInfo? = null

    //????????????????????????????????????gaslimit
    var mainAssetGasLimit: Long = 35000

    //token ?????????????????????gaslimit
    var tokenGasLimit: Long = 100000

    /**
     * ??????????????????
     */
    var assetsEntity: AssetsEntity? = null

    //?????????nvt?????????
    var nvtFee: BigInteger = BigInteger("0")

    var feeLevel = 1.0
    private val mExecutor = Executors.newSingleThreadScheduledExecutor()
    private var mFuture: ScheduledFuture<*>? = null

    /**
     * ????????????????????????????????????
     */
    var firstTxPayFee: String = ""

    /**
     * ?????????token??????hash
     */
    var tokenAuthHash = ""

    /**
     * ???????????????
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
                    //?????????????????????to??????????????????Nerve?????????????????????????????????from??????????????????????????????Nerve?????????????????????to??????????????????NULS????????????
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

                            //???????????????????????????????????? ?????????????????????????????????????????????
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
                                showToast("??????????????????????????????")
                            }
                        }
                    })
            }
        }
        tv_num.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (amountDelayRun != null) {
                    //??????editText?????????????????????????????????????????????????????????
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


        //??????
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
                        //?????????????????????????????????????????????????????????????????????????????????
                        return@setOnClickListener
                    }
                    //????????????????????????
                    showPasswordPop(this, object : InputPasswordPop.InputListener {
                        override fun inputSuccess(password: String?) {
                            var walletInfo = WalletInfoDaoHelper.loadDefaultWallet()
                            if (walletInfo.validatePassword(password)) {
                                var heterogeneousInfo =
                                    getHeterogeneousInfo(assetsEntity!!, fromChain!!)
                                tokenAuthHash = ""
                                Thread(Runnable {
                                    //???????????? ???????????????????????????hash
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
                                        //???????????????????????????
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
     * ???????????????????????????????????????????????????
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
                //?????????????????????????????? ????????????  ?????????????????????Wie???
                tv_num.text = null
                showToast(
                    "????????????" + NaboxUtils.coinValueOf(
                        BigInteger(assetsEntity?.balance),
                        assetsEntity?.decimals!!
                    )
                )
                return@Runnable
            }
        }
        if (getNumberOfDecimalPlaces(tv_num.text.toString()) > assetsEntity?.decimals!!) {
            showToast(assetsEntity?.symbol + "????????????" + assetsEntity?.decimals + "?????????")
            tv_num.text = null
            return@Runnable
        }
        //????????????????????????
        if (!TextUtils.isEmpty(fromChain) && assetsEntity != null && !TextUtils.isEmpty(toChain) && !TextUtils.isEmpty(
                tv_num.text
            )
        ) {
            calculationFee(assetsEntity!!, tv_num.text.toString())
        }
    }
    private val handler = Handler()

    /**
     * ????????????????????????????????????
     * ??????true?????????????????? false??????????????????
     */
    private fun checkUserBalance(): Boolean {

        return calculationPayFee()
    }


    /**
     * ????????????????????????????????????
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
     * ??????????????????
     */
    fun clearChoiceAsset() {
        assetsEntity = null
        tv_network.text = ""
        tv_payfee.text = getString(R.string.null_content)
        tv_num.text = null
        tv_use_amount.text = null
    }

    /**
     * ?????????????????????????????????????????????
     */
    private fun checkHeterogeneous(heterogeneousList: List<HeterogeneousInfoBean>): Boolean {
        //???????????????????????????
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


        //?????????????????? ???????????????????????????????????????
        if (fromChain != CoinTypeEnum.NULS.code && fromChain != CoinTypeEnum.NERVE.code) {
            if (item.configType != 1) {//1 ????????????   ?????????????????????
                //token???????????????????????????
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
     * ???????????????  ????????????????????????????????????????????????????????????????????????
     */
    private fun calculationFee(assetsEntity: AssetsEntity, amount: String) {
        showFeeLoading()
        firstTxPayFee = ""
        //?????????????????????????????????  ??????????????????????????????
        //???????????????????????????
        var payFeeFirst: String = getString(R.string.null_content)
        //??????????????????????????????
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
                            //???????????????
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
     * ???????????????????????????
     */
    fun showFeeLoading() {
        if (progress_fee.visibility == View.GONE) {
            progress_fee.visibility = View.VISIBLE
        }

    }

    /**
     * ???????????????????????????
     */
    fun hideFeeLoading() {
        if (progress_fee.visibility == View.VISIBLE) {
            progress_fee.visibility = View.GONE
        }
    }


    /**
     * languagestringY?????????EN/CHSchainstringY??????????????????NULS???NERVE???Ethereum???BSCaddressstringY?????????????????????valuebigIntegerY???????????????????????????????????????????????????????????????????????????0contractAddressstringY????????????methodNamestringY????????????methodDescstringN????????????????????????????????????????????????????????????????????????
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


    //???????????????????????????  oldFee ?????????????????????????????? ????????????
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
                //???????????????
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

    //??????gas??????
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
                //??????gasprice??????
                hideFeeLoading()
            }
        })

    }


    fun calculationPayFee(): Boolean {

        //?????????
        var fee = getEffectiveFree(tv_payfee.text.toString()).toDouble()
        //???????????????
        var amount = tv_num.text.toString().trim().toDouble()
        //????????????
        var totalBalance = NaboxUtils.coinValueOf(
            BigInteger(assetsEntity?.balance),
            assetsEntity?.decimals!!
        ).toDouble()

        var isSuccess = false
        when (fromChain) {
            CoinTypeEnum.NULS.code -> {
                //????????? 0.01 nuls
                if (assetsEntity?.configType == 1) {//1 ????????????   2 token   ??????????????????nuls
                    //????????????????????????????????????
                    return amount + fee <= totalBalance
                } else {
                    //?????????????????????NULS???????????????0.01
                    return (amount <= totalBalance) && (findFeeFromCode(CoinTypeEnum.NULS.code) >= 0.01)
                }
            }
            CoinTypeEnum.NERVE.code -> {
                when (toChain) {
                    CoinTypeEnum.NULS.code -> {
//                        ????????????"0.01NULS+0.01NVT"
                        if (assetsEntity?.configType == 1) {//1 ????????????   2 token   ??????????????????NVT
                            //????????????????????????????????????
                            //????????????????????????????????????????????????????????????
                            return (amount + fee <= totalBalance) && (findFeeFromCode(CoinTypeEnum.NERVE.code) >= 0.01)
                        } else {
                            //?????????????????????????????????????????? ??????NULS ????????????????????????
                            //NULS ??????NULS ??????????????????
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
                        //nvt ??????????????????
                        //???????????? firstTxPayFee??????  ??????NVT
                        //?????????????????????????????????NVT
                        if (assetsEntity?.configType == 1) {//????????????  ????????????nvt
                            //?????????????????? + ????????? ??????????????????????????????
                            return amount + fee <= totalBalance


                        } else {
                            //?????????????????????nvt
                            //???????????????????????????????????????nvt???????????????????????????
                            return (amount <= totalBalance) && (findFeeFromCode(CoinTypeEnum.NERVE.code) >= fee)

                        }


                    }
                }
            }
            else -> {
                //???????????? firstTxPayFee??????  ??????fromChain???????????????
                if (assetsEntity?.configType == 1) {//????????????????????????
                    //?????????????????? + ????????? ??????????????????????????????
                    return amount + fee <= totalBalance

                } else {
                    //??????????????????????????????????????????????????????????????????????????????
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
     * ???????????????????????????????????????
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
                    //??????????????? ??????????????????????????????
                    var assetsEntity = getMainAsset(t.data, chain)
                    if (assetsEntity != null) {
                        //?????????????????????
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
        //???????????????????????? ?????????????????????????????????
        mFuture?.run {
            if (!isCancelled) cancel(true)
            null
        }

        //???????????????????????????
        mFuture = mExecutor.scheduleWithFixedDelay({
            Log.i("naboxpro", "???????????????token??????????????????")
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
            Log.i("naboxpro", "token?????????")
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
            Log.i("naboxpro", "token????????????")
            //?????????????????? ??????????????????????????????????????????????????????
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