package com.twotoasters.sectioncursoradapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

public abstract class SectionCursorAdapter extends CursorAdapter implements SectionIndexer {

    public static final int NO_CURSOR_POSITION = -99; // used when mapping section list position to cursor position

    protected static final int VIEW_TYPE_SECTION = 0;
    protected static final int VIEW_TYPE_ITEM = 1;

    protected SortedMap<Integer, Object> mSections = new TreeMap<Integer, Object>(); // should not be null
    ArrayList<Integer> mSectionList = new ArrayList<Integer>();
    private Object[] mFastScrollObjects;

    private LayoutInflater mLayoutInflater;

    public SectionCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        init(context, null);
    }

    protected SectionCursorAdapter(Context context, Cursor c, boolean autoRequery, SortedMap<Integer, Object> sections) {
        super(context, c, autoRequery);
        init(context, sections);
    }

    @Deprecated
    public SectionCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        init(context, null);
    }

    private void init(Context context, SortedMap<Integer, Object> sections) {
        mLayoutInflater = LayoutInflater.from(context);
        if (sections != null) {
            mSections = sections;
        } else {
            buildSections();
        }
    }

    /**
     * @return an inflater to inflate your view with.
     */
    protected LayoutInflater getLayoutInflater() {
        return mLayoutInflater;
    }

    /**
     * If the adapter's cursor is not null then this method will call buildSections(Cursor cursor).
     */
    private void buildSections() {
        if (hasOpenCursor()) {
            Cursor cursor = getCursor();
            cursor.moveToPosition(-1);
            mSections = buildSections(cursor);
            if (mSections == null) {
                mSections = new TreeMap<Integer, Object>();
            }
        }
    }

    /**
     * @param cursor a non-null cursor at position -1.
     * @return A map whose keys are the position at which a section is and values are an object
     * which will be passed to newSectionView and bindSectionView
     */
    protected SortedMap<Integer, Object> buildSections(Cursor cursor) {
        TreeMap<Integer, Object> sections = new TreeMap<Integer, Object>();
        int cursorPosition = 0;
        while (cursor.moveToNext()) {
            Object section = getSectionFromCursor(cursor);
            if (cursor.getPosition() != cursorPosition)
                throw new IllegalStateException("Do no move the cursor's position in getSectionFromCursor.");
            if (!sections.containsValue(section))
                sections.put(cursorPosition + sections.size(), section);
            cursorPosition++;
        }
        return sections;
    }

    /**
     * The object which is return will determine what section this cursor position will be in.
     * @param cursor
     * @return the section from the cursor at its current position.
     * This object will be passed to newSectionView and bindSectionView.
     */
    protected abstract Object getSectionFromCursor(Cursor cursor);

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        boolean isSection = isSection(position);
        Context context = parent.getContext();
        Cursor cursor = getCursor();
        View view;

        if (!isSection) {
            int newPosition = getCursorPositionWithoutSections(position);
            if (!cursor.moveToPosition(newPosition)) {
                throw new IllegalStateException("couldn't move cursor to position " + newPosition);
            }
        }

        if (convertView == null) {
            view = isSection ? newSectionView(context, getItem(position), parent)
                    : newItemView(context, cursor, parent);
        } else {
            view = convertView;
        }

        if (isSection) {
            bindSectionView(view, context, position, getItem(position));
        } else {
            bindItemView(view, context, cursor);
        }

        return view;
    }

    @Override
    @Deprecated
    /**
     * This method is from the CursorAdapter and will never be called.
     */
    public final View newView(Context context, Cursor cursor, ViewGroup parent) {
        throw new IllegalStateException("This method is not used by " + SectionCursorAdapter.class.getSimpleName());
    }

    @Override
    @Deprecated
    /**
     * This method is from the CursorAdapter and will never be called.
     */
    public final void bindView(View view, Context context, Cursor cursor) {
        throw new IllegalStateException("This method is not used by " + SectionCursorAdapter.class.getSimpleName());
    }

    /**
     * Creates a new section view.
     * @param context Interface to application's global information
     * @param item is the item stored in the sorted map for the section header.
     * @param parent The parent to which the new view is attached.
     * @return
     */
    protected abstract View newSectionView(Context context, Object item, ViewGroup parent);

    /**
     * Binds data to an existing view.
     * @param convertView Existing view, returned earlier by newView
     * @param context Interface to application's global information
     * @param position
     * @param item is the item stored in the sorted map for the section header.
     */
    protected abstract void bindSectionView(View convertView, Context context, int position, Object item);

    /**
     * Creates a new item view to use within a section.
     * @param cursor The cursor from which to get the data. The cursor is already moved to the correct position.
     * @param parent The parent to which the new view is attached to
     * @return
     */
    protected abstract View newItemView(Context context, Cursor cursor, ViewGroup parent);

    /**
     * Binds data to an item view
     * @param convertView Existing view, returned earlier by newView
     * @param context Interface to application's global information
     * @param cursor The cursor from which to get the data. The cursor is already moved to the correct position.
     */
    protected abstract void bindItemView(View convertView, Context context, Cursor cursor);

    /**
     *
     * @param listPosition  the position of the current item in the list with mSections included
     * @return Whether or not the listPosition points to a section.
     */
    public boolean isSection(int listPosition) {
        return mSections.containsKey(listPosition);
    }

    /**
     * This will map a position in the list adapter (which includes mSections) to a position in
     * the cursor (which does not contain mSections).
     *
     * @param listPosition the position of the current item in the list with mSections included
     * @return the correct position to use with the cursor
     */
    public int getCursorPositionWithoutSections(int listPosition) {
        if (mSections.size() == 0) {
            return listPosition;
        } else if (!isSection(listPosition)) {
            int sectionIndex = getIndexWithinSections(listPosition);
            if (isListPositionBeforeFirstSection(listPosition, sectionIndex)) {
                return listPosition;
            } else {
                return listPosition - (sectionIndex + 1);
            }
        } else {
            return NO_CURSOR_POSITION;
        }
    }

    /**
     * Finds the section index for a given list position.
     *
     * @param listPosition the position of the current item in the list with mSections included
     * @return an index in an ordered list of section names
     */
    public int getIndexWithinSections(int listPosition) {
        boolean isSection = false;
        int numPrecedingSections = 0;
        for (Integer sectionPosition : mSections.keySet()) {
            if (listPosition > sectionPosition)
                numPrecedingSections++;
            else if (listPosition == sectionPosition)
                isSection = true;
            else
                break;
        }
        return isSection ? numPrecedingSections : Math.max(numPrecedingSections - 1, 0);
    }

    private boolean isListPositionBeforeFirstSection(int listPosition, int sectionIndex) {
        boolean hasSections = mSections != null && mSections.size() > 0;
        return sectionIndex == 0 && hasSections && listPosition < mSections.firstKey();
    }

    /**
     * Clears out all section data before rebuilding it.
     */
    @Override
    public void notifyDataSetChanged() {
        if (hasOpenCursor()) {
            buildSections();
            mFastScrollObjects = null;
            mSectionList.clear();
        }
        super.notifyDataSetChanged();
    }

    /**
     * Clears out all section data before rebuilding it.
     */
    @Override
    public void notifyDataSetInvalidated() {
        if (hasOpenCursor()) {
            buildSections();
            mFastScrollObjects = null;
            mSectionList.clear();
        }
        super.notifyDataSetInvalidated();
    }

    /**
     * @param listPosition the position of the current item in the list with mSections included
     * @return If the position is a section it will return the value for the position from the section map.
     * Otherwise it will convert to the cursor position and return super.
     */
    @Override
    public Object getItem(int listPosition) {
        if (isSection(listPosition))
            return mSections.get(listPosition);
        else
            return super.getItem(getCursorPositionWithoutSections(listPosition));
    }

    /**
     * @param listPosition the position of the current item in the list with mSections included
     * @return If the position is a section it will return the value for the position from the section map.
     * Otherwise it will return the _id column value.
     */
    @Override
    public long getItemId(int listPosition) {
        if (isSection(listPosition))
            return listPosition;
        else {
            int cursorPosition = getCursorPositionWithoutSections(listPosition);
            Cursor cursor = getCursor();
            if (hasOpenCursor() && cursor.moveToPosition(cursorPosition)) {
                return cursor.getLong(cursor.getColumnIndex("_id"));
            }
            return NO_CURSOR_POSITION;
        }
    }

    /**
     * @return How many items are in the data set represented by this Adapter.
     */
    @Override
    public int getCount() {
        return super.getCount() + mSections.size();
    }

    /**
     * @param listPosition
     * @return Get the type of View that will be created by getView(int, View, ViewGroup) for the specified item.
     */
    @Override
    public int getItemViewType(int listPosition) {
        return isSection(listPosition) ? VIEW_TYPE_SECTION : VIEW_TYPE_ITEM;
    }

    /**
     * @return Returns the number of types of Views that will be created by getView(int, View, ViewGroup).
     */
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /**
     * @return True if cursor is not null and open.
     */
    protected boolean hasOpenCursor() {
        Cursor cursor = getCursor();
        return cursor != null && !cursor.isClosed();
    }

    ////////////////////////////////////
    // Methods for the SectionIndexer
    ////////////////////////////////////

    /**
     * Given the index of a section within the array of section objects, returns
     * the starting position of that section within the adapter.
     *
     * If the section's starting position is outside of the adapter bounds, the
     * position must be clipped to fall within the size of the adapter.
     *
     * @param sectionIndex the index of the section within the array of section
     *            objects
     * @return the starting position of that section within the adapter,
     *         constrained to fall within the adapter bounds
     */
    @Override
    public int getPositionForSection(int sectionIndex) {
        if (mSectionList.size() == 0) {
            for (Integer key : mSections.keySet()) {
                mSectionList.add(key);
            }
        }
        return sectionIndex < mSectionList.size() ? mSectionList.get(sectionIndex) : getCount();
    }

    /**
     * Given a position within the adapter, returns the index of the
     * corresponding section within the array of section objects.
     *
     * If the section index is outside of the section array bounds, the index
     * must be clipped to fall within the size of the section array.
     *
     * For example, consider an indexer where the section at array index 0
     * starts at adapter position 100. Calling this method with position 10,
     * which is before the first section, must return index 0.
     *
     * @param position the position within the adapter for which to return the
     *            corresponding section index
     * @return the index of the corresponding section within the array of
     *         section objects, constrained to fall within the array bounds
     */
    @Override
    public int getSectionForPosition(int position) {
        Object[] objects = getSections(); // the fast scroll section objects
        int sectionIndex = getIndexWithinSections(position);

        return sectionIndex < objects.length ? sectionIndex : 0;
    }

    /**
     * Returns an array of objects representing mSections of the list. The
     * returned array and its contents should be non-null.
     *
     * The list view will call toString() on the objects to get the preview text
     * to display while scrolling. For example, an adapter may return an array
     * of Strings representing letters of the alphabet. Or, it may return an
     * array of objects whose toString() methods return their section titles.
     *
     * @return the array of section objects
     */
    @Override
    public Object[] getSections() {
        if (mFastScrollObjects == null) {
            mFastScrollObjects = getValues();
        }
        return mFastScrollObjects;
    }

    /**
     * This only affects SDK < 19.
     * Override this to control the amount of characters the fast scroll dialog can display.
     */
    protected int getMaxIndexerLength() {
        return 3;
    }

    /**
     * The fast scroll dialog only fits a max of 3 letters before KitKat.
     */
    private Object[] getValues() {
        Collection<Object> sectionsCollection = mSections.values();
        Object[] objects = sectionsCollection.toArray(new Object[sectionsCollection.size()]);
        if (VERSION.SDK_INT < VERSION_CODES.KITKAT) {
            int max = getMaxIndexerLength();
            for (int i = 0; i < objects.length; i++) {
                if (objects[i].toString().length() >= max) {
                    objects[i] = objects[i].toString().substring(0, max);
                }
            }
        }
        return objects;
    }
}
