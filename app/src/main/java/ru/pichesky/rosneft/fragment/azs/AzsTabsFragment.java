package ru.pichesky.rosneft.fragment.azs;

import android.content.Intent;
import android.location.Location;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import ru.pichesky.rosneft.R;
import ru.pichesky.rosneft.adapter.AzsPagerAdapter;
import ru.pichesky.rosneft.fragment.BaseTabsFragment;
import ru.pichesky.rosneft.helper.FilterHelper;
import ru.pichesky.rosneft.helper.PrefsHelper;
import ru.pichesky.rosneft.interfaces.ILocationObservable;
import ru.pichesky.rosneft.utils.CONST;

/**
 * Created by Andrew Vasilev on 16.07.2015.
 */
public class AzsTabsFragment extends BaseTabsFragment {

    private static Location sMapCetnerLocation = null;

    @Override
    protected void setData() {
        super.setData();

        initFilters();
    }

    private void initFilters() {
        FilterHelper.setFuelSpinner(getActivity(), mAq, R.id.view_filters_fuel_spinner, null);

        FilterHelper.setFilterBtn(mAq, R.id.view_filters_btn_wash, getFilterBtnClickListener());
        FilterHelper.setFilterBtn(mAq, R.id.view_filters_btn_service, getFilterBtnClickListener());
        FilterHelper.setFilterBtn(mAq, R.id.view_filters_btn_cafe, getFilterBtnClickListener());
        FilterHelper.setFilterBtn(mAq, R.id.view_filters_btn_shop, getFilterBtnClickListener());
    }

    private static void notifyLocObservers() {
        ILocationObservable observer =
                (ILocationObservable) ((AzsPagerAdapter) mAdapter).getAzsListFragment();
        observer.onLocationUpdated();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_azs_tabs;
    }

    @Override
    protected PagerAdapter getPagerAdapter() {
        return new AzsPagerAdapter(getChildFragmentManager());
    }

    public static Location getLocation() {
        return sMapCetnerLocation;
    }

    public static void setMapCenterLocation(Location pLoc) {
        sMapCetnerLocation = pLoc;
        notifyLocObservers();
    }

    private View.OnClickListener getFilterBtnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefsHelper prefsHelper = PrefsHelper.getInstance();

                int btnId = v.getId();
                switch (btnId) {
                    case R.id.view_filters_btn_wash:
                        prefsHelper.setFilterWash(!prefsHelper.getFilterWash());
                        break;

                    case R.id.view_filters_btn_service:
                        prefsHelper.setFilterService(!prefsHelper.getFilterService());
                        break;

                    case R.id.view_filters_btn_cafe:
                        prefsHelper.setFilterCafe(!prefsHelper.getFilterCafe());
                        break;

                    case R.id.view_filters_btn_shop:
                        prefsHelper.setFilterShop(!prefsHelper.getFilterShop());
                        break;
                }

                FilterHelper.setFilterBtnImage(mAq, btnId);
                getActivity().sendBroadcast(new Intent(CONST.INTENT_UPDATE_AZS));
            }
        };
    }
}
