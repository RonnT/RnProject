package ru.pichesky.rosneft.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.pichesky.rosneft.MyApp;
import ru.pichesky.rosneft.R;
import ru.pichesky.rosneft.fragment.azs.AzsListFragment;
import ru.pichesky.rosneft.fragment.azs.AzsMapFragment;

/**
 * Created by Andrew Vasilev on 16.07.2015.
 */
public class AzsPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] mFragments = {
            new AzsMapFragment(),
            new AzsListFragment()
    };

    public AzsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:     return MyApp.getStringFromRes(R.string.caps_on_map);
            default:    return MyApp.getStringFromRes(R.string.caps_on_list);
        }
    }

    public AzsListFragment getAzsListFragment() {
        return (AzsListFragment) mFragments[1];
    }
}
