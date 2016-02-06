package top.titov.gas.fragment.events;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONObject;

import top.titov.gas.R;
import top.titov.gas.activity.RegionSelectorActivity;
import top.titov.gas.adapter.EventsTabsPagerAdapter;
import top.titov.gas.extension.CustomViewPager;
import top.titov.gas.fragment.BaseTabsFragment;
import top.titov.gas.helper.PrefsHelper;
import top.titov.gas.helper.ToastHelper;
import top.titov.gas.interfaces.IObservable;
import top.titov.gas.model.Region;
import top.titov.gas.model.SimpleWrapper;
import top.titov.gas.utils.CONST;
import top.titov.gas.utils.api.Api;

/**
 * Created by Andrew Vasilev on 16.07.2015.
 */
public class EventsTabsFragment extends BaseTabsFragment implements View.OnClickListener {

    private static final int TAB_EVENT = 0;
    private static final int TAB_PRODUCT = 1;
    private static final int NOT_NEED_SWITCH = -1;
    private static final int NEED_SWITCH_TAB = 2;

    private static Location sLocation = null;
    private Region mRegion = Region.getByRegionId(5);
    private int stateSwitch = 0;

    @Override
    protected void setData() {
        setupTabLayout();
        mAq.id(R.id.regions_button).text(mRegion.getName()).clicked(this);

        if(getTag() == null)
            return;
        ((CustomViewPager) mAq.id(R.id.fragment_tabs_base_view_pager).getView())
                .setCurrentItem(Integer.parseInt(getTag()) == CONST.PUSH_TYPE_EVENT ? TAB_EVENT : TAB_PRODUCT);
    }

    private void setupTabLayout() {
        TabLayout mTabLayout = (TabLayout) mAq.id(R.id.fragment_tabs_base_tab_layout).getView();

        CustomViewPager pager = (CustomViewPager) mAq.id(R.id.fragment_tabs_base_view_pager).getView();
        pager.setPagingEnabled(false);
        mAdapter = getPagerAdapter();
        pager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(pager);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_events_tabs;
    }

    @Override
    protected PagerAdapter getPagerAdapter() {
        return new EventsTabsPagerAdapter(getChildFragmentManager());
    }

    public static Location getLocation() {
        return sLocation;
    }

    public Region getRegion() {
        return mRegion;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case (CONST.REQ_CODE_SELECT_REGION):
                    mRegion = (Region) data.getSerializableExtra(CONST.NEW_REGION);
                    saveNewRegion();
            }
        }
    }

    private void saveNewRegion() {
        PrefsHelper.getInstance().setUserRegionId(mRegion.getRegionId());
        //Api.getInstance().setRegion(mRegion.getRegionId() ,getSetRegionResponseListener(),getSetRegionErrorListener());
        for (Fragment fragment : getChildFragmentManager().getFragments())
            ((IObservable) fragment).onDataChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.regions_button:
                onRegionButtonClick();
        }
    }

    private void onRegionButtonClick() {
        Intent intent = new Intent(getActivity(), RegionSelectorActivity.class);
        startActivityForResult(intent, CONST.REQ_CODE_SELECT_REGION);
    }

    public synchronized void checkSwitch() {
        if (stateSwitch != NOT_NEED_SWITCH) {
            stateSwitch++;
            if (stateSwitch == NEED_SWITCH_TAB) {
                ((CustomViewPager) mAq.id(R.id.fragment_tabs_base_view_pager).getView())
                        .setCurrentItem(TAB_PRODUCT);
                stateSwitch = NOT_NEED_SWITCH;
            }
        }
    }

    public void dontNeedSwitch() {
        if (stateSwitch != NOT_NEED_SWITCH) stateSwitch = NOT_NEED_SWITCH;
    }
}
