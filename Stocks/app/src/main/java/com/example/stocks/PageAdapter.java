package com.example.stocks;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {

    private int numOfTabs;
    private final List<Fragment> mFragmentList = new ArrayList<>();

    PageAdapter(FragmentManager manager, int numOfTabs) {
        super(manager);
        this.numOfTabs = numOfTabs;
    }

    public void addFrag(Fragment fragment) {
        mFragmentList.add(fragment);
    }


    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                return new DailyChartFragment();

            case 1:
                return new HistoricalChartFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
