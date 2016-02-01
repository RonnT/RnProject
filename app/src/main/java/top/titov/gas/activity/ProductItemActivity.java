package top.titov.gas.activity;

import android.os.Bundle;
import android.text.Html;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONObject;

import top.titov.gas.R;
import top.titov.gas.helper.ToastHelper;
import top.titov.gas.model.event.BaseEvent;
import top.titov.gas.model.ProductWrapper;
import top.titov.gas.utils.CONST;
import top.titov.gas.utils.api.Api;

/**
 * Created by Roman Titov on 23.07.2015.
 */
public class ProductItemActivity extends BaseActivity implements Response.ErrorListener {

    private BaseEvent mProduct;

    @Override
    protected void setData(Bundle savedInstanceState) {
        setTitle(R.string.product_title);
        if(getIntent().getExtras().getInt(CONST.PUSH_ITEM_ID) != 0)
            Api.getInstance().getProduct(getIntent().getExtras().getInt(CONST.PUSH_ITEM_ID),
                    getEventResponseListener(), this);
        else{
            mProduct = (BaseEvent) getIntent().getExtras().getSerializable(CONST.SELECTED_ITEM);
            fillFields();
        }
    }

    private void fillFields(){
        mAq.id(R.id.activity_product_title).text(mProduct.getName());
        mAq.id(R.id.activity_product_text).text(Html.fromHtml(mProduct.getText()));
        mAq.id(R.id.activity_product_image).image(mProduct.getImage());
    }

    private Response.Listener<JSONObject> getEventResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ProductWrapper wrapper = new Gson().fromJson(response.toString(), ProductWrapper.class);
                if (wrapper.getErrorCode() == CONST.ERROR_CODE_OK)
                    if (wrapper.getList().size() > 0) {
                        mProduct = wrapper.getList().get(0);
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
