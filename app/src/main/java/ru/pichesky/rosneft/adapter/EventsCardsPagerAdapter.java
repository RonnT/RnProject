package ru.pichesky.rosneft.adapter;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.io.Serializable;
import java.util.List;

import ru.pichesky.rosneft.fragment.events.EventPageFragment;
import ru.pichesky.rosneft.utils.CONST;

/**
 * Created by Roman Titov on 11.11.2015.
 */
public class EventsCardsPagerAdapter<T> extends FragmentStatePagerAdapter {

    private List<T> mEventsList;

    public EventsCardsPagerAdapter(FragmentManager fm, List<T> pEventsList){
        super(fm);
        mEventsList = pEventsList;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle args = new Bundle();
        args.putSerializable(CONST.TAG_EVENT, (Serializable) mEventsList.get(position));
        EventPageFragment eventPageFragment = new EventPageFragment<T>();
        eventPageFragment.setArguments(args);
        return eventPageFragment;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mEventsList.size();
    }
}
