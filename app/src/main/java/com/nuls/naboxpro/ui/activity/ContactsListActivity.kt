package com.nuls.naboxpro.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gyf.immersionbar.ktx.immersionBar
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnConfirmListener
import com.nuls.naboxpro.R
import com.nuls.naboxpro.db.ContactDaoHelper
import com.nuls.naboxpro.entity.ContactsDaoEntity
import com.nuls.naboxpro.entity.WalletInfo
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.ui.popup.InputPasswordPop
import com.nuls.naboxpro.ui.widget.EmptyView
import com.nuls.naboxpro.utils.showPasswordPop
import kotlinx.android.synthetic.main.activity_wallet_list.*
import kotlinx.android.synthetic.main.activity_wallet_list.back
import kotlinx.android.synthetic.main.activity_wallet_list.tv_title
import kotlinx.android.synthetic.main.base_title_layout.*


/**
 * 联系人列表
 */
class ContactsListActivity:BaseActivity(){


    var adapter:ContactAdapter?=null
    override fun getLayoutId() = R.layout.activity_wallet_list

    var emptyView:EmptyView?=null

    override fun initView(){
        immersionBar {
            statusBarDarkFont(true,0.2f)
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
        }
        back.setOnClickListener {
            finish()
        }

        emptyView = EmptyView(this)
        emptyView?.setEmptyImg(R.mipmap.png_no_contacts)
        emptyView?.setEmptyText(R.string.no_contact_at_present)
        tv_title.text = getString(R.string.contacts)
        adapter = ContactAdapter()
        adapter?.setEmptyView(emptyView!!)
        adapter?.setOnItemLongClickListener { adapter, view, position ->
            XPopup.Builder(this)
                .dismissOnBackPressed(true)
                .dismissOnTouchOutside(true)
                .asConfirm(getString(R.string.delete_contact),null,object :OnConfirmListener{
                    override fun onConfirm() {
                        ContactDaoHelper.deleteContact(adapter.data[position] as ContactsDaoEntity)
                        adapter.data.removeAt(position)
                        adapter.notifyDataSetChanged()
                    }
                })
                .show()
            true
        }

        if(intent!=null&& intent.getIntExtra("resultCode",0)!=0){
            //这是选择联系人 隐藏新建联系人按钮
            tv_add.visibility = View.GONE
            adapter?.setOnItemClickListener { adapter, view, position ->
                var item:ContactsDaoEntity = adapter.data[position] as ContactsDaoEntity
                var intent:Intent = Intent()
                intent.putExtra("address",item.address)
                setResult(Activity.RESULT_OK,intent)
                finish()

            }
        }
        recycle_wallet.layoutManager = LinearLayoutManager(this)
        recycle_wallet.adapter = adapter
        back.setOnClickListener {
            finish()
        }
        tv_add.setOnClickListener {
            //添加联系人
            startActivity(Intent(this,AddContactActivity().javaClass))

        }
    }

    override fun initData() {

    }


    override fun onResume() {
        super.onResume()


        if(ContactDaoHelper.loadAllWallet()!=null){
            adapter?.setNewInstance(ContactDaoHelper.loadAllWallet())
        }

    }


    class ContactAdapter:BaseQuickAdapter<ContactsDaoEntity,BaseViewHolder>(R.layout.item_contact_layout){
        override fun convert(holder: BaseViewHolder, item: ContactsDaoEntity) {
            holder.setText(R.id.tv_name,item.name)
            holder.setText(R.id.tv_address,item.address)
        }
    }


    companion object{

        fun startForResult(context: Activity,resultCode:Int){
            var intent = Intent(context,ContactsListActivity().javaClass)
            intent.putExtra("resultCode",resultCode)
            context.startActivityForResult(intent,resultCode)
        }

    }




}