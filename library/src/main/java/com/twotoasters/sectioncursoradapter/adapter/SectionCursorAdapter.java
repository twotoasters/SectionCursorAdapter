package com.twotoasters.sectioncursoradapter.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;

import com.twotoasters.sectioncursoradapter.exception.IllegalCursorMovementException;

import java.util.ArrayList;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * This adapter uses the ViewHolder class to completely handle view recycling for you.
 * This is similar to the new RecyclerViewAdapter.
 *
 * @param <T> section type.
 * @param <S> ViewHolder type for sections.
 * @param <H> ViewHolder type for items.
 */
public abstract class SectionCursorAdapter<T, S extends ViewHolder, H extends ViewHolder>  extends NonSectioningCursorAdapter implements SectionIndexer {

    private static final String ERROR_ILLEGAL_STATE = "IllegalStateException during build sections. "
            + "More then likely your cursor has been disconnected from the database, so your cursor will be set to null. "
            + "In most cases your content observer has already been notified of a database change and SectionCursorAdapter should get a new cursor shortly.";

    public static final int NO_CURSOR_POSITION = -99; // used when mapping section list position to cursor position

    protected static final int VIEW_TYPE_SECTION = 0;
    protected static final int VIEW_TYPE_ITEM = 1;

    private int mSectionLayoutResId;
    private int mItemLayoutResId;

    /**
     * You can no longer override notifyDataSetChanged() so an observer has to be registered instead.
     */
    private AdapterDataObserver observer;

    protected SortedMap<Integer, T> mSectionMap = new TreeMap<Integer, T>(); // should not be null
    ArrayList<Integer> mSectionList = new ArrayList<Integer>();
    private Object[] mFastScrollObjects;

    private LayoutInflater mLayoutInflater;

    public SectionCursorAdapter(Context context, Cursor cursor, int flags, int sectionLayoutResId, int itemLayoutResId) {
        super(context, cursor, flags, 0);
        init(context, null, sectionLayoutResId, itemLayoutResId);
    }

    protected SectionCursorAdapter(Context context, Cursor c, boolean autoRequery, int sectionLayoutResId, int itemLayoutResId, SortedMap<Integer, T> sections) {
        super(context, c, autoRequery, 0);
        init(context, sections, sectionLayoutResId, itemLayoutResId);
    }

    @Deprecated
    public SectionCursorAdapter(Context context, Cursor cursor, int sectionLayoutResId, int itemLayoutResId) {
        super(context, cursor, 0);
        init(context, null, sectionLayoutResId, itemLayoutResId);
    }

    private void init(Context context, SortedMap<Integer, T> sections, int sectionLayoutResId, int itemLayoutResId) {
        observer = new SectionCursorObserver();
        registerAdapterDataObserver(observer);

        this.mSectionLayoutResId = sectionLayoutResId;
        this.mItemLayoutResId = itemLayoutResId;
        mLayoutInflater = LayoutInflater.from(context);
        if (sections != null) {
            mSectionMap = sections;
        } else {
            buildSections();
        }
    }

    /**
     * @return an inflater to inflate your view with.
     */
    protected LayoutInflater getInflater() {
        return mLayoutInflater;
    }

    /**
     * If the adapter's cursor is not null then this method will call buildSections(Cursor cursor).
     */
    private void buildSections() {
        if (hasOpenCursor()) {
            Cursor cursor = getCursor();
            cursor.moveToPosition(-1);
            try {
                mSectionMap = buildSections(cursor);
            } catch (IllegalStateException e) {
                Log.w(SectionCursorAdapter.class.getName(), ERROR_ILLEGAL_STATE, e);
                swapCursor(null);
                mSectionMap = new TreeMap<Integer, T>();
                return;
            }
            if (mSectionMap == null) {
                mSectionMap = new TreeMap<Integer, T>();
            }
        }
    }

    /**
     * @param cursor a non-null cursor at position -1.
     * @return A map whose keys are the position at which a section is and values are an object
     * which will be passed to newSectionView and bindSectionView
     */
    protected SortedMap<Integer, T> buildSections(Cursor cursor) throws IllegalStateException {
        TreeMap<Integer, T> sections = new TreeMap<Integer, T>();
        int cursorPosition = 0;
        while (hasOpenCursor() && cursor.moveToNext()) {
            T section = getSectionFromCursor(cursor);
            if (cursor.getPosition() != cursorPosition)
                throw new IllegalCursorMovementException("Do no move the cursor's position in getSectionFromCursor.");
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
    protected abstract T getSectionFromCursor(Cursor cursor) throws IllegalStateException;

    ////////////////////////////////////////
    // Implemented Abstract/Overrode methods
    ////////////////////////////////////////


    @Override
    /**
     * @return How many items are in the data set represented by this Adapter.
     */
    public int getItemCount() {
        return super.getItemCount() + mSectionMap.size();
    }

    /**
     * @param listPosition the position of the current item in the list with mSectionMap included
     * @return If the position is a section it will return the value for the position from the section map.
     * Otherwise it will convert to the cursor position and return super.
     */
    @Override
    public Object getItem(int listPosition) {
        if (isSection(listPosition))
            return mSectionMap.get(listPosition);
        else
            return super.getItem(getCursorPositionWithoutSections(listPosition));
    }

    /**
     * @param listPosition the position of the current item in the list with mSectionMap included
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

    /////////////////
    // Managing Data
    /////////////////

    /**
     * @return True if cursor is not null and open.
     * If the cursor is closed a null cursor will be swapped out.
     */
    protected boolean hasOpenCursor() {
        Cursor cursor = getCursor();
        if (cursor != null && cursor.isClosed()) {
            swapCursor(null);
            return false;
        }
        return cursor != null;
    }

    /////////////////
    // Positions
    /////////////////

    /**
     * @return all of the positions that sections are at.
     */
    public Set<Integer> getSectionListPositions() {
        return mSectionMap.keySet();
    }

    /**
     * @return the section for the given sectionPosition.
     * Null will be returned if the section doesn't exist.
     */
    public T getSection(int sectionPosition) {
        if (mSectionList.contains(sectionPosition)) {
            return mSectionMap.get(mSectionList.get(sectionPosition));
        }
        return null;
    }

    /**
     *
     * @param listPosition  the position of the current item in the list with mSectionMap included
     * @return Whether or not the listPosition points to a section.
     */
    public boolean isSection(int listPosition) {
        return mSectionMap.containsKey(listPosition);
    }

    /**
     * This will map a position in the list adapter (which includes mSectionMap) to a position in
     * the cursor (which does not contain mSectionMap).
     *
     * @param listPosition the position of the current item in the list with mSectionMap included
     * @return the correct position to use with the cursor
     */
    public int getCursorPositionWithoutSections(int listPosition) {
        if (mSectionMap.size() == 0) {
            return listPosition;
        } else if (!isSection(listPosition)) {
            int sectionIndex = getSectionPosition(listPosition);
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
     * @param listPosition the position of the current item in the list with mSectionMap included
     * @return an index in an ordered list of section names
     */
    public int getSectionPosition(int listPosition) {
        boolean isSection = false;
        int numPrecedingSections = 0;
        for (Integer sectionPosition : mSectionMap.keySet()) {
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
        boolean hasSections = mSectionMap != null && mSectionMap.size() > 0;
        return sectionIndex == 0 && hasSections && listPosition < mSectionMap.firstKey();
    }

    /////////////////
    // Views
    /////////////////

    /**
     * @param listPosition
     * @return Get the type of View that will be created by getView(int, View, ViewGroup) for the specified item.
     */
    @Override
    public int getItemViewType(int listPosition) {
        return isSection(listPosition) ? VIEW_TYPE_SECTION : VIEW_TYPE_ITEM;
    }

    @Override
    protected final View onNewView(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SECTION) {
            return onNewSectionView(parent);
        } else {
            return onNewItemView(parent);
        }
    }

    protected View onNewSectionView(ViewGroup parent) {
        return getInflater().inflate(mSectionLayoutResId, parent, false);
    }

    protected View onNewItemView(ViewGroup parent) {
        return getInflater().inflate(mItemLayoutResId, parent, false);
    }

    @Override
    protected final ViewHolder onCreateViewHolder(View view, ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SECTION) {
            return onCreateSectionViewHolder(view, parent);
        }
        return onCreateItemViewHolder(view, parent, viewType);
    }

    @Override
    public final void onBindViewHolder(ViewHolder holder, int position) {
        if (isSection(position)) {
            T section = mSectionMap.get(position);
            onBindSectionViewHolder((S) holder, position, section);
        } else {
            int newPosition = getCursorPositionWithoutSections(position);
            Cursor cursor = getCursor();
            if (!cursor.moveToPosition(newPosition)) {
                throw new IllegalStateException("couldn't move cursor to position " + newPosition);
            }

            onBindItemViewHolder((H) holder, cursor);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, Cursor cursor) {
        throw new IllegalAccessError("Please use onBindItemViewHolder or onBindSectionViewHolder.");
    }

    protected abstract S onCreateSectionViewHolder(View view, ViewGroup parent);
    protected abstract void onBindSectionViewHolder(S holder, int position, T section);
    protected abstract H onCreateItemViewHolder(View view, ViewGroup parent, int viewType);
    protected abstract void onBindItemViewHolder(H holder, Cursor cursor);

    private class SectionCursorObserver extends AdapterDataObserver {

        /**
         * Clears out all section data before rebuilding it.
         */
        @Override
        public void onChanged() {
            if (hasOpenCursor()) {
                buildSections();
                mFastScrollObjects = null;
                mSectionList.clear();
            }
            super.onChanged();
        }
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
            for (Integer key : mSectionMap.keySet()) {
                mSectionList.add(key);
            }
        }
        return sectionIndex < mSectionList.size() ? mSectionList.get(sectionIndex) : getItemCount();
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
        int sectionIndex = getSectionPosition(position);

        return sectionIndex < objects.length ? sectionIndex : 0;
    }

    /**
     * Returns an array of objects representing mSectionMap of the list. The
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
            mFastScrollObjects = getFastScrollDialogLabels();
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
     * @return The values which for the sections which will be shown in the fast scroll dialog.
     * As the only a max of three letters can fit in this dialog before KitKat,
     * the string value will be trimmed according to to length specified in getMaxIndexerLength().
     */
    private Object[] getFastScrollDialogLabels() {
        if (mSectionMap == null) return new Object[] { };

        int sectionCount = mSectionMap.size();
        String[] titles = new String[sectionCount];

        int max = VERSION.SDK_INT < VERSION_CODES.KITKAT ? getMaxIndexerLength() : Integer.MAX_VALUE;
        int i = 0;
        for (Object object : mSectionMap.values()) {
            if (object == null) {
                titles[i] = "";
            } else if (object.toString().length() >= max) {
                titles[i] = object.toString().substring(0, max);
            } else {
                titles[i] = object.toString();
            }
            i++;
        }
        return titles;
    }
}
