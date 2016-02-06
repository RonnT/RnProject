package top.titov.gas.fragment.azs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.view.View;

import com.android.volley.Response;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

import top.titov.gas.R;
import top.titov.gas.extension.ObservableList;
import top.titov.gas.fragment.AzsBaseMapFragment;
import top.titov.gas.helper.LocationHelper;
import top.titov.gas.helper.NetworkHelper;
import top.titov.gas.helper.ToastHelper;
import top.titov.gas.interfaces.IMapTouchObserver;
import top.titov.gas.interfaces.IObservable;
import top.titov.gas.model.azs.Azs;
import top.titov.gas.model.azs.AzsWrapper;
import top.titov.gas.utils.CONST;
import top.titov.gas.utils.DataManager;
import top.titov.gas.utils.Utility;
import top.titov.gas.utils.api.Api;

/**
 * Created by Andrew Vasilev on 16.07.2015.
 */
public class AzsMapFragment extends AzsBaseMapFragment implements View.OnClickListener, GoogleMap.OnMapLoadedCallback, IObservable, IMapTouchObserver {

    private ObservableList<Azs> mAzsList = DataManager.getInstance().getAzsList(this);
    private BroadcastReceiver mAzsBr;
    private boolean isMapTouched = false;
    private boolean mFirstRefreshByIntent = true;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_azs_map;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mAzsBr == null) initAzsBr();
        registerAzsBr();
        if (mMap == null) return;

        mLocation = LocationHelper.getCurrentLocation();
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterAzsBr();
    }

    @Override
    protected void setData() {
        super.setData();

        if (mMap == null) return;
        mMapFragment.setMapTouchObserver(this);

        mAq.id(R.id.fragment_map_base_btn_my_loc).clicked(this);
        mAq.id(R.id.fragment_map_base_btn_zoom_in).clicked(this);
        mAq.id(R.id.fragment_map_base_btn_zoom_out).clicked(this);
        mAq.id(R.id.fragment_azs_map_btn_route_to_closest).clicked(this);

        setupClusterer();
        refreshAzsList(false);
        mLocation = LocationHelper.getCurrentLocation();
        /*if (mLocation != null)
            moveCamera(mLocation.getLatitude(), mLocation.getLongitude(), CONST.ZOOM_MY_LOCATION);*/
        mMap.setOnMapLoadedCallback(this);
    }

    @Override
    protected void refreshAzsList(boolean isNeedCheckChanges) {
        List<Azs> newAzsList = Azs.getAll();
        if (!mAzsList.equals(newAzsList) || !isNeedCheckChanges) {
            mAzsList.clear();
            mAzsList.addAll(newAzsList);
        }
    }

    private void initAzsBr() {
        mAzsBr = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case CONST.INTENT_UPDATE_LOCATION:
                        DataManager.getInstance().setMyLocation(LocationHelper.getCurrentLocation());
                       /* if (!isMapTouched)
                            changeMyLocation(false, (int) mMap.getCameraPosition().zoom);*/
                        break;
                    case CONST.INTENT_UPDATE_AZS:
                        mAzsList.clear();
                        refreshAzsList(CONST.CHECK_CHANGES);
                        break;
                }
            }
        };
    }

    private void registerAzsBr() {
        LocationHelper.checkLocationTurnedOn(getActivity());

        IntentFilter intentFilter = new IntentFilter(CONST.INTENT_UPDATE_LOCATION);
        intentFilter.addAction(CONST.INTENT_UPDATE_AZS);
        getActivity().registerReceiver(mAzsBr, intentFilter);
    }

    private void unregisterAzsBr() {
        if (mAzsBr != null) getActivity().unregisterReceiver(mAzsBr);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
/*
    private void changeMyLocation(boolean pNeedRecreateMyMarker, int pZoom) {
        changeMyLocation(pNeedRecreateMyMarker, pZoom, null);
    }

    private void changeMyLocation(boolean pNeedRecreateMyMarker, int pZoom,
                                  GoogleMap.CancelableCallback pCallback) {

        if (mLocation == null) return;

        double lat = mLocation.getLatitude();
        double lng = mLocation.getLongitude();

        if (mCurrentLocationMarker == null || pNeedRecreateMyMarker) {
            addCurrentLocationMarker();
        } else mCurrentLocationMarker.setPosition(new LatLng(lat, lng));

        moveCamToCurrentPosition(pZoom, pCallback);
    }
*/
    @Override
    protected void refreshAzsOnMap() {
        clearMap();
        addCurrentLocationMarker();
        addAzsMarkers(mAzsList);
    }

    @Override
    public void onClick(View v) {
        LatLng position = mMap.getCameraPosition().target;
        float zoom = mMap.getCameraPosition().zoom;

        switch (v.getId()) {
            case R.id.fragment_map_base_btn_my_loc:
                mLocation = LocationHelper.getCurrentLocation();
                moveCamToCurrentPosition();
                mAq.id(R.id.fragment_azs_map_btn_route_to_closest).visible();
                return;

            case R.id.fragment_azs_map_btn_route_to_closest:
                mLocation = LocationHelper.getCurrentLocation();

                if (!NetworkHelper.isNetworkAvailableWToast()) {
                    return;
                }

                if (mLocation == null) {
                    ToastHelper.showToast(R.string.cant_find_loc);
                    return;
                }
                Api.getInstance().getNearestStation(mLocation, getNearestStationListener(), this);
                break;

            case R.id.fragment_map_base_btn_zoom_in:
                zoom++;
                zoom++;
                break;

            default:
                zoom--;
                zoom--;
                break;
        }
        moveCamera(position.latitude, position.longitude, zoom);
    }

    private Response.Listener<JSONObject> getNearestStationListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                AzsWrapper wrapper = new Gson().fromJson(response.toString(), AzsWrapper.class);
                Azs azs = wrapper.getAzs();
                Location azsLocation = azs.getLocation();
                Utility.runRouteIntent(getActivity(), mLocation, azsLocation);
            }
        };
    }

    @Override
    public void onMapLoaded() {
        moveToCenterOfUsa();
        refreshAzsOnMap();
        //if (mLocation == null) moveToCenterOfUsa();
        /*else {
            changeMyLocation(true, CONST.ZOOM_MY_LOCATION, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    refreshAzsList(CONST.CHECK_CHANGES);
                }
                @Override
                public void onCancel() {
                }
            });
        }*/
    }

    @Override
    public void onDataChanged() {
        //refreshAzsOnMap();
    }

    @Override
    public void onMapTouchEventUp() {
    }

    @Override
    public boolean onMapTouch() {
        isMapTouched = true;
        mAq.id(R.id.fragment_azs_map_btn_route_to_closest).gone();
        return false;
    }
}
