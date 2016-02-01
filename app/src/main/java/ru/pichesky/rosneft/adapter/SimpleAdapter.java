package ru.pichesky.rosneft.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.androidquery.AQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew Vasilev on 09.06.2015.
 */
public abstract class SimpleAdapter<T> extends BaseAdapter {

    protected List<T> mItems = new ArrayList<>();

    public SimpleAdapter(List<T> pItems) {
        mItems = pItems;
    }

    public SimpleAdapter() {}

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder h;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(getConvertViewId(), parent, false);
            h = new Holder(convertView); //initHolder(convertView);
            convertView.setTag(h);

        } else h = (Holder) convertView.getTag();

        fillHolderValues(h, position);

        return convertView;
    }

    protected class Holder {
        public AQuery aq;

        public Holder(View pView) {
            aq = new AQuery(pView);
        }
    }

    protected abstract int getConvertViewId();
    protected abstract void fillHolderValues(Holder pHolder, int pPosition);
}
