package top.titov.gas.utils.api.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * MultipartRequest - To handle the large file uploads.
 * Extended from JSONRequest. You might want to change to StringRequest based on your response type.
 *
 * @author Mani Selvaraj
 */
public class MultiPartRequest extends JsonRequest<JSONObject> {

    /* To hold the parameter name and the File to upload */
    private Map<String, List<File>> fileUploads = new HashMap<>();

    /* To hold the parameter name and the string content to upload */
    private Map<String, String> stringUploads = new HashMap<>();

    private Map<String, String> headers = new HashMap<>();

    /**
     * Creates a new request.
     *
     * @param method        the HTTP method to use
     * @param url           URL to fetch the JSON from
     * @param jsonRequest   A {@link JSONObject} to post with the request. Null is allowed and
     *                      indicates no parameters will be posted along with request.
     * @param listener      Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public MultiPartRequest(int method, String url, JSONObject jsonRequest,
                            Listener<JSONObject> listener, ErrorListener errorListener) {
        super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener,
                errorListener);
    }

    /**
     * Constructor which defaults to <code>GET</code> if <code>jsonRequest</code> is
     * <code>null</code>, <code>POST</code> otherwise.
     * <p/>
     * //@see #JsonObjectRequest(int, String, JSONObject, Listener, ErrorListener)
     */
    public MultiPartRequest(String url, JSONObject jsonRequest, Listener<JSONObject> listener,
                            ErrorListener errorListener) {
        this(jsonRequest == null ? Method.GET : Method.POST, url, jsonRequest,
                listener, errorListener);
    }

    /*
    public void addPhoto(List<Photo> pPhoto) {
        if (pPhoto == null) return;

        for (Photo photo : pPhoto) {
            String photoPath = photo.getStoragePath();
            File photoFile = new File(photoPath);
            if (photoFile.exists()) addFileUpload("images[]", new File(photoPath));
        }
    }
    */

    public void addStrings(Map<String, String> pParams) {
        if (pParams == null) return;

        for (Map.Entry<String, String> entry : pParams.entrySet()) {
            addStringUpload(entry.getKey(), entry.getValue());
        }
    }

    public void addFileUpload(String param, File file) {
        if (fileUploads == null) fileUploads = new HashMap<>();

        if (fileUploads.containsKey(param)) {
            fileUploads.get(param).add(file);
        } else {
            List<File> list = new ArrayList<>();
            list.add(file);
            fileUploads.put(param, list);
        }
    }

    public void addStringUpload(String param, String content) {
        stringUploads.put(param, content);
    }

    public Map<String, List<File>> getFileUploads() {
        return fileUploads;
    }

    public Map<String, String> getStringUploads() {
        return stringUploads;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    public void setHeader(String title, String content) {
        headers.put(title, content);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString =
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

}