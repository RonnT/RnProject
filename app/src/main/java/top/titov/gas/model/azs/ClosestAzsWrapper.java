package top.titov.gas.model.azs;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Roman Titov on 02.11.2015.
 */
public class ClosestAzsWrapper {

    @SerializedName("error_code")
    private int mErrorCode;

    @SerializedName("error_name")
    private String mErrorName;

    private DataWrapper data;

    private class DataWrapper {
        private List<Azs> list;
    }

    //-----

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorName() {
        return mErrorName;
    }

    public List<Azs> getAzsList() {

        return data.list;
    }
}
