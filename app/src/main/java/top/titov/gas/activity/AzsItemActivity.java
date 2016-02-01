package top.titov.gas.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidquery.AQuery;
import com.devspark.robototextview.widget.RobotoTextView;
import com.google.android.gms.maps.CameraUpdate;
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

import org.json.JSONObject;

import top.titov.gas.MyApp;
import top.titov.gas.R;
import top.titov.gas.activity.rate.AzsRateActivity;
import top.titov.gas.extension.CustomScrollView;
import top.titov.gas.extension.MySupportMapFragment;
import top.titov.gas.helper.AzsHelper;
import top.titov.gas.helper.DrawableHelper;
import top.titov.gas.helper.LocationHelper;
import top.titov.gas.helper.PrefsHelper;
import top.titov.gas.helper.SizeHelper;
import top.titov.gas.helper.ToastHelper;
import top.titov.gas.model.azs.Azs;
import top.titov.gas.model.azs.PriceItem;
import top.titov.gas.model.azs.PriceItemWrapper;
import top.titov.gas.utils.CONST;
import top.titov.gas.utils.DataManager;
import top.titov.gas.utils.Utility;
import top.titov.gas.utils.api.Api;

/**
 * Created by Andrew Vasilev on 29.07.2015.
 */
public class AzsItemActivity extends BaseActivity implements View.OnClickListener, Response.ErrorListener {

    private MySupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private Location mCurrentLocation;

    private String[] mFuelNames;

    private Azs mAzsItem = null;

    public static void launch(Activity pActivityFrom, View pView, Azs pAzs) {
        Intent intent = new Intent(pActivityFrom, AzsItemActivity.class);
        intent.putExtra(CONST.TAG_AZS, pAzs);

        Utility.launchActivity(pActivityFrom, pView, intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_azs_item;
    }

    @Override
    protected void setData(Bundle savedInstanceState) {
        Bundle b = getIntent().getExtras();
        if (b != null) mAzsItem = (Azs) b.getSerializable(CONST.TAG_AZS);

        if (mAzsItem == null) {
            ToastHelper.showToast(R.string.error_unknown);
            finish();
            return;
        }

        mCurrentLocation = LocationHelper.getCurrentLocation(false);
        mFuelNames = getResources().getStringArray(R.array.fuel_types);

        setTitle(R.string.caps_azs);
        setupMap();
        fillViews();
    }

    private void fillViews() {
        mAq.id(R.id.price_LL).gone();

        mAq.id(R.id.activity_azs_item_btn_route).clicked(this);
        mAq.id(R.id.activity_azs_item_btn_fav).text(getBtnFavTextId()).clicked(this);
        mAq.id(R.id.activity_azs_item_btn_feedback).clicked(this);
        mAq.id(R.id.activity_azs_item_btn_hot_line).clicked(this);
        mAq.id(R.id.view_azs_list_item_address).text(mAzsItem.getAddress());
        mAq.id(R.id.activity_azs_price_date).text(getPriceActualDate());

        AzsHelper.setDistance(mAq, mAzsItem.getDistance());

        ViewGroup servicesRoot =
                (ViewGroup) mAq.id(R.id.view_azs_list_item_services_LL).getView();
        servicesRoot.removeAllViews();
        AzsHelper.setAzsServices(mAzsItem.getServiceAvailable(), servicesRoot);

        setFuelPricesTable();
    }

    private String getPriceActualDate() {
        long timeStamp = PriceItem.getPriceItemById(mAzsItem.getId()).getPriceDate();
        if (timeStamp == 0) return "";
        return MyApp.getStringFromRes(R.string.fuel_prices_actual_on)
                + Utility.getDate(timeStamp, CONST.ACTUAL_PRICE_DATE_FORMAT);
    }

    private void setFuelPricesTable() {
        TableLayout table = (TableLayout) mAq.id(R.id.activity_azs_item_table).getView();
        View dividerView =
                getLayoutInflater().inflate(R.layout.view_table_divider, table, false);

        int dividerBrendId = mAzsItem.getBrand() == CONST.AZS_TYPE_0 ? R.layout.view_table_divider_fora
                : R.layout.view_table_divider_pulsar;
        View dividerBrendView = getLayoutInflater().inflate(dividerBrendId, null);

        table.addView(getFuelNamesRow());
        table.addView(dividerView);
        table.addView(getFuelPricesRow(false));
        table.addView(dividerBrendView);
        table.addView(getFuelPricesRow(true));
    }

    private void deleteFuelPricesTable() {
        TableLayout table = (TableLayout) mAq.id(R.id.activity_azs_item_table).getView();
        table.removeAllViews();
    }

    private LinearLayout getNewTableRow() {
        LinearLayout.LayoutParams rowParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        LinearLayout tableRow = new LinearLayout(this);
        tableRow.setOrientation(LinearLayout.HORIZONTAL);
        tableRow.setLayoutParams(rowParams);

        return tableRow;
    }

    private LinearLayout getFuelNamesRow() {
        LinearLayout tableRow = getNewTableRow();

        for (String fuel : mFuelNames) {
            View tvFuelLL = getLayoutInflater()
                    .inflate(R.layout.view_table_fuel_name, tableRow, false);
            RobotoTextView tvFuel = (RobotoTextView) tvFuelLL.findViewById(R.id.view_fuel_name_tv);
            tvFuel.setText(fuel);
            if (isCurrentFuel(fuel)) setFuelNameActive(tvFuel);
            addFuelToView(tableRow, tvFuelLL, fuel);
        }
        return tableRow;
    }

    private void addFuelToView(ViewGroup pTableRow, View pView, String pFuel) {
        if (theFuelHasPrice(pFuel)) pTableRow.addView(pView);
    }

    private boolean theFuelHasPrice(String pFuelName) {
        float fuelPrice = mAzsItem.getFuelPrice(pFuelName);
        float foraPrice = mAzsItem.getForaFuelPrice(pFuelName);
        return fuelPrice != 0f || foraPrice != 0f;
    }

    @SuppressLint("NewApi")
    private void setFuelNameActive(RobotoTextView pTextView) {
        Drawable bgDrawable = DrawableHelper.getCircleDrawable(
                MyApp.getColorFromRes(R.color.color_accent));

        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            pTextView.setBackgroundDrawable(bgDrawable);

        } else pTextView.setBackground(bgDrawable);

        pTextView.setTextColor(Color.WHITE);
    }

    private LinearLayout getFuelPricesRow(boolean pIsFora) {
        LinearLayout tableRow = getNewTableRow();

        for (String fuel : mFuelNames) {
            View tvFuelLL = getLayoutInflater()
                    .inflate(R.layout.view_table_fuel_price, tableRow, false);

            AQuery aq = new AQuery(tvFuelLL);

            float price = pIsFora ? mAzsItem.getForaFuelPrice(fuel) : mAzsItem.getFuelPrice(fuel);

            if (price == 0) {
                aq.id(R.id.view_price_tv_rur).text("");
                aq.id(R.id.view_price_tv_kop).text("");

            } else AzsHelper.setPriceToView(aq, price);

            if (isCurrentFuel(fuel)) {
                aq.id(R.id.view_price_tv_rur).textColorId(R.color.color_accent);
                aq.id(R.id.view_price_tv_kop).textColorId(R.color.color_accent);
            }

            addFuelToView(tableRow, tvFuelLL, fuel);
        }

        return tableRow;
    }

    private boolean isCurrentFuel(String pFuel) {
        return pFuel.equalsIgnoreCase(PrefsHelper.getInstance().getFilterFuelType());
    }

    private void setupMap() {
        setUpMapIfNeeded();
        int markerDrawableId = mAzsItem.getBrand() == 1 ? R.drawable.ic_azs_type_1 :
                R.drawable.ic_azs_type_0;
        addMarker(mAzsItem.getLat(), mAzsItem.getLng(), markerDrawableId);
        if (mCurrentLocation != null) {
            addCurrentLocationMarker();
            setMapBounds();
        } else moveCamera(mAzsItem.getPosition(), CONST.ZOOM_MY_LOCATION);
    }

    private void addCurrentLocationMarker() {
        double lat = mCurrentLocation.getLatitude();
        double lng = mCurrentLocation.getLongitude();
        addMarker(lat, lng, R.drawable.ic_map_current);
    }

    private void setMapBounds() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
        builder.include(mAzsItem.getPosition());
        LatLngBounds bounds = builder.build();

        int padding = (int) MyApp.getAppContext().getResources()
                .getDimension(R.dimen.azs_item_map_padding);
        int width = (int) SizeHelper.getInstance(this).getScreenWidth();
        int height = (int) MyApp.getDimenFromRes(R.dimen.azs_item_map_height);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.moveCamera(cu);
    }

    protected void moveCamera(LatLng pPosition, int pZoom) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(pPosition)
                .zoom(pZoom)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMapFragment = (MySupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.activity_azs_item_map);
            mMap = mMapFragment.getMap();
            mMap.setOnMarkerClickListener(null);

            if (mMap != null) setUpMap();
        }
        ((CustomScrollView) findViewById(R.id.activity_azs_item_scrollview))
                .addInterceptScrollView(findViewById(R.id.activity_azs_item_map));
    }

    protected Marker addMarker(double pLat, double pLng, int pDrawableId) {
        Marker addedMarker = mMap.addMarker(
                new MarkerOptions()
                        .position(new LatLng(pLat, pLng))
                        .title(MyApp.getStringFromRes(R.string.current_location))
                        .anchor(0.5F, 0.5F)
                        .icon(BitmapDescriptorFactory.fromResource(pDrawableId))
        );

        return addedMarker;
    }

    private void setUpMap() {
        mMap.setMyLocationEnabled(false);
        mMap.setOnMapClickListener(null);
        mMap.setOnMarkerClickListener(null);
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });

        UiSettings s = mMap.getUiSettings();
        s.setZoomControlsEnabled(false);
        s.setCompassEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_azs_item_btn_route:
                Location myLoc = DataManager.getInstance().getMyLocation();
                Utility.runRouteIntent(this, myLoc, mAzsItem.getLocation());
                break;

            case R.id.activity_azs_item_btn_fav:
                if (mAzsItem.isFavorite()) mAzsItem.removeFavorite();
                else mAzsItem.addFavorite();

                mAq.id(R.id.activity_azs_item_btn_fav)
                        .text(getBtnFavTextId());
                break;

            case R.id.activity_azs_item_btn_feedback:
                AzsRateActivity.launch(this, v, mAzsItem);
                break;

            case R.id.activity_azs_item_btn_hot_line:
                Utility.runDialIntent(this, MyApp.getStringFromRes(R.string.hot_line_number));
                break;
        }
    }

    private int getBtnFavTextId() {
        return mAzsItem.isFavorite() ? R.string.caps_remove_from_fav : R.string.caps_add_to_fav;
    }

    @Override
    protected void actionOnForeground() {
        if (mAzsItem != null && !mAzsItem.isUpdatedPrice()) {
            Api.getInstance().getPrice(mAzsItem.getId(), getPriceResponse(), this);
        }
    }

    private Response.Listener<JSONObject> getPriceResponse() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                PriceItemWrapper wrapper = new Gson().fromJson(response.toString(), PriceItemWrapper.class);
                if (wrapper.getmErrorCode() == CONST.ERROR_CODE_OK) {
                    PriceItem.updateAll(wrapper.getData());
                    deleteFuelPricesTable();
                    //ToastHelper.showToast("Table deleted");
                    fillViews();
                }
            }
        };
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
    }
}
