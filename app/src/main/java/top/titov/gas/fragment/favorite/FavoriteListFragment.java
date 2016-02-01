package top.titov.gas.fragment.favorite;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import top.titov.gas.R;
import top.titov.gas.activity.AzsItemActivity;
import top.titov.gas.adapter.FavoriteAdapter;
import top.titov.gas.fragment.BaseFragment;
import top.titov.gas.helper.FilterHelper;
import top.titov.gas.interfaces.IFuelObserver;
import top.titov.gas.model.azs.Azs;
import top.titov.gas.utils.CONST;
import top.titov.gas.utils.DataManager;


public class FavoriteListFragment extends BaseFragment implements AdapterView.OnItemClickListener, IFuelObserver {

    private ListView mFavoriteList;

    private List<Azs> mAzsOriginal;
    private FavoriteAdapter mAdapter;

    private boolean isFilterWash;
    private boolean isFilterService;
    private boolean isFilterCafe;
    private boolean isFilterShop;

    private List<Azs> mFavoriteFiltered;
    private List<String> mServiceFilters = new ArrayList<>();
    private String mFuelType;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_favorite_list;
    }

    @Override
    protected void setData() {
        initFilters();
        initFavoritesView();
    }

    @Override
    public void onResume() {
        loadFavorites();
        selectBackground();

        super.onResume();
    }

    private void initFilters() {
        FilterHelper.setFuelSpinner(getActivity(), mAq, R.id.view_filters_fuel_spinner, this);

        FilterHelper.setFilterBtn(mAq, R.id.view_filters_btn_wash, false, getFilterBtnClickListener());
        FilterHelper.setFilterBtn(mAq, R.id.view_filters_btn_service, false, getFilterBtnClickListener());
        FilterHelper.setFilterBtn(mAq, R.id.view_filters_btn_cafe, false, getFilterBtnClickListener());
        FilterHelper.setFilterBtn(mAq, R.id.view_filters_btn_shop, false, getFilterBtnClickListener());
    }

    @Override
    public void updateFuelType(String pFuelType) {

        if (mAzsOriginal == null) loadFavorites();

        mFuelType = pFuelType;

        DataManager.getInstance().setFuelType(pFuelType);

        acceptFilters();
    }

    private View.OnClickListener getFilterBtnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int btnId = v.getId();
                boolean isActive = false;
                String filter = "";
                switch (btnId) {
                    case R.id.view_filters_btn_wash:
                        isActive = isFilterWash = !isFilterWash;
                        filter = CONST.FILTER_WASH;
                        break;
                    case R.id.view_filters_btn_service:
                        isActive = isFilterService = !isFilterService;
                        filter = CONST.FILTER_TIRE;
                        break;

                    case R.id.view_filters_btn_cafe:
                        isActive = isFilterCafe = !isFilterCafe;
                        filter = CONST.FILTER_CAFE;
                        break;

                    case R.id.view_filters_btn_shop:
                        isActive = isFilterShop = !isFilterShop;
                        filter = CONST.FILTER_SHOP;
                        break;
                }

                changeFilter(filter, isActive);

                FilterHelper.setFilterBtnImage(mAq, btnId, isActive);

                acceptFilters();
            }
        };
    }

    private void acceptFilters() {
        if (mAzsOriginal != null) {

            mFavoriteFiltered.clear();

            if (mServiceFilters.isEmpty() && mFuelType.isEmpty()) {
                mFavoriteFiltered.addAll(mAzsOriginal);
            } else separateByFilter();

            mAdapter.notifyDataSetChanged();
        }
    }

    private void separateByFilter() {
        for (int idx = 0; idx < mAzsOriginal.size(); idx++) {
            Azs preFilteredAzs = mAzsOriginal.get(idx);
            if (FilterHelper.hasAllFilters(preFilteredAzs, mServiceFilters, mFuelType, 0)) {
                mFavoriteFiltered.add(preFilteredAzs);
            }
        }
    }

    private void initFavoritesView() {
        mFavoriteList = mAq.id(R.id.favorites_list).getListView();
        mFavoriteList.setOnItemClickListener(this);
    }

    private void loadFavorites() {
        List<Azs> favorites = loadSavedFavorites();
        fillFavoritesList(favorites);
    }

    private void fillFavoritesList(List<Azs> pAzses) {
        mAzsOriginal = pAzses;
        mFavoriteFiltered = cloneAzsList(mAzsOriginal);
        mAdapter = createAdapter(mFavoriteFiltered);
        mFavoriteList.setAdapter(mAdapter);
    }

    private List<Azs> cloneAzsList(List<Azs> pOriginal) {
        return new ArrayList<>(pOriginal);
    }

    private FavoriteAdapter createAdapter(List<Azs> pFavoriteFiltered) {
        return new FavoriteAdapter(getActivity(), pFavoriteFiltered,
                getRemoveFavoriteListener());
    }

    private List<Azs> loadSavedFavorites() {
        return Azs.getFavorites();
    }

    private View.OnClickListener getRemoveFavoriteListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Azs.getById((int)v.getTag()) != null) {
                    Azs.getById((int) v.getTag()).removeFavorite();
                    loadFavorites();
                }
            }
        };
    }

    private void selectBackground() {
        if (hasFavorites()) mAq.id(R.id.favorite_empty_bg).gone();
        else {
            setEmptyBackgroundText();
            mAq.id(R.id.favorite_empty_bg).visible();
        }
    }

    private void setEmptyBackgroundText() {
        TextView tvBgHeader = mAq.id(R.id.empty_bg_header).getTextView();
        TextView tvBgBody = mAq.id(R.id.empty_bg_body).getTextView();

        tvBgHeader.setText(R.string.until_have_no_fav);
        tvBgBody.setText(R.string.add_azs_fav);
    }

    private boolean hasFavorites() {
        return !mFavoriteFiltered.isEmpty();
    }

    private void changeFilter(String pFilter, boolean pIsActive) {
        if (pIsActive) mServiceFilters.add(pFilter);
        else mServiceFilters.remove(pFilter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AzsItemActivity.launch(getActivity(), view, mAdapter.getItem(position));
    }
}
