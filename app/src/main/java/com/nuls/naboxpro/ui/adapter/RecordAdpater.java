package com.nuls.naboxpro.ui.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

import static androidx.viewpager.widget.PagerAdapter.POSITION_NONE;

public class RecordAdpater extends FragmentPagerAdapter {
    private List<Fragment> listFragment; // Fragment列表
    private List<String> listTitle; // Tab名的列表
    public RecordAdpater(FragmentManager fm, List<Fragment> listFragment, List<String> listTitle) {
        super(fm);
        this.listFragment = listFragment;
        this.listTitle = listTitle;
    }
    //FragmentStatePagerAdapter


    @Override
    public Fragment getItem(int position) {
        return listFragment.get(position);
    }
    @Override
    public int getCount() {
        return listTitle.size();
    }
    // 此方法用来显示Tab上的名字
    @Override
    public CharSequence getPageTitle(int position) {
        return listTitle.get(position % listTitle.size());
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
