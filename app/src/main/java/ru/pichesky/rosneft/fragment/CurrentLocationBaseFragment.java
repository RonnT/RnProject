package ru.pichesky.rosneft.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;

import ru.pichesky.rosneft.helper.LocationHelper;
import ru.pichesky.rosneft.utils.CONST;


/**
 * Created by Andrew Vasilev on 29.05.2015.
 */
public abstract class CurrentLocationBaseFragment extends BaseFragment {

    private static boolean sIsGpsChecked = false;

    private BroadcastReceiver mBr;
    private boolean mIsReceiverRegistered = false;

    protected Location mLocation = null;

    @Override
    protected void onVisible() {
        if (mBr == null) initBr();
        registerBr();
    }

    @Override
    protected void onInvisible() {
        unregisterBr();
    }

    @Override
    protected void setData() {
        initViews();

        if (mBr == null) initBr();
    }

    private void initBr() {
        mLocation = LocationHelper.getCurrentLocation();

        mBr = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case CONST.INTENT_UPDATE_LOCATION:
                        updateLocIfNeeded();
                        break;
                }
            }
        };
    }

    private void updateLocIfNeeded() {
        mLocation = LocationHelper.getCurrentLocation();
        onLocationUpdated();
    }

    private void registerBr() {
        if (mIsReceiverRegistered) return;

        if (!sIsGpsChecked) {
            LocationHelper.checkGpsTurnedOn(getActivity());
            sIsGpsChecked = true;
        }

        IntentFilter intentFilter = new IntentFilter(CONST.INTENT_UPDATE_LOCATION);
        getActivity().registerReceiver(mBr, intentFilter);
        mIsReceiverRegistered = true;
    }

    private void unregisterBr() {
        if (!mIsReceiverRegistered) return;

        LocationHelper.stopCheckingLocation();
        if (mBr != null) {
            getActivity().unregisterReceiver(mBr);
            mIsReceiverRegistered = false;
        }
    }

    protected abstract void onLocationUpdated();
    protected abstract void initViews();
}
