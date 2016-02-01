package top.titov.gas.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import top.titov.gas.MyApp;
import top.titov.gas.R;
import top.titov.gas.extension.OnMoreListener;
import top.titov.gas.helper.NetworkHelper;
import top.titov.gas.helper.ToastHelper;


/**
 * Created by Alexander Smirnov on 13.11.2014.
 * Some utilities
 */
public class Utility {

    public static Response.ErrorListener getDefaultErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
    }

    public static Bitmap drawWhiteViewOntoBitmap(View view) {
        Bitmap image = Bitmap
                .createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(image);
        canvas.drawRGB(255, 255, 255);
        return image;
    }

    public static Bitmap drawViewOntoBitmap(View view) {
        Bitmap image = Bitmap
                .createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(image);
        view.draw(canvas);
        return image;
    }

    public static String getAndroidID() {
        return Settings.Secure.getString(MyApp.getAppContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static int getAppVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    public static boolean isTablet() {
        Context context = MyApp.getAppContext();

        boolean xlarge = (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) == 4;
        boolean large = (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE;
        return (xlarge || large);
    }

    // For future ideas
    public static boolean isPortrait() {
        return MyApp.getAppContext().getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) MyApp.getAppContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        View currentFocus = activity.getCurrentFocus();

        if(null != imm && null != currentFocus) {
            try {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getDate(long timeStamp, String format){
        try{
            DateFormat sdf = new SimpleDateFormat(format);
            Date netDate = (new Date(timeStamp * 1000L));
            return sdf.format(netDate);
        }
        catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public static void checkIsNeedLoadMore(int pPosition, int pCount, OnMoreListener pMoreListener) {
        if (pMoreListener != null && pCount - pPosition <= CONST.ITEMS_TO_LOAD_MORE) {
            pMoreListener.onMoreAsked(pCount, CONST.ITEMS_TO_LOAD_MORE, pPosition);
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static float getScreenWidth(Context pContext) {
        return pContext.getResources().getDisplayMetrics().widthPixels;
    }

    public static String getFileSize(File pFile) {
        long sizeBytes = pFile.length();

        return fileSizeRound(sizeBytes);
    }

    private static String fileSizeRound(long pSizeBytes) {
        float sizeKBytes = pSizeBytes / 1024;

        if (sizeKBytes < 1024) return String.format("%.2f Кб", sizeKBytes);
        else return String.format("%.2f Кб", (sizeKBytes / 1024));
    }

    public static boolean checkConnection(Response.ErrorListener pEL) {
        if (!NetworkHelper.isNetworkAvailable()) {
            ToastHelper.showToast(R.string.error_check_connection, Toast.LENGTH_LONG);

            String volleyErrorText =
                    MyApp.getStringFromRes(R.string.volley_connection_error);
            pEL.onErrorResponse(new VolleyError(volleyErrorText));

            return false;
        }
        return true;
    }

    public static int getArrayDrawableId(int pArrayId, int pDrawableIndex) {
        TypedArray drawables = MyApp.getAppContext().getResources()
                .obtainTypedArray(pArrayId);

        return drawables.getResourceId(pDrawableIndex, -1);
    }

    public static void showFragment(FragmentManager pFm, Fragment pFragment, int pLayoutId,
                                    String pTag) {
        showFragment(pFm, pFragment, pLayoutId, pTag, -1, -1);
    }

    public static void showFragment(FragmentManager pFm, Fragment pFragment, int pLayoutId,
                                    String pTag, int pAnimIn, int pAnimOut) {
        showFragment(pFm, pFragment, pLayoutId, pTag, pAnimIn, pAnimOut, -1, -1);
    }

    public static void showFragment(FragmentManager pFm, Fragment pFragment, int pLayoutId,
                                    String pTag, int pAnimIn, int pAnimOut,
                                    int pAnimInBack, int pAnimOutBack) {
        showFragment(pFm, pFragment, pLayoutId, pTag
                , pAnimIn, pAnimOut, pAnimInBack, pAnimOutBack, false);
    }

    public static void showFragment(FragmentManager pFm, Fragment pFragment, int pLayoutId,
                                    String pTag, int pAnimIn, int pAnimOut,
                                    int pAnimInBack, int pAnimOutBack, boolean wBackStack) {
        FragmentTransaction ft = pFm.beginTransaction();

        if (pAnimIn > -1 && pAnimOut > -1 && pAnimInBack > -1 && pAnimOutBack > -1) {
            ft.setCustomAnimations(pAnimIn, pAnimOut, pAnimInBack, pAnimOutBack);

        } else if (pAnimIn > -1 && pAnimOut > -1) {
            ft.setCustomAnimations(pAnimIn, pAnimOut);
        }

        ft.replace(pLayoutId, pFragment, pTag);
        if (wBackStack) ft.addToBackStack(null);
        ft.commit();
    }

    public static void runRouteIntent(Activity pActivity, Location pLoc1, Location pLoc2) {
        if (pLoc1 == null || pLoc2 == null) {
            ToastHelper.showToast(pActivity.getString(R.string.not_enough_data_to_create_route));
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(pActivity.getString(R.string.route_intent_1) + pLoc1.getLatitude()
                        + pActivity.getString(R.string.route_intent_2) + pLoc1.getLongitude()
                        + pActivity.getString(R.string.route_intent_3) + pLoc2.getLatitude()
                        + pActivity.getString(R.string.route_intent_4)  + pLoc2.getLongitude()));
        pActivity.startActivity(intent);
    }

    public static void launchActivity(Activity pActivityFrom, View pView, Intent pIntent) {
        Bundle b = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && pView != null) {
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeThumbnailScaleUpAnimation(pView, Utility.drawViewOntoBitmap(pView), 0, 0);
            b = options.toBundle();
        }

        ActivityCompat.startActivity(pActivityFrom, pIntent, b);
    }

    public static void launchActivityForResult(Activity pActivityFrom, View pView, Intent pIntent,
                                               int pRequestCode) {
        Bundle b = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && pView != null) {
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeThumbnailScaleUpAnimation(pView, Utility.drawViewOntoBitmap(pView), 0, 0);
            b = options.toBundle();
        }

        ActivityCompat.startActivityForResult(pActivityFrom, pIntent, pRequestCode, b);
    }

    public static void runDialIntent(Activity pActivityFrom, String pPhoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + pPhoneNumber));
        pActivityFrom.startActivity(callIntent);
    }

    public static long getCurrentTimestamp(){
        return Calendar.getInstance().getTimeInMillis();
    }
}
