package com.nuls.naboxpro.ui.splash

import androidx.fragment.app.Fragment
import com.gyf.immersionbar.ktx.immersionBar
import com.nuls.naboxpro.R
import com.nuls.naboxpro.ui.BaseActivity
import com.nuls.naboxpro.ui.adapter.RecordAdpater
import com.nuls.naboxpro.ui.splash.fragment.ImportKeystoreFragment
import com.nuls.naboxpro.utils.FileUtil
import kotlinx.android.synthetic.main.activity_key_store_import_wallet.*
import kotlinx.android.synthetic.main.base_title_layout.*
import java.util.ArrayList


/**
 * keystore导入
 */
class ImportByKeystoreActivity :BaseActivity(){

    internal var fragments: MutableList<Fragment> = ArrayList<Fragment>()
    internal var titles: MutableList<String> = ArrayList()
    lateinit var file: String
    override fun getLayoutId() = R.layout.activity_key_store_import_wallet

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
        val intent = intent
        val action = intent.action
//        if (intent.ACTION_VIEW == action) {
//            val uri = intent.data
//            //            String str = Uri.decode(uri.getEncodedPath());
//            file = FileUtil.getFPUriToPath(this, uri)
//            //            editFilecontent.setText(FileUtil.readString(FileUtil.getFPUriToPath(this,uri), "UTF-8"));
//        }
        fragments.add(
           ImportKeystoreFragment()
        )
//        fragments.add(
//            ImportKeystoreFragment()
//        )
        titles.add("Keystore 2.0")
//        titles.add("Keystore 1.0")
        tablayout.addTab(tablayout.newTab().setText(titles[0]))
//        tablayout.addTab(tablayout.newTab().setText(titles[1]))
        viewpager.adapter = RecordAdpater(supportFragmentManager, fragments, titles)
        tablayout.setupWithViewPager(viewpager)


    }

    override fun initData() {

    }
}