package ru.pichesky.rosneft.model.azs;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Andrew Vasilev on 09.02.2015.
 */
public class SynchronizeWrapper {

    @SerializedName("error_code")
    private int mErrorCode;

    @SerializedName("error_name")
    private String mErrorName;

    private DataWrapper data;

    private class DataWrapper {
        private Update list;
    }

    private class Update {
        List<Azs> stations;
    }

    //-----

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorName() {
        return mErrorName;
    }

    public List<Azs> getAzsList() {

        return data.list.stations;
    }
}
