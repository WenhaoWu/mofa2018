package com.mofa.metropolia.architectmuseo.POIDetail;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class Adapter_ImageSlideAdapter extends FragmentStatePagerAdapter {

    private List<String> picList = new ArrayList<>();

    public Adapter_ImageSlideAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return Fragment_ImageFragment.newInstance(picList.get(position));
    }

    @Override
    public int getCount() {
        return picList.size();
    }

    public void setList (List<String> images){
        this.picList= images;
    }
}
