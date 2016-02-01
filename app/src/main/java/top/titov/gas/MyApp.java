package top.titov.gas;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;

import top.titov.gas.helper.HelperFactory;
import top.titov.gas.helper.PrefsHelper;
import top.titov.gas.utils.Utility;


public class MyApp extends Application {
    private static Context sContext;
    private static LocationManager sLocationManager;


    @Override
    public void onCreate() {
        super.onCreate();
        //Fabric.with(this, new Crashlytics());         TODO Enable in release
        sContext = getApplicationContext();
        HelperFactory.setHelper(getAppContext());

        sLocationManager =
                (LocationManager) getAppContext().getSystemService(Context.LOCATION_SERVICE);

        String token = PrefsHelper.getInstance().getToken();
        if (token == null || token.length() == 0) {
            PrefsHelper.getInstance().setToken(Utility.getAndroidID());
        }
    }

    @Override
    public void onTerminate() {
        sContext = null;
        HelperFactory.releaseHelper();

        super.onTerminate();
    }

    public static Context getAppContext() {
        return sContext;
    }

    public static LocationManager getLocationManager() {
        return sLocationManager;
    }

    public static String getStringFromRes(int id) {
        return sContext != null ? sContext.getString(id) : "";
    }

    public static int getColorFromRes(int pId) {
        return getAppContext().getResources().getColor(pId);
    }

    public static float getDimenFromRes(int pId) {
        return getAppContext().getResources().getDimension(pId);
    }

    public static Drawable getDrawableFromRes(int pId) {
        return getAppContext().getResources().getDrawable(pId);
    }
}
