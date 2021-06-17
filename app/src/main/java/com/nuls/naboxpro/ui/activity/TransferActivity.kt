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
 * 发送者：发送者为nerve地址则网络标签处展示全部5条网络，发送者为其余4条网络，则网络标签处展示发送网络和Nerve网络2个标签

接收者：接收者分为3类：NULS、Nerve 和 以太坊系。若发送地址为Nerve，当接收地址为NULS或nerve，自动选中对应网络标签，其余网络标签消失。当接收地址为以太坊系，则展示3个以太坊系网络标签，用户需选择网络标签。
若发送者为非Nerve地址，用户就只能填入本链地址或者Nerve地址，则当用户填入接收地址后，自动选中网络标签

手续费：以太坊系本链转账和以太坊系和Nerve之间相互转账，则显示低中高手续费模式。NULS和Nerve之间转账则直接显示手续费金额

备注：所有本链转账提供备注输入框，所有跨链转账不提供填写备注输入框

确认框：所有跨链转账，展示免责信息【我已知晓，直接向交易所地址跨链充值将造成资产丢失】，用户需勾选才可进行跨链转账，本链转账无需展示该免责信息
 */
class TransferActivity : BaseActivity() {


    var editString = ""
    var amountString = ""

    //当前主链
    var fromChain = ""

    //入金地址所属链
    var toChain = ""

    /**
     * 低手续费倍率
     */
    var lowFee: Double = 0.8

    /**
     * 正常手续费倍率
     */
    var normalFee: Double = 1.0

    /**
     * 高费倍率
     */
    var highFee: Double = 1.3

    //主资产交易时候所需要的的gaslimit
    var mainAssetGasLimit: Long = 35000

    //token 交易所需要的的gaslimit
    var tokenGasLimit: Long = 100000

    /**
     * 被交易的资产
     */
    var assetsEntity: AssetsEntity? = null


    /**
     * 默认的手续费倍数
     */
    var defaultFeeLevel = 1.0

    /**
     * 手续费  text
     */
    var feeStr: String = ""

    /**
     * 异构链token授权hash
     */
    var tokenAuthHash = ""

    /**
     * 资产胡列表
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
            //选择联系人
            ContactsListActivity.startForResult(this, 10086)
        }

        if (intent != null && intent.getStringExtra("coinType") != null) {
            //开始初始化界面配置

            fromChain = intent.getStringExtra("coinType")

            when (intent.getStringExtra("coinType")) {
                CoinTypeEnum.NULS.code -> {
                    rb_eth.visibility = View.GONE
                    rb_heco.visibility = View.GONE
                    rb_bsc.visibility = View.GONE
                    rb_okex.visibility = View.GONE
                }
                CoinTypeEnum.NERVE.code -> {
                    //显示全部标签

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
                //切换了网络把输入的金额清空
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
                        showToast("请选择所属链")
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
                                showToast("该资产不支持当前交易")
                            }
                        }
                    })
            }
        }


        //手续费切换
        layout_low.setOnClickListener {
            checkLowFee()
        }
        //手续费切换
        layout_normal.setOnClickListener {
            checkNormalFee()
        }
        //手续费切换
        layout_high.setOnClickListener {
            checkHighFee()
        }

        iv_scan.setOnClickListener {
            //跳转到扫码页面
            var intent: Intent = Intent(this, ScanActivity().javaClass)
            startActivityForResult(intent, 10085)


        }
        edit_address.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (delayRun != null) {
                    //每次editText有变化的时候，则移除上次发出的延迟线程
                    handler.removeCallbacks(delayRun)
                }
                if (TextUtils.isEmpty(p0)) {
                    editString = ""
                } else {
                    editString = p0.toString()
                }

                if (editString.length > 4) {//长度小于4的时候不需要判断，也判断不出来结果
                    //延迟800ms，如果不再输入字符，则执行该线程的run方法
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
                    //每次editText有变化的时候，则移除上次发出的延迟线程
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
                    //一下几种情况需要在全部的时候先扣除手续费
//                    var transferCoin  = getCoinType(assetsEntity!!,fromChain)
//                      if(fromChain==toChain
//                          &&fromChain==CoinTypeEnum.NULS.code){
//                          //nuls 链内交易
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

                    //跨链时必须勾选提示
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
                                        if (!TextUtils.isEmpty(tokenAuthHash)) {
                                            runOnUiThread {
                                                btn_auth.text = getString(R.string.authing)
                                            }
                                            //轮询当前授权的结果
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


    /**
     * 选中低手续费
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
     * 选中正常手续费
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
     * 选中高手续费
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
     * 延迟线程，看是否还有下一个字符输入
     */
    private val delayRun = Runnable {
        //判断输入地址类型
        if (!TextUtils.isEmpty(editString)) {
            when (getCoinType(editString)) {
                CoinTypeEnum.NULS.code -> {
                    initNetFlag(intent.getStringExtra("coinType"), CoinTypeEnum.NULS.code)
                }
                CoinTypeEnum.NERVE.code -> {
                    initNetFlag(intent.getStringExtra("coinType"), CoinTypeEnum.NERVE.code)
                }
                "eths" -> {//eth相关链
                    initNetFlag(intent.getStringExtra("coinType"), "eths")
                }
                "error" -> {//暂时不支持的链
                    notSurport()
                }
            }

        }
    }

    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private val amountDelayRun = Runnable {

        if (!TextUtils.isEmpty(tv_num.text) && tv_num.text.toString().startsWith(".")) {
            showToast(getString(R.string.amount_format_error))
            tv_num.text = null
            return@Runnable
        }
        if (assetsEntity != null && getNumberOfDecimalPlaces(tv_num.text.toString()) > assetsEntity?.decimals!!) {
            showToast(assetsEntity?.symbol + "最多支持" + assetsEntity?.decimals + "位小数")
            tv_num.text = null
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

        //计算手续费
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


    private fun choiceAssetFail() {
        tv_network.text = ""
        assetsEntity = null
        tv_fee.text = ""

    }


    /**
     * 控制网络标签的显示 确定交易类型
     */
    private fun initNetFlag(fromCoinType: String, toCoinType: String) {
        //每次进入判断 先清空之前的选中
        radio_group.clearCheck()
        //根据from address 和to address 来控制网络标签的选中和显示隐藏
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
                        //暂不支持该转账
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
        if (fromChain == toChain) {//同一条链 可以输入备注

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
     * 选中nuls
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
     * 选中bsc
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
     * 暂不支持
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
     * 选中heco
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
     * 选中eth
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
     * 选中eth
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
     * 选中nvt
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
     * 选中以太坊系
     */
    private fun checkETHSFlag() {

        tv_not_sup.visibility = View.INVISIBLE
        //不默认选中 让用户自己选择 具体的链
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
                //小数点八位
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
                    //选择联系人
                    if (data?.getStringExtra("address") != null) {
                        edit_address.setText(data.getStringExtra("address"))
                    }
                }
                10085 -> {
                    //扫描二维码
                    if (data?.getStringExtra("zxing") != null) {
                        edit_address.setText(data.getStringExtra("zxing"))
                    }
                }
            }
        }

    }


    /**
     * languagestringY语言：EN/CHSchainstringY地址所属链：NULS、NERVE、Ethereum、BSCaddressstringY合约调用者地址valuebigIntegerY调用者向合约地址转入的主网资产金额，没有此业务时填0contractAddressstringY合约地址methodNamestringY合约方法methodDescstringN合约方法描述，若合约内方法没有重载，参数可以为空
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
     * 获取当前链资产列表
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
                    //获取成功后 默认选中该链的主资产
                    var assetsEntity = getMainAsset(t.data, chain)
                    if (assetsEntity != null) {
                        //获取主资产成功

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
     * 初始化手续费和备注、警告信息
     */
    private fun initFeeAndRemark(
        fromChain: String,
        toChain: String,
        assetsEntity: AssetsEntity,
        amount: String
    ) {
        showFeeLoading()
        //计算手续费和判断备注输入框的显示和隐藏
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
                        //nuls 跨链到nerve 手续费固定为 0.001NULS
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
                //转账只会存在 像nvt转或者像nuls转
                layout_low.visibility = View.VISIBLE
                tv_fee.visibility = View.GONE
                showFeeLevel()
                when (toChain) {
                    CoinTypeEnum.ETH.code -> {
                        if (isMainAsset(assetsEntity, CoinTypeEnum.ETH.code)) {
                            //是否交易的主资产
                            getGasPrice(
                                mainAssetGasLimit,
                                CoinTypeEnum.ETH.code,
                                CoinTypeEnum.ETH.code,
                                assetsEntity
                            )
                        } else {
                            //交易的合约资产
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
                //转账只会存在 像nvt转或者像nuls转
                layout_low.visibility = View.VISIBLE
                tv_fee.visibility = View.GONE
                when (toChain) {
                    CoinTypeEnum.HECO.code -> {
                        if (isMainAsset(assetsEntity, CoinTypeEnum.HECO.code)) {
                            //是否交易的主资产
                            getGasPrice(
                                mainAssetGasLimit,
                                CoinTypeEnum.HECO.code,
                                CoinTypeEnum.HECO.code,
                                assetsEntity
                            )
                        } else {
                            //交易的合约资产
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
                //转账只会存在 像nvt转或者像nuls转
                layout_low.visibility = View.VISIBLE
                tv_fee.visibility = View.GONE
                when (toChain) {
                    CoinTypeEnum.BSC.code -> {
                        if (isMainAsset(assetsEntity, CoinTypeEnum.BSC.code)) {
                            //是否交易的主资产
                            getGasPrice(
                                mainAssetGasLimit,
                                CoinTypeEnum.BSC.code,
                                CoinTypeEnum.BSC.code,
                                assetsEntity
                            )
                        } else {
                            //交易的合约资产
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
                //转账只会存在 像nvt转或者像nuls转
                layout_low.visibility = View.VISIBLE
                tv_fee.visibility = View.GONE
                when (toChain) {
                    CoinTypeEnum.OKEX.code -> {
                        if (isMainAsset(assetsEntity, CoinTypeEnum.OKEX.code)) {
                            //是否交易的主资产
                            getGasPrice(
                                mainAssetGasLimit,
                                CoinTypeEnum.OKEX.code,
                                CoinTypeEnum.OKEX.code,
                                assetsEntity
                            )
                        } else {
                            //交易的合约资产
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


    //查询gas价格
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
                //获取gasprice失败
                hideFeeLoading()
            }
        })
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
     * 展示手续费信息级别分类
     */
    fun showFeeLevel() {
        layout_fee_level.visibility = View.VISIBLE
        tv_fee.visibility = View.GONE
    }

    /**
     * 隐藏手续费级别分类
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

    //判断手续费 输入金额 和 可用余额的大小关系 （当前账户是否有足够金额发起支付）
    fun calculationPayFee(): Boolean {
        //手续费
        var fee = if (!TextUtils.isEmpty(feeStr)) {
            getEffectiveFree(feeStr).toDouble()
        } else {
            getEffectiveFree(tv_fee.text.toString()).toDouble()
        }
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

                when (toChain) {
                    CoinTypeEnum.NULS.code -> {
                        //手续费 0.001 nuls
                        if (assetsEntity?.configType == 1) {
                            //1 主链资产   2 token   被交易资产是nuls
                            //直接相加去和可用资产判断
                            return amount + fee <= totalBalance
                        } else {
                            //用输入金额和可用余额比较大小，同时判断当前账户的NULS余额是否有0.001
                            return (amount <= totalBalance) && (findFeeFromCode(CoinTypeEnum.NULS.code) >= 0.001)
                        }
                    }
                    CoinTypeEnum.NERVE.code -> {
                        //手续费 0.01 nuls
                        if (assetsEntity?.configType == 1) {//1 主链资产   2 token   被交易资产是nuls
                            //直接相加去和可用资产判断
                            return amount + fee <= totalBalance
                        } else {
                            //用输入金额和可用余额比较大小，同时判断当前账户的NULS余额是否有0.01
                            return (amount <= totalBalance) && (findFeeFromCode(CoinTypeEnum.NULS.code) >= 0.001)
                        }
                    }
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
                    CoinTypeEnum.NERVE.code -> {
                        //Nerve转Nerve  不需要手续费 直接用输入金额和可用余额进行比较
                        return amount <= totalBalance
                    }
                    else -> {
                        //nvt 跨链到异构链
                        //手续费为 手续费TextView的值(记住去掉单位)  单位NVT
                        //判断被交易的资产是不是NVT
                        if (assetsEntity?.configType == 1) {//主链资产  这里就是nvt
                            //判断输入金额 + 手续费 和可用余额的大小关系
                            return amount + fee <= totalBalance
//                        BigInteger("0.0011")

                        } else {
                            //被交易资产不是nvt
                            //首先判断输入金额和可用余额的大小 然后 判断当前账户是否有足够多的nvt余额用来支付手续费
                            return (amount <= totalBalance) && (findFeeFromCode(CoinTypeEnum.NERVE.code) >= fee)

                        }


                    }
                }
            }
            else -> {

                //手续费为 feeStr的值（去掉单位）  单位fromChain的主链资产
                if (assetsEntity?.configType == 1) {//交易的是主链资产
                    //判断输入金额 + 手续费 和可用余额的大小关系
                    return amount + fee <= totalBalance
                } else {
                    //被交易的是非主链资产
                    //首先判断输入金额和可用余额的大小关系 再 判断当前账户是否有足够多的主链资产余额用来支付手续费
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