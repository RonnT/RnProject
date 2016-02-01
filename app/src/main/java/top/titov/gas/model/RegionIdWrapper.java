package top.titov.gas.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Dmitry on 19.10.2015.
 */
public class RegionIdWrapper {
    @SerializedName("error_code")
    private int mErrorCode;

    @SerializedName("error_name")
    private String mErrorName;

    private Data data;

    public int getRegionId() {
        return data.regionCode;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorName() {
        return mErrorName;
    }

    public class Data {
        @SerializedName("region_code")
        public int regionCode;
    }
}
