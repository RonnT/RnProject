package ru.pichesky.rosneft.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ru.pichesky.rosneft.MyApp;
import ru.pichesky.rosneft.R;
import ru.pichesky.rosneft.fragment.dialog.LoadingDialogFragment;
import ru.pichesky.rosneft.utils.CONST;

/**
 * Created by Andrew Vasilev on 04.02.2015.
 * LocationHelper for getting last location
 */
public class LocationHelper {
    private static final int
            MIN_CHECK_TIME = 1000,
            MIN_CHECK_DIST = 0;

    private static final double
            EARTH_RADIUS = 6371000; // m

    private static boolean sIsChecking = false;

    private static Location sCurrentLocation = null;
    private static LocationManager sLocManager = (LocationManager) MyApp.getAppContext()
            .getSystemService(Context.LOCATION_SERVICE);

    private static Activity sActivity;
    private static LoadingDialogFragment mLoadingDialog;

    private static LocationListener sLocListener =
            new LocationListener() {
                @Override
                public void onLocationChanged(Location loc) {
                    if (null != loc) {
                        sCurrentLocation = loc;
                        notifyAboutUpdatedLocation();
                    }
                }

                @Override
                public void onProviderEnabled(String provider) {
                    /*
                    if (provider.equals(LocationManager.GPS_PROVIDER)) {
                        showRequestGpsDialog();
                    }
                    */
                }

                @Override
                public void onProviderDisabled(String provider) {}

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}
            };

    public static Location getCurrentLocation() {
        return getCurrentLocation(true);
    }

    public static Location getCurrentLocation(boolean pStartCheckingLocation) {
        if (pStartCheckingLocation) startCheckingLocation();

        if (null != sCurrentLocation) return sCurrentLocation;
        else {
            getBestLastLocation();
            return sCurrentLocation;
        }
    }

    public static void startCheckingLocation() {
        List<String> providers = sLocManager.getAllProviders();

        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            sLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_CHECK_TIME, MIN_CHECK_DIST, sLocListener);
            sIsChecking = true;
        }

        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            sLocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    MIN_CHECK_TIME, MIN_CHECK_DIST, sLocListener);
            sIsChecking = true;
        }
    }

    public static void stopCheckingLocation() {
        if (isChecking()) {
            sLocManager.removeUpdates(sLocListener);
            sIsChecking = false;
        }
    }

    private static void getBestLastLocation() {
        Location gpsLoc, netLoc;

        gpsLoc = sLocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        netLoc = sLocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        sCurrentLocation = getBestOfTwoLocations(gpsLoc, netLoc);
    }

    private static  Location getBestOfTwoLocations(Location loc1, Location loc2) {
        Location tempLoc = null;

        if (loc1 != null && loc2 != null) {
            long loc1Time = loc1.getTime();
            long loc2Time = loc2.getTime();

            float loc1Accuracy = loc1.getAccuracy();
            float loc2Accuracy = loc2.getAccuracy();


            if (loc1Time > loc2Time && loc1Accuracy < loc2Accuracy) tempLoc = loc1;
            else if (loc1Time < loc2Time && loc1Accuracy > loc2Accuracy) tempLoc = loc2;
            else if (loc1Time > loc2Time ) tempLoc = loc1;
            else tempLoc = loc2;

        } else if (loc1 != null) {
            tempLoc = loc1;

        } else if (loc2 != null) {
            tempLoc = loc2;
        }
        return tempLoc;
    }

    public static boolean checkLocationTurnedOn(Activity pActivity) {
        LocationManager lm = MyApp.getLocationManager();
        boolean gpsOn = true, netOn = true;

        if (lm.getAllProviders().contains(LocationManager.GPS_PROVIDER)
            && !lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            gpsOn = false;
        }

        if (lm.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)
            && !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            netOn = false;
        }

        if (!gpsOn && !netOn) {
            showRequestLocationDialog(pActivity, R.string.question_enable_location,R.string.need_location);
            return false;
        }

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_CHECK_TIME, MIN_CHECK_DIST,
                sLocListener);

        return gpsOn || netOn;
    }

    public static void checkGpsTurnedOn(Activity pActivity) {
        LocationManager lm = MyApp.getLocationManager();
        boolean gpsOn = true;

        if (lm.getAllProviders().contains(LocationManager.GPS_PROVIDER)
            && !lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            gpsOn = false;
        }


        if (!gpsOn) showRequestLocationDialog(pActivity, R.string.question_enable_gps,R.string.need_gps);

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_CHECK_TIME, MIN_CHECK_DIST,
                sLocListener);
    }

    private static void showRequestLocationDialog(final Activity activity, int pQuestionTitleId,
                                                  int pQuestionId) {
        Runnable positiveRunnable =
                new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        activity.startActivity(intent);
                    }
                };

        DialogHelper.initAlertDialog(activity, pQuestionTitleId, pQuestionId,
                R.string.enable, R.string.cancel, positiveRunnable, null, false);
    }

    public static String getAccuracyString(float accuracy) {
        if (accuracy == 0) return "";

        if (accuracy < 1000) {
            return removeZeros(String.format("%.2f", accuracy)) + MyApp.getStringFromRes(R.string.location_m_postfix);
        } else {
            return removeZeros(String.format("%.2f", accuracy / 1000))
                    + MyApp.getStringFromRes(R.string.location_km_postfix);
        }
    }

    private static String removeZeros(String pAccuracy) {
        int length = pAccuracy.length();

        if (pAccuracy.charAt(length - 1) == '0') {
            return removeZeros(pAccuracy.substring(0, length - 1));
        }

        String[] splitted = pAccuracy.split(",");

        if (splitted.length > 1) return pAccuracy;
        else return splitted[0];
    }

    private static void startCheckingTimer(FragmentManager pFm) {
        final boolean showLoading = pFm != null;
        if (showLoading) {
            mLoadingDialog = new LoadingDialogFragment();
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.show(pFm, null);
        }

        Timer t = new Timer();
        t.schedule(new TimerTask() {

            private Handler updateUI = new Handler() {
                @Override
                public void dispatchMessage(Message msg) {
                    super.dispatchMessage(msg);

                    if (showLoading) mLoadingDialog.dismiss();
                    ToastHelper.showToast(R.string.cant_find_loc);
                }
            };

            @Override
            public void run() {
                Looper.prepare();
                sCurrentLocation = getCurrentLocation();
                if (null == sCurrentLocation) {
                    updateUI.sendEmptyMessage(0);
                }
                if (LocationHelper.isChecking()) LocationHelper.stopCheckingLocation();
            }
        }, 5000);
    }

    private static  void notifyAboutUpdatedLocation() {
        Intent intent = new Intent(CONST.INTENT_UPDATE_LOCATION);
        MyApp.getAppContext().sendBroadcast(intent);
    }

    public static boolean isChecking() {
        return sIsChecking;
    }

    public static Location calculateDerivedPosition(Location location, double range, double bearing)
    {
        double latA = Math.toRadians(location.getLatitude());
        double lonA = Math.toRadians(location.getLongitude());
        double angularDistance = range / EARTH_RADIUS;
        double trueCourse = Math.toRadians(bearing);

        double lat = Math.asin(
                Math.sin(latA) * Math.cos(angularDistance) +
                        Math.cos(latA) * Math.sin(angularDistance)
                                * Math.cos(trueCourse));

        double dlon = Math.atan2(
                Math.sin(trueCourse) * Math.sin(angularDistance)
                        * Math.cos(latA),
                Math.cos(angularDistance) - Math.sin(latA) * Math.sin(lat));

        double lon = ((lonA + dlon + Math.PI) % (Math.PI * 2)) - Math.PI;

        lat = Math.toDegrees(lat);
        lon = Math.toDegrees(lon);

        Location newPoint = new Location("point");
        newPoint.setLatitude((float) lat);
        newPoint.setLongitude((float) lon);

        return newPoint;
    }

    public static float calculateDist(Location loc1, Location loc2) {
        return loc1 == null || loc2 == null ? -1 : loc1.distanceTo(loc2);
    }

    public static float calculateDist(Location pLoc2) {
        if (sCurrentLocation == null || pLoc2 == null) return 0;

        return sCurrentLocation.distanceTo(pLoc2);
    }
 }
