package com.nuls.naboxpro.ui.splash

import android.content.Intent
import android.text.*
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.gyf.immersionbar.ktx.immersionBar
import com.nuls.naboxpro.R
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.utils.NaboxUtils
import com.nuls.naboxpro.wallet.NaboxAccount
import com.nuls.naboxpro.wallet.NaboxAccountUtil
import kotlinx.android.synthetic.main.activity_create_wallet_first.*
import kotlinx.android.synthetic.main.base_title_layout.*
import java.util.regex.Pattern

class InitWalletActivity:BaseActivity(){
    override fun getLayoutId() = R.layout.activity_create_wallet_first


    /**
     * 禁止EditText输入空格
     *
     * @param editText
     */
    private fun setEditTextInhibitInputSpaChat(editText: EditText) {
        val filter_space = InputFilter { source, start, end, dest, dstart, dend ->
            if (source == " ")
                ""
            else
                null
        }
        val filter_speChat = InputFilter { charSequence, i, i1, spanned, i2, i3 ->
            val speChat = "[`~!@#_$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）— +|{}【】‘；：”“’。，、？]"
            val pattern = Pattern.compile(speChat)
            val matcher = pattern.matcher(charSequence.toString())
            if (matcher.find())
                ""
            else
                null
        }
        editText.filters = arrayOf(filter_space, filter_speChat, InputFilter.LengthFilter(12))
    }

    override fun initView() {
        back.setOnClickListener {
            finish()
        }
        immersionBar {
            statusBarDarkFont(true,0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }
        setEditTextInhibitInputSpaChat(edit_name)
        //对两个密码输入框进行监听
        edit_name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                tv_hint1.visibility = View.INVISIBLE

            }
        })
        edit_repeat_name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (TextUtils.isEmpty(s)) {
                    chedkbox_psw_checked.visibility = View.INVISIBLE
                    edit_repeat_name.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

                } else {
                    chedkbox_psw_checked.visibility = View.VISIBLE
                }

            }
        })
        checkbox_agree.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            btn_create.isEnabled = isChecked
        })
        edit_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (TextUtils.isEmpty(s)) {
                    tv_psw_level.visibility = View.INVISIBLE
                    tv_hint2.visibility = View.INVISIBLE
                } else {
                    tv_psw_level.visibility = View.VISIBLE
                    checkPassword(s.toString(), tv_psw_level)
                }
            }
        })
        chedkbox_psw_checked.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            //未选中 显示明文

            if (isChecked) {
                //默认选择 显示密码
//                edit_repeat_name.setInputType(InputType.TYPE_CLASS_TEXT,InputType.TYPE_TEXT_VARIATION_PASSWORD)
//                edit_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                edit_repeat_name.transformationMethod = PasswordTransformationMethod.getInstance()
                edit_password.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {

                //未选中 显示明文

                edit_repeat_name.transformationMethod = HideReturnsTransformationMethod.getInstance()
                edit_password.transformationMethod = HideReturnsTransformationMethod.getInstance()

                //                    editRepeatName.setInputType(InputType.TYPE_CLASS_TEXT);
                //                    editPassword.setInputType(InputType.TYPE_CLASS_TEXT);

            }
            //                editRepeatName.setKeyListener(DigitsKeyListener.getInstance(getString(R.string.digits)));
            //                editPassword.setKeyListener(DigitsKeyListener.getInstance(getString(R.string.digits)));
            //手动设置光标到最后
            if (!TextUtils.isEmpty(edit_repeat_name.text)) {
                edit_repeat_name.setSelection(edit_repeat_name.text.length)
            }
            if (!TextUtils.isEmpty(edit_password.text)) {
                edit_password.setSelection(edit_password.text.length)
            }
        })
        btn_create.setOnClickListener {
            if (createRule()) {
                var naboxAccount:NaboxAccount = NaboxAccountUtil.createAccount(edit_password.text.toString().trim())
                if(naboxAccount!=null&&!TextUtils.isEmpty(naboxAccount.nulsAddress)){
                    //如果生成的nuls地址不为空，则认为创建钱包成功
                        //这里不再选择钱包皮肤
//                    ChoiceWalletSkinActivity.start(this,naboxAccount,edit_name.text.toString().trim())
                    SaveWalletHintActivity.start(this,0,naboxAccount,edit_name.text.toString().trim())
                }
            }
        }
    }


    /**
     * 判断密码的危险等级
     *
     * @param password 输入的密码
     */
    fun checkPassword(password: String, tvPasswordLevel: TextView) {
        val level = NaboxUtils.checkPasswordLevel(password)
        passWordLevel =level
        initPasswordLevelShow(level!!, tvPasswordLevel)
    }

    var passWordLevel = 0
    fun initPasswordLevelShow(level: Int, passwordLevel: TextView) {
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


    /**
     * 判断用户名密码是否为空 两次密码是否相等 是否同意用户条款
     *
     * @return
     */
    private fun createRule(): Boolean {
        tv_hint1.visibility = View.INVISIBLE
        tv_hint2.visibility = View.INVISIBLE
        tv_hint3.visibility = View.INVISIBLE
        if (checkbox_agree.isChecked
            && !TextUtils.isEmpty(edit_name.text)
            && !TextUtils.isEmpty(edit_password.text)
            && !TextUtils.isEmpty(edit_repeat_name.text)
            && edit_password.text.toString() == edit_repeat_name.text.toString()
        ) {
            if (edit_name.text.toString().trim { it <= ' ' }.isEmpty()) {
                tv_hint1.visibility = View.VISIBLE
                tv_hint1.text = getString(R.string.please_input_wallet_name)
                //                ArmsUtils.makeText(getApplicationContext(), "请输入钱包名");
                return false
            } else if (edit_password.text.toString().length < 8) {
                tv_hint2.visibility = View.VISIBLE
                tv_hint2.text = getString(R.string.password_must_morethan_6)
                return false
            } else if (edit_repeat_name.text.toString().length < 8) {
                tv_hint3.visibility = View.VISIBLE
                tv_hint3.text = getString(R.string.password_must_morethan_6)
                return false
            } else if(passWordLevel<2){
                tv_hint3.visibility = View.VISIBLE
                tv_hint3.text = getString(R.string.password_simple)
                return false

            }else if (edit_password.text.toString() != edit_repeat_name.text.toString()) {
                tv_hint3.visibility = View.VISIBLE
                tv_hint3.text = getString(R.string.two_password_inconsistencies)
                return false
            }
            return true
        } else {
            if (TextUtils.isEmpty(edit_name.text)) {
                tv_hint1.visibility = View.VISIBLE
                tv_hint1.text = getString(R.string.please_input_wallet_name)
            } else if (TextUtils.isEmpty(edit_password.text)) {
                tv_hint2.visibility = View.VISIBLE
                tv_hint2.text = getString(R.string.please_input_password)
            } else if (TextUtils.isEmpty(edit_repeat_name.text)) {
                tv_hint3.visibility = View.VISIBLE
                tv_hint3.text = getString(R.string.please_reset_password)
            } else if (!checkbox_agree.isChecked) {
                Toast.makeText(this,getString(R.string.please_read_and_agree_to_the_privacy_clause_first),Toast.LENGTH_SHORT).show()
            } else if (edit_password.text.toString() != edit_repeat_name.text.toString()) {
                tv_hint3.visibility = View.VISIBLE
                tv_hint3.text = getString(R.string.two_password_inconsistencies)
                return false
            }
        }
        return false
    }


}