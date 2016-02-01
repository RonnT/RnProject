package ru.pichesky.rosneft.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yagupov Ruslan on 27.10.2015.
 */
public class DataWrapper<T> {

    @SerializedName("error_code")
    private int mErrorCode;

    @SerializedName("error_name")
    private String mErrorName;

    private T data;

    public T getData() { return data; }

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorName() {
        return mErrorName;
    }
}
