package ru.pichesky.rosneft.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Dmitry on 19.10.2015.
 */
public class SimpleWrapper {
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
