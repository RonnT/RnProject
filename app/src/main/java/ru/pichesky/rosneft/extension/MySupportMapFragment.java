package ru.pichesky.rosneft.extension;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

import ru.pichesky.rosneft.interfaces.IMapTouchObserver;

public class MySupportMapFragment extends SupportMapFragment {

    public View mOriginalContentView;
    public MapFrameLayout mTouchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mOriginalContentView = super.onCreateView(inflater, parent, savedInstanceState);
        mTouchView = new MapFrameLayout(getActivity());
        mTouchView.addView(mOriginalContentView);
        return mTouchView;
    }

    @Override
    public View getView() {
        return mOriginalContentView;
    }

    public void setMapTouchObserver(IMapTouchObserver pObserver) {
        mTouchView.setMapTouchObserver(pObserver);
    }
}