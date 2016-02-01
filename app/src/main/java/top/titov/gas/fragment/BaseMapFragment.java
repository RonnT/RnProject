package top.titov.gas.fragment;

import android.location.Location;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

import top.titov.gas.R;
import top.titov.gas.helper.SizeHelper;
import top.titov.gas.model.MapMarker;


/**
 * Created by Andrew Vasilev on 27.05.2015.
 */
public abstract class BaseMapFragment extends CurrentLocationBaseFragment
                                            implements GoogleMap.OnMarkerClickListener,
                                                        GoogleMap.OnCameraChangeListener,
                                                            GoogleMap.OnMapClickListener {
    protected GoogleMap mMap;

    protected Marker mCurrentLocationMarker = null;
    protected boolean mIsMapTouched = false;

    protected Map<Marker, MapMarker> mMarkerHashMap = new HashMap<>();

    @Override
    protected void initViews() {
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            if (mMap != null) setUpMap();
        }
    }

    private void setUpMap() {
        mMap.setMyLocationEnabled(false);
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
        UiSettings s = mMap.getUiSettings();
        s.setZoomControlsEnabled(false);
        s.setCompassEnabled(false);

        mMarkerHashMap.clear();
    }

    protected Marker addMarker(MapMarker pMarker) {
        Marker addedMarker = mMap.addMarker(
                new MarkerOptions()
                        .position(new LatLng(pMarker.getLat(), pMarker.getLng()))
                        .title(pMarker.getText())
                        .anchor(0.5F, 0.5F)
                        .icon(pMarker.getIcon())
        );

        mMarkerHashMap.put(addedMarker, pMarker);

        return addedMarker;
    }

    protected void moveCamera(Location pLoc, int pZoom) {
        moveCamera(pLoc.getLatitude(), pLoc.getLongitude(), pZoom);
    }

    protected void moveCamera(Marker pMarker, int pZoom){
        moveCamera(mMarkerHashMap.get(pMarker), pZoom);
    }

    protected void moveCamera(MapMarker pMarker, int pZoom) {
        moveCamera(pMarker.getLat(), pMarker.getLng(), pZoom);
    }

    protected void moveCamera(double pLat, double pLng, int pZoom) {
        LatLng position = new LatLng(pLat, pLng);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)
                .zoom(pZoom)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    protected void setBounds() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));

        for (Marker marker : mMarkerHashMap.keySet()) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        // offset from edges of the map in pixels
        int padding = (int) (20 * SizeHelper.getInstance(getActivity()).getScreenDensity());

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.animateCamera(cu);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        mIsMapTouched = true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mIsMapTouched = true;
    }
}
