package com.nuls.naboxpro.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Looper
import android.text.TextUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.db.WalletInfoDaoHelper
import com.nuls.naboxpro.entity.AssetsEntity
import com.nuls.naboxpro.entity.CrossAssetInfo
import com.nuls.naboxpro.entity.WalletInfo
import com.nuls.naboxpro.enums.CoinTypeEnum
import com.nuls.naboxpro.eventbus.TransferSuccess
import com.nuls.naboxpro.net.Api
import com.nuls.naboxpro.net.BaseResponse
import com.nuls.naboxpro.net.api.RetrofitClient
import com.nuls.naboxpro.net.observable.RecObserver
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.ui.main.MainMenuActivity
import com.nuls.naboxpro.ui.popup.InputPasswordPop
import com.nuls.naboxpro.ui.popup.LoadingPopup
import com.nuls.naboxpro.utils.*
import com.nuls.naboxpro.wallet.transfer.*
import kotlinx.android.synthetic.main.activity_confirm_transaction.*
import kotlinx.android.synthetic.main.activity_confirm_transaction.iv_icon
import kotlinx.android.synthetic.main.activity_confirm_transaction.tv_fee
import kotlinx.android.synthetic.main.activity_trans_layout.*
import kotlinx.android.synthetic.main.base_title_maincolor_layout.*
import kotlinx.android.synthetic.main.base_title_maincolor_layout.rl_finish
import kotlinx.android.synthetic.main.layout_coin_detail_layout.*

import network.nerve.heterogeneous.BSCTool
import network.nerve.heterogeneous.ETHTool
import network.nerve.heterogeneous.HTTool
import network.nerve.heterogeneous.OKTTool
import org.greenrobot.eventbus.EventBus
import java.math.BigDecimal
import java.math.BigInteger

/**
 * 确认交易
 */
class ConfirmTransferActivity:BaseActivity() {

    private var loadingPopup: BasePopupView? = null

    override fun getLayoutId() = R.layout.activity_confirm_transaction
    //主资产交易时候所需要的的gaslimit
    var mainAssetGasLimit:BigInteger =BigInteger("35000")
    //token 交易所需要的的gaslimit
    var tokenGasLimit:BigInteger =BigInteger("100000")

    var gasPrice:BigInteger = BigInteger("0")
    /**
     *  出金地址
     */
    var  fromAddress:String?=""
    /**
     *  入金地址
     */
    var  toAddress:String?=""
    /**
     *  出金链
     */
    var  fromChain:String?=""
    /**
     * 入金链
     */
    var  toChain:String?=""

    /**
     * 交易金额
     */
    var  amount:String?=""

    /**
     * 备注
     */
    var  remark:String?=""

    /**
     * 手续费（带手续费单位的文本）
     */
    var payFee:String?=""

    /**
     * 被交易的资产信息
     */
    var  assets:AssetsEntity?=null

    /**
     * nvt异构链提现手续费
     */
    var crossChainPayFee:BigInteger = BigInteger("0")

    /**
     * 手续费倍数
     */
    var feeLevel:Double = 1.0

    /**
     * 异构链资产在nerve上的链id
     */
    var bridgeChainId:Int = 0

    /**
     * 异构链资产在nerve上的资产id
     *
     */
    var bridgeAssetId:Int = 0

    /**
     * 转入链的链id
     */
    var toChainId:Int = 0

    override fun initView() {
        tv_title.text = getString(R.string.confirm_transaction)
        rl_finish.setOnClickListener {
            finish()
        }
        loadingPopup = XPopup.Builder(this)
            .hasStatusBarShadow(false)
            .hasShadowBg(false)
            .dismissOnBackPressed(false)
            .dismissOnTouchOutside(false)
            .asCustom(LoadingPopup(this) {

            })
        if(intent!=null){
//
            if(!TextUtils.isEmpty(intent.getStringExtra("fromAddress"))){
                fromAddress = intent.getStringExtra("fromAddress")
                tv_fromAddress.text = fromAddress
            }
            if(!TextUtils.isEmpty(intent.getStringExtra("toAddress"))){
                toAddress = intent.getStringExtra("toAddress")
                tv_toAddress.text = toAddress
            }

            if(!TextUtils.isEmpty(intent.getStringExtra("fromChain"))){
                fromChain = intent.getStringExtra("fromChain")
            }

            if(!TextUtils.isEmpty(intent.getStringExtra("toChain"))){
                toChain = intent.getStringExtra("toChain")
                tv_to_network.text = toChain
                if(UserDao.getChainConfig()!=null){
                    for(index in 0 until UserDao.getChainConfig()?.size!!){
                        if(toChain== UserDao.getChainConfig()?.get(index)?.chain){
                            toChainId = UserDao.getChainConfig()?.get(index)?.chainId!!
                        }
                    }
                }
                UserDao.getChainConfig()
                if(toChain!=CoinTypeEnum.NULS.code&&toChain!=CoinTypeEnum.NERVE.code){
                    //这两个不一定用得到，但是可以先请求了来
                    getFeeByNvtCross(toChain.toString())
                    getGasPrice(toChain.toString())
                }
            }

            if(intent.getSerializableExtra("assets")!=null){
                assets = intent.getSerializableExtra("assets") as AssetsEntity?
                tv_coin_name.text = assets?.symbol
                if(!TextUtils.isEmpty(assets?.symbol)){
                    loadCoinIcon(this,iv_icon,getImageUrl(assets?.symbol!!))
                }
            }
            if(!TextUtils.isEmpty(intent.getStringExtra("amount"))){
                amount = intent.getStringExtra("amount")
                tv_actualFee.text = amount+assets?.symbol
            }

            if(!TextUtils.isEmpty(intent.getStringExtra("remark"))){
                remark = intent.getStringExtra("remark")
                tv_remark.text = remark
            }
            if(!TextUtils.isEmpty(intent.getStringExtra("payFee"))){
                payFee = intent.getStringExtra("payFee")
                tv_fee.text = payFee
            }
            if(intent.getDoubleExtra("feeLevel",1.0)!=null){
                feeLevel = intent.getDoubleExtra("feeLevel",1.0)
            }
            toChain?.let {
                assets?.chainId?.let { it1 ->
                    assets?.assetId?.let { it2 ->
                        getChangeAssetInfo(it,
                            it1, it2, assets?.contractAddress
                        )
                    }
                }
            }
            btn_commit.setOnClickListener {
                                //确认交易
                                showPasswordPop(this,object : InputPasswordPop.InputListener{
                                    override fun inputSuccess(password: String?) {
                                       if(WalletInfoDaoHelper.loadDefaultWallet().validatePassword(password)){
                                           loadingPopup?.show()
                                           Thread(Runnable {
                                                //更具不用交易类型调用不同交易
                                               try {
                                                   password?.let { it1 -> createTx(it1) }
                                               }catch (e:Exception){
                                                   runOnUiThread {
                                                       loadingPopup?.dismiss()
                                                   }

                                                   Looper.prepare()
                                                   e.message?.let { it1 -> showToast(it1) }
                                                   Looper.loop()

                                               }
                                           }).start()
                                       }else{
                                           showToast(getString(R.string.password_error))
                                       }
                                    }
                                })
            }

        }
    }


    fun createTx(password:String){

        if(fromChain!=CoinTypeEnum.NERVE.code
            ||toChain!=CoinTypeEnum.NERVE.code
            &&fromChain!=toChain
        ){
            if(bridgeAssetId==0||bridgeChainId==0){
                runOnUiThread {
                    loadingPopup?.dismiss()
                    showToast("获取资产信息失败")
                }

                return
            }
        }
        //先把两个关键参数置空  避免计算异常情况
        var txHash = ""
        var crossTxHash = ""
        var  money = NaboxUtils.tokenValueOf(amount,assets?.decimals!!)
        var tranferCoin = fromChain?.let { getCoinType(assets!!, it) }
        if(TextUtils.isEmpty(tranferCoin)){
           // 获取被交易资产的资产类型失败 可能是chainConfig缓存失败  提示用户退出app重试
            showToast(getString(R.string.create_tx_fail))
            return
        }
        //获取资产被toChain的配置信息
        var heterogeneousInfo  = getHeterogeneousInfo(assets!!,fromChain!!)
        var contractAddress = if(TextUtils.isEmpty(assets?.contractAddress)){
            ""
        }else{
            assets?.contractAddress
        }
        var walletInfo:WalletInfo = WalletInfoDaoHelper.loadDefaultWallet()
        when(fromChain){
            CoinTypeEnum.NULS.code->{
                when(toChain){
                    //nuls到nuls的链内交易
                    CoinTypeEnum.NULS.code->{
                        txHash = if(isMainAsset(assets!!,CoinTypeEnum.NULS.code)){
                            //主资产 NULS
                            SendTransctions.createNulsTxSimpleTransferOfNuls(fromAddress,walletInfo?.decrypt(password),toAddress,money,System.currentTimeMillis()/1000,remark)
                        }else{
                            if(TextUtils.isEmpty(assets?.contractAddress)){
                                //非nuls 非合约资产
                                SendTransctions.createNulsTxSimpleTransferOfNonNuls(fromAddress,walletInfo?.decrypt(password),toAddress,assets?.chainId!!,assets?.assetId!!,money,System.currentTimeMillis()/1000,remark)
                            }else{
                                //合约资产
                                SendTransctions.createNulsNrc20Transfer(fromAddress,walletInfo?.decrypt(password),toAddress,money,contractAddress,System.currentTimeMillis()/1000,remark)
                            }
                        }
                    }
                    CoinTypeEnum.NERVE.code->{
                        if(isMainAsset(assets!!,CoinTypeEnum.NULS.code)){
                            //主资产 NULS
                           txHash = SendTransctions.createNulsCrossTxSimpleTransferOfNuls(fromAddress,walletInfo?.decrypt(password),toAddress,
                                    money,System.currentTimeMillis()/1000,remark)
                        }else{
                            txHash = if(TextUtils.isEmpty(assets?.contractAddress)){
                                //非nuls 非合约资产
                                   SendTransctions.createNulsCrossTxSimpleTransferOfNonNuls(fromAddress,walletInfo?.decrypt(password),toAddress,
                                    assets?.chainId!!,assets?.assetId!!,
                                    money,System.currentTimeMillis()/1000,remark
                                )
                            }else{
                                //合约资产
                                SendTransctions.createNulsNrc20CrossTx(fromAddress,walletInfo?.decrypt(password),toAddress,
                                    money,contractAddress,System.currentTimeMillis()/1000,remark
                                )
                            }
                        }
                    }
                    else ->{
                        if(isMainAsset(assets!!,CoinTypeEnum.NULS.code)){
                            txHash = SendTransctions.createNulsCrossTxSimpleTransferOfNuls(fromAddress,walletInfo?.decrypt(password),walletInfo.nerveAddress,
                                money,System.currentTimeMillis()/1000,remark
                            )
                        }else{
                            txHash = if(TextUtils.isEmpty(assets?.contractAddress)){
                                SendTransctions.createNulsCrossTxSimpleTransferOfNonNuls(fromAddress,walletInfo?.decrypt(password),walletInfo.nerveAddress,
                                    assets?.chainId!!,assets?.assetId!!,
                                    money,System.currentTimeMillis()/1000,remark
                                )
                            }else{
                                SendTransctions.createNulsNrc20CrossTx(fromAddress,walletInfo?.decrypt(password),walletInfo.nerveAddress,
                                    money,contractAddress,System.currentTimeMillis()/1000,remark
                                )
                            }
                        }
                        crossTxHash = SendTransctions.createNerveWithdrawalTx(walletInfo?.nerveAddress,walletInfo?.decrypt(password),toChainId,toAddress,
                            assets?.nerveChainId!!,assets?.nerveAssetId!!,money,crossChainPayFee,System.currentTimeMillis()/1000,remark
                        )
                    }
                }
            }
            CoinTypeEnum.NERVE.code->{
                when(toChain){
                    CoinTypeEnum.NULS.code->{
                        when (tranferCoin) {
                            CoinTypeEnum.NULS.symbol -> {
                                txHash = SendTransctions.createNerveCrossTxSimpleTransferOfNuls(fromAddress,walletInfo?.decrypt(password),toAddress,
                                    money,System.currentTimeMillis()/1000,remark
                                )
                            }
                            CoinTypeEnum.NERVE.symbol -> {
                                txHash = SendTransctions.createNerveCrossTxSimpleTransferOfNvt(fromAddress,walletInfo?.decrypt(password),toAddress,
                                    money,System.currentTimeMillis()/1000,remark
                                )
                            }
                            else -> {
                                txHash = SendTransctions.createNerveCrossTxSimpleTransferOfNonNvtNuls(fromAddress,walletInfo?.decrypt(password),toAddress,assets?.chainId!!,assets?.assetId!!,
                                    money,System.currentTimeMillis()/1000,remark
                                )
                            }
                        }
                    }
                    //nerve 到nerve 的链内交易
                    CoinTypeEnum.NERVE.code->{
                        txHash = if(isMainAsset(assets!!,CoinTypeEnum.NERVE.code)){
                            SendTransctions.createNerveTxSimpleTransferOfNvt(fromAddress,walletInfo?.decrypt(password),toAddress,money,System.currentTimeMillis()/1000,remark)
                        }else{
                            SendTransctions.createNerveTxSimpleTransferOfNonNvt(fromAddress,walletInfo?.decrypt(password),toAddress,assets?.chainId!!,assets?.assetId!!,money,System.currentTimeMillis()/1000,remark)
                        }
                    }
                    else ->{
                        txHash = SendTransctions.createNerveWithdrawalTx(walletInfo?.nerveAddress,walletInfo?.decrypt(password),toChainId,toAddress,
                            assets?.chainId!!,assets?.assetId!!,money,crossChainPayFee,System.currentTimeMillis()/1000,remark
                        )
                    }
                }
            }
            CoinTypeEnum.BSC.code->{
                when(toChain){
                    CoinTypeEnum.NERVE.code->{
                        txHash = if(isMainAsset(assets!!,CoinTypeEnum.BSC.code)){
                            BSCTool.createRechargeBnb(fromAddress,walletInfo?.decrypt(password), money,toAddress,heterogeneousInfo.heterogeneousChainMultySignAddress).txHex
                        }else{
                            BSCTool.createRechargeBep20(fromAddress,walletInfo?.decrypt(password), money,toAddress,heterogeneousInfo?.heterogeneousChainMultySignAddress,heterogeneousInfo?.contractAddress).txHex
                        }
                    }
                    CoinTypeEnum.NULS.code->{
                        //需要拆分成两笔交易
                        when(tranferCoin){
                            CoinTypeEnum.NULS.symbol->{
                                //先转到nerve:
                                txHash = BSCTool.createRechargeBep20(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress,heterogeneousInfo.contractAddress).txHex
                                //再从nvrve到nuls
                                crossTxHash = SendTransctions.createNerveCrossTxSimpleTransferOfNuls(walletInfo.nerveAddress,walletInfo?.decrypt(password),toAddress,
                                    money,System.currentTimeMillis()/1000,remark
                                )
                            }
                            CoinTypeEnum.NERVE.symbol->{
                                //先转到nerve
                                txHash = BSCTool.createRechargeBep20(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress,heterogeneousInfo.contractAddress).txHex
                                //再从nvrve到nuls
                                crossTxHash = SendTransctions.createNerveCrossTxSimpleTransferOfNvt(walletInfo.nerveAddress,walletInfo?.decrypt(password),toAddress,
                                    money,System.currentTimeMillis()/1000,remark)
                            }
                            CoinTypeEnum.BSC.symbol->{
                                txHash = BSCTool.createRechargeBnb(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress).txHex
                                crossTxHash = SendTransctions.createNerveCrossTxSimpleTransferOfNonNvtNuls(walletInfo.nerveAddress,walletInfo?.decrypt(password),toAddress,assets?.nerveChainId!!,assets?.nerveAssetId!!,
                                    money,System.currentTimeMillis()/1000,remark)
                            }else ->{
                            txHash = BSCTool.rechargeBep20(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress,heterogeneousInfo.contractAddress).txHex
                            crossTxHash = SendTransctions.createNerveCrossTxSimpleTransferOfNonNvtNuls(walletInfo.nerveAddress,walletInfo?.decrypt(password),toAddress,assets?.nerveChainId!!,assets?.nerveAssetId!!,
                                money,System.currentTimeMillis()/1000,remark)
                            }
                        }
                    }
                    CoinTypeEnum.BSC.code->{
                        txHash = if(isMainAsset(assets!!,CoinTypeEnum.BSC.code)){
                            SendBSCTransction.transferBnb(fromAddress,walletInfo?.decrypt(password),toAddress,
                                BigDecimal(amount),mainAssetGasLimit,gasPrice
                            )
                        }else{
                            SendBSCTransction.transferBep20(fromAddress,walletInfo?.decrypt(password),toAddress,money,assets?.contractAddress,tokenGasLimit,gasPrice)
                        }
                    }
                    else->{
                        //需要拆分成两笔交易
                        txHash = if(isMainAsset(assets!!,CoinTypeEnum.BSC.code)){
                            BSCTool.createRechargeBnb(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress).txHex
                        }else {
                            BSCTool.createRechargeBep20(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress,heterogeneousInfo.contractAddress).txHex
                        }
                        crossTxHash = SendTransctions.createNerveWithdrawalTx(walletInfo?.nerveAddress,walletInfo?.decrypt(password),toChainId,toAddress,
                            assets?.nerveChainId!!,assets?.nerveAssetId!!,money,crossChainPayFee,System.currentTimeMillis()/1000,remark
                        )
                    }
                }
            }
            CoinTypeEnum.ETH.code->{
                when(toChain){
                    CoinTypeEnum.NERVE.code->{
                        txHash = if(isMainAsset(assets!!,CoinTypeEnum.ETH.code)){
                            ETHTool.createRechargeEth(fromAddress,walletInfo?.decrypt(password), money,toAddress,heterogeneousInfo.heterogeneousChainMultySignAddress).txHex
                        }else{
                            ETHTool.createRechargeErc20(fromAddress,walletInfo?.decrypt(password), money,toAddress,heterogeneousInfo.heterogeneousChainMultySignAddress,heterogeneousInfo.contractAddress).txHex
                        }
                    }
                    CoinTypeEnum.NULS.code->{
                        //需要拆分成两笔交易
                        when(tranferCoin){
                            CoinTypeEnum.NULS.symbol->{
                                //先转到nerve
                                txHash = ETHTool.createRechargeErc20(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress,heterogeneousInfo.contractAddress).txHex
                                //再从nvrve到nuls
                                crossTxHash = SendTransctions.createNerveCrossTxSimpleTransferOfNuls(walletInfo.nerveAddress,walletInfo?.decrypt(password),toAddress,
                                    money,System.currentTimeMillis()/1000,remark
                                )
                            }
                            CoinTypeEnum.NERVE.symbol->{

                                //先转到nerve
                                txHash = ETHTool.createRechargeErc20(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress,heterogeneousInfo.contractAddress).txHex
                                //再从nvrve到nuls
                                crossTxHash = SendTransctions.createNerveCrossTxSimpleTransferOfNvt(walletInfo.nerveAddress,walletInfo?.decrypt(password),toAddress,
                                    money,System.currentTimeMillis()/1000,remark)
                            }
                            CoinTypeEnum.ETH.symbol->{
                                //先转到nerve
                                txHash =ETHTool.createRechargeEth(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress).txHex
                                crossTxHash = SendTransctions.createNerveCrossTxSimpleTransferOfNonNvtNuls(walletInfo.nerveAddress,walletInfo?.decrypt(password),toAddress,assets?.nerveChainId!!,assets?.nerveAssetId!!,
                                    money,System.currentTimeMillis()/1000,remark)
                            }
                            else ->{
                                //先转到nerve
                                txHash = ETHTool.createRechargeErc20(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress,heterogeneousInfo.contractAddress).txHex
                                crossTxHash = SendTransctions.createNerveCrossTxSimpleTransferOfNonNvtNuls(walletInfo.nerveAddress,walletInfo?.decrypt(password),toAddress,assets?.nerveChainId!!,assets?.nerveAssetId!!,
                                    money,System.currentTimeMillis()/1000,remark)
                            }
                        }
                    }
                    CoinTypeEnum.ETH.code->{
                        txHash = if(isMainAsset(assets!!,CoinTypeEnum.ETH.code)){
                            SendETHTransction.transferEth(fromAddress,walletInfo?.decrypt(password),toAddress,
                                BigDecimal(amount),mainAssetGasLimit,gasPrice
                            )
                        }else{
                            SendETHTransction.transferErc20(fromAddress,walletInfo?.decrypt(password),toAddress,money,assets?.contractAddress,tokenGasLimit,gasPrice)
                        }
                    }
                    else->{
                        //需要拆分成两笔交易
                        txHash = if(isMainAsset(assets!!,CoinTypeEnum.ETH.code)){
                            ETHTool.createRechargeEth(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress).txHex
                        }else{
                            ETHTool.createRechargeErc20(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress,heterogeneousInfo.contractAddress).txHex
                        }
                        crossTxHash = SendTransctions.createNerveWithdrawalTx(walletInfo?.nerveAddress,walletInfo?.decrypt(password),toChainId,toAddress,
                            assets?.nerveChainId!!,assets?.nerveAssetId!!,money,crossChainPayFee,System.currentTimeMillis()/1000,remark
                        )
                    }
                }
            }
            CoinTypeEnum.HECO.code->{

                when(toChain){
                    CoinTypeEnum.NERVE.code->{
                        txHash = if(isMainAsset(assets!!,CoinTypeEnum.HECO.code)){
                            HTTool.createRechargeHt(fromAddress,walletInfo?.decrypt(password), money,toAddress,heterogeneousInfo.heterogeneousChainMultySignAddress).txHex
                        }else{
                            HTTool.createRechargeErc20(fromAddress,walletInfo?.decrypt(password), money,toAddress,heterogeneousInfo.heterogeneousChainMultySignAddress,heterogeneousInfo.contractAddress).txHex
                        }
                    }
                    CoinTypeEnum.NULS.code->{
                        //需要拆分成两笔交易
                        when(tranferCoin){
                            CoinTypeEnum.NULS.symbol->{
                                //先转到nerve
                                txHash =HTTool.createRechargeErc20(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress,heterogeneousInfo.contractAddress).txHex
                                //再从nvrve到nuls
                                crossTxHash = SendTransctions.createNerveCrossTxSimpleTransferOfNuls(walletInfo.nerveAddress,walletInfo?.decrypt(password),toAddress,
                                    money,System.currentTimeMillis()/1000,remark
                                )
                            }
                            CoinTypeEnum.NERVE.symbol->{
                                //先转到nerve
                                txHash = HTTool.createRechargeErc20(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress,heterogeneousInfo.contractAddress).txHex
                                //再从nvrve到nuls
                                crossTxHash = SendTransctions.createNerveCrossTxSimpleTransferOfNvt(walletInfo.nerveAddress,walletInfo?.decrypt(password),toAddress,
                                    money,System.currentTimeMillis()/1000,remark)
                            }
                            CoinTypeEnum.HECO.symbol->{
                                //先转到nerve
                                txHash = HTTool.createRechargeHt(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress).txHex
                                crossTxHash = SendTransctions.createNerveCrossTxSimpleTransferOfNonNvtNuls(walletInfo.nerveAddress,walletInfo?.decrypt(password),toAddress,assets?.nerveChainId!!,assets?.nerveAssetId!!,
                                    money,System.currentTimeMillis()/1000,remark)
                            }
                            else ->{
                                //先转到nerve
                                txHash = HTTool.createRechargeErc20(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress,heterogeneousInfo.contractAddress).txHex
                                crossTxHash = SendTransctions.createNerveCrossTxSimpleTransferOfNonNvtNuls(walletInfo.nerveAddress,walletInfo?.decrypt(password),toAddress,assets?.nerveChainId!!,assets?.nerveAssetId!!,
                                    money,System.currentTimeMillis()/1000,remark)
                            }
                        }
                    }
                    CoinTypeEnum.HECO.code->{
                        //heco到heco 判断一下是主链资产还是合约资产就行了
                        txHash = if(isMainAsset(assets!!,CoinTypeEnum.HECO.code)){
                            SendHECOTransction.transferHt(fromAddress,walletInfo?.decrypt(password),toAddress,
                                BigDecimal(amount),mainAssetGasLimit,gasPrice)
                        }else{
                            SendHECOTransction.transferErc20(fromAddress,walletInfo?.decrypt(password),toAddress,money,assets?.contractAddress,mainAssetGasLimit,gasPrice)
                        }

                    }
                    else->{
                        //需要拆分成两笔交易

                        txHash = if(isMainAsset(assets!!,CoinTypeEnum.HECO.code)){
                            HTTool.createRechargeHt(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress).txHex
                        }else{
                            HTTool.createRechargeErc20(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress,heterogeneousInfo.contractAddress).txHex
                        }
//                        when(tranferCoin){
//                            CoinTypeEnum.HECO.symbol->{
//                                //先转到nerve
//                                txHash = HTTool.createRechargeHt(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress).txHex
//
//                            }
//                            else ->{
//                                //先转到nerve
//                                txHash = HTTool.createRechargeErc20(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress,heterogeneousInfo.contractAddress).txHex
//
//                            }
//                        }
                        crossTxHash = SendTransctions.createNerveWithdrawalTx(walletInfo?.nerveAddress,walletInfo?.decrypt(password),toChainId,toAddress,
                            assets?.nerveChainId!!,assets?.nerveAssetId!!,money,crossChainPayFee,System.currentTimeMillis()/1000,remark
                        )
                    }

                }
            }
            CoinTypeEnum.OKEX.code->{

                when(toChain){
                    CoinTypeEnum.NERVE.code->{
                        txHash = if(isMainAsset(assets!!,CoinTypeEnum.OKEX.code)){
                            OKTTool.createRechargeOkt(fromAddress,walletInfo?.decrypt(password), money,toAddress,heterogeneousInfo.heterogeneousChainMultySignAddress).txHex
                        }else{
                            OKTTool.createRechargeKip20(fromAddress,walletInfo?.decrypt(password), money,toAddress,heterogeneousInfo.heterogeneousChainMultySignAddress,heterogeneousInfo.contractAddress).txHex
                        }
                    }
                    CoinTypeEnum.NULS.code->{
                        //需要拆分成两笔交易
                        when(tranferCoin){
                            CoinTypeEnum.NULS.symbol->{
                                //先转到nerve
                                txHash =OKTTool.createRechargeKip20(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress,heterogeneousInfo.contractAddress).txHex
                                //再从nvrve到nuls
                                crossTxHash = SendTransctions.createNerveCrossTxSimpleTransferOfNuls(walletInfo.nerveAddress,walletInfo?.decrypt(password),toAddress,
                                    money,System.currentTimeMillis()/1000,remark
                                )
                            }
                            CoinTypeEnum.NERVE.symbol->{
                                //先转到nerve
                                txHash = OKTTool.createRechargeKip20(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress,heterogeneousInfo.contractAddress).txHex
                                //再从nvrve到nuls
                                crossTxHash = SendTransctions.createNerveCrossTxSimpleTransferOfNvt(walletInfo.nerveAddress,walletInfo?.decrypt(password),toAddress,
                                    money,System.currentTimeMillis()/1000,remark)
                            }
                            CoinTypeEnum.OKEX.symbol->{
                                //先转到nerve
                                txHash = OKTTool.createRechargeOkt(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress).txHex
                                crossTxHash = SendTransctions.createNerveCrossTxSimpleTransferOfNonNvtNuls(walletInfo.nerveAddress,walletInfo?.decrypt(password),toAddress,assets?.nerveChainId!!,assets?.nerveAssetId!!,
                                    money,System.currentTimeMillis()/1000,remark)
                            }
                            else ->{
                                //先转到nerve
                                txHash = OKTTool.createRechargeKip20(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress,heterogeneousInfo.contractAddress).txHex
                                crossTxHash = SendTransctions.createNerveCrossTxSimpleTransferOfNonNvtNuls(walletInfo.nerveAddress,walletInfo?.decrypt(password),toAddress,assets?.nerveChainId!!,assets?.nerveAssetId!!,
                                    money,System.currentTimeMillis()/1000,remark)
                            }
                        }
                    }
                    CoinTypeEnum.OKEX.code->{
                        //heco到heco 判断一下是主链资产还是合约资产就行了
                        txHash = if(isMainAsset(assets!!,CoinTypeEnum.OKEX.code)){
                            SendOKTTransction.transferOkt(fromAddress,walletInfo?.decrypt(password),toAddress,
                                BigDecimal(amount),mainAssetGasLimit,gasPrice)
                        }else{
                            SendOKTTransction.transferKip20(fromAddress,walletInfo?.decrypt(password),toAddress,money,assets?.contractAddress,mainAssetGasLimit,gasPrice)
                        }

                    }
                    else->{
                        //需要拆分成两笔交易
                        txHash =  if(isMainAsset(assets!!,CoinTypeEnum.OKEX.code)){
                            OKTTool.createRechargeOkt(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress).txHex
                        }else{
                            OKTTool.createRechargeKip20(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress,heterogeneousInfo.contractAddress).txHex
                        }
//                        txHash = when(tranferCoin){
//                            CoinTypeEnum.OKEX.symbol->{
//                                //先转到nerve
//                                OKTTool.createRechargeOkt(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress).txHex
//                            }
//                            else ->{
//                                //先转到nerve
//                                OKTTool.createRechargeKip20(fromAddress,walletInfo?.decrypt(password), money,walletInfo.nerveAddress,heterogeneousInfo.heterogeneousChainMultySignAddress,heterogeneousInfo.contractAddress).txHex
//                            }
//                        }
                        crossTxHash = SendTransctions.createNerveWithdrawalTx(walletInfo?.nerveAddress,walletInfo?.decrypt(password),toChainId,toAddress,
                            assets?.nerveChainId!!,assets?.nerveAssetId!!,money,crossChainPayFee,System.currentTimeMillis()/1000,remark
                        )
                    }
                }
            }
        }

        runOnUiThread {
            loadingPopup?.dismiss()
            if(fromChain==toChain){
                //广播链内交易
                createTransfer(txHash)
            }else {
                //跨链交易
                //异构链如果要跨链到nerve 需要先进行授权
                //某些情况下不需要授权，剔除一哈
//                if(fromChain==CoinTypeEnum.NULS.code
//                    &&toChain==CoinTypeEnum.NERVE.code){
//
//                    crossTransfer(txHash,crossTxHash)
//                }else{
//                    if(fromChain==CoinTypeEnum.NERVE.code||fromChain==CoinTypeEnum.NULS.code){
//                        crossTransfer(txHash,crossTxHash)
//                    }else{
//                        crossAuther(fromChain!!,txHash,crossTxHash)
//                    }
//
//                }
                crossTransfer(txHash,crossTxHash)
            }

        }
    }

    override fun initData() {

    }

    companion object{
        fun start(context: Context,
                  fromAddress: String,
                  toAddress:String,
                  fromChain:String,
                  toChain:String,
                  amount: String,
                  assetsEntity: AssetsEntity,
                  payFee:String,
                  remark: String,
                  feeLevel: Double ){
            var intent:Intent = Intent(context,ConfirmTransferActivity().javaClass)
            intent.putExtra("fromAddress",fromAddress)
            intent.putExtra("toAddress",toAddress)
            intent.putExtra("fromChain",fromChain)
            intent.putExtra("toChain",toChain)
            intent.putExtra("amount",amount)
            intent.putExtra("assets",assetsEntity)
            intent.putExtra("payFee",payFee)
            intent.putExtra("remark",remark)
            intent.putExtra("feeLevel",feeLevel)
            context.startActivity(intent)
        }



    }


    /**
     * 广播链内交易
     */
    private fun createTransfer(txHex:String){

        RetrofitClient.getInstance().invokePostBody(this,Api.TX_TRANSFER, mapOf(
            "language" to  UserDao.language,//语言
            "chain" to  fromChain,//地址所属链 因为是链内交易 所以fromchain==tochain
            "address" to fromAddress,//地址
            "txHex" to  txHex,//交易hex
            "chainId" to  assets?.chainId,//转账资产chainId       (chainId assetId contractId 不能同时为空)
            "assetId" to  assets?.assetId,//转账资产assetId      (chainId assetId contractId 不能同时为空)
            "contractAddress" to assets?.contractAddress//转账资产contractAddress     (chainId assetId contractId 不能同时为空)
        )).subscribe(object : RecObserver<BaseResponse<Any>>(this,false,false,Api.TX_TRANSFER){
            override fun onSuccess(t: BaseResponse<Any>?) {
//                ActivityUtils.finishActivity(TransferActivity().javaClass)
//                ActivityUtils.finishActivity(CrossChainActivity().javaClass)
//                showCommonHint(this@ConfirmTransferActivity,null,getString(R.string.transaction_success),true)
                EventBus.getDefault().post(TransferSuccess())
                showToast(getString(R.string.transaction_success))
                startActivity(Intent(this@ConfirmTransferActivity, MainMenuActivity().javaClass))
            }
            override fun onFail(msg: String, code: Int) {
                showToast(getString(R.string.transaction_fail)+msg)
//                showCommonHint(this@ConfirmTransferActivity,null,getString(R.string.transaction_fail)+msg,false)
            }
        })
    }


    /**
     * 异构链资产在跨链转出到nerve链上之前，需要在本链广播授权交易
     */
    private fun crossAuther(crossChain:String,txHex:String,crossTxHex:String){
        RetrofitClient.getInstance().invokePostBody(this,Api.CROSS_AUTHER, mapOf(
            "language" to UserDao.language,
            "chain"  to crossChain,
            "txHex" to txHex
        )).subscribe(object :RecObserver<BaseResponse<Any>>(this,true,false,Api.CROSS_AUTHER){
            override fun onSuccess(t: BaseResponse<Any>?) {
                crossTransfer(txHex,crossTxHex)

            }

            override fun onFail(msg: String, code: Int) {
                    showToast(msg)
            }
        })
    }



    private fun crossTransfer(txHex:String,crossTxHex:String){

        RetrofitClient.getInstance().invokePostBody(this,Api.CROSS_TRANSFER, mapOf(
            "language" to UserDao.language,
            "fromChain"  to fromChain,
            "toChain" to toChain,
            "fromAddress" to fromAddress,
            "toAddress" to  toAddress,
            "txHex" to  txHex,
            "chainId" to assets?.chainId,
            "assetId" to assets?.assetId,
            "contractAddress" to assets?.contractAddress,
            "crossTxHex" to crossTxHex

        )).subscribe(object :RecObserver<BaseResponse<Any>>(this,true,false,Api.CROSS_TRANSFER){
            override fun onSuccess(t: BaseResponse<Any>?) {
                //手动移除前面可能存在的这三个activity 吧   不去修改mainActivity的启动方式
//                ActivityUtils.finishActivity(TransferActivity().javaClass)
//                ActivityUtils.finishActivity(CrossChainActivity().javaClass)
//                ActivityUtils.finishActivity(AssetsDetailActivity().javaClass)
                EventBus.getDefault().post(TransferSuccess())
                showToast(getString(R.string.transaction_success))
                startActivity(Intent(this@ConfirmTransferActivity, MainMenuActivity().javaClass))
//                showCommonHint(this@ConfirmTransferActivity,null,getString(R.string.transaction_success),true)
            }

            override fun onFail(msg: String, code: Int) {
                showToast(getString(R.string.transaction_fail)+msg)
//                showCommonHint(this@ConfirmTransferActivity,null,getString(R.string.transaction_fail)+msg,false)
            }
        })
    }


    //查询异构跨链手续费
    private fun getFeeByNvtCross(chain:String){
        //每次进来置为0
        crossChainPayFee = BigInteger("0")
        RetrofitClient.getInstance().invokePostBody(this,Api.NVT_CROSS_FEE, mapOf(
            "language" to UserDao.language,
            "chain" to chain
        )).subscribe(object :RecObserver<BaseResponse<Number>>(this,false,false,Api.NVT_CROSS_FEE){
            override fun onSuccess(t: BaseResponse<Number>?) {
                //小数点八位
                if(t?.data!=null){
                   crossChainPayFee = BigInteger.valueOf((t.data.toDouble()*feeLevel).toLong())
                }
            }
            override fun onFail(msg: String, code: Int) {
                showToast(msg)
            }
        })
    }


    //查询gas价格
    private fun getGasPrice(chain: String){
        RetrofitClient.getInstance().invokePostBody(this,Api.GAS_PRICE, mapOf(
            "language" to  UserDao.language,
            "chain" to  chain
        )).subscribe(object :RecObserver<BaseResponse<Number>>(this,false,false,Api.GAS_PRICE){
            override fun onSuccess(t: BaseResponse<Number>?) {
                if(t?.data!=null){
//                    gasPrice = BiBigInteger.valueOf(t.data*feeLevel)
                    gasPrice = BigInteger(Arith.mulNorGrouping(t.data.toDouble(),feeLevel,CoinTypeEnum.getDecimalByCode(chain)))
                }
            }
            override fun onFail(msg: String, code: Int) {
                //获取gasprice失败

            }
        })

    }


    override fun onDestroy() {
        super.onDestroy()
        if(loadingPopup!=null&&loadingPopup?.isShow!!){
            loadingPopup?.dismiss()
        }
    }


    /**
     * 获取变更后的资产信息 （在异构链向nuls进行跨连划转的时候。或者异构链之间的跨连划转）
     */
    private fun getChangeAssetInfo(chain:String,chainId:Int,assetId:Int,contractAddress:String?=""){
        //Fromchain或者tochain不等于nerve的时候，组装跨连交易的时候就需要重新获取变更后的资产信息
        if(fromChain!=CoinTypeEnum.NERVE.code
            ||toChain!=CoinTypeEnum.NERVE.code&&fromChain!=toChain
           ){
            RetrofitClient.getInstance().invokePostBody(this,Api.CROSS_CHAIN_INFO, mapOf(
                "language" to UserDao.language,
                "chain" to chain,
                "chainId" to chainId,
                "assetId" to assetId,
                "contractAddress" to contractAddress
            )).subscribe(object :RecObserver<BaseResponse<CrossAssetInfo>>(this,true,false,Api.CROSS_CHAIN_INFO){
                override fun onSuccess(t: BaseResponse<CrossAssetInfo>?) {
                    if(t?.data!=null){
                        bridgeChainId = t?.data?.chainId?.toInt()!!
                        bridgeAssetId = t?.data?.assetId?.toInt()!!
                    }
                }

                override fun onFail(msg: String, code: Int) {

                }
            })
        }
    }

}