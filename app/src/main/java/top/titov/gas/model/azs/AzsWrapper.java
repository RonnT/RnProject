package top.titov.gas.model.azs;

import com.google.gson.annotations.SerializedName;


/**
 * Created by Roman Titov on 03.11.2015.
 */
public class AzsWrapper {

    @SerializedName("error_code")
    private int mErrorCode;

    @SerializedName("error_name")
    private String mErrorName;

    private Data data;

    private class Data{
        private Azs station;
    }
    public Azs getAzs(){
        return data.station;
    }
}
