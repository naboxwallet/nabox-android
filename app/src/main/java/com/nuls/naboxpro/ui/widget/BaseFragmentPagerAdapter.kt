package com.nuls.naboxpro.ui.widget

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.nuls.naboxpro.ui.widget.lazy.LazyFragmentPagerAdapter

/**
 *
 * @function 基类adapter
 * Created by xhw on 2020-1-16 15:17
 */
class BaseFragmentPagerAdapter(fm: FragmentManager) : LazyFragmentPagerAdapter(fm) {
    override fun getItem(container: ViewGroup?, position: Int): Fragment {
        return fragmentList[position]
    }

    private var fragmentList = mutableListOf<Fragment>()
    private var titleList = mutableListOf<String>()

    constructor(fragmentList: MutableList<Fragment>, fm: FragmentManager) : this(fm) {
        this.fragmentList = fragmentList
    }

    fun setNewData(fragmentList: MutableList<Fragment>) {
        this.fragmentList = fragmentList
        notifyDataSetChanged()
    }

    fun setNewData(fragmentList: MutableList<Fragment>,titleList: MutableList<String>) {
        this.fragmentList = fragmentList
        this.titleList=titleList
        notifyDataSetChanged()
    }

    constructor(fragmentList: MutableList<Fragment>, titleList: MutableList<String>, fm: FragmentManager) : this(fm) {
        this.fragmentList = fragmentList
        this.titleList = titleList
    }

    override fun getCount(): Int {
        return fragmentList.size
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

    }

    override fun getPageTitle(position: Int): CharSequence? {
        if (titleList.isEmpty()) {
            return null
        }
        return titleList[position]
    }



}