package ru.pichesky.rosneft.utils;

import android.location.Location;

import ru.pichesky.rosneft.extension.ObservableList;
import ru.pichesky.rosneft.interfaces.IObservable;
import ru.pichesky.rosneft.model.azs.Azs;

/**
 * Created by Andrew Vasilev on 19.01.2015.
 * Contain temp data
 */
public class DataManager {

    private String fuelType;

    private static class SingletonHolder {
        public static DataManager INSTANCE = new DataManager();

    }

    public static DataManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    //-------------------------------------

    public static boolean sDisableFragmentAnimations = false;

    private String searchQuery = "";
    private ObservableList<Azs> mAzsList = new ObservableList<>();
    private Location mMyLocation = null;
    private long mPriceDate = 0;

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public ObservableList<Azs> getAzsList(IObservable pObserver) {
        mAzsList.addObserver(pObserver);
        return mAzsList;
    }

    public ObservableList<Azs> getAzsList() {
        return mAzsList;
    }

    public void clearAll() {
        // TODO: clear all instances
    }

    public long getPriceDate() {
        return mPriceDate;
    }

    public void setPriceDate(long mPriceDate) {
        this.mPriceDate = mPriceDate;
    }

    public Location getMyLocation() {
        return mMyLocation;
    }

    public void setMyLocation(Location pMyLocation) {
        mMyLocation = pMyLocation;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }
}