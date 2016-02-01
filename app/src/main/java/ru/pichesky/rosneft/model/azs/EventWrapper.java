package ru.pichesky.rosneft.model.azs;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.pichesky.rosneft.model.event.Event;

/**
 * Created by Roman Titov on 24.07.2015.
 */
public class EventWrapper {

    @SerializedName("error_code")
    private int mErrorCode;

    @SerializedName("error_name")
    private String mErrorName;

    private DataWrapper data;

    private class DataWrapper {
        private List<Event> list;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorName() {
        return mErrorName;
    }

    public List<Event> getList() {
        return data.list;
    }
}
