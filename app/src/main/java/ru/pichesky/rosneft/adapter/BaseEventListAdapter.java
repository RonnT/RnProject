package ru.pichesky.rosneft.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;

import java.util.ArrayList;
import java.util.List;

import ru.pichesky.rosneft.R;
import ru.pichesky.rosneft.extension.OnClickListener;
import ru.pichesky.rosneft.extension.OnMoreListener;
import ru.pichesky.rosneft.utils.CONST;
import ru.pichesky.rosneft.utils.Utility;

/**
 * Created by Roman Titov on 23.07.2015.
 */
public abstract class BaseEventListAdapter<T> extends RecyclerView.Adapter<BaseEventListAdapter.BaseViewHolder> {

    protected List<T> mItems = new ArrayList<>();
    protected final OnMoreListener mOnMoreListener;
    protected final OnClickListener mOnClickListener;
    protected final Activity mActivity;

    public BaseEventListAdapter(List<T> pItems, OnClickListener pOnClickListener,
                                     OnMoreListener pOnMoreListener) {
        this(pItems, pOnClickListener, pOnMoreListener, null);
    }

    public BaseEventListAdapter(List<T> pItems, OnClickListener pOnClickListener,
                                OnMoreListener pOnMoreListener, Activity pActivity) {
        mItems = pItems;
        mOnMoreListener = pOnMoreListener;
        mOnClickListener = pOnClickListener;
        mActivity = pActivity;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CONST.LIST_HEADER) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_list_header, parent, false);
            v.setOnTouchListener(new View.OnTouchListener() {
                                     @Override
                                     public boolean onTouch(View view, MotionEvent motionEvent) {
                                         return true;
                                     }
                                 }
            );
            return new BaseViewHolder(v, null);
        }
        View v = LayoutInflater.from(parent.getContext())
                .inflate(getHolderLayoutId(), parent, false);
        return new BaseViewHolder(v, mOnClickListener);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        Utility.checkIsNeedLoadMore(position, getItemCount(), mOnMoreListener);

        setViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    static class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected int position = 0;
        protected ViewGroup rootView;
        protected OnClickListener mListener;
        protected AQuery aq;

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
    protected abstract void setViewHolder(BaseViewHolder pHolder, int pPosition);
    public abstract int getItemViewType(int position);
    protected abstract int getHolderLayoutId();
}
