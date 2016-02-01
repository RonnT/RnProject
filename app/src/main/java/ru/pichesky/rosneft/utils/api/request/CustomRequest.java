package ru.pichesky.rosneft.utils.api.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ru.pichesky.rosneft.utils.CONST;


/**
 * Created by Alexander Smirnov on 28.11.2014.
 * CustomRequest can send data by POST
 */
public class CustomRequest extends Request<JSONObject> {

    private Listener<JSONObject> mListener;
    private Map<String, String> mParams;

    public CustomRequest(String url, Map<String, String> params,
                         Listener<JSONObject> responseListener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        mListener = responseListener;
        mParams = params;
    }

    public CustomRequest(int method, String url, Map<String, String> params,
                         Listener<JSONObject> responseListener, ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = responseListener;
        mParams = params;
    }

    @Override
    public Map<String, String> getParams()
            throws AuthFailureError {
        return mParams;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        // TODO Auto-generated method stub
        mListener.onResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();

        if (headers == null
                || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
            headers.put(CONST.INDEX_USER_AGENT, "Android");
        }
        return headers;
    }

}