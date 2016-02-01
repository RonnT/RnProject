package top.titov.gas.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Andrew Vasilev on 09.02.2015.
 */
public class BalancerWrapper {

    @SerializedName("error_code")
    private int mErrorCode;

    @SerializedName("error_name")
    private String mErrorName;

    private DataWrapper data;

    private class DataWrapper {
        private String host, uri, proto;
    }

    //-----

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorName() {
        return mErrorName;
    }

    public String getHost() {
        return data.host;
    }

    public String getUri() {
        return data.uri;
    }

    public String getProto() {
        return data.proto;
    }
}
