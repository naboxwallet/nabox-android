package com.nuls.naboxpro.ui.activity

import android.content.Intent
import android.text.TextUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.nuls.naboxpro.R
import com.nuls.naboxpro.db.ContactDaoHelper
import com.nuls.naboxpro.db.WalletInfoDaoHelper
import com.nuls.naboxpro.entity.ContactsDaoEntity
import com.nuls.naboxpro.enums.CoinTypeEnum
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.ui.popup.ContactChainPop
import com.nuls.naboxpro.utils.showContactChainPop
import com.nuls.naboxpro.utils.showToast
import kotlinx.android.synthetic.main.activity_add_contacts.*
import kotlinx.android.synthetic.main.base_title_layout.*


/**
 * 添加联系人
 */
class AddContactActivity :BaseActivity(){

    override fun getLayoutId() = R.layout.activity_add_contacts

    override fun initView() {

        immersionBar {
            statusBarDarkFont(true,0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }
        back.setOnClickListener {
            finish()
        }
        tv_title.text = getString(R.string.add_new_contacts)
        btn_complete.setOnClickListener {

         if(TextUtils.isEmpty(edit_address.text)){
            showToast(getString(R.string.input_contacts_address))
        }else if(TextUtils.isEmpty(edit_name.text)){
            showToast(getString(R.string.input_contacts_name))
        }else{
            var item:ContactsDaoEntity  = ContactsDaoEntity()
            item.address = edit_address.text.toString().trim()
             //不选择联系人所在链了，这里全部默认为Nuls
            item.chainId = CoinTypeEnum.NULS.code
            item.name = edit_name.text.toString().trim()
            ContactDaoHelper.insertContact(item)
            showToast(getString(R.string.add_contacts_success))
            finish()
        }
        }
        edit_coin_type.setOnClickListener {
            showContactChainPop(this, WalletInfoDaoHelper.loadDefaultWallet(),object : ContactChainPop.ChoiceListener{
                override fun itemClick(chain: String) {
                    edit_coin_type.text = chain
                }
            })
        }
        iv_zxing.setOnClickListener {
            startActivityForResult(Intent(this,ScanActivity().javaClass),10086)
        }

    }

    override fun initData() {

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==-1){
            if(data?.getStringExtra("zxing") != null){
                edit_address.setText( data.getStringExtra("zxing"))
            }
        }
    }

}