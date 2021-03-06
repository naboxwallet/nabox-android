package com.nuls.naboxpro.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.RadioButton
import com.gyf.immersionbar.ktx.immersionBar
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.db.WalletInfoDaoHelper
import com.nuls.naboxpro.entity.AssetsEntity
import com.nuls.naboxpro.entity.GaslimitEntity
import com.nuls.naboxpro.entity.HeterogeneousInfoBean
import com.nuls.naboxpro.entity.WalletInfo
import com.nuls.naboxpro.enums.CoinTypeEnum
import com.nuls.naboxpro.enums.TransferType
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.BaseResponse
import com.nuls.naboxpro.net.api.RetrofitClient
import com.nuls.naboxpro.net.observable.RecObserver
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.ui.popup.ChoiceAssetPop
import com.nuls.naboxpro.ui.popup.InputPasswordPop
import com.nuls.naboxpro.ui.popup.LoadingPopup
import com.nuls.naboxpro.utils.*
import com.nuls.naboxpro.wallet.Na
import com.nuls.naboxpro.wallet.transfer.SendBSCTransction
import com.nuls.naboxpro.wallet.transfer.SendETHTransction
import com.nuls.naboxpro.wallet.transfer.SendHECOTransction
import com.nuls.naboxpro.wallet.transfer.SendTransctions
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_trans_layout.*
import kotlinx.android.synthetic.main.base_title_layout.*
import network.nerve.heterogeneous.BSCTool
import network.nerve.heterogeneous.ETHTool
import network.nerve.heterogeneous.HTTool
import network.nerve.heterogeneous.OKTTool
import org.apache.commons.net.imap.IMAPClient
import java.math.BigDecimal


import java.math.BigInteger
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


/**
 * ????????????????????????nerve????????????????????????????????????5??????????????????????????????4???????????????????????????????????????????????????Nerve??????2?????????

???????????????????????????3??????NULS???Nerve ??? ?????????????????????????????????Nerve?????????????????????NULS???nerve?????????????????????????????????????????????????????????????????????????????????????????????????????????3????????????????????????????????????????????????????????????
??????????????????Nerve????????????????????????????????????????????????Nerve?????????????????????????????????????????????????????????????????????

??????????????????????????????????????????????????????Nerve?????????????????????????????????????????????????????????NULS???Nerve??????????????????????????????????????????

???????????????????????????????????????????????????????????????????????????????????????????????????

?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
 */
class TransferActivity : BaseActivity() {


    var editString = ""
    var amountString = ""

    //????????????
    var fromChain = ""

    //?????????????????????
    var toChain = ""

    /**
     * ??????????????????
     */
    var lowFee: Double = 0.8

    /**
     * ?????????????????????
     */
    var normalFee: Double = 1.0

    /**
     * ????????????
     */
    var highFee: Double = 1.3

    //????????????????????????????????????gaslimit
    var mainAssetGasLimit: Long = 35000

    //token ?????????????????????gaslimit
    var tokenGasLimit: Long = 100000

    /**
     * ??????????????????
     */
    var assetsEntity: AssetsEntity? = null


    /**
     * ????????????????????????
     */
    var defaultFeeLevel = 1.0

    /**
     * ?????????  text
     */
    var feeStr: String = ""

    /**
     * ?????????token??????hash
     */
    var tokenAuthHash = ""

    /**
     * ???????????????
     */
    var assetsList: List<AssetsEntity>? = null


    override fun getLayoutId() = R.layout.activity_trans_layout

    private val mExecutor = Executors.newSingleThreadScheduledExecutor()
    private var mFuture: ScheduledFuture<*>? = null

    override fun initView() {
        immersionBar {
            statusBarDarkFont(true, 0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }

        tv_title.text = resources.getString(R.string.transfer_accounts)
        back.setOnClickListener {
            finish()
        }
        iv_connect.setOnClickListener {
            //???????????????
            ContactsListActivity.startForResult(this, 10086)
        }

        if (intent != null && intent.getStringExtra("coinType") != null) {
            //???????????????????????????

            fromChain = intent.getStringExtra("coinType")

            when (intent.getStringExtra("coinType")) {
                CoinTypeEnum.NULS.code -> {
                    rb_eth.visibility = View.GONE
                    rb_heco.visibility = View.GONE
                    rb_bsc.visibility = View.GONE
                    rb_okex.visibility = View.GONE
                }
                CoinTypeEnum.NERVE.code -> {
                    //??????????????????

                }
                CoinTypeEnum.BSC.code -> {
                    rb_nuls.visibility = View.GONE
                    rb_eth.visibility = View.GONE
                    rb_heco.visibility = View.GONE
                    rb_okex.visibility = View.GONE
                }
                CoinTypeEnum.ETH.code -> {
                    rb_nuls.visibility = View.GONE
                    rb_heco.visibility = View.GONE
                    rb_bsc.visibility = View.GONE
                    rb_okex.visibility = View.GONE
                }
                CoinTypeEnum.HECO.code -> {
                    rb_nuls.visibility = View.GONE
                    rb_eth.visibility = View.GONE
                    rb_bsc.visibility = View.GONE
                    rb_okex.visibility = View.GONE
                }
                CoinTypeEnum.OKEX.code -> {
                    rb_nuls.visibility = View.GONE
                    rb_eth.visibility = View.GONE
                    rb_bsc.visibility = View.GONE
                    rb_heco.visibility = View.GONE
                }
            }
            radio_group.setOnCheckedChangeListener { radioGroup, i ->
                //???????????????????????????????????????
                tv_num.text = null
                when {
                    rb_nuls.isChecked -> {
                        toChain = CoinTypeEnum.NULS.code
                    }
                    rb_eth.isChecked -> {
                        toChain = CoinTypeEnum.ETH.code
                    }
                    rb_nvt.isChecked -> {
                        toChain = CoinTypeEnum.NERVE.code
                    }
                    rb_bsc.isChecked -> {
                        toChain = CoinTypeEnum.BSC.code
                    }
                    rb_heco.isChecked -> {
                        toChain = CoinTypeEnum.HECO.code
                    }
                    rb_okex.isChecked -> {
                        toChain = CoinTypeEnum.OKEX.code
                    }
                }
            }
            tv_network.setOnClickListener {

                if (TextUtils.isEmpty(edit_address.text)) {
                    showToast(getString(R.string.please_enter_the_transfer_address))
                    return@setOnClickListener
                }
                when {
                    rb_nuls.isChecked -> {
                        toChain = CoinTypeEnum.NULS.code
                    }
                    rb_eth.isChecked -> {
                        toChain = CoinTypeEnum.ETH.code
                    }
                    rb_nvt.isChecked -> {
                        toChain = CoinTypeEnum.NERVE.code
                    }
                    rb_bsc.isChecked -> {
                        toChain = CoinTypeEnum.BSC.code
                    }
                    rb_heco.isChecked -> {
                        toChain = CoinTypeEnum.HECO.code
                    }
                    rb_okex.isChecked -> {
                        toChain = CoinTypeEnum.OKEX.code
                    }
                    else -> {
                        showToast("??????????????????")
                        return@setOnClickListener
                    }

                }
                showChoiceAssetPop(
                    this,
                    fromChain,
                    intent.getStringExtra("fromAddress"),
                    toChain,
                    object : ChoiceAssetPop.OnItemClickListener {
                        override fun itemClick(item: AssetsEntity, items: List<AssetsEntity>) {
//                        checkTransSupport(item)
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


        //???????????????
        layout_low.setOnClickListener {
            checkLowFee()
        }
        //???????????????
        layout_normal.setOnClickListener {
            checkNormalFee()
        }
        //???????????????
        layout_high.setOnClickListener {
            checkHighFee()
        }

        iv_scan.setOnClickListener {
            //?????????????????????
            var intent: Intent = Intent(this, ScanActivity().javaClass)
            startActivityForResult(intent, 10085)


        }
        edit_address.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (delayRun != null) {
                    //??????editText?????????????????????????????????????????????????????????
                    handler.removeCallbacks(delayRun)
                }
                if (TextUtils.isEmpty(p0)) {
                    editString = ""
                } else {
                    editString = p0.toString()
                }

                if (editString.length > 4) {//????????????4???????????????????????????????????????????????????
                    //??????800ms???????????????????????????????????????????????????run??????
                    handler.postDelayed(delayRun, 800)
                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
        tv_num.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (amountDelayRun != null) {
                    //??????editText?????????????????????????????????????????????????????????
                    handler.removeCallbacks(amountDelayRun)
                }
                if (TextUtils.isEmpty(p0)) {
                    amountString = ""
                    tv_fee.text = ""
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
        tv_all.setOnClickListener {

            when {
                TextUtils.isEmpty(edit_address.text) -> {
                    showToast(getString(R.string.please_enter_the_transfer_address))
                }
//                TextUtils.isEmpty(tv_num.text) -> {
//                    showToast(getString(R.string.please_enter_the_amount_of_transfer))
//                }
                assetsEntity == null -> {
                    showToast(getString(R.string.choice_transfer_asset))
                }
                else -> {
                    if (assetsEntity != null) {
                        tv_num.setText(assetsEntity?.decimals?.let { it1 ->
                            NaboxUtils.coinValueOf(
                                BigInteger(assetsEntity?.balance),
                                it1
                            )
                        })
                    }
                    //????????????????????????????????????????????????????????????
//                    var transferCoin  = getCoinType(assetsEntity!!,fromChain)
//                      if(fromChain==toChain
//                          &&fromChain==CoinTypeEnum.NULS.code){
//                          //nuls ????????????
//                          when(transferCoin){
//
//                          }
//
//                      }

                }
            }

        }


//        val amount = Na.parseNuls(amountStr).toBigInteger()
        btn_next.setOnClickListener {

            when {

                TextUtils.isEmpty(feeStr) && TextUtils.isEmpty(tv_fee.text) -> {
                    showToast(getString(R.string.fee_calculation_faild))
                }
                TextUtils.isEmpty(edit_address.text) -> {
                    showToast(getString(R.string.please_enter_the_transfer_address))
                }
                TextUtils.isEmpty(tv_num.text) -> {
                    showToast(getString(R.string.please_enter_the_amount_of_transfer))
                }
                assetsEntity == null -> {
                    showToast(getString(R.string.choice_transfer_asset))
                }
                else -> {

                    if (!checkUserBalance()) {
                        showToast(R.string.balance_not_enough)
                        return@setOnClickListener
                    }

                    //???????????????????????????
                    if (fromChain != toChain) {
                        if (!check_crosschain_hint.isChecked) {
                            showToast(getString(R.string.crosschain_hint_warning))
                            return@setOnClickListener
                        }
                    }
                    ConfirmTransferActivity.start(
                        this,
                        intent.getStringExtra("fromAddress"),
                        edit_address.text.toString().trim(),
                        fromChain,
                        toChain,
                        tv_num.text.toString().trim(),
                        assetsEntity!!,
                        if (!TextUtils.isEmpty(feeStr)) {
                            feeStr
                        } else {
                            tv_fee.text.toString().trim()
                        },
                        edit_hint.text.toString().trim(),
                        defaultFeeLevel
                    )

                }
            }
        }
        btn_auth.setOnClickListener {
            when {
                TextUtils.isEmpty(edit_address.text) -> showToast(getString(R.string.please_enter_the_transfer_address))
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
                                        if (!TextUtils.isEmpty(tokenAuthHash)) {
                                            runOnUiThread {
                                                btn_auth.text = getString(R.string.authing)
                                            }
                                            //???????????????????????????
                                            authQuery()
                                        }


                                } catch (e: Exception) {
                                   runOnUiThread {
                                       var s = e.message
                                       if (s!!.contains("insufficient funds")) {
                                           showToast(getString(R.string.balance_not_enough))
                                       }else{
                                           showToast(s)
                                       }
                                   }
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


    /**
     * ??????????????????
     */
    private fun checkLowFee() {
        defaultFeeLevel = lowFee
        layout_low.setBackgroundResource(R.drawable.shape_main_noradus)
        title_low.setTextColor(getColor(R.color.white))
        tv_fee_low.setTextColor(getColor(R.color.white))
        feeStr = tv_fee_low.text.toString()
        layout_normal.setBackgroundResource(R.drawable.shape_fff2f2f4)
        title_normal.setTextColor(getColor(R.color.color_8F95A8))
        tv_fee_nomal.setTextColor(getColor(R.color.color_8F95A8))
        layout_high.setBackgroundResource(R.drawable.shape_fff2f2f4)
        title_high.setTextColor(getColor(R.color.color_8F95A8))
        tv_fee_high.setTextColor(getColor(R.color.color_8F95A8))
    }

    /**
     * ?????????????????????
     */
    private fun checkNormalFee() {
        defaultFeeLevel = normalFee
        feeStr = tv_fee_nomal.text.toString()
        layout_low.setBackgroundResource(R.drawable.shape_fff2f2f4)
        title_low.setTextColor(getColor(R.color.color_8F95A8))
        tv_fee_low.setTextColor(getColor(R.color.color_8F95A8))
        layout_normal.setBackgroundResource(R.drawable.shape_main_noradus)
        title_normal.setTextColor(getColor(R.color.white))
        tv_fee_nomal.setTextColor(getColor(R.color.white))
        layout_high.setBackgroundResource(R.drawable.shape_fff2f2f4)
        title_high.setTextColor(getColor(R.color.color_8F95A8))
        tv_fee_high.setTextColor(getColor(R.color.color_8F95A8))
    }

    /**
     * ??????????????????
     */
    private fun checkHighFee() {
        defaultFeeLevel = highFee
        layout_low.setBackgroundResource(R.drawable.shape_fff2f2f4)
        title_low.setTextColor(getColor(R.color.color_8F95A8))
        tv_fee_low.setTextColor(getColor(R.color.color_8F95A8))
        feeStr = tv_fee_high.text.toString()
        layout_normal.setBackgroundResource(R.drawable.shape_fff2f2f4)
        title_normal.setTextColor(getColor(R.color.color_8F95A8))
        tv_fee_nomal.setTextColor(getColor(R.color.color_8F95A8))
        layout_high.setBackgroundResource(R.drawable.shape_main_noradus)
        title_high.setTextColor(getColor(R.color.white))
        tv_fee_high.setTextColor(getColor(R.color.white))
    }

    private val handler = Handler()

    /**
     * ???????????????????????????????????????????????????
     */
    private val delayRun = Runnable {
        //????????????????????????
        if (!TextUtils.isEmpty(editString)) {
            when (getCoinType(editString)) {
                CoinTypeEnum.NULS.code -> {
                    initNetFlag(intent.getStringExtra("coinType"), CoinTypeEnum.NULS.code)
                }
                CoinTypeEnum.NERVE.code -> {
                    initNetFlag(intent.getStringExtra("coinType"), CoinTypeEnum.NERVE.code)
                }
                "eths" -> {//eth?????????
                    initNetFlag(intent.getStringExtra("coinType"), "eths")
                }
                "error" -> {//?????????????????????
                    notSurport()
                }
            }

        }
    }

    /**
     * ???????????????????????????????????????????????????
     */
    private val amountDelayRun = Runnable {

        if (!TextUtils.isEmpty(tv_num.text) && tv_num.text.toString().startsWith(".")) {
            showToast(getString(R.string.amount_format_error))
            tv_num.text = null
            return@Runnable
        }
        if (assetsEntity != null && getNumberOfDecimalPlaces(tv_num.text.toString()) > assetsEntity?.decimals!!) {
            showToast(assetsEntity?.symbol + "????????????" + assetsEntity?.decimals + "?????????")
            tv_num.text = null
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

        //???????????????
        if (!TextUtils.isEmpty(tv_num.text) && assetsEntity != null && !TextUtils.isEmpty(toChain)) {
            initFeeAndRemark(fromChain, toChain, assetsEntity!!, tv_num.text.toString())
        }
    }

    override fun initData() {

    }

    companion object {
        fun start(
            context: Context,
            coinType: String,
            fromAddress: String,
            assetsEntity: AssetsEntity? = null
        ) {
            var intent = Intent(context, TransferActivity().javaClass)
            intent.putExtra("coinType", coinType)
            intent.putExtra("fromAddress", fromAddress)
            intent.putExtra("assetsEntity", assetsEntity)
            context.startActivity(intent)
        }
    }

    private fun choiceAssetSuccess(item: AssetsEntity) {
        tv_network.text = item.symbol
        assetsEntity = item
        if (!TextUtils.isEmpty(tv_num.text) && !TextUtils.isEmpty(toChain)) {
            initFeeAndRemark(fromChain, toChain, assetsEntity!!, tv_num.text.toString())
        }
        tv_use_amount.text = getString(R.string.available) + ": " + NaboxUtils.coinValueOf(
            BigInteger(item.balance),
            item.decimals
        )
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


    private fun choiceAssetFail() {
        tv_network.text = ""
        assetsEntity = null
        tv_fee.text = ""

    }


    /**
     * ??????????????????????????? ??????????????????
     */
    private fun initNetFlag(fromCoinType: String, toCoinType: String) {
        //?????????????????? ????????????????????????
        radio_group.clearCheck()
        //??????from address ???to address ?????????????????????????????????????????????
        when (fromCoinType) {
            CoinTypeEnum.NULS.code -> {
                when (toCoinType) {
                    CoinTypeEnum.NULS.code -> {

                        checkNULSFlag()
                    }
                    CoinTypeEnum.NERVE.code -> {

                        checkNVTFlag()
                    }
                    else -> {
                        //?????????????????????
                        notSurport()
                    }
                }
            }
            CoinTypeEnum.NERVE.code -> {
                when (toCoinType) {
                    CoinTypeEnum.NULS.code -> {

                        checkNULSFlag()
                    }
                    CoinTypeEnum.NERVE.code -> {

                        checkNVTFlag()
                    }
                    else -> {

                        checkETHSFlag()
                    }
                }
            }
            CoinTypeEnum.BSC.code -> {
                when (toCoinType) {
                    CoinTypeEnum.NERVE.code -> {
                        checkNVTFlag()
                    }
                    else -> {
                        checkBSCFlag()
                    }
                }
            }
            CoinTypeEnum.HECO.code -> {
                when (toCoinType) {
                    CoinTypeEnum.NERVE.code -> {

                        checkNVTFlag()
                    }
                    else -> {

                        checkHECOFlag()
                    }
                }
            }
            CoinTypeEnum.ETH.code -> {
                when (toCoinType) {
                    CoinTypeEnum.NERVE.code -> {
                        checkNVTFlag()
                    }
                    else -> {
                        checkETHFlag()
                    }
                }
            }
            CoinTypeEnum.OKEX.code -> {
                when (toCoinType) {
                    CoinTypeEnum.NERVE.code -> {

                        checkNVTFlag()
                    }
                    else -> {
                        checkOKexFlag()
                    }
                }
            }
        }
        if (fromChain == toChain) {//???????????? ??????????????????

            layout_remark.visibility = View.VISIBLE
            layout_crosschain_hint.visibility = View.GONE
            check_crosschain_hint.isChecked = false
        } else {
            edit_hint.text = null
            layout_remark.visibility = View.GONE
            layout_crosschain_hint.visibility = View.VISIBLE
        }
    }


    /**
     * ??????nuls
     */
    private fun checkNULSFlag() {
        toChain = CoinTypeEnum.NULS.code
        tv_not_sup.visibility = View.INVISIBLE
        rb_nuls.isChecked = true
        rb_nuls.visibility = View.VISIBLE
        rb_eth.visibility = View.GONE
        rb_nvt.visibility = View.GONE
        rb_bsc.visibility = View.GONE
        rb_heco.visibility = View.GONE
        rb_okex.visibility = View.GONE
    }

    /**
     * ??????bsc
     */
    private fun checkBSCFlag() {
        toChain = CoinTypeEnum.BSC.code
        tv_not_sup.visibility = View.INVISIBLE
        rb_bsc.isChecked = true
        rb_nuls.visibility = View.GONE
        rb_eth.visibility = View.GONE
        rb_nvt.visibility = View.GONE
        rb_bsc.visibility = View.VISIBLE
        rb_heco.visibility = View.GONE
        rb_okex.visibility = View.GONE
    }

    /**
     * ????????????
     */
    private fun notSurport() {

        tv_not_sup.visibility = View.VISIBLE
        rb_nuls.visibility = View.GONE
        rb_eth.visibility = View.GONE
        rb_nvt.visibility = View.GONE
        rb_bsc.visibility = View.GONE
        rb_heco.visibility = View.GONE
        rb_okex.visibility = View.GONE
    }

    /**
     * ??????heco
     */
    private fun checkHECOFlag() {
        rb_heco.isChecked = true
        toChain = CoinTypeEnum.HECO.code
        tv_not_sup.visibility = View.INVISIBLE
        rb_nuls.visibility = View.GONE
        rb_eth.visibility = View.GONE
        rb_nvt.visibility = View.GONE
        rb_bsc.visibility = View.GONE
        rb_heco.visibility = View.VISIBLE
        rb_okex.visibility = View.GONE
    }

    /**
     * ??????eth
     */
    private fun checkETHFlag() {
        toChain = CoinTypeEnum.ETH.code
        rb_eth.isChecked = true
        tv_not_sup.visibility = View.INVISIBLE
        rb_nuls.visibility = View.GONE
        rb_eth.visibility = View.VISIBLE
        rb_nvt.visibility = View.GONE
        rb_bsc.visibility = View.GONE
        rb_heco.visibility = View.GONE
        rb_okex.visibility = View.GONE
    }

    /**
     * ??????eth
     */
    private fun checkOKexFlag() {
        toChain = CoinTypeEnum.OKEX.code
        rb_okex.isChecked = true
        tv_not_sup.visibility = View.INVISIBLE
        rb_nuls.visibility = View.GONE
        rb_okex.visibility = View.VISIBLE
        rb_nvt.visibility = View.GONE
        rb_bsc.visibility = View.GONE
        rb_heco.visibility = View.GONE
        rb_eth.visibility = View.GONE
    }

    /**
     * ??????nvt
     */
    private fun checkNVTFlag() {
        toChain = CoinTypeEnum.NERVE.code
        tv_not_sup.visibility = View.INVISIBLE
        rb_nvt.isChecked = true
        rb_nvt.visibility = View.VISIBLE
        rb_eth.visibility = View.GONE
        rb_nuls.visibility = View.GONE
        rb_bsc.visibility = View.GONE
        rb_heco.visibility = View.GONE
        rb_okex.visibility = View.GONE
    }

    /**
     * ??????????????????
     */
    private fun checkETHSFlag() {

        tv_not_sup.visibility = View.INVISIBLE
        //??????????????? ????????????????????? ????????????
        rb_nvt.visibility = View.GONE
        rb_eth.visibility = View.VISIBLE
        rb_nuls.visibility = View.GONE
        rb_bsc.visibility = View.VISIBLE
        rb_heco.visibility = View.VISIBLE
        rb_okex.visibility = View.VISIBLE
    }


    //
    private fun getFeeByNvtCross(chain: String) {
        layout_fee_level.visibility = View.VISIBLE
        tv_fee.visibility = View.GONE
        RetrofitClient.getInstance().invokePostBody(
            this, Api.NVT_CROSS_FEE, mapOf(
                "language" to UserDao.language,
                "chain" to chain
            )
        ).subscribe(object : RecObserver<BaseResponse<Number>>(this, false, false,Api.NVT_CROSS_FEE) {
            override fun onSuccess(t: BaseResponse<Number>?) {
                hideFeeLoading()
                //???????????????
                if (t?.data != null) {
                    tv_fee_low.text = NaboxUtils.coinValueOf(
                        BigInteger(
                            Arith.mulNorGrouping(
                                t.data.toDouble(),
                                lowFee,
                                8
                            )
                        ), 8
                    ) + " NVT"
                    tv_fee_nomal.text = NaboxUtils.coinValueOf(
                        BigInteger(
                            Arith.mulNorGrouping(
                                t.data.toDouble(),
                                normalFee,
                                8
                            )
                        ), 8
                    ) + " NVT"
                    tv_fee_high.text = NaboxUtils.coinValueOf(
                        BigInteger(
                            Arith.mulNorGrouping(
                                t.data.toDouble(),
                                highFee,
                                8
                            )
                        ), 8
                    ) + " NVT"
                    feeStr = tv_fee_nomal.text.toString()
                }
            }

            override fun onFail(msg: String, code: Int) {
                hideFeeLoading()
                showToast(msg)
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == -1) {
            when (requestCode) {
                10086 -> {
                    //???????????????
                    if (data?.getStringExtra("address") != null) {
                        edit_address.setText(data.getStringExtra("address"))
                    }
                }
                10085 -> {
                    //???????????????
                    if (data?.getStringExtra("zxing") != null) {
                        edit_address.setText(data.getStringExtra("zxing"))
                    }
                }
            }
        }

    }


    /**
     * languagestringY?????????EN/CHSchainstringY??????????????????NULS???NERVE???Ethereum???BSCaddressstringY?????????????????????valuebigIntegerY???????????????????????????????????????????????????????????????????????????0contractAddressstringY????????????methodNamestringY????????????methodDescstringN????????????????????????????????????????????????????????????????????????
     */
    private fun getGasLimit(
        chain: String,
        address: String,
        contractAddress: String? = "",
        toAddress: String,
        amount: BigInteger
    ) {
        var array = arrayOf(toAddress, amount)
        var transfer: String = if (fromChain == toChain) {
            "transfer"
        } else {
            "transferCrossChain"
        }
        var value = if (fromChain == toChain) {
            0
        } else {
            10000000
        }
        RetrofitClient.getInstance().invokePostBody(
            this, Api.CALL_GASLIMIT, mapOf(
                "language" to UserDao.language,
                "chain" to chain,
                "address" to address,
                "value" to value,
                "contractAddress" to contractAddress,
                "methodName" to transfer,
                "args" to array
            )
        ).subscribe(object : RecObserver<BaseResponse<GaslimitEntity>>(this, false, false,Api.CALL_GASLIMIT) {
            override fun onSuccess(t: BaseResponse<GaslimitEntity>?) {
                hideFeeLoading()
                if (t?.data != null) {
                    tv_fee.text = NaboxUtils.coinValueOf(
                        BigInteger.valueOf(t.data.gasLimit * 25 + 100000),
                        8
                    ) + "NULS"
                }
            }

            override fun onFail(msg: String, code: Int) {
                hideFeeLoading()
                showToast(msg)
            }
        })
    }


    /**
     * ???????????????????????????
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
                if (t?.data != null) {
                    //??????????????? ??????????????????????????????
                    var assetsEntity = getMainAsset(t.data, chain)
                    if (assetsEntity != null) {
                        //?????????????????????

                        choiceAssetSuccess(assetsEntity)
                    }
                }
            }

            override fun onFail(msg: String, code: Int) {
                showToast(msg)
            }
        })
    }


    /**
     * ??????????????????????????????????????????
     */
    private fun initFeeAndRemark(
        fromChain: String,
        toChain: String,
        assetsEntity: AssetsEntity,
        amount: String
    ) {
        showFeeLoading()
        //?????????????????????????????????????????????????????????
        feeStr = ""
        defaultFeeLevel = 1.0
        when (fromChain) {
            CoinTypeEnum.NULS.code -> {
                hideFeeLevel()
                when (toChain) {
                    CoinTypeEnum.NULS.code -> {
                        if (TextUtils.isEmpty(assetsEntity.contractAddress)) {
                            tv_fee.text = "0.001NULS"
                        } else {
                            getGasLimit(
                                CoinTypeEnum.NULS.code,
                                getAddressByCoinType(
                                    WalletInfoDaoHelper.loadDefaultWallet(),
                                    fromChain
                                ),
                                assetsEntity.contractAddress,
                                edit_address.text.toString().trim(),
                                NaboxUtils.getAmount(amount, assetsEntity.decimals)
                            )
                        }
                        hideFeeLoading()
                    }
                    CoinTypeEnum.NERVE.code -> {
                        if (TextUtils.isEmpty(assetsEntity.contractAddress)) {
                            tv_fee.text = "0.01NULS"
                        } else {
                            getGasLimit(
                                CoinTypeEnum.NULS.code,
                                getAddressByCoinType(
                                    WalletInfoDaoHelper.loadDefaultWallet(),
                                    fromChain
                                ),
                                assetsEntity.contractAddress,
                                edit_address.text.toString().trim(),
                                NaboxUtils.getAmount(amount, assetsEntity.decimals)
                            )
                        }
                        hideFeeLoading()
                    }
                }
            }
            CoinTypeEnum.NERVE.code -> {
                when (toChain) {
                    CoinTypeEnum.NULS.code -> {
                        hideFeeLevel()
                        tv_fee.text = "0.01NULS + 0.01NVT"
                        hideFeeLoading()
                    }
                    CoinTypeEnum.NERVE.code -> {
                        hideFeeLevel()
                        //nuls ?????????nerve ?????????????????? 0.001NULS
                        tv_fee.text = "0 NVT"
                        hideFeeLoading()
                    }
                    CoinTypeEnum.BSC.code -> {
                        showFeeLevel()
                        getFeeByNvtCross(CoinTypeEnum.BSC.code)
                    }

                    CoinTypeEnum.HECO.code -> {
                        showFeeLevel()
                        getFeeByNvtCross(CoinTypeEnum.HECO.code)
                    }

                    CoinTypeEnum.ETH.code -> {
                        showFeeLevel()
                        getFeeByNvtCross(CoinTypeEnum.ETH.code)
                    }

                }
            }
            CoinTypeEnum.ETH.code -> {
                //?????????????????? ???nvt????????????nuls???
                layout_low.visibility = View.VISIBLE
                tv_fee.visibility = View.GONE
                showFeeLevel()
                when (toChain) {
                    CoinTypeEnum.ETH.code -> {
                        if (isMainAsset(assetsEntity, CoinTypeEnum.ETH.code)) {
                            //????????????????????????
                            getGasPrice(
                                mainAssetGasLimit,
                                CoinTypeEnum.ETH.code,
                                CoinTypeEnum.ETH.code,
                                assetsEntity
                            )
                        } else {
                            //?????????????????????
                            getGasPrice(
                                tokenGasLimit,
                                CoinTypeEnum.ETH.code,
                                CoinTypeEnum.ETH.code,
                                assetsEntity
                            )
                        }
                    }
                    CoinTypeEnum.NERVE.code -> {
                        getGasPrice(
                            mainAssetGasLimit,
                            CoinTypeEnum.ETH.code,
                            CoinTypeEnum.NERVE.code,
                            assetsEntity
                        )
                    }
                }
            }
            CoinTypeEnum.HECO.code -> {
                showFeeLevel()
                //?????????????????? ???nvt????????????nuls???
                layout_low.visibility = View.VISIBLE
                tv_fee.visibility = View.GONE
                when (toChain) {
                    CoinTypeEnum.HECO.code -> {
                        if (isMainAsset(assetsEntity, CoinTypeEnum.HECO.code)) {
                            //????????????????????????
                            getGasPrice(
                                mainAssetGasLimit,
                                CoinTypeEnum.HECO.code,
                                CoinTypeEnum.HECO.code,
                                assetsEntity
                            )
                        } else {
                            //?????????????????????
                            getGasPrice(
                                tokenGasLimit,
                                CoinTypeEnum.HECO.code,
                                CoinTypeEnum.HECO.code,
                                assetsEntity
                            )
                        }
                    }
                    CoinTypeEnum.NERVE.code -> {
                        getGasPrice(
                            mainAssetGasLimit,
                            CoinTypeEnum.HECO.code,
                            CoinTypeEnum.NERVE.code,
                            assetsEntity
                        )
                    }
                }
            }
            CoinTypeEnum.BSC.code -> {
                showFeeLevel()
                //?????????????????? ???nvt????????????nuls???
                layout_low.visibility = View.VISIBLE
                tv_fee.visibility = View.GONE
                when (toChain) {
                    CoinTypeEnum.BSC.code -> {
                        if (isMainAsset(assetsEntity, CoinTypeEnum.BSC.code)) {
                            //????????????????????????
                            getGasPrice(
                                mainAssetGasLimit,
                                CoinTypeEnum.BSC.code,
                                CoinTypeEnum.BSC.code,
                                assetsEntity
                            )
                        } else {
                            //?????????????????????
                            getGasPrice(
                                tokenGasLimit,
                                CoinTypeEnum.BSC.code,
                                CoinTypeEnum.NERVE.code,
                                assetsEntity
                            )
                        }
                    }
                    CoinTypeEnum.NERVE.code -> {
                        getGasPrice(
                            mainAssetGasLimit,
                            CoinTypeEnum.BSC.code,
                            CoinTypeEnum.NERVE.code,
                            assetsEntity
                        )
                    }
                }
            }
            CoinTypeEnum.OKEX.code -> {
                showFeeLevel()
                //?????????????????? ???nvt????????????nuls???
                layout_low.visibility = View.VISIBLE
                tv_fee.visibility = View.GONE
                when (toChain) {
                    CoinTypeEnum.OKEX.code -> {
                        if (isMainAsset(assetsEntity, CoinTypeEnum.OKEX.code)) {
                            //????????????????????????
                            getGasPrice(
                                mainAssetGasLimit,
                                CoinTypeEnum.OKEX.code,
                                CoinTypeEnum.OKEX.code,
                                assetsEntity
                            )
                        } else {
                            //?????????????????????
                            getGasPrice(
                                tokenGasLimit,
                                CoinTypeEnum.OKEX.code,
                                CoinTypeEnum.OKEX.code,
                                assetsEntity
                            )
                        }
                    }
                    CoinTypeEnum.NERVE.code -> {
                        getGasPrice(
                            mainAssetGasLimit,
                            CoinTypeEnum.OKEX.code,
                            CoinTypeEnum.NERVE.code,
                            assetsEntity
                        )
                    }
                }

            }
        }
    }


    //??????gas??????
    private fun getGasPrice(
        gasLimit: Long,
        fromchain: String,
        toChain: String,
        assetsEntity: AssetsEntity
    ) {
        RetrofitClient.getInstance().invokePostBody(
            this, Api.GAS_PRICE, mapOf(
                "language" to UserDao.language,
                "chain" to fromchain
            )
        ).subscribe(object : RecObserver<BaseResponse<Number>>(this, false, false,Api.GAS_PRICE) {
            override fun onSuccess(t: BaseResponse<Number>?) {
                if (t?.data != null) {

                    hideFeeLoading()
                    tv_fee_low.text = NaboxUtils.coinValueOf(
                        BigInteger(
                            Arith.mulNorGrouping(
                                gasLimit * t.data.toDouble(),
                                lowFee,
                                CoinTypeEnum.getDecimalByCode(fromchain)
                            )
                        ), CoinTypeEnum.getDecimalByCode(fromchain)
                    ) + CoinTypeEnum.getByCode(fromchain).symbol
                    tv_fee_nomal.text = NaboxUtils.coinValueOf(
                        BigInteger(
                            Arith.mulNorGrouping(
                                gasLimit * t.data.toDouble(),
                                normalFee,
                                CoinTypeEnum.getDecimalByCode(fromchain)
                            )
                        ), CoinTypeEnum.getDecimalByCode(fromchain)
                    ) + CoinTypeEnum.getByCode(fromchain).symbol
                    tv_fee_high.text = NaboxUtils.coinValueOf(
                        BigInteger(
                            Arith.mulNorGrouping(
                                gasLimit * t.data.toDouble(),
                                highFee,
                                CoinTypeEnum.getDecimalByCode(fromchain)
                            )
                        ), CoinTypeEnum.getDecimalByCode(fromchain)
                    ) + CoinTypeEnum.getByCode(fromchain).symbol
                    feeStr = tv_fee_nomal.text.toString()
                }
            }

            override fun onFail(msg: String, code: Int) {
                //??????gasprice??????
                hideFeeLoading()
            }
        })
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
     * ?????????????????????????????????
     */
    fun showFeeLevel() {
        layout_fee_level.visibility = View.VISIBLE
        tv_fee.visibility = View.GONE
    }

    /**
     * ???????????????????????????
     */
    fun hideFeeLevel() {
        layout_fee_level.visibility = View.GONE
        tv_fee.visibility = View.VISIBLE
    }


    override fun onDestroy() {
        super.onDestroy()
        mFuture?.run {
            if (!isCancelled) cancel(true)
            null
        }
    }

    //??????????????? ???????????? ??? ??????????????????????????? ???????????????????????????????????????????????????
    fun calculationPayFee(): Boolean {
        //?????????
        var fee = if (!TextUtils.isEmpty(feeStr)) {
            getEffectiveFree(feeStr).toDouble()
        } else {
            getEffectiveFree(tv_fee.text.toString()).toDouble()
        }
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

                when (toChain) {
                    CoinTypeEnum.NULS.code -> {
                        //????????? 0.001 nuls
                        if (assetsEntity?.configType == 1) {
                            //1 ????????????   2 token   ??????????????????nuls
                            //????????????????????????????????????
                            return amount + fee <= totalBalance
                        } else {
                            //????????????????????????????????????????????????????????????????????????NULS???????????????0.001
                            return (amount <= totalBalance) && (findFeeFromCode(CoinTypeEnum.NULS.code) >= 0.001)
                        }
                    }
                    CoinTypeEnum.NERVE.code -> {
                        //????????? 0.01 nuls
                        if (assetsEntity?.configType == 1) {//1 ????????????   2 token   ??????????????????nuls
                            //????????????????????????????????????
                            return amount + fee <= totalBalance
                        } else {
                            //????????????????????????????????????????????????????????????????????????NULS???????????????0.01
                            return (amount <= totalBalance) && (findFeeFromCode(CoinTypeEnum.NULS.code) >= 0.001)
                        }
                    }
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
                    CoinTypeEnum.NERVE.code -> {
                        //Nerve???Nerve  ?????????????????? ????????????????????????????????????????????????
                        return amount <= totalBalance
                    }
                    else -> {
                        //nvt ??????????????????
                        //???????????? ?????????TextView??????(??????????????????)  ??????NVT
                        //?????????????????????????????????NVT
                        if (assetsEntity?.configType == 1) {//????????????  ????????????nvt
                            //?????????????????? + ????????? ??????????????????????????????
                            return amount + fee <= totalBalance
//                        BigInteger("0.0011")

                        } else {
                            //?????????????????????nvt
                            //???????????????????????????????????????????????? ?????? ???????????????????????????????????????nvt???????????????????????????
                            return (amount <= totalBalance) && (findFeeFromCode(CoinTypeEnum.NERVE.code) >= fee)

                        }


                    }
                }
            }
            else -> {

                //???????????? feeStr????????????????????????  ??????fromChain???????????????
                if (assetsEntity?.configType == 1) {//????????????????????????
                    //?????????????????? + ????????? ??????????????????????????????
                    return amount + fee <= totalBalance
                } else {
                    //??????????????????????????????
                    //?????????????????????????????????????????????????????? ??? ??????????????????????????????????????????????????????????????????????????????
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


}