package top.titov.gas.fragment;

import android.location.Location;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.titov.gas.MyApp;
import top.titov.gas.R;
import top.titov.gas.activity.AzsItemActivity;
import top.titov.gas.extension.AzsClusterRenderer;
import top.titov.gas.extension.MySupportMapFragment;
import top.titov.gas.helper.ToastHelper;
import top.titov.gas.model.azs.Azs;
import top.titov.gas.model.azs.PriceItem;
import top.titov.gas.model.azs.PriceItemWrapper;
import top.titov.gas.utils.CONST;
import top.titov.gas.utils.api.Api;


/**
 * Created by Andrew Vasilev on 27.05.2015.
 */
public abstract class AzsBaseMapFragment
        extends BaseFragment
        implements GoogleMap.OnMarkerClickListener,ClusterManager.OnClusterClickListener<Azs>,
                    ClusterManager.OnClusterInfoWindowClickListener<Azs>,
                    ClusterManager.OnClusterItemClickListener<Azs>,
                    ClusterManager.OnClusterItemInfoWindowClickListener<Azs>,
                    Response.ErrorListener{

    private static int CAMERA_MOVE_REACT_THRESHOLD_MS = 500;

    private double
            RUSSIA_LAT = 61.812337,
            RUSSIA_LNG = 98.429688;


    protected GoogleMap mMap;

    protected Marker mCurrentLocationMarker = null;
    protected boolean mIsMapTouched = false;
    protected Location mLocation = null;
    protected ClusterManager<Azs> mClusterManager;

    protected Map<Marker, Azs> mMarkerToAzsMap = new HashMap<>();
    protected MySupportMapFragment mMapFragment;

    @Override
    protected void setData() {
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMapFragment =
                    (MySupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mMap = mMapFragment.getMap();

            if (mMap != null) setUpMap();
        }
    }

    protected Marker addMarker(double pLat, double pLng, int pDrawableId) {
        return mMap.addMarker(
                new MarkerOptions()
                        .position(new LatLng(pLat, pLng))
                        .title(MyApp.getStringFromRes(R.string.current_location))
                        .anchor(0.5F, 0.5F)
                        .icon(BitmapDescriptorFactory.fromResource(pDrawableId))
        );
    }

    private void setUpMap() {
        mMap.setMyLocationEnabled(false);
        mMap.setOnMarkerClickListener(this);
        UiSettings s = mMap.getUiSettings();
        s.setMapToolbarEnabled(false);
        s.setZoomControlsEnabled(false);
        s.setCompassEnabled(false);

        mMarkerToAzsMap.clear();
    }

    protected void clearMap() {
        mMap.clear();
        mMarkerToAzsMap.clear();
        mClusterManager.clearItems();
    }

    protected void moveCamera(double pLat, double pLng, float pZoom,
                              GoogleMap.CancelableCallback pCallback) {
        LatLng position = new LatLng(pLat, pLng);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)
                .zoom(pZoom)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), pCallback);
    }

    protected void moveCamera(double pLat, double pLng, float pZoom) {
        moveCamera(pLat, pLng, pZoom, null);
    }

    protected void moveCamToCurrentPosition(){
        moveCamToCurrentPosition(mMap.getCameraPosition().zoom);
    }

    protected void moveCamToCurrentPosition(float pZoom){
        moveCamToCurrentPosition(pZoom, null);
    }

    protected void moveCamToCurrentPosition(float pZoom, GoogleMap.CancelableCallback pCallback){
        if (mLocation == null) {
            ToastHelper.showToast(R.string.cant_find_loc);
        } else moveCamera(mLocation.getLatitude(), mLocation.getLongitude(), pZoom, pCallback);
    }

/*
    protected void setClusterBounds(List<Azs> pAzsList) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (mLocation != null) {
            builder.include(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
        }

        for (Azs azs : pAzsList) {
            builder.include(azs.getPosition());
        }
        LatLngBounds bounds = builder.build();


        int padding = (int) MyApp.getAppContext().getResources()
                .getDimension(R.dimen.azs_cluster_padding);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.animateCamera(cu);
    }
*/


    protected void setupClusterer() {
        mClusterManager = new ClusterManager<Azs>(getActivity(), mMap){

            private long lastCallMs = Long.MIN_VALUE;

            @Override
            public void onCameraChange(CameraPosition cameraPosition){
                super.onCameraChange(cameraPosition);
                final long snap = System.currentTimeMillis();
                if (lastCallMs + CAMERA_MOVE_REACT_THRESHOLD_MS < snap) {
                    lastCallMs = snap;

                    refreshAzsList(CONST.CHECK_CHANGES);

                    return;
                }
                lastCallMs = snap;
            }
        };
        mClusterManager.setRenderer(new AzsClusterRenderer(getActivity(), mMap, mClusterManager) {
            @Override
            protected void onClusterItemRendered(Azs clusterItem, Marker marker) {
                if (!clusterItem.isUpdatedPrice())
                    Api.getInstance().getPrice(clusterItem.getId(), getPriceResponse(), AzsBaseMapFragment.this);
                super.onClusterItemRendered(clusterItem, marker);
            }
        });
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);

        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
    }

    public void moveToCenterOfRussia() {
        moveCamera(RUSSIA_LAT, RUSSIA_LNG, CONST.ZOOM_RUSSIA);
    }

    protected void addAzsMarkers(List<Azs> pAzsList) {
        mClusterManager.addItems(pAzsList);
        mClusterManager.cluster();
    }

    @Override
    public boolean onClusterClick(Cluster<Azs> cluster) {
        LatLngBounds.Builder builder = LatLngBounds.builder();

        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }

        int padding = (int) getResources().getDimension(R.dimen.azs_cluster_padding);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), padding));

        return true;
    }


    @Override
    public void onClusterInfoWindowClick(Cluster<Azs> cluster) {}

    @Override
    public boolean onClusterItemClick(Azs item) {
        if (item != null) {
            AzsItemActivity.launch(getActivity(), mMapFragment.getView(), item);
            return true;
        }
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(Azs item) {}

    private Response.Listener<JSONObject> getPriceResponse(){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                PriceItemWrapper wrapper = new Gson().fromJson(response.toString(), PriceItemWrapper.class);
                if(wrapper.getmErrorCode() == CONST.ERROR_CODE_OK)
                    PriceItem.updateAll(wrapper.getData());
                refreshAzsList(CONST.DONT_CHECK);
            }
        };
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
    }

    protected void addCurrentLocationMarker(){

        if (mLocation == null) return;

        double lat = mLocation.getLatitude();
        double lng = mLocation.getLongitude();
        mCurrentLocationMarker = addMarker(lat, lng, R.drawable.ic_map_current);
    }

    protected abstract void refreshAzsOnMap();
    protected abstract void refreshAzsList(boolean needCheckChanges);
}
