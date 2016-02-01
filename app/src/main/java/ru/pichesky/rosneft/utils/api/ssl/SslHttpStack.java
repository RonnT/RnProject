package ru.pichesky.rosneft.utils.api.ssl;
/**
 * Copyright 2013 Mani Selvaraj
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.pichesky.rosneft.utils.api.request.CustomRequest;
import ru.pichesky.rosneft.utils.api.request.MultiPartRequest;


/**
 * Custom implementation of com.android.volley.toolboox.HttpStack
 * Uses apache HttpClient-4.2.5 jar to take care of . You can download it from here
 * http://hc.apache.org/downloads.cgi
 *
 * @author Mani Selvaraj
 */
public class SslHttpStack implements HttpStack {

    private boolean mIsConnectingToYourServer = false;
    private final static String HEADER_CONTENT_TYPE = "Content-Type";
    static final ContentType CONTENT_TYPE = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);

    public SslHttpStack(boolean isYourServer) {
        mIsConnectingToYourServer = isYourServer;
    }

    private static void addHeaders(HttpUriRequest httpRequest, Map<String, String> headers) {
        for (String key : headers.keySet()) {
            httpRequest.setHeader(key, headers.get(key));
        }
    }

    @SuppressWarnings("unused")
    private static List<NameValuePair> getPostParameterPairs(Map<String, String> postParams) {
        List<NameValuePair> result = new ArrayList<>(postParams.size());
        for (String key : postParams.keySet()) {
            result.add(new BasicNameValuePair(key, postParams.get(key)));
        }
        return result;
    }

    @Override
    public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders)
            throws IOException, AuthFailureError {
        HttpUriRequest httpRequest = createHttpRequest(request);
        addHeaders(httpRequest, additionalHeaders);
        addHeaders(httpRequest, request.getHeaders());
        onPrepareRequest(httpRequest);
        HttpParams httpParams = httpRequest.getParams();
        int timeoutMs = request.getTimeoutMs();
        // TODO: Reevaluate this connection timeout based on more wide-scale
        // data collection and possibly different for wifi vs. 3G.
        HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
        HttpConnectionParams.setSoTimeout(httpParams, timeoutMs);
        /* Register schemes, HTTP and HTTPS */
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", new PlainSocketFactory(), 80));

        /* TODO: commented for sending files. uncomment when https will be enabled
        registry.register(new Scheme("https",
                new EasySSLSocketFactory(mIsConnectingToYourServer), 443));
                */

        /* Make a thread safe connection manager for the client */
        ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(httpParams, registry);
        HttpClient httpClient = new DefaultHttpClient(manager, httpParams);

        return httpClient.execute(httpRequest);
    }

    /**
     * Creates the appropriate subclass of HttpUriRequest for passed in request.
     */
    static HttpUriRequest createHttpRequest(Request<?> request) throws AuthFailureError {
        switch (request.getMethod()) {
            case Method.DEPRECATED_GET_OR_POST: {
                // This is the deprecated way that needs to be handled for backwards compatibility.
                // If the request's post body is null, then the assumption is that the request is
                // GET.  Otherwise, it is assumed that the request is a POST.
                byte[] postBody = request.getPostBody();
                if (postBody != null) {
                    HttpPost postRequest = new HttpPost(request.getUrl());
                    postRequest.addHeader(HEADER_CONTENT_TYPE, request.getPostBodyContentType());
                    HttpEntity entity;
                    entity = new ByteArrayEntity(postBody);
                    postRequest.setEntity(entity);
                    return postRequest;
                } else {
                    return new HttpGet(request.getUrl());
                }
            }
            case Method.GET:
                return new HttpGet(request.getUrl());
            case Method.DELETE:
                return new HttpDelete(request.getUrl());
            case Method.POST: {
                HttpPost postRequest = new HttpPost(request.getUrl());
                setMultiPartBody(postRequest, request);
                if (postRequest.getEntity() == null) setEntityIfNonEmptyBody(postRequest, request);
                return postRequest;
            }
            case Method.PUT: {
                HttpPut putRequest = new HttpPut(request.getUrl());
                putRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
                setMultiPartBody(putRequest, request);
                setEntityIfNonEmptyBody(putRequest, request);
                return putRequest;
            }
            // Added in source code of Volley library.
            case Method.PATCH: {
                HttpClientStack.HttpPatch patchRequest =
                        new HttpClientStack.HttpPatch(request.getUrl());
                patchRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
                setEntityIfNonEmptyBody(patchRequest, request);
                return patchRequest;
            }
            default:
                throw new IllegalStateException("Unknown request method.");
        }
    }

    private static void setEntityIfNonEmptyBody(HttpEntityEnclosingRequestBase httpRequest,
                                                Request<?> request) throws AuthFailureError {
        if (request instanceof CustomRequest) {
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create().setLaxMode();
            CustomRequest customRequest = (CustomRequest) request;

            for (Map.Entry<String, String> entry : customRequest.getParams().entrySet()) {
                entityBuilder.addTextBody(entry.getKey(), entry.getValue(), CONTENT_TYPE);
            }

            httpRequest.setEntity(entityBuilder.build());
        } else {
            byte[] body = request.getBody();
            if (body != null) {
                HttpEntity entity = new ByteArrayEntity(body);
                httpRequest.setEntity(entity);
            }
        }
    }

    /**
     * If Request is MultiPartRequest type, then set MultiPartEntity in the httpRequest object.
     *
     * @throws AuthFailureError
     */
    private static void setMultiPartBody(HttpEntityEnclosingRequestBase httpRequest,
                                         Request<?> request) throws AuthFailureError {

        // Return if Request is not MultiPartRequest
        if (!(request instanceof MultiPartRequest)) return;

        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create().setLaxMode();
        //Iterate the fileUploads
        Map<String, List<File>> fileUpload = ((MultiPartRequest) request).getFileUploads();
        for (Map.Entry<String, List<File>> entry : fileUpload.entrySet()) {
            for (File file: entry.getValue()) {
                entityBuilder.addPart(entry.getKey(), new FileBody(file));
            }
        }

        //Iterate the stringUploads
        Map<String, String> stringUpload = ((MultiPartRequest) request).getStringUploads();
        for (Map.Entry<String, String> entry : stringUpload.entrySet()) {
            try {
                entityBuilder.addTextBody(entry.getKey(), entry.getValue(), CONTENT_TYPE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        httpRequest.setEntity(entityBuilder.build());
    }

    /**
     * Called before the request is executed using the underlying HttpClient.
     * <p/>
     * <p>Overwrite in subclasses to augment the request.</p>
     */
    protected void onPrepareRequest(HttpUriRequest request) throws IOException {
        // Nothing.
    }

}
