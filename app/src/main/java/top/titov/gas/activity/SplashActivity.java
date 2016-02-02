package top.titov.gas.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

import top.titov.gas.R;
import top.titov.gas.helper.ToastHelper;
import top.titov.gas.model.azs.Azs;
import top.titov.gas.model.azs.AzsWrapper;
import top.titov.gas.utils.api.Api;


/**
 * Created by Roman Titov on 03.04.2015.
 */
public class SplashActivity extends Activity implements Response.ErrorListener, Response.Listener<JSONObject> {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Api.getInstance().getAzsList(this, this);

        runNeededActivity();

    }

    private void runNeededActivity() {



        //SynchronizeHelper.synchronize();

        //About.loadingAbout();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        runNeededActivity();
    }

    @Override
    public void onResponse(JSONObject response) {
        AzsWrapper wrapper = new Gson().fromJson(response.toString(), AzsWrapper.class);
        List<Azs> itemList = wrapper.getAzsList();
        Azs.updateAll(itemList);
        ToastHelper.showToast(String.valueOf(itemList.size()));
        runNeededActivity();
    }
}