package ru.pichesky.rosneft.fragment.azs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.Response;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.pichesky.rosneft.R;
import ru.pichesky.rosneft.activity.AzsItemActivity;
import ru.pichesky.rosneft.adapter.AzsListAdapter;
import ru.pichesky.rosneft.extension.SuperSwipeRecyclerView;
import ru.pichesky.rosneft.fragment.BaseListFragment;
import ru.pichesky.rosneft.helper.LocationHelper;
import ru.pichesky.rosneft.interfaces.ILocationObservable;
import ru.pichesky.rosneft.model.azs.Azs;
import ru.pichesky.rosneft.model.azs.ClosestAzsWrapper;
import ru.pichesky.rosneft.model.azs.PriceItem;
import ru.pichesky.rosneft.model.azs.PriceItemWrapper;
import ru.pichesky.rosneft.utils.CONST;
import ru.pichesky.rosneft.utils.api.Api;

/**
 * Created by Andrew Vasilev on 16.07.2015.
 */
public class AzsListFragment extends BaseListFragment<Azs> implements ILocationObservable{

    private Location mLocation = null;
    private BroadcastReceiver mAzsBr;

    @Override
    protected void setData() {

        mLocation = LocationHelper.getCurrentLocation();

        super.setData();
    }

    @Override
    protected void getList() {
        mDataList = new ArrayList<>();
    }

    @Override
    protected RecyclerView.Adapter<?> getAdapter() {
        return new AzsListAdapter(mDataList, this, this);
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    @Override
    protected void loadData() {

        mLocation = LocationHelper.getCurrentLocation();

        if (mLocation != null) Api.getInstance().getClosestAzs(mLocation, this, this);
        else showListWithError();
    }

    private void showListWithError() {
        try {
            mAdapter.notifyDataSetChanged();
            mRecyclerView.showList();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Integer> getAzsIdList(List<Azs> pAzsList){
        List<Integer> result = new ArrayList<>();
        if (pAzsList.size() > 0) {
            for (Azs azs : pAzsList) result.add(azs.getId());
        }
        return result;
    }

    private void checkAzsListPrice(List<Azs> pAzsList){
        List<Azs> needUpdatePrice = new ArrayList<>();
        for (Azs azsItem : pAzsList){
            if (!azsItem.isUpdatedPrice()) needUpdatePrice.add(azsItem);
        }
        if (needUpdatePrice.size() > 0) updateAzsPrice(needUpdatePrice);
    }

    private void updateAzsPrice(List<Azs> pAzsList){
        Api.getInstance().getPrices(getAzsIdList(pAzsList), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                PriceItemWrapper priceWrapper = new Gson().fromJson(response.toString(), PriceItemWrapper.class);
                List<PriceItem> priceItemList = priceWrapper.getPriceItemList();
                if (priceItemList.size() > 0) PriceItem.updateAll(priceItemList);
                mAdapter.notifyDataSetChanged();
            }
        }, this);
    }

    @Override
    protected List<Azs> parseList(JSONObject pResponse) throws JSONException {
        List<Azs> items = new ArrayList<>();
        ClosestAzsWrapper wrapper = new Gson().fromJson(pResponse.toString(), ClosestAzsWrapper.class);

        if (wrapper.getErrorCode() == CONST.ERROR_CODE_OK) {
            if (wrapper.getAzsList().size() > 0) {
                mDataList.clear();
                items.addAll(wrapper.getAzsList());
            }
        }

        checkAzsListPrice(items);

        return items;
    }

    @Override
    protected SuperSwipeRecyclerView getRecycleListView() {
        return (SuperSwipeRecyclerView) mAq.id(R.id.fragment_list_base_recycler_view).getView();
    }

    @Override
    protected void onItemClick(View pView, int pPosition) {
        Azs clickedAzs = mDataList.get(pPosition);

        if (clickedAzs != null) {
            AzsItemActivity.launch(getActivity(), pView, clickedAzs);
        }
    }

    @Override
    protected int getTitleId() {
        return R.string.left_menu_0;
    }

    @Override
    protected void cacheData() {
        mOffset = 1000;
    }

    @Override
    protected void setCachedData() {}

    @Override
    protected void doStuffOnResume() {}

    @Override
    protected void doStuffOnPause() {}

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_list_base;
    }

    @Override
    public void onLocationUpdated() {
        mLocation = AzsTabsFragment.getLocation();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mAzsBr == null) initAzsBr();
        registerAzsBr();
    }

    @Override
    public void onStop() {
        super.onStop();

        unregisterAzsBr();
    }

    private void unregisterAzsBr() {
        if (mAzsBr != null) getActivity().unregisterReceiver(mAzsBr);
    }

    private void initAzsBr() {
        mAzsBr = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case CONST.INTENT_UPDATE_AZS:
                        refreshData();
                        break;
                }
            }
        };
    }

    private void registerAzsBr() {
        getActivity().registerReceiver(mAzsBr, new IntentFilter(CONST.INTENT_UPDATE_AZS));
    }

}
