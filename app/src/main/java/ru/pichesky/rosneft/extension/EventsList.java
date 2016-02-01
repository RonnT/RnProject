package ru.pichesky.rosneft.extension;

import java.util.ArrayList;

/**
 * Created by Roman Titov on 24.07.2015.
 */
public class EventsList<T> extends ArrayList<T> {
    private int lastFederalPosition = -1;

    public int getLastFederalPosition() {
        return lastFederalPosition;
    }

    public void setLastFederalPosition(int lastFederalPosition) {
        this.lastFederalPosition = lastFederalPosition;
    }

    @Override
    public void clear() {
        lastFederalPosition = -1;
        super.clear();
    }
}
