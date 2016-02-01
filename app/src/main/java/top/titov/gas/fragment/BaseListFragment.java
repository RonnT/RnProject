package top.titov.gas.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import top.titov.gas.R;
import top.titov.gas.extension.DividerItemDecoration;
import top.titov.gas.extension.OnClickListener;
import top.titov.gas.extension.OnMoreListener;
import top.titov.gas.extension.SuperSwipeRecyclerView;
import top.titov.gas.helper.ToastHelper;
import top.titov.gas.utils.CONST;
import top.titov.gas.utils.DataParser;
import top.titov.gas.utils.api.Api;

/**
 * Created by Andrew Vasilev on 22.01.2015.
 */
public abstract class BaseListFragment<T> extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener, OnMoreListener,
            Response.Listener<JSONObject>, Response.ErrorListener, OnClickListener {

    protected RecyclerView.Adapter<?> mAdapter;
    protected List<T> mDataList = new ArrayList<>();
    protected SuperSwipeRecyclerView mRecyclerView;
    protected int mOffset = CONST.DEFALUT_OFFSET;
    protected int mLimit = CONST.DEFALUT_LIMIT;
    protected boolean mAdapterIsBlocked;

    @Override
    protected void setData() {
        int titleId = getTitleId();
        if (titleId > 0) getActivity().setTitle(titleId);

        getList();

        mAdapter = getAdapter();
        mRecyclerView = getRecycleListView();

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setRefreshListener(this);

        if (isNeedListDivider()) {
            mRecyclerView.getRV().addItemDecoration(
                    new DividerItemDecoration(getActivity(), R.drawable.list_divider));
        }

        mRecyclerView.getRV().setHasFixedSize(true);
        mRecyclerView.getRV().setLayoutManager(getLayoutManager());
        mRecyclerView.getRV().setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setRefreshingColor(
                android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light
        );

        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mDataList == null || mDataList.isEmpty()) refreshData();
            }
        }, 300);
    }

    protected void refreshData() {
        mRecyclerView.setIsLoading();

        mRecyclerView.showProgress();
        mOffset = 0;
        mDataList.clear();
        loadData();
    }

    @Override
    public void onRefresh() {
        refreshData();
    }

    @Override
    public void onMoreAsked(int i, int i2, int i3) {
        if (!mRecyclerView.isLoadingMore() && mDataList.size() >= mOffset) {
            mRecyclerView.setIsLoadingMore(true);
            loadData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mAdapterIsBlocked = false;
        if (mAdapter != null) mAdapter.notifyDataSetChanged();

        doStuffOnResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        doStuffOnPause();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();

        if(mDataList.size() == 0) loadCached();
    }

    @Override
    public void onResponse(JSONObject response) {
        if (mAdapterIsBlocked) return;

        try {
            if (DataParser.getErrorCode(response) != 0) {
                ToastHelper.showToast(DataParser.getErrorName(response));
                return;
            }

            List<T> tempList = parseList(response);

            if (tempList != null && tempList.size() > 0) {
                mDataList.addAll(tempList);
                mOffset += mLimit;
            }

            cacheData();

            mAdapter.notifyDataSetChanged();
            mRecyclerView.showList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view, int position) {
        mAdapterIsBlocked = true;
        Api.getInstance().stopRequest();
        if (mRecyclerView.isLoadingMore()) mRecyclerView.showList();

        onItemClick(view, position);
    }

    protected void loadCached() {
        setCachedData();

        if (null != mAdapter) mAdapter.notifyDataSetChanged();
        if (null != mRecyclerView) mRecyclerView.showList();
    };

    protected abstract void getList();

    protected abstract RecyclerView.Adapter<?> getAdapter();

    protected abstract RecyclerView.LayoutManager getLayoutManager();

    protected abstract void loadData();

    protected abstract List<T> parseList(JSONObject pResponse) throws JSONException;

    protected abstract SuperSwipeRecyclerView getRecycleListView();

    protected abstract void onItemClick(View pView, int pPosition);

    protected abstract int getTitleId();

    protected abstract void cacheData();
    protected abstract void setCachedData();
    protected abstract void doStuffOnResume();

    protected void doStuffOnPause() {}

    protected boolean canGoBack() {
        return false;
    }

    protected boolean isNeedListDivider() {
        return true;
    }
}
