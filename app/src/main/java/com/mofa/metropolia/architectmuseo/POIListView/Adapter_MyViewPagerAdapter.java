package com.mofa.metropolia.architectmuseo.POIListView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class Adapter_MyViewPagerAdapter extends FragmentPagerAdapter{

    private final int PAGE_COUNT = 3;
    private String cateStr;

    private String tabTitles[] = new String[]{"Near", "Popular", "Suggest"};

    Adapter_MyViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return Fragment_Location.newInstance(getCateStr());
            case 1:
                return Fragment_TabFragment.newInstance(1, getCateStr());
            case 2:
                return Fragment_TabFragment.newInstance(2, getCateStr());
            default:
                return Fragment_Location.newInstance(getCateStr());
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    public CharSequence getPageTitle(int position){
        return tabTitles[position];
    }

    private String getCateStr() {
        return cateStr;
    }

    void setCateStr(String cate){
        this.cateStr = cate;
    }
}
