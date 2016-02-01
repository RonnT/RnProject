package ru.pichesky.rosneft.helper;

import android.content.Intent;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import ru.pichesky.rosneft.MyApp;
import ru.pichesky.rosneft.model.DataWrapper;
import ru.pichesky.rosneft.model.LastTimestamp;
import ru.pichesky.rosneft.model.azs.Azs;
import ru.pichesky.rosneft.model.azs.SynchronizeWrapper;
import ru.pichesky.rosneft.utils.CONST;
import ru.pichesky.rosneft.utils.api.Api;

/**
 * Created by Yagupov Ruslan on 27.10.2015.
 */
public class SynchronizeHelper {

    public static void synchronize() {
        Api.getInstance().getSynchronizeLastTime(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                DataWrapper<LastTimestamp> lastTime = new Gson().fromJson(response.toString(),
                        new TypeToken<DataWrapper<LastTimestamp>>() {
                        }.getType());
                if (lastTime.getErrorCode() == CONST.ERROR_CODE_OK) {
                    if (PrefsHelper.getInstance().getLastSyncTime() != lastTime.getData().getLastTime()) {
                        Api.getInstance().syncronize(PrefsHelper.getInstance().getLastSyncTime(), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                SynchronizeWrapper wrapper = new Gson().fromJson(response.toString(), SynchronizeWrapper.class);
                                if(wrapper.getErrorCode() == CONST.ERROR_CODE_OK){
                                    Azs.updateAll(wrapper.getAzsList());
                                    MyApp.getAppContext().sendBroadcast(new Intent(CONST.INTENT_UPDATE_AZS));
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        });
                        PrefsHelper.getInstance().setLastSyncTime(lastTime.getData().getLastTime());
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
    }
}
