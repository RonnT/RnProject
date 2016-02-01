package ru.pichesky.rosneft.model;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * Created by Andrew Vasilev on 27.05.2015.
 */
public class MapMarker {

    private int type = -1;
    private String title = null;
    private String text = null;
    private BitmapDescriptor icon;
    private double lat = 0, lng = 0;

    private Object relatedObject = null;

    public MapMarker() {}

    public Object getRelatedObject() {
        return relatedObject;
    }

    public void setRelatedObject(Object relatedObject) {
        this.relatedObject = relatedObject;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public BitmapDescriptor getIcon() {
        return icon;
    }

    public void setIcon(BitmapDescriptor icon) {
        this.icon = icon;
    }

    public void setIcon(int pDrawableId) {
        icon = BitmapDescriptorFactory.fromResource(pDrawableId);
    }

    public void setIcon(Bitmap pBitmap) {
        icon = BitmapDescriptorFactory.fromBitmap(pBitmap);
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
