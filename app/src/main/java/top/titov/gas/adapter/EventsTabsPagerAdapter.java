package top.titov.gas.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import top.titov.gas.MyApp;
import top.titov.gas.R;
import top.titov.gas.fragment.events.EventsFragment;
import top.titov.gas.fragment.events.ProductsFragment;

/**
 * Created by Andrew Vasilev on 16.07.2015.
 */
public class EventsTabsPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] mFragments = {
            new EventsFragment(),
            new ProductsFragment()
    };

    public EventsTabsPagerAdapter(FragmentManager fm) {
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
            case 0:     return MyApp.getStringFromRes(R.string.caps_events);
            default:    return MyApp.getStringFromRes(R.string.caps_products);
        }
    }
}
