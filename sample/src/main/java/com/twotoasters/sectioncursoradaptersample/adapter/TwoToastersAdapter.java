package com.twotoasters.sectioncursoradaptersample.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

import com.twotoasters.sectioncursoradapter.SectionCursorAdapter;

import java.util.SortedMap;

public class TwoToastersAdapter extends SectionCursorAdapter {

    public TwoToastersAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    protected SortedMap<Integer, Object> buildSections(Cursor cursor) {
        return null;
    }

    @Override
    protected View newSectionView(Context context, Object item, ViewGroup parent) {
        return null;
    }

    @Override
    protected void bindSectionView(View convertView, Context context, int position, Object item) {

    }

    @Override
    protected View newItemView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    protected void bindItemView(View convertView, Context context, Cursor cursor) {

    }
}
