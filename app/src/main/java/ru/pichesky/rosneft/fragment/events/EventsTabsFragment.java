package ru.pichesky.rosneft.fragment.events;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONObject;

import ru.pichesky.rosneft.R;
import ru.pichesky.rosneft.activity.RegionSelectorActivity;
import ru.pichesky.rosneft.adapter.EventsTabsPagerAdapter;
import ru.pichesky.rosneft.extension.CustomViewPager;
import ru.pichesky.rosneft.fragment.BaseTabsFragment;
import ru.pichesky.rosneft.helper.PrefsHelper;
import ru.pichesky.rosneft.helper.ToastHelper;
import ru.pichesky.rosneft.interfaces.IObservable;
import ru.pichesky.rosneft.model.Region;
import ru.pichesky.rosneft.model.SimpleWrapper;
import ru.pichesky.rosneft.utils.CONST;
import ru.pichesky.rosneft.utils.api.Api;

/**
 * Created by Andrew Vasilev on 16.07.2015.
 */
public class EventsTabsFragment extends BaseTabsFragment implements View.OnClickListener {

    private static final int TAB_EVENT = 0;
    private static final int TAB_PRODUCT = 1;
    private static final int NOT_NEED_SWITCH = -1;
    private static final int NEED_SWITCH_TAB = 2;

    private static Location sLocation = null;
    private Region mRegion = Region.getByRegionId(PrefsHelper.getInstance().getUserRegionId());
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
        Api.getInstance().setRegion(mRegion.getRegionId() ,getSetRegionResponseListener(),getSetRegionErrorListener());
        for (Fragment fragment : getChildFragmentManager().getFragments())
            ((IObservable) fragment).onDataChanged();
    }

    private Response.Listener<JSONObject> getSetRegionResponseListener(){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                SimpleWrapper wrapper = new Gson().fromJson(response.toString(), SimpleWrapper.class);
                if(wrapper.getErrorCode() == CONST.ERROR_CODE_OK){
                    PrefsHelper.getInstance().setUserRegionId(mRegion.getRegionId());
                    mAq.id(R.id.regions_button).text(mRegion.getName());
                } else {
                    ToastHelper.showToast(R.string.error_unknown);
                }
            }
        };
    }

    private Response.ErrorListener getSetRegionErrorListener(){
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
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
