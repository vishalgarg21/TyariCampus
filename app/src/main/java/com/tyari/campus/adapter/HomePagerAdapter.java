package com.tyari.campus.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tyari.campus.fragment.HomeFragment;
import com.tyari.campus.fragment.SolvedQuestionFragment;
import com.tyari.campus.fragment.TestFragment;

public class HomePagerAdapter extends FragmentPagerAdapter {

    public static final int TAB_COUNT = 3;

    public static final int TAB_HOME = 0;
    public static final int TAB_PRACTICE = 1;
    public static final int TAB_TEST = 2;


    public HomePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == TAB_HOME) {
            fragment = HomeFragment.newInstance();
        } else if (position == TAB_PRACTICE) {
            fragment = SolvedQuestionFragment.newInstance();
        } else if (position == TAB_TEST) {
            fragment = TestFragment.newInstance();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }
}

