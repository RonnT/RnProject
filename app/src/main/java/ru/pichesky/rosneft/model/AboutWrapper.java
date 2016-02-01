package ru.pichesky.rosneft.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Programmer16 on 28.07.2015.
 */
public class AboutWrapper {

    @SerializedName("error_code")
    private int mErrorCode;

    @SerializedName("error_name")
    private String mErrorName;

    private About data;

    public About getAbout() { return data; }

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorName() {
        return mErrorName;
    }
}
