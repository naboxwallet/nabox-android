package com.nuls.naboxpro.ui.splash.fragment

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import com.alibaba.fastjson.JSON
import com.duke.dfileselector.activity.DefaultSelectorActivity
import com.duke.dfileselector.util.FileSelectorUtils
import com.nuls.naboxpro.R
import com.nuls.naboxpro.entity.WalletInfo
import com.nuls.naboxpro.ui.BaseFragment
import com.nuls.naboxpro.ui.splash.ChoiceWalletSkinActivity
import com.nuls.naboxpro.ui.splash.SaveWalletHintActivity
import com.nuls.naboxpro.utils.FileUtil
import com.nuls.naboxpro.utils.showToast
import com.nuls.naboxpro.wallet.NaboxAccount
import com.nuls.naboxpro.wallet.NaboxAccountUtil
import com.nuls.naboxpro.wallet.NulsKeyStore
import com.tbruyelle.rxpermissions2.RxPermissions
import io.nuls.core.crypto.AESEncrypt
import io.nuls.core.crypto.HexUtil
import kotlinx.android.synthetic.main.activity_create_wallet_first.*
import kotlinx.android.synthetic.main.fragment_import_keystore.*
import kotlinx.android.synthetic.main.fragment_import_keystore.chedkbox_psw_checked
import kotlinx.android.synthetic.main.fragment_import_keystore.edit_password
import java.lang.Exception
import java.math.BigInteger
import java.util.ArrayList

class ImportKeystoreFragment:BaseFragment() {
    override fun getLayoutId() = R.layout.fragment_import_keystore

    override fun initView() {
        NaboxAccountUtil.createAccount()
        btn_addfile.setOnClickListener {
            //选择keystore
            val rxPermission = RxPermissions(this)
            rxPermission
                .request(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe { granted ->
                    if (granted) { // Always true pre-M
                        DefaultSelectorActivity.startActivity(activity)
                    } else {

                    }
                }

        }

        edit_filecontent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                btn_next.isEnabled =
                    !(TextUtils.isEmpty(s) && !TextUtils.isEmpty(edit_password.text))
            }
        })

        edit_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                //                if (TextUtils.isEmpty(s)) {
                //                    editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                //                } else {
                //                    chedkboxPswChecked.setVisibility(View.VISIBLE);
                //                }
                if (TextUtils.isEmpty(s)) {
                    chedkbox_psw_checked.visibility = View.INVISIBLE
                    btn_next.isEnabled = false
                } else {
                    chedkbox_psw_checked.visibility = View.VISIBLE
                    if (!TextUtils.isEmpty(edit_filecontent.text)) {
                        btn_next.isEnabled = true
                    }
                }

            }
        })
        chedkbox_psw_checked.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //默认选择 显示密码
                edit_password.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {
                //未选中 显示明文
                edit_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
            if (!TextUtils.isEmpty(edit_password.text)) {
                edit_password.setSelection(edit_password.text.length)
            }
        })


        btn_next.setOnClickListener {
            var nulsKeyStore:NulsKeyStore =
                JSON.parseObject(edit_filecontent.text.toString().trim(), NulsKeyStore::class.java)
            try {
                val privateKey = AESEncrypt.decrypt(HexUtil.decode(nulsKeyStore.encryptedPrivateKey), edit_password.text.toString())
                var naboxAccount  =  NaboxAccountUtil.importAccount(HexUtil.encode(privateKey),edit_password.text.toString().trim())
                if(nulsKeyStore.address!=NaboxAccountUtil.initAddress(HexUtil.encode(privateKey))){
                    Toast.makeText(activity,getString(R.string.password_error),Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if(naboxAccount!=null&&!TextUtils.isEmpty(naboxAccount.nulsAddress)){
                    //如果生成的nuls地址不为空，则认为创建钱包成功
//                    activity?.let { it1 -> ChoiceWalletSkinActivity.start(it1,naboxAccount,nulsKeyStore.alias) }

                    activity?.let { it1 -> SaveWalletHintActivity.start(it1,0,naboxAccount,nulsKeyStore.alias) }

                }
            }catch (e:Exception){
               showToast(getString(R.string.password_error))
            }
        }
    }

    override fun initData() {

    }
    private val intentFilter = IntentFilter(DefaultSelectorActivity.FILE_SELECT_ACTION)
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context == null || intent == null) {
                return
            }
            if (DefaultSelectorActivity.FILE_SELECT_ACTION == intent.action) {
                if (userVisibleHint) {
                    printData(DefaultSelectorActivity.getDataFromIntent(intent))
                }
            }
        }
    }

    private fun printData(list: ArrayList<String>) {
        if (FileSelectorUtils.isEmpty(list)) {
            return
        }
        if (FileUtil.isKeystore(list[0])) {
            edit_filecontent.setText(FileUtil.readString(list[0], "UTF-8"))
        } else {
            Toast.makeText(activity,getString(R.string.incorrect_file_format),Toast.LENGTH_SHORT).show()

        }
    }
    private var isRegister: Boolean = false
    override fun onResume() {
        super.onResume()

        if (!isRegister) {
            activity?.registerReceiver(receiver, intentFilter)
            isRegister = true
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (isRegister) {
            activity?.unregisterReceiver(receiver)
            isRegister = false
        }

    }

}