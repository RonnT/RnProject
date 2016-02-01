package ru.pichesky.rosneft.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Filter;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ru.pichesky.rosneft.R;
import ru.pichesky.rosneft.adapter.RegionAdapter;
import ru.pichesky.rosneft.fragment.dialog.LoadingDialogFragment;
import ru.pichesky.rosneft.helper.LocationHelper;
import ru.pichesky.rosneft.helper.ToastHelper;
import ru.pichesky.rosneft.model.Region;
import ru.pichesky.rosneft.utils.CONST;
import ru.pichesky.rosneft.utils.api.Api;

/**
 * Created by Roman Titov on 29.07.2015.
 */
public class RegionSelectorActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        Response.Listener<JSONObject>, Response.ErrorListener {

    protected static LoadingDialogFragment mLoadingDialog;
    private List<Region> mItemList = Region.getAll();
    private RegionAdapter mAdapter = new RegionAdapter(mItemList);
    private MenuItem mSearchItem;

    @Override
    protected void setData(Bundle savedInstanceState) {
        setTitle(R.string.regions_selector_title);
        mAq.id(R.id.region_selector_listview).adapter(mAdapter).itemClicked(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_region_selector;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Region selectedItem = mItemList.get(i);
        Intent intent = new Intent();
        intent.putExtra(CONST.NEW_REGION, selectedItem);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void loadCurrentRegion(){
        Location mLocation = LocationHelper.getCurrentLocation();
        if (mLocation == null) {
            ToastHelper.showToast(R.string.error_no_location);
            return;
        }

        showLoadingDialog(getResources().getString(R.string.determine_region), false);
        Api.getInstance().getCurrentRegionId(mLocation.getLatitude(),
                mLocation.getLongitude(), this, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.ab_current_region:
                loadCurrentRegion();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_regions, menu);

        mSearchItem = menu.findItem(R.id.ab_search);
        setSearchView();
        return true;
    }

    private void setSearchView(){
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) mSearchItem.getActionView();

        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    performFiltering(s);
                    return true;
                }
            });

            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));
        }

        MenuItemCompat.setOnActionExpandListener(mSearchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true;
                    }
                });

        MenuItemCompat.setActionView(mSearchItem, searchView);
    }

    private void performFiltering(String pSearchQuery) {
        mAdapter.getFilter().filter(pSearchQuery, new Filter.FilterListener() {
            @Override
            public void onFilterComplete(int count) {
                if (mAdapter.getCount() == 0) showEmptyList(true);
                else showEmptyList(false);
            }
        });
    }

    private void showEmptyList(boolean pVisible){
        int visibility = pVisible ? View.VISIBLE : View.GONE;
        mAq.id(R.id.emptyList).visibility(visibility);
    }

    @Override
    public void onResponse(JSONObject response) {
        hideLoadingDialog();
        String codeString = "";
        int code = 0;
        try {
            codeString = response.getJSONObject(CONST.INDEX_DATA).getString(CONST.INDEX_REGION_CODE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (codeString.length() > 0) code = Integer.parseInt(codeString);
        if (code > 0) {
            Region currentRegion = Region.getByRegionId(code);
            Intent intent = new Intent();
            intent.putExtra(CONST.NEW_REGION, currentRegion);
            setResult(RESULT_OK, intent);
            finish();
        } else ToastHelper.showToast(R.string.error_determine_region);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        hideLoadingDialog();
        ToastHelper.showToast(R.string.error_loading_data_try_again);
    }

    protected void showLoadingDialog(String pText, boolean pIsCancelable){
        mLoadingDialog = LoadingDialogFragment.newInstance(pText);
        mLoadingDialog.setCancelable(pIsCancelable);
        mLoadingDialog.show(mFm, null);
    }

    public static void hideLoadingDialog(){
        if (null != mLoadingDialog) mLoadingDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        if (mSearchItem != null && MenuItemCompat.isActionViewExpanded(mSearchItem)) {
            MenuItemCompat.collapseActionView(mSearchItem);
        }
        else super.onBackPressed();
    }
}
