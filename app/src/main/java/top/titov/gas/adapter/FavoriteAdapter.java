package top.titov.gas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;

import java.util.List;

import top.titov.gas.R;
import top.titov.gas.helper.AzsHelper;
import top.titov.gas.helper.LocationHelper;
import top.titov.gas.model.azs.Azs;
import top.titov.gas.utils.DataManager;


public class FavoriteAdapter extends BaseSwipeAdapter {

    private Context mContext;
    private List<Azs> mFavoriteItems;
    private View.OnClickListener mRemoveListener;

    public FavoriteAdapter(Context pContext, List<Azs> pItems, View.OnClickListener pRemoveListener) {
        mContext = pContext;
        mFavoriteItems = pItems;
        mRemoveListener = pRemoveListener;
    }

    @Override
    public int getSwipeLayoutResourceId(int pPosition) {
        return R.id.favorite_swipe;
    }

    @Override
    public View generateView(final int pPosition, ViewGroup pParent) {

        return LayoutInflater.from(mContext)
                .inflate(R.layout.view_favorite_list_item, pParent, false);
    }

    @Override
    public int getCount() {
        return mFavoriteItems.size();
    }

    @Override
    public Azs getItem(int pPosition) {
        return mFavoriteItems.get(pPosition);
    }

    @Override
    public long getItemId(int pPosition) {
        return mFavoriteItems.get(pPosition).getId();
    }

    @Override
    public void fillValues(int pPosition, View pConvertView) {
        AQuery rowAq = new AQuery(pConvertView);
        Azs fav = mFavoriteItems.get(pPosition);

        rowAq.id(R.id.button_trash).tag(fav.getId()).clicked(mRemoveListener);

        if(DataManager.getInstance().getFuelType() != null)
            AzsHelper.setPriceToView(rowAq, fav.getFuelPrice(DataManager.getInstance().getFuelType()));
        setAddress(rowAq, fav);
        setDistance(rowAq, fav.getDistance());

        ViewGroup serviceRoot = (ViewGroup) rowAq.id(R.id.view_azs_list_item_services_LL).getView();
        serviceRoot.removeAllViews();
        AzsHelper.setAzsServices(fav.getServiceAvailable(), serviceRoot);
    }

    private void setAddress(AQuery pAq, Azs pAzs) {
        pAq.id(R.id.view_azs_list_item_address).text(pAzs.getAddress());
    }

    private void setDistance(AQuery pAq, float pDistance) {
        String pDistanceString = LocationHelper.getAccuracyString(pDistance);

        pAq.id(R.id.view_azs_list_item_distance).text(pDistanceString);
    }
}
