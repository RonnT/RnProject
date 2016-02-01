package top.titov.gas.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONObject;

import top.titov.gas.R;
import top.titov.gas.gcm.GcmRegisterer;
import top.titov.gas.helper.LocationHelper;
import top.titov.gas.helper.PrefsHelper;
import top.titov.gas.helper.SynchronizeHelper;
import top.titov.gas.helper.ToastHelper;
import top.titov.gas.model.About;
import top.titov.gas.model.PushWrapper;
import top.titov.gas.model.RegionIdWrapper;
import top.titov.gas.utils.CONST;
import top.titov.gas.utils.api.Api;


/**
 * Created by Roman Titov on 03.04.2015.
 */
public class SplashActivity extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new GcmRegisterer(this).registerDevice();
        runNeededActivity();
    }

    private void runNeededActivity() {

        if(PrefsHelper.getInstance().getIsFirstRun())
            setUserRegionAndSend();

        SynchronizeHelper.synchronize();

        About.loadingAbout();

        Intent intent = getIntentFarbic();
        Bundle extras = getIntent().getExtras();
        if(extras != null &&extras.getBoolean(CONST.FROM_PUSH, false)) {
            finish();
        }
        else {
            startActivity(intent);
            finish();
        }
    }

    private Intent getIntentFarbic() {
        return new Intent(this, MainActivity.class);
    }

    private void setUserRegionAndSend(){
        Location currentLocation = LocationHelper.getCurrentLocation();
        if(currentLocation != null)
            Api.getInstance().getCurrentRegionId(currentLocation.getLatitude(), currentLocation.getLongitude(), getCurrentLocationResponseListener(), getCurrentLocationErrorListener());
        else
            Api.getInstance().setRegion(PrefsHelper.getInstance().getUserRegionId(), getSetRegionResponseListener(), getSetRegionErrorListener());
    }

    private Response.Listener<JSONObject> getCurrentLocationResponseListener(){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                RegionIdWrapper wrapper = new Gson().fromJson(response.toString(), RegionIdWrapper.class);
                if(wrapper.getErrorCode() == CONST.ERROR_CODE_OK){
                    PrefsHelper.getInstance().setIsFirstRun(false);
                    PrefsHelper.getInstance().setUserRegionId(wrapper.getRegionId());
                }
                Api.getInstance().setRegion(PrefsHelper.getInstance().getUserRegionId(), getSetRegionResponseListener(), getSetRegionErrorListener());
            }
        };
    }

    public Response.ErrorListener getCurrentLocationErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Api.getInstance().setRegion(PrefsHelper.getInstance().getUserRegionId(), getSetRegionResponseListener(), getSetRegionErrorListener());
            }
        };
    }

    private Response.Listener<JSONObject> getSetRegionResponseListener(){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                PushWrapper wrapper = new Gson().fromJson(response.toString(), PushWrapper.class);
                if(wrapper.getErrorCode() != CONST.ERROR_CODE_OK)
                    ToastHelper.showToast(R.string.error_unknown);
            }
        };
    }

    private Response.ErrorListener getSetRegionErrorListener(){
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
    }


}