package top.titov.gas.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Dmitry on 12.10.2015.
 */
public class PushWrapper {
    @SerializedName("error_code")
    private int mErrorCode;

    @SerializedName("error_name")
    private String mErrorName;

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorName() {
        return mErrorName;
    }
}
