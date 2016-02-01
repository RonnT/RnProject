package top.titov.gas.helper;

import android.content.Context;
import android.content.SharedPreferences;

import top.titov.gas.MyApp;
import top.titov.gas.R;
import top.titov.gas.utils.CONST;

/**
 * Created by Alexander Smirnov on 13.01.2015.
 * PrefsHelper is used as wrapper for managin preferences
 */
public class PrefsHelper {

    private static class SingletonHolder {
        private static PrefsHelper INSTANCE;
    }

    public static PrefsHelper getInstance() {
        if (SingletonHolder.INSTANCE == null) {
            SingletonHolder.INSTANCE = new PrefsHelper(MyApp.getAppContext());
        }
        return SingletonHolder.INSTANCE;
    }

    //----------------------------------------------------------------

    private static final String
            PREFS_NAME          = "PREFS_NAME",
            TOKEN               = "TOKEN",
            API_URL             = "API_URL",
            SERVER_URL          = "SERVER_URL",

            FILTER_FUEL_TYPE    = "FILTER_FUEL_TYPE",
            FILTER_WASH         = "FILTER_WASH",
            FILTER_SERVICE      = "FILTER_TIRE",
            FILTER_CAFE         = "FILTER_CAFE",
            FILTER_SHOP         = "FILTER_SHOP",

            IS_FIRST_RUN        = "IS_FIRST_RUN",
            USER_REGION         = "USER_REGION",
            ABOUT_NAME          = "ABOUT_NAME",
            ABOUT_TEXT          = "ABOUT_TEXT",
            ABOUT_IMAGE         = "ABOUT_IMAGE",
            PUSH_IS_REG         = "PUSH_IS_REG",

            STATION_LAST_SYNC   = "STATION_LAST_SYNC",

            PROPERTY_REG_ID         = "PROPERTY_REG_ID",
            PROPERTY_APP_VERSION    = "PROPERTY_APP_VERSION";


    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;

    private PrefsHelper(Context pContext) {
        mPrefs = pContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private void putString(String name, String value) {
        mEditor = mPrefs.edit();
        mEditor.putString(name, value);

        if (!mEditor.commit()) Logger.e("Can't commit putString(" + name + ", " + value + ")");
    }

    private void putLong(String name, long value) {
        mEditor = mPrefs.edit();
        mEditor.putLong(name, value);

        if (!mEditor.commit()) Logger.e("Can't commit putLong(" + name + ", " + value + ")");
    }

    private void putBoolean(String name, boolean value) {
        mEditor = mPrefs.edit();
        mEditor.putBoolean(name, value);

        if (!mEditor.commit()) Logger.e("Can't commit putBoolean(" + name + ", " + value + ")");
    }

    private boolean getBoolean(String pKey, boolean pDefValue) {
        return mPrefs.getBoolean(pKey, pDefValue);
    }

    private int getInt(String pKey, int pDefValue) {
        return mPrefs.getInt(pKey, pDefValue);
    }

    private String getString(String pKey, String pDefValue) {
        return mPrefs.getString(pKey, pDefValue);
    }

    private long getLong(String pKey, long pDefValue) {
        return mPrefs.getLong(pKey, pDefValue);
    }

    private void putInt(String name, int value) {
        mEditor = mPrefs.edit();
        mEditor.putInt(name, value);

        if (!mEditor.commit()) Logger.e("Can't commit putInt(" + name + ", " + value + ")");
    }

    public String getName() {
        return getString(PREFS_NAME, "");
    }

    public void setName(String pName) {
        putString(PREFS_NAME, pName);
    }

    public String getToken() {
        return mPrefs.getString(TOKEN, "");
    }

    public void setToken(String token) {
        putString(TOKEN, token);
    }


    public boolean getIsFirstRun() {
        return getBoolean(IS_FIRST_RUN, true);
    }

    public void setIsFirstRun(boolean pValue) {
        putBoolean(IS_FIRST_RUN, pValue);
    }

    public boolean getFilterWash() {
        return getBoolean(FILTER_WASH, false);
    }

    public void setFilterWash(boolean pValue) {
        putBoolean(FILTER_WASH, pValue);
    }

    public boolean getFilterService() {
        return getBoolean(FILTER_SERVICE, false);
    }

    public void setFilterService(boolean pValue) {
        putBoolean(FILTER_SERVICE, pValue);
    }

    public boolean getFilterCafe() {
        return getBoolean(FILTER_CAFE, false);
    }

    public void setFilterCafe(boolean pValue) {
        putBoolean(FILTER_CAFE, pValue);
    }

    public boolean getFilterShop() {
        return getBoolean(FILTER_SHOP, false);
    }

    public void setFilterShop(boolean pValue) {
        putBoolean(FILTER_SHOP, pValue);
    }

    public String getFilterFuelType() {
        String[] fuelTypes =
                MyApp.getAppContext().getResources().getStringArray(R.array.fuel_types);
        return getString(FILTER_FUEL_TYPE, fuelTypes[0]);
    }

    public void setFilterFuelType(String pValue) {
        putString(FILTER_FUEL_TYPE, pValue);
    }

    public void setUserRegionId(int pRegionId) {
        putInt(USER_REGION, pRegionId);
    }

    public int getUserRegionId() {
        return getInt(USER_REGION, CONST.DEFAULT_REGION);
    }

    public String getAboutName() {
        return getString(ABOUT_NAME, "");
    }

    public String getAboutText() {
        return getString(ABOUT_TEXT, "");
    }

    public String getAboutImage() {
        return getString(ABOUT_IMAGE, "");
    }

    public void setAboutName(String pAboutName) {
        putString(ABOUT_NAME, pAboutName);
    }

    public void setAboutText(String pAboutText) {
        putString(ABOUT_TEXT, pAboutText);
    }

    public void setAboutImage(String pAboutImage) {
        putString(ABOUT_IMAGE, pAboutImage);
    }

    public int getAppVersion() {
        return getInt(PROPERTY_APP_VERSION, 0);
    }

    public void setAppVersion(int version) {
        putInt(PROPERTY_APP_VERSION, version);
    }

    public String getRegId() {
        return getString(PROPERTY_REG_ID, "");
    }

    public void setRegId(String id) {
        putString(PROPERTY_REG_ID, id);
    }

    public boolean getPushStatus() {
        return getBoolean(PUSH_IS_REG, false);
    }

    public void setPushStatus(boolean status) {
        putBoolean(PUSH_IS_REG, status);
    }

    public void setLastSyncTime(long pTimeStamp) {
        putLong(STATION_LAST_SYNC, pTimeStamp);
    }

    public long getLastSyncTime() {
        return getLong(STATION_LAST_SYNC, CONST.DEFAULT_UPDATE_TIMESTAMP);
    }
}

