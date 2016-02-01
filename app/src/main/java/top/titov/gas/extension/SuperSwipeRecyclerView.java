package top.titov.gas.extension;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;

import top.titov.gas.R;


/**
 * Created by Alexander Smirnov on 19.01.2015.
 * Usefully RecyclerView with Swipe
 */
public class SuperSwipeRecyclerView extends FrameLayout {

    protected ViewStub mProgress;
    protected ViewStub mMoreProgress;
    protected RecyclerView mRecyclerView;
    protected ViewStub mEmpty;

    protected float mDividerHeight;
    protected boolean mClipToPadding;
    protected ColorDrawable mDivider;
    protected int mPadding;
    protected int mPaddingTop;
    protected int mPaddingBottom;
    protected int mPaddingLeft;
    protected int mPaddingRight;
    protected int mScrollbarStyle;
    protected int mEmptyId;
    protected int mMoreProgressId;

    protected SwipeRefreshLayout mPtrLayout;
    protected boolean isLoadingMore;
    protected int mSelector;


    protected int mSuperListViewMainLayout;
    private int mProgressId;

    public SuperSwipeRecyclerView(Context context) {
        super(context);
        initView();
    }

    public SuperSwipeRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initView();
    }

    public SuperSwipeRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(attrs);
        initView();
    }

    @SuppressWarnings("unused")
    public SwipeRefreshLayout getSwipeToRefresh() {
        return mPtrLayout;
    }

    protected void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.superlistview, 0, 0);
        try {
            mClipToPadding = a.getBoolean(R.styleable.superlistview_superlv__listClipToPadding, false);
            //mDivider = new ColorDrawable(a.getColor(R.styleable.superlistview_superlv__listDivider, 0));
            mDividerHeight = a.getDimension(R.styleable.superlistview_superlv__listDividerHeight, 1.0f);
            mPadding = (int) a.getDimension(R.styleable.superlistview_superlv__listPadding, -1.0f);
            mPaddingTop = (int) a.getDimension(R.styleable.superlistview_superlv__listPaddingTop, 0.0f);
            mPaddingBottom = (int) a.getDimension(R.styleable.superlistview_superlv__listPaddingBottom, 0.0f);
            mPaddingLeft = (int) a.getDimension(R.styleable.superlistview_superlv__listPaddingLeft, 0.0f);
            mPaddingRight = (int) a.getDimension(R.styleable.superlistview_superlv__listPaddingRight, 0.0f);
            mScrollbarStyle = a.getInt(R.styleable.superlistview_superlv__scrollbarStyle, -1);
            mEmptyId = a.getResourceId(R.styleable.superlistview_superlv__empty, 0);
            mMoreProgressId = a.getResourceId(R.styleable.superlistview_superlv__moreProgress, R.layout.view_super_more_progress);
            mProgressId = a.getResourceId(R.styleable.superlistview_superlv__progress, R.layout.view_super_progress);
            mSelector = a.getResourceId(R.styleable.superlistview_superlv__listSelector, 0);
            mSuperListViewMainLayout = a.getResourceId(R.styleable.superlistview_superlv_mainLayoutID, R.layout.view_super_progress_recyclerview);
        } finally {
            a.recycle();
        }
    }

    private void initView() {
        if (isInEditMode()) return;

        View v = LayoutInflater.from(getContext()).inflate(mSuperListViewMainLayout, this);
        mPtrLayout = (SwipeRefreshLayout) v.findViewById(R.id.ptr_layout);
        mPtrLayout.setEnabled(false);

        mProgress = (ViewStub) v.findViewById(android.R.id.progress);
        mProgress.setLayoutResource(mProgressId);
        if (mProgressId != 0) mProgress.inflate();

        mMoreProgress = (ViewStub) v.findViewById(R.id.more_progress);
        mMoreProgress.setLayoutResource(mMoreProgressId);
        if (mMoreProgressId != 0) mMoreProgress.inflate();
        mMoreProgress.setVisibility(View.GONE);

        mEmpty = (ViewStub) v.findViewById(R.id.empty);
        mEmpty.setLayoutResource(mEmptyId);
        if (mEmptyId != 0) mEmpty.inflate();
        mEmpty.setVisibility(View.GONE);

        initRecyclerView(v);
    }

    /**
     * Show the progressbar
     */
    public void showProgress() {
        hideList();
        if (mEmptyId != 0) mEmpty.setVisibility(View.INVISIBLE);
        // if (mProgressId != 0) mProgress.setVisibility(View.VISIBLE);
    }

    public void setIsLoading() {
        mPtrLayout.setRefreshing(true);
        if (mEmptyId != 0) mEmpty.setVisibility(View.INVISIBLE);
    }

    /**
     * Hide the progressbar and show the listview
     */
    public void showList() {
        hideProgress();
        setIsLoadingMore(false);
        mRecyclerView.setVisibility(View.VISIBLE);
        if (mEmptyId != 0 && mRecyclerView.getAdapter().getItemCount() == 0) {
            mEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void showMoreProgress() {
        mMoreProgress.setVisibility(View.VISIBLE);
    }

    public void hideMoreProgress() {
        mMoreProgress.setVisibility(View.GONE);
    }

    /**
     * Set the listener when refresh is triggered and enable the SwipeRefreshLayout
     *
     * @param listener default realization of SwipeRefreshLayout.OnRefreshListener
     */
    public void setRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        mPtrLayout.setEnabled(true);
        mPtrLayout.setOnRefreshListener(listener);
    }

    /**
     * Hide the progressbar
     */
    public void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }

    /**
     * Hide the listview
     */
    public void hideList() {
        mRecyclerView.setVisibility(View.GONE);
    }

    @SuppressWarnings("unused")
    public void clear() {
        getRV().setAdapter(null);
    }

    /**
     * Set the colors for the SwipeRefreshLayout states.
     * Work with pre L and L SwipeRefreshLayout realization
     *
     * @param col1 first color
     * @param col2 second color
     * @param col3 third color
     * @param col4 fourth color
     */
    @SuppressWarnings("deprecation")
    public void setRefreshingColor(int col1, int col2, int col3, int col4) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            mPtrLayout.setColorSchemeResources(col1, col2, col3);
        } else {
            mPtrLayout.setColorScheme(col1, col2, col3, col4);
        }
    }

    /**
     * Set the adapter to the RecyclerView.
     * Automatic hide the progressbar
     * Set the refresh to false
     * If adapter is empty, then the emptyView is shown
     *
     * @param adapter RecyclerView adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        getRV().setAdapter(adapter);

        hideProgress();

        mRecyclerView.setVisibility(View.VISIBLE);
        mPtrLayout.setRefreshing(false);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();

                    hideProgress();
                    isLoadingMore = false;
                    mPtrLayout.setRefreshing(false);

                    if (mRecyclerView.getAdapter().getItemCount() == 0 && mEmptyId != 0) {
                        mEmpty.setVisibility(View.VISIBLE);
                    } else if (mEmptyId != 0) {
                        mEmpty.setVisibility(View.GONE);
                    }
                }
            });
        }
        if (mEmptyId != 0 && (adapter == null || adapter.getItemCount() == 0)) {
            mEmpty.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Implement this method to customize the AbsListView
     */
    protected void initRecyclerView(View view) {
        View recyclerView = view.findViewById(android.R.id.list);
        if (recyclerView instanceof RecyclerView) mRecyclerView = (RecyclerView) recyclerView;
        else throw new IllegalArgumentException(recyclerView.getClass().getName());

        getRV().setClipToPadding(mClipToPadding);

        if (mPadding != -1.0f) getRV().setPadding(mPadding, mPadding, mPadding, mPadding);
        else getRV().setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);

        if (mScrollbarStyle != -1) getRV().setScrollBarStyle(mScrollbarStyle);
    }

    public RecyclerView getRV() {
        return mRecyclerView;
    }

    /**
     * @return the listview adapter
     */
    public RecyclerView.Adapter getAdapter() {
        return mRecyclerView.getAdapter();
    }

    @SuppressWarnings("unused")
    public LinearLayoutManager getLayoutManager() {
        return (LinearLayoutManager) mRecyclerView.getLayoutManager();
    }

    public boolean isLoadingMore() {
        return isLoadingMore;
    }

    /**
     * Enable/Disable the More event
     *
     * @param isLoadingMore boolean status
     */
    public void setIsLoadingMore(boolean isLoadingMore) {
        if (isLoadingMore) showMoreProgress();
        else hideMoreProgress();

        this.isLoadingMore = isLoadingMore;
    }

    public void setOnTouchListener(OnTouchListener listener) {
        mRecyclerView.setOnTouchListener(listener);
    }

}
