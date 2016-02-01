package top.titov.gas.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;

import top.titov.gas.R;
import top.titov.gas.helper.PrefsHelper;
import top.titov.gas.helper.ToastHelper;
import top.titov.gas.model.PushWrapper;
import top.titov.gas.utils.CONST;
import top.titov.gas.utils.api.Api;

/**
 * Created by Andrew Vasilev on 22.06.2015.
 */
public class GcmRegisterer {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String
            PROJECT_ID = "612526004741",
            GCM_LOG_TAG = "GCM_LOG_TAG";

    private Activity mActivity;

    private String mRegId;
    private GoogleCloudMessaging mGcm;


    public GcmRegisterer(Activity pActivity) {
        mActivity = pActivity;
    }

    public void registerDevice() {
        if (checkPlayServices()) {
            mGcm = GoogleCloudMessaging.getInstance(mActivity);
            mRegId = getRegistrationId(mActivity);

            if (mRegId.isEmpty()) registerInBackground();
            else sendRegistrationIdToBackend();
        } else Log.i(GCM_LOG_TAG, "gcm_no_valid_play_services");
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, mActivity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else Log.i(GCM_LOG_TAG, "gcm_device_not_supported");

            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {
        PrefsHelper prefsHelper = PrefsHelper.getInstance();

        String registrationId = prefsHelper.getRegId();

        if (registrationId.isEmpty()) {
            Log.i(GCM_LOG_TAG, "gcm_reg_id_not_found");
            return "";
        }

        int registeredVersion = prefsHelper.getAppVersion();
        int currentVersion = getAppVersion(context);

        if (registeredVersion != currentVersion) {
            Log.i(GCM_LOG_TAG, "gcm_app_version_changed");
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void registerInBackground() {
        new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";

                try {
                    if (mGcm == null) mGcm = GoogleCloudMessaging.getInstance(mActivity);

                    mRegId = mGcm.register(PROJECT_ID);
                    msg = "gcm_id_registered" + mRegId;

                    sendRegistrationIdToBackend();
                    storeRegistrationId(mActivity, mRegId);

                } catch (IOException ex) {
                    ex.printStackTrace();
                    msg = "gcm_error" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.d(GCM_LOG_TAG, "gcm_reg_result_msg" + msg);
            }

        }.execute(null, null, null);
    }

    private void storeRegistrationId(Context context, String regId) {

        PrefsHelper prefsHelper = PrefsHelper.getInstance();
        prefsHelper.setRegId(regId);

        int appVersion = getAppVersion(context);
        prefsHelper.setAppVersion(appVersion);
    }

    private Response.Listener<JSONObject> getPushResponseListener(){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                PushWrapper wrapper = new Gson().fromJson(response.toString(), PushWrapper.class);
                if(wrapper.getErrorCode() == CONST.ERROR_CODE_OK){
                    PrefsHelper.getInstance().setPushStatus(true);
                } else {
                    ToastHelper.showToast(R.string.error_unknown);
                }
            }
        };
    }

    private Response.ErrorListener getPushErrorListener(){
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                ToastHelper.showToast(R.string.error_check_connection);
            }
        };
    }

    private void sendRegistrationIdToBackend() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                if(PrefsHelper.getInstance().getPushStatus() == false)
                    Api.getInstance().subscribePush(getPushResponseListener(), getPushErrorListener());
                return "";
            }
        }.execute();
    }
}
