package top.titov.gas.model.azs;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Dmitry on 30.10.2015.
 */
public class PriceItemWrapper {
    @SerializedName("error_code")
    private int mErrorCode;

    public int getmErrorCode() {
        return mErrorCode;
    }

    public String getmErrorName() {
        return mErrorName;
    }

    public List<PriceItem> getData() {
        return data.list;
    }

    @SerializedName("error_name")
    private String mErrorName;

    private DataWrapper data;

    private class DataWrapper {
        private List<PriceItem> list;
    }

    public List<PriceItem> getPriceItemList() {
        return data.list;
    }
}
