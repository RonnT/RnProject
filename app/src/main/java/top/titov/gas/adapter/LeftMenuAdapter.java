package top.titov.gas.adapter;

import android.content.Context;
import android.content.res.TypedArray;

import top.titov.gas.MyApp;
import top.titov.gas.R;

/**
 * Created by Andrew Vasilev on 03.07.2015.
 */
public class LeftMenuAdapter extends SimpleAdapter {

    private static final int NOTIF_ICON_SIZE = 24;
    private static final int NOTIF_TEXT_SIZE = 14;

    private String[] mTitles;
    private TypedArray mIcons;
    private Context mContext;
    private int mSelectedPosition = 0;

    public void setSelectedPosition(int pSelectedPosition) {
        mSelectedPosition = pSelectedPosition;
    }

    public LeftMenuAdapter(Context pContext) {
        super();

        mContext = pContext;

        mTitles = MyApp.getAppContext().getResources().getStringArray(R.array.left_menu_titles);
        mIcons = MyApp.getAppContext().getResources()
                .obtainTypedArray(R.array.left_menu_icons);
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    protected int getConvertViewId() {
        return R.layout.view_drawer_item;
    }

    @Override
    protected void fillHolderValues(Holder pHolder, int pPosition) {
        String title = mTitles[pPosition];

        boolean isActive = pPosition == mSelectedPosition;

        int iconId = isActive ? mIcons.getResourceId(pPosition + mIcons.length() / 2, -1)
                : mIcons.getResourceId(pPosition, -1);

        int titleColorId = isActive ? R.color.color_accent : R.color.black_87;

        pHolder.aq.id(R.id.view_drawer_item_icon).image(iconId);
        pHolder.aq.id(R.id.view_drawer_item_title).text(title).textColorId(titleColorId);
    }
}
