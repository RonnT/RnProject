package ru.pichesky.rosneft.activity;

import android.os.Bundle;
import android.text.Html;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONObject;

import ru.pichesky.rosneft.R;
import ru.pichesky.rosneft.helper.ToastHelper;
import ru.pichesky.rosneft.model.event.BaseEvent;
import ru.pichesky.rosneft.model.event.Event;
import ru.pichesky.rosneft.model.azs.EventWrapper;
import ru.pichesky.rosneft.utils.CONST;
import ru.pichesky.rosneft.utils.api.Api;

/**
 * Created by Roman Titov on 23.07.2015.
 */
public class EventItemActivity extends BaseActivity implements Response.ErrorListener {

    private BaseEvent mEvent;

    @Override
    protected void setData(Bundle savedInstanceState) {
        setTitle(R.string.event_title);
        if (getIntent().getExtras().getInt(CONST.PUSH_ITEM_ID) != 0)
            Api.getInstance().getEvent(getIntent().getExtras().getInt(CONST.PUSH_ITEM_ID),
                    getEventResponseListener(), this);
        else {
            mEvent = (BaseEvent) getIntent().getExtras().getSerializable(CONST.SELECTED_ITEM);
            fillFields();
        }
    }

    private void fillFields() {
        mAq.id(R.id.activity_product_title).text(mEvent.getName());
        mAq.id(R.id.activity_product_text).text(Html.fromHtml(mEvent.getText()));
        mAq.id(R.id.activity_product_image).image(mEvent.getImage());
    }

    private Response.Listener<JSONObject> getEventResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                EventWrapper wrapper = new Gson().fromJson(response.toString(), EventWrapper.class);
                if (wrapper.getErrorCode() == CONST.ERROR_CODE_OK)
                    if (wrapper.getList().size() > 0) {
                        mEvent = wrapper.getList().get(0);
                        fillFields();
                    }
            }
        };
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_product_item;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        ToastHelper.showToast(R.string.error_loading_data_try_again);
        finish();
        error.printStackTrace();
    }
}
