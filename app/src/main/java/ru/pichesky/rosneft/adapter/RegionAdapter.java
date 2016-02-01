package ru.pichesky.rosneft.adapter;

import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import ru.pichesky.rosneft.R;
import ru.pichesky.rosneft.model.Region;

/**
 * Created by Roman Titov on 29.07.2015.
 */
public class RegionAdapter extends SimpleAdapter<Region> implements Filterable{

    List<Region> mFullList = new ArrayList<>();

    public RegionAdapter(List<Region> pList){
        super(pList);
        mFullList.addAll(pList);
    }

    @Override
    protected int getConvertViewId() {
        return R.layout.view_region_list_item;
    }

    @Override
    protected void fillHolderValues(Holder pHolder, int pPosition) {
        pHolder.aq.id(R.id.region_name).text(((Region) getItem(pPosition)).getName());
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<Region> results = new ArrayList<>();

                if (constraint != null) {
                    if (mFullList.size() > 0) {
                        String s = constraint.toString().toLowerCase();

                        for (final Region region : mFullList) {
                            if (region.getName().toLowerCase().contains(s) || s.length() == 0) {
                                results.add(region);

                            }
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mItems.clear();
                mItems.addAll((ArrayList<Region>) results.values);
                notifyDataSetChanged();
            }
        };
    }
}
