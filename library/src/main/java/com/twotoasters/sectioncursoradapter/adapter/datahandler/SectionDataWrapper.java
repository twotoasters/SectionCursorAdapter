package com.twotoasters.sectioncursoradapter.adapter.datahandler;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.widget.SectionIndexer;

import com.twotoasters.sectioncursoradapter.adapter.datahandler.DataHandler.DataChangeListener;

import java.util.ArrayList;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class SectionDataWrapper<S, T, D extends DataHandler<T>> extends DataWrapper<T, D> implements DataChangeListener, SectionIndexer {
    private SectionBuilder<S, T, D> mSectionBuilder;
    private SortedMap<Integer, S> mSectionMap;
    private final ArrayList<Integer> mSectionList = new ArrayList<Integer>();
    private Object[] mFastScrollObjects;

    public SectionDataWrapper(D wrapped) {
        super(wrapped);
        wrapped.registerObservable(this);
    }

    public void setSectionBuilder(SectionBuilder<S, T, D> sectionBuilder) {
        this.mSectionBuilder = sectionBuilder;
        buildSections();
    }

    @Override
    public void onDataChanged() {
        buildSections();
    }

    private void buildSections() {
        if (mSectionMap != null) mSectionMap.clear();
        mSectionList.clear();

        if (mSectionBuilder != null) {
            mSectionMap = buildSectionMap();
            mSectionList.addAll(mSectionMap.keySet());
        }
    }

    /**
     * @return A map whose keys are the position at which a section is and values are an object
     * which will be passed to newSectionView and bindSectionView
     */
    private SortedMap<Integer, S> buildSectionMap() {
        SortedMap<Integer, S> sections  = mSectionBuilder.buildSections(getWrapped());
        if (sections != null) return new TreeMap<>(sections);

        sections = new TreeMap<>();
        int size = super.getItemCount();
        for (int i = 0; i < size; i++) {
            T item = super.getItemAtPosition(i);
            S section = mSectionBuilder.getSectionFromItem(item);
            if (!sections.containsValue(section))
                sections.put(i + sections.size(), section);
        }
        return sections;
    }

    /**
     * An interface that helps build sections for {@link SectionDataWrapper}.
     * @param <S>
     * @param <T>
     * @param <D>
     */
    public interface SectionBuilder<S, T, D extends DataHandler<T>> {
        /**
         * @return A map whose keys are the position at which a section is and values are an object
         * that defines the section.
         * If null is returned then getSectionFromItem will be called and to help build sections.
         */
        @Nullable
        SortedMap<Integer, S> buildSections(D dataHandler);
        /**
         * The object which is return will determine what section this cursor position will be in.
         * @return the section from the cursor at its current position.
         * This object will be passed to newSectionView and bindSectionView.
         */
        @NonNull
        S getSectionFromItem(T item);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + mSectionList.size();
    }

    @Override
    public T getItemAtPosition(int listPosition) {
        int wrappedPosition = getWrappedPosition(listPosition);

        return wrappedPosition == RecyclerView.NO_POSITION
                ? null : super.getItemAtPosition(wrappedPosition);
    }

    @Override
    public long getItemId(int listPosition) {
        int wrappedPosition = getWrappedPosition(listPosition);

        return wrappedPosition == RecyclerView.NO_POSITION
                ? getSectionFromListPosition(listPosition).hashCode()
                : super.getItemId(wrappedPosition);
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
    public S getSectionFromSectionPosition(int sectionPosition) {
        if (mSectionList.contains(sectionPosition)) {
            return mSectionMap.get(mSectionList.get(sectionPosition));
        }
        return null;
    }

    public S getSectionFromListPosition(int listPosition) {
        if (isSection(listPosition)) {
            return mSectionMap.get(listPosition);
        }
        return null;
    }

    /**
     * @param listPosition the position of the current item in the list with sections included
     * @return the position without sections included
     */
    public int getWrappedPosition(int listPosition) {
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
            return RecyclerView.NO_POSITION;
        }
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
