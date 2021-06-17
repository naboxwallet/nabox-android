package com.nuls.naboxpro.ui.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import com.nuls.naboxpro.R
import com.nuls.naboxpro.common.UserDao
import com.nuls.naboxpro.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_safe_setting.*
import kotlinx.android.synthetic.main.base_title_layout.*

class SafeSettingActivity :BaseActivity() {
    override fun getLayoutId() = R.layout.activity_safe_setting

    override fun initView() {
        back.setOnClickListener(View.OnClickListener { finish() })
        checkbox.setOnClickListener {

            if (checkbox.isChecked) {

            } else {
                AlertDialog.Builder(this)
                    .setTitle(R.string.hint)
                    .setMessage(R.string.close_guesture)
                    .setNegativeButton(
                        getString(R.string.cancel),
                        DialogInterface.OnClickListener { dialog, which ->
                            checkbox.isChecked = true
                        })
                    .setPositiveButton(
                        getString(R.string.sure),
                        DialogInterface.OnClickListener { dialog, which -> //关闭
                            UserDao.isGesture = false
                        })
                    .show()
            }
        }
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        checkbox.isChecked = UserDao.isGesture
    }
}