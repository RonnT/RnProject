package top.titov.gas.helper;

import android.content.Context;

import top.titov.gas.MyApp;
import top.titov.gas.R;
import top.titov.gas.utils.api.Api_Url;

/**
 * Created by Andrew Vasilev on 02.06.2015.
 */
public class SizeHelper {

    private static class SingletonHolder {
        private static SizeHelper INSTANCE = null;
    }

    public static SizeHelper getInstance(Context pContext) {
        if (SingletonHolder.INSTANCE == null) SingletonHolder.INSTANCE = new SizeHelper(pContext);
        return SingletonHolder.INSTANCE;
    }

    //------------------------------------------

    private static float
            sSrceenHeight = -1,
            sSrceenWidth = -1,
            sScreenDensity = -1;

    private Context mContext = null;

    public SizeHelper(Context pContext) {
        mContext = pContext;
    }

    public float getScreenHeight() {
        if (sSrceenHeight != -1) return sSrceenHeight;
        else return sSrceenHeight = mContext.getResources().getDisplayMetrics().heightPixels;
    }

    public float getScreenWidth() {
        if (sSrceenWidth != -1) return sSrceenWidth;
        else return sSrceenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
    }

    public float getScreenDensity() {
        if (sScreenDensity != -1) return sScreenDensity;
        else return sScreenDensity = mContext.getResources().getDisplayMetrics().density;
    }

    public int getScreenHeightByPercent(float pPercent) {
        float screenHeightDp = getScreenHeight() / getScreenDensity();
        return (int) (screenHeightDp / 100 * pPercent);
    }

    public int getScreenWidthByPercent(float pPercent) {
        float screenWidthDp = getScreenWidth() / getScreenDensity();
        return (int) (screenWidthDp / 100 * pPercent);
    }

    public float convertPixelsToDp(float px) {
        float t = getScreenDensity();
        float dp = px * t;
        return dp;
    }

    public String createSizeDependedImageUrl(String pImageUrl) {

        StringBuilder builder = new StringBuilder();

        float width = SizeHelper.getInstance(MyApp.getAppContext()).getScreenWidth();
        float height = MyApp.getDimenFromRes(R.dimen.not_matter_size);

        builder.append(Api_Url.getImageUrl());
        builder.append((int) width);
        builder.append(MyApp.getAppContext().getString(R.string.image_lock_ratio_suffix));
        builder.append((int) height);
        builder.append(pImageUrl);

        return builder.toString();
    }
}
