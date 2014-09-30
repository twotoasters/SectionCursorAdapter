package com.twotoasters.sectioncursoradapter.util;

import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;

public class ListAdapterUtils {

    private ListAdapterUtils() { }

    /**
     * @return listAdapter that is wrapped in a HeaderViewListAdapter.
     * If your adapter is not wrapped then it will just return your adapter.
     */
    public static ListAdapter getWrappedAdapter(AdapterView<?> parent) {

        if (parent.getAdapter() instanceof HeaderViewListAdapter) {
            return ((HeaderViewListAdapter) parent.getAdapter()).getWrappedAdapter();
        } else {
            return (ListAdapter) parent.getAdapter();
        }
    }

    /**
     * return adjusted position if your AdapterView is using a HeaderViewListAdapter so that headers at not counted.
     * If it is not a HeaderViewListAdapter then the position handed in will be returned.
     */
    public static int getHeaderAdjustedPosition(AdapterView<?> parent, int position) {

        if (parent.getAdapter() instanceof HeaderViewListAdapter) {
            return position - ((HeaderViewListAdapter) parent.getAdapter()).getHeadersCount();
        }
        return position;
    }
}
