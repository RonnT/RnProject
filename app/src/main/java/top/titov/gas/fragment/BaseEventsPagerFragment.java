package top.titov.gas.fragment;

import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import top.titov.gas.R;
import top.titov.gas.adapter.EventsCardsPagerAdapter;
import top.titov.gas.extension.IndicatorView;
import top.titov.gas.fragment.events.EventsTabsFragment;
import top.titov.gas.interfaces.IObservable;

/**
 * Created by Roman Titov on 22.07.2015.
 */

public abstract class BaseEventsPagerFragment<T> extends BaseFragment implements IObservable, Response.ErrorListener, View.OnClickListener {

    protected ViewPager pager;

    protected List<T> mDataList = new ArrayList<>();
    protected EventsCardsPagerAdapter mAdapter;
    protected List<T> mFederalList = new ArrayList<>();
    protected List<T> mRegionalList = new ArrayList<>();
    protected IndicatorView mIndicatorView;

    @Override
         protected int getLayoutId() {
        return R.layout.fragment_events;
    }

    @Override
    protected void setData() {

        mIndicatorView = (IndicatorView) mAq.id(R.id.indicator_view).getView();

        pager = (ViewPager) mView.findViewById(R.id.pager);
        pager.setOnTouchListener(getPagerTouchListener());
        mAdapter = new EventsCardsPagerAdapter(getChildFragmentManager(), mDataList);
        pager.setAdapter(mAdapter);
        pager.addOnPageChangeListener(mIndicatorView);
        loadData();
    }

    private View.OnTouchListener getPagerTouchListener(){
        return new View.OnTouchListener() {
            float oldX = 0, newX = 0, sens = 5;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        oldX = event.getX();
                        break;

                    case MotionEvent.ACTION_UP:
                        newX = event.getX();
                        if (Math.abs(oldX - newX) < sens) {
                            onItemClick(v, pager.getCurrentItem());
                            return true;
                        }
                        oldX = 0;
                        newX = 0;
                        break;
                }
                return false;
            }
        };
    }

    protected void setSwitchTab(boolean pIsNeedSwitchTab) {
        EventsTabsFragment tabsFragment = (EventsTabsFragment) getParentFragment();
        if (pIsNeedSwitchTab) tabsFragment.checkSwitch();
        else tabsFragment.dontNeedSwitch();
    }

    @Override
    public void onDataChanged() {
        loadData();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
    }

    @Override
    public void onClick(View view) {
        onItemClick(view, pager.getCurrentItem());
    }

    protected abstract void loadData();
    protected abstract void onItemClick(View view, int position);
}


