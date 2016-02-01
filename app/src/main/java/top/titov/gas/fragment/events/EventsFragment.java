package top.titov.gas.fragment.events;

import android.content.Intent;
import android.view.View;

import com.android.volley.Response;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import top.titov.gas.activity.EventItemActivity;
import top.titov.gas.fragment.BaseEventsPagerFragment;
import top.titov.gas.helper.PrefsHelper;
import top.titov.gas.helper.ToastHelper;
import top.titov.gas.interfaces.IObservable;
import top.titov.gas.model.event.Event;
import top.titov.gas.model.azs.EventWrapper;
import top.titov.gas.utils.CONST;
import top.titov.gas.utils.DataParser;
import top.titov.gas.utils.Utility;
import top.titov.gas.utils.api.Api;

/**
 * Created by Roman Titov on 22.07.2015.
 */

public class EventsFragment extends BaseEventsPagerFragment<Event> implements IObservable, Response.Listener<JSONObject>{

    protected void loadData() {
        Api.getInstance().getEventsList(PrefsHelper.getInstance().getUserRegionId(), this, this);
    }

    @Override
    protected void onItemClick(View pView, int pPosition) {
        Event selectedItem = mDataList.get(pPosition);

        //Don't click if fields are empty
        //if (selectedItem.getAnnouncement().equals("") && selectedItem.getText().equals("")) return;

        Intent intent = new Intent(getActivity(), EventItemActivity.class);
        intent.putExtra(CONST.SELECTED_ITEM, selectedItem);
        Utility.launchActivity(getActivity(), pView, intent);
    }
    public void onResponse(JSONObject response) {
        try {
            if (DataParser.getErrorCode(response) != 0) {
                ToastHelper.showToast(DataParser.getErrorName(response));
                return;
            }

            List<Event> tempList = parseList(response);

            if (tempList != null && tempList.size() > 0) {
                mFederalList.clear();
                mRegionalList.clear();
                mDataList.clear();
                for (Event item : tempList) {
                    if (item.getIsFederal() == 1) mFederalList.add(item);
                    else mRegionalList.add(item);
                }
                if (mFederalList.size() > 0) mDataList.addAll(mFederalList);

                if (mRegionalList.size() > 0) mDataList.addAll(mRegionalList);
            }
            mAdapter.notifyDataSetChanged();
            mIndicatorView.setItemsSize(mDataList.size());
            pager.setCurrentItem(0);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected List<Event> parseList(JSONObject pResponse) throws JSONException {
        List<Event> items = new ArrayList<>();
        EventWrapper wrapper = new Gson().fromJson(pResponse.toString(), EventWrapper.class);
        if (wrapper.getErrorCode() == CONST.ERROR_CODE_OK) {
            if (wrapper.getList().size() > 0) {
                items.addAll(wrapper.getList());
                setSwitchTab(false);
            } else setSwitchTab(true);
        }
        return items;
    }
}