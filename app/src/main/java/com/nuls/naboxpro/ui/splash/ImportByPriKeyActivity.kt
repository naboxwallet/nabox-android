package com.nuls.naboxpro.ui.splash

import android.text.*
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import com.gyf.immersionbar.ktx.immersionBar
import com.nuls.naboxpro.R
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.utils.NaboxUtils
import com.nuls.naboxpro.utils.showToast
import com.nuls.naboxpro.wallet.NaboxAccountUtil
import kotlinx.android.synthetic.main.activity_personal_key_import_wallet.*
import kotlinx.android.synthetic.main.base_title_layout.*

class ImportByPriKeyActivity:BaseActivity() {
    override fun getLayoutId() = R.layout.activity_personal_key_import_wallet
    override fun initView() {

        immersionBar {
            statusBarDarkFont(true,0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }

        back.setOnClickListener {
            finish()
        }
        tv_title.text = getString(R.string.import_wallet)
        edit_password.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(20))
        edit_reset_password.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(20))
        edit_personal_key.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                btn_next.isEnabled = !TextUtils.isEmpty(s)


            }
        })
        edit_reset_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (TextUtils.isEmpty(s)) {
                    chedkbox_psw_checked.visibility = View.INVISIBLE
                    edit_reset_password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                } else {
                    if(s.length>20){
                        showToast("密码应为8-20位的数字和字母")
                    }
                    chedkbox_psw_checked.visibility = View.VISIBLE
                }
            }
        })
        edit_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (TextUtils.isEmpty(s)) {
                    tv_psw_level.visibility = View.INVISIBLE
                    //                    tvHint2.setVisibility(View.INVISIBLE);
                } else {
                    tv_psw_level.visibility = View.VISIBLE
                    checkPassword(s.toString(), tv_psw_level)
                }
            }
        })
        chedkbox_psw_checked.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                                    //默认选择 显示密码
//                edit_reset_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                edit_password.setInputType(InputType.TYPE_CLASS_TEXT || InputType.TYPE_TEXT_VARIATION_PASSWORD);
                edit_reset_password.transformationMethod = PasswordTransformationMethod.getInstance()
                edit_password.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {

                edit_reset_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                edit_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
            //手动设置光标到最后
            if (!TextUtils.isEmpty(edit_reset_password.text)) {
                edit_reset_password.setSelection(edit_reset_password.text.length)
            }
            if (!TextUtils.isEmpty(edit_password.text)) {
                edit_password.setSelection(edit_password.text.length)
            }
        })
        btn_next.setOnClickListener {
           if(TextUtils.isEmpty(edit_personal_key.text)){
                showToast(getString(R.string.please_input_privatekey))
            }else if(TextUtils.isEmpty(edit_password.text)){
                showToast(getString(R.string.please_input_password))
            }else if(edit_password.text.length<8){
                showToast(getString(R.string.password_must_morethan_6))
            }else if(passwordLevel<2){
                showToast(getString(R.string.password_simple))
           }else if(TextUtils.isEmpty(edit_reset_password.text)){
                showToast(getString(R.string.please_reset_password))
            }else if(edit_reset_password.text.toString() != edit_password.text.toString()){
                showToast(getString(R.string.two_password_inconsistencies))
            }else {
                //导入私钥
                var naboxAccount  =  NaboxAccountUtil.importAccount(edit_personal_key.text.toString().trim(),edit_password.text.toString().trim())
                if(naboxAccount!=null&&!TextUtils.isEmpty(naboxAccount.nulsAddress)){
                    //如果生成的nuls地址不为空，则认为创建钱包成功
                    InputWalletNameActivity.start(this,naboxAccount)
                }
            }


        }

    }

    var passwordLevel:Int = 0

    /**
     * 判断密码的危险等级
     *
     * @param password 输入的密码
     */
    fun checkPassword(password: String, tvPasswordLevel: TextView) {
        val level = NaboxUtils.checkPasswordLevel(password)
        passwordLevel =level
        initPasswordLevelShow(level!!, tvPasswordLevel)
    }

    private fun initPasswordLevelShow(level: Int, passwordLevel: TextView) {
        var drawable = resources.getDrawable(R.mipmap.icon_norm)
        when (level) {
            0 -> {
                passwordLevel.text = getString(R.string.danger)
                passwordLevel.setTextColor(resources.getColor(R.color.red))
                drawable = resources.getDrawable(R.mipmap.icon_danger)
            }
            1 -> {
                passwordLevel.text = getString(R.string.danger)
                passwordLevel.setTextColor(resources.getColor(R.color.red))
                drawable = resources.getDrawable(R.mipmap.icon_danger)
            }
            2 -> {
                passwordLevel.text = getString(R.string.normal)
                passwordLevel.setTextColor(resources.getColor(R.color.yellow))
                drawable = resources.getDrawable(R.mipmap.icon_norm)
            }
            3 -> {
                passwordLevel.text = getString(R.string.safe)
                passwordLevel.setTextColor(resources.getColor(R.color.main))
                drawable = resources.getDrawable(R.mipmap.icon_safe)
            }
        }
        // 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        passwordLevel.setCompoundDrawables(null, null, drawable, null)
    }


    override fun initData() {

    }
}