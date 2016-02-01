package ru.pichesky.rosneft.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import ru.pichesky.rosneft.R;

/**
 * Created by Andrew Vasilev on 16.07.2015.
 */
public abstract class BaseTabsFragment extends BaseFragment {

    protected static PagerAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tabs_base;
    }

    @Override
    protected void setData() {
        setupTabLayout();
    }

    private void setupTabLayout() {
        TabLayout mTabLayout = (TabLayout) mAq.id(R.id.fragment_tabs_base_tab_layout).getView();

        ViewPager pager = (ViewPager) mAq.id(R.id.fragment_tabs_base_view_pager).getView();
        mAdapter = getPagerAdapter();
        pager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(pager);
    }

    protected abstract PagerAdapter getPagerAdapter();
}
