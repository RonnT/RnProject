package ru.pichesky.rosneft.fragment.events;

import android.content.Intent;
import android.view.View;

import com.android.volley.Response;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.pichesky.rosneft.activity.ProductItemActivity;
import ru.pichesky.rosneft.fragment.BaseEventsPagerFragment;
import ru.pichesky.rosneft.helper.PrefsHelper;
import ru.pichesky.rosneft.helper.ToastHelper;
import ru.pichesky.rosneft.model.event.Product;
import ru.pichesky.rosneft.model.ProductWrapper;
import ru.pichesky.rosneft.utils.CONST;
import ru.pichesky.rosneft.utils.DataParser;
import ru.pichesky.rosneft.utils.Utility;
import ru.pichesky.rosneft.utils.api.Api;

/**
 * Created by Roman Titov on 11.11.2015.
 */
public class ProductsFragment extends BaseEventsPagerFragment<Product> implements Response.Listener<JSONObject>{

    @Override
    protected void loadData() {
        Api.getInstance().getProductList(PrefsHelper.getInstance().getUserRegionId(), this, this);
    }

    @Override
    protected void onItemClick(View pView, int pPosition) {
        Product selectedItem = mDataList.get(pPosition);

        //Don't click if fields are empty
        if (selectedItem.getText().equals("") && selectedItem.getText().equals("")) return;

        Intent intent = new Intent(getActivity(), ProductItemActivity.class);
        intent.putExtra(CONST.SELECTED_ITEM, selectedItem);
        Utility.launchActivity(getActivity(), pView, intent);
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            if (DataParser.getErrorCode(response) != 0) {
                ToastHelper.showToast(DataParser.getErrorName(response));
                return;
            }

            List<Product> tempList = parseList(response);

            if (tempList != null && tempList.size() > 0) {
                mFederalList.clear();
                mRegionalList.clear();
                setSwitchTab(true);
                for (Product item : tempList) {
                    if (item.getIsFederal() == 1) mFederalList.add(item);
                    else mRegionalList.add(item);
                }
                if (mFederalList.size() > 0) mDataList.addAll(mFederalList);
                if (mRegionalList.size() > 0) mDataList.addAll(mRegionalList);
            } else {
                setSwitchTab(false);
            }

            mAdapter.notifyDataSetChanged();
            mIndicatorView.setItemsSize(mDataList.size());
            pager.setCurrentItem(0);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected List<Product> parseList(JSONObject pResponse) throws JSONException {
        List<Product> items = new ArrayList<>();
        ProductWrapper wrapper = new Gson().fromJson(pResponse.toString(), ProductWrapper.class);
        if (wrapper.getErrorCode() == CONST.ERROR_CODE_OK) {
            if (wrapper.getList().size() > 0) items.addAll(wrapper.getList());
        }
        return items;
    }
}
