package top.titov.gas.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import top.titov.gas.model.event.Product;

/**
 * Created by Roman Titov on 22.07.2015.
 */
public class ProductWrapper {

    @SerializedName("error_code")
    private int mErrorCode;

    @SerializedName("error_name")
    private String mErrorName;

    private DataWrapper data;

    private class DataWrapper {
        private List<Product> list;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorName() {
        return mErrorName;
    }

    public List<Product> getList() {
        return data.list;
    }
}
