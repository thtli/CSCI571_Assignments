package com.example.productsearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

public class AutoZipAdapter extends ArrayAdapter<String> implements Filterable {
    private List<String> mZipData;

    public AutoZipAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        mZipData = new ArrayList<>();
    }

    public void setData(List<String> list) {
        mZipData.clear();
        mZipData.addAll(list);
    }

    @Override
    public int getCount() {
        return mZipData.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return mZipData.get(position);
    }

    // return the full object from adapter
    public String getObject(int position) {
        return mZipData.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter dataFilter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    filterResults.values = mZipData;
                    filterResults.count = mZipData.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && (results.count > 0)) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return dataFilter;
    }
}
