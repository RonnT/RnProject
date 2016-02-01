package ru.pichesky.rosneft.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;

import java.util.ArrayList;
import java.util.List;

import ru.pichesky.rosneft.MyApp;
import ru.pichesky.rosneft.R;
import ru.pichesky.rosneft.extension.OnClickListener;
import ru.pichesky.rosneft.extension.OnMoreListener;
import ru.pichesky.rosneft.helper.AzsHelper;
import ru.pichesky.rosneft.model.azs.Azs;
import ru.pichesky.rosneft.utils.Utility;

/**
 * Created by Andrew Vasilev on 16.07.2015.
 */
public class AzsListAdapter extends RecyclerView.Adapter<AzsListAdapter.BaseViewHolder> {

    private List<Azs> mItems = new ArrayList<>();
    private final OnMoreListener mOnMoreListener;
    private final OnClickListener mOnClickListener;

    public AzsListAdapter(List<Azs> pItems, OnClickListener pOnClickListener,
                          OnMoreListener pOnMoreListener) {
        mItems = pItems;
        mOnMoreListener = pOnMoreListener;
        mOnClickListener = pOnClickListener;
    }

    @Override
    public AzsListAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_azs_list_item, parent, false);
        return new BaseViewHolder(v, mOnClickListener);
    }

    @Override
    public void onBindViewHolder(AzsListAdapter.BaseViewHolder holder, int position) {
        Utility.checkIsNeedLoadMore(position, getItemCount(), mOnMoreListener);

        setViewHolder(holder, position);
    }

    private void setViewHolder(BaseViewHolder pHolder, int pPosition) {
        Azs azs = mItems.get(pPosition);
        pHolder.position = pPosition;

        pHolder.aq.id(R.id.view_azs_list_item_address).text(azs.getAddress());
        setPrice(pHolder, azs);
        setDistance(pHolder, azs.getDistance());

        ViewGroup servicesRoot =
                (ViewGroup) pHolder.aq.id(R.id.view_azs_list_item_services_LL).getView();
        servicesRoot.removeAllViews();
        azs.copyServiceAvailable();
        AzsHelper.setAzsServices(azs.getServiceAvailable(), servicesRoot);
    }

    private void setDistance(BaseViewHolder pHolder, float pDistance) {

        String pDistanceString = pDistance == 0 ? "" : pDistance
                + MyApp.getStringFromRes(R.string.location_km_postfix);

        pHolder.aq.id(R.id.view_azs_list_item_distance).text(pDistanceString);
    }

    private void setPrice(BaseViewHolder pHolder, Azs pAzs) {
        float price = pAzs.isUpdatedPrice() ? pAzs.getSelectedFuelPrice() : 0;

        AzsHelper.setPriceToView(pHolder.aq, price);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    static class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private int position = 0;
        private ViewGroup rootView;
        private OnClickListener mListener;
        private AQuery aq;

        public BaseViewHolder(final View pItemView, OnClickListener pOnClickListener) {
            super(pItemView);

            rootView = (ViewGroup) pItemView;
            aq = new AQuery(pItemView);

            mListener = pOnClickListener;
            pItemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) mListener.onClick(v, position);
        }
    }
}
