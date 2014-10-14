package com.twotoasters.sectioncursoradapter.adapter;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

import com.twotoasters.sectioncursoradapter.adapter.viewholder.ViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * This adapter uses the ViewHolder class to completely handle view recycling for you.
 * This is similar to the new RecyclerViewAdapter.
 *
 * @param <K> section type.
 * @param <V> item type
 * @param <S> ViewHolder type for sections.
 * @param <H> ViewHolder type for items.
 */
public abstract class SectionArrayAdapter<K, V, S extends ViewHolder, H extends ViewHolder> extends BaseAdapter implements SectionIndexer {

    public static final int POSITION_NOT_FOUND = -1;

    protected static final int NUMBER_OF_VIEW_TYPES = 2;
    protected static final int TYPE_SECTION = 0;
    protected static final int TYPE_ITEM = 1;

    private int mSectionLayoutResId;
    private int mItemLayoutResId;

    int mCount = 0;
    private LayoutInflater mInflater;
    private LinkedHashMap<K, List<V>> mSectionsMap;
    private Object[] mFastScrollObjects;

    private boolean mAreSectionsDirty = true;
    private LinkedHashSet<Integer> mSectionsSet = new LinkedHashSet<Integer>();
    private boolean mStartsWithNullSection;

    public SectionArrayAdapter(Context context, int sectionLayoutResId, int itemLayoutResId) {
        init(context, sectionLayoutResId, itemLayoutResId);
    }

    public SectionArrayAdapter(Context context, int sectionLayoutResId, int itemLayoutResId, List<V> listData) {
        init(context, sectionLayoutResId, itemLayoutResId);
        setDataAndBuildSections(listData);
    }

    public SectionArrayAdapter(Context context, int sectionLayoutResId, int itemLayoutResId, V[] arrayData) {
        init(context, sectionLayoutResId, itemLayoutResId);
        setDataAndBuildSections(arrayData);
    }

    private void init(Context context, int sectionLayoutResId, int itemLayoutResId) {
        mInflater = LayoutInflater.from(context);
        this.mSectionLayoutResId = sectionLayoutResId;
        this.mItemLayoutResId = itemLayoutResId;
    }

    protected LayoutInflater getInflater() {
        return mInflater;
    }

    ////////////////////////////////
    // Implemented Abstract methods
    ////////////////////////////////

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        if (mCount == 0 && mSectionsMap != null) {
            rebuildCount();
        }
        return mCount;
    }

    private void rebuildCount() {
        mCount = 0;
        for (K key : mSectionsMap.keySet()) {
            List<V> sectionValues = mSectionsMap.get(key);
            if (key != null) {
                mCount++;
            }
            if (sectionValues != null) {
                mCount += sectionValues.size();
            }
        }
    }

    @Override
    public int getViewTypeCount() {
        return NUMBER_OF_VIEW_TYPES;
    }

    ///////////////////////
    // Managing Data
    ///////////////////////

    /**
     * This method helps with the building of the internal sectionMap.
     * <br />
     * <strong>Note:</strong> this is only called when setDataAndBuildSections() is used.
     * @param item an item from the given data set
     * @return The section that this item should be in.
     */
    protected abstract K getSectionFromItem(V item);

    /**
     * This method with call getSectionFromItem to help build the mSectionsMap.
     * <br />
     * <strong>Note:</strong> if your sections are not presorted sections will then be built in random order.
     * @param arrayData an array of all the data which section are to be built with.
     */
    public void setDataAndBuildSections(V[] arrayData) {
        setDataAndBuildSections(Arrays.asList(arrayData));
    }

    /**
     * This method with call getSectionFromItem to help build the mSectionsMap.
     * <br />
     * <strong>Note:</strong> if your sections are not presorted sections will then be built in random order.
     * @param listData a list of all the data which section are to be built with.
     */
    public void setDataAndBuildSections(List<V> listData) {
        if (listData == null) listData = new ArrayList<V>();
        LinkedHashMap<K, List<V>> sectionsMap = new LinkedHashMap<K, List<V>>();

        for (V item : listData) {
            K section = getSectionFromItem(item);
            List<V> list = sectionsMap.containsKey(section) ? sectionsMap.get(section) : new ArrayList<V>();
            list.add(item);
            sectionsMap.put(section, list);
        }
        setDataMapWithList(sectionsMap);
    }

    /**
     * @param sectionsMap The keys are the section in bindSection.
     *                    If you don't want the list to start with a section use a null key.
     *                    Each value in the object array is an item in bindView.
     */
    public void setDataMapWithArray(LinkedHashMap<K, V[]> sectionsMap) {
        if (sectionsMap == null) sectionsMap = new LinkedHashMap<K, V[]>();
        this.mSectionsMap = new LinkedHashMap<K, List<V>>();

        for (K section : sectionsMap.keySet()) {
            this.mSectionsMap.put(section, Arrays.asList(sectionsMap.get(section)));
        }
        notifyDataSetChanged();
    }

    /**
     * @param sectionsMap The keys are the section in bindSection.
     *                    If you don't want the list to start with a section use a null key.
     *                    Each value in the object array is an item in bindView.
     */
    public void setDataMapWithList(LinkedHashMap<K, List<V>> sectionsMap) {
        this.mSectionsMap = sectionsMap;
        notifyDataSetChanged();
    }

    /**
     * @param sectionsMap The keys are the section in bindSection.
     *                    This will be added to the existing mSectionsMap or a new one will be created if one doesn't exist.
     */
    public void putAllDataMapWithList(LinkedHashMap<K, List<V>> sectionsMap) {
        if (sectionsMap == null) sectionsMap = new LinkedHashMap<K, List<V>>();

        this.mSectionsMap.putAll(sectionsMap);
        notifyDataSetChanged();
    }

    /**
     * This section will be added to the end of the current sections if it doesn't exist.
     * If this section does exist this will overwrite all the item in that section.
     * In this case the section will retain it's current position.
     */
    public void putSection(K section, V[] items) {
        if (mSectionsMap == null) mSectionsMap = new LinkedHashMap<K, List<V>>();

        mSectionsMap.put(section, Arrays.asList(items));
        notifyDataSetChanged();
    }

    /**
     * This section will be added to the end of the current sections if it doesn't exist.
     * If this section does exist this will overwrite all the item in that section.
     * In this case the section will retain it's current position.
     */
    public void putSection(K section, List<V> list) {
        putSection(section, (V[]) list.toArray());
    }

    /**
     * Removes a section from the adapter. All items will be removed with it.
     * @param section the section to be removed.
     */
    public void removeSection(K section) {
        mSectionsMap.remove(section);
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mCount = 0;
        mAreSectionsDirty = true;
        mStartsWithNullSection = mSectionsMap.containsKey(null);
    }

    @Override
    public void notifyDataSetInvalidated() {
        super.notifyDataSetInvalidated();
        mCount = 0;
        mAreSectionsDirty = true;
        mStartsWithNullSection = mSectionsMap.containsKey(null);
    }

    ///////////////////
    // Getters
    ///////////////////

    /**
     * @return all of the positions that sections are at.
     */
    public Set<Integer> getSectionListPositions() {
        if (mAreSectionsDirty && mSectionsMap != null) {
            mSectionsSet.clear();

            int position = 0;
            for (K key : mSectionsMap.keySet()) {
                // The map can start will a null key so that it doesn't start with a section.
                if (key != null) {
                    mSectionsSet.add(position);
                    position++;
                }
                if (mSectionsMap.get(key) != null) {
                    position += mSectionsMap.get(key).size();
                }
            }
            mAreSectionsDirty = false;
        } else if (mAreSectionsDirty) {
            // We hit this because we don't have a map any longer.
            mSectionsSet.clear();
            mAreSectionsDirty = false;
        }
        return mSectionsSet;
    }

    /**
     * @return the section for the given sectionPosition.
     * Null will be returned if the section doesn't exist.
     */
    public K getSection(int sectionPosition) {
        int position = mStartsWithNullSection ? -1 : 0;
        for (K section : mSectionsMap.keySet()) {
            if (position == sectionPosition) {
                return section;
            }
            position++;
        }
        return null;
    }

    /**
     * @return the list the given sectionPosition.
     * Null will be returned if the section doesn't exist.
     */
    public List<V> getSectionList(int sectionPosition) {
        int position = 0;
        for (K section : mSectionsMap.keySet()) {
            if (position == sectionPosition) {
                return mSectionsMap.get(section);
            }
            position++;
        }
        return null;
    }

    /**
     * @return the list the given section.
     * Null will be returned if the section doesn't exist.
     */
    public List<V> getSectionList(K section) {
        return mSectionsMap.get(section);
    }

    /**
     * @return the item within the section specified.
     * Null will be returned if the object doesn't exist.
     */
    public V getItem(int sectionPosition, int itemPosition) {
        if (sectionPosition >= getSectionListPositions().size()) {
            // As null could be returned from getSection as a key we need to handle it here.
            return null;
        }
        K key = getSection(sectionPosition);
        List<V> sectionValues = mSectionsMap.get(key);
        // We want to just return null if they are out of bounds.
        if (itemPosition < sectionValues.size()) {
            return sectionValues.get(itemPosition);
        } else {
            return null;
        }
    }

    /**
     * @param listPosition
     * @return The object for the position. This could be a section or item.
     */
    @Override
    public Object getItem(int listPosition) {
        if (mSectionsMap == null || listPosition >= getCount()) {
            return null;
        }

        int sectionPosition = getSectionPosition(listPosition);
        if (getSectionListPositions().contains(listPosition)) {
            return getSection(sectionPosition);
        } else {
            int itemPosition = getItemPosition(listPosition);
            return getItem(sectionPosition, itemPosition);
        }
    }

    ///////////////
    // Positions
    ///////////////

    /**
     * @param listPosition This is the position within the full list.
     * @return If no sections are in the list then POSITION_NOT_FOUND will be returned.
     * Else the index of the section, not including TYPE_ITEMs, is returned.
     */
    public int getSectionPosition(int listPosition) {
        int size = getSectionListPositions().size();
        if (size == 0) {
            return POSITION_NOT_FOUND;
        }

        int secPosition = -1;
        boolean isFirstSection = true;
        for (Integer secListPosition : getSectionListPositions()) {
            secPosition++;
            if (secListPosition == listPosition) {
                break;
            } else if (secListPosition > listPosition) {
                if (isFirstSection) {
                    // This item is before the first section.
                    return POSITION_NOT_FOUND;
                }
                // In this case we know it is in the previous section.
                return --secPosition;
            }
            isFirstSection = false;
        }
        return secPosition;
    }

    /**
     * @param listPosition This is the position within the full list.
     * @return POSITION_NOT_FOUND if the listPosition is not an item listPosition
     */
    public int getItemPosition(int listPosition) {
        if (listPosition >= getCount()) {
            return POSITION_NOT_FOUND;
        }
        // There are no sections we return the list position
        int itemPosition = listPosition;
        for (Integer secListPosition : getSectionListPositions()) {
            if (listPosition >= secListPosition) {
                itemPosition = (listPosition - secListPosition) - 1;
            } else {
                break;
            }
        }
        return itemPosition;
    }

    /**
     * @param listPosition This is the position within the full list.
     * @return true if this listPosition is a section.
     */
    public boolean isSection(int listPosition) {
        return getSectionListPositions().contains(listPosition);
    }

    /**
     * @param sectionPosition is the position within the sections not including TYPE_ITEMs
     * @return the amount of items which this section has.
     *         If this section does not exist, POSITION_NOT_FOUND will be returned.
     */
    public int getSectionSize(int sectionPosition) {
        K section = getSection(sectionPosition);
        return getSectionSize(section);
    }

    /**
     * @param section is the section object whos size you wish to know.
     * @return the amount of items which this section has.
     *         If this section does not exist, POSITION_NOT_FOUND will be returned.
     */
    public int getSectionSize(K section) {
        List<V> items = mSectionsMap.get(section);
        if (items !=  null) {
            return items.size();
        }
        return POSITION_NOT_FOUND;
    }

    ////////////////
    // View stuff
    ////////////////

    @Override
    public int getItemViewType(int position) {
        if (getSectionListPositions().contains(position)) {
            return SectionArrayAdapter.TYPE_SECTION;
        } else {
            return SectionArrayAdapter.TYPE_ITEM;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int sectionPosition = getSectionPosition(position);
        if (getSectionListPositions().contains(position)) {
            K section = getSection(sectionPosition);
            if (convertView == null) {
                convertView = newSectionView(parent, section);
            }
            bindSectionViewHolder(sectionPosition, (S) convertView.getTag(), parent, section);
        } else if (getItemViewType(position) >= TYPE_ITEM) {
            int itemPosition = getItemPosition(position);
            V item = getItem(sectionPosition, itemPosition);
            if (convertView == null) {
                convertView = newItemView(sectionPosition, parent, item);
            }
            bindItemViewHolder(sectionPosition, itemPosition, (H) convertView.getTag(), parent, item);
        }
        return convertView;
    }

    /**
     * Override to manually create your views. MAKE SURE YOU TAG A ViewHolder TO THIS VIEW!
     * If you do not tag a ViewHolder, the bind methods will give you a null ViewHolder.
     */
    protected View newSectionView(ViewGroup parent, K section) {
        View view = getInflater().inflate(mSectionLayoutResId, parent, false);
        view.setTag(createSectionViewHolder(view, section));

        return view;
    }

    /**
     * @param sectionView the view which was created for this ViewHolder. There is no need to setTag.
     * @param section the section key from the supplied map.
     * @return The newly created section ViewHolder.
     */
    protected abstract S createSectionViewHolder(View sectionView, K section);

    /**
     * @param sectionPosition is the position within the sections not including TYPE_ITEMs
     * @param section the section key from the supplied map.
     * @param sectionViewHolder the ViewHolder which should have data bound to. This maybe reused and have old data in it.
     * @param parent the parent view. Typically a ListView.
     */
    protected abstract void bindSectionViewHolder(int sectionPosition, S sectionViewHolder, ViewGroup parent, K section);

    /**
     * Override to manually create your views. MAKE SURE YOU TAG A ViewHolder TO THIS VIEW!
     * If you do not tag a ViewHolder, the bind methods will give you a null ViewHolder.
     */
    protected View newItemView(int sectionPosition, ViewGroup parent, V item) {
        View view = getInflater().inflate(mItemLayoutResId, parent, false);
        view.setTag(createItemViewHolder(sectionPosition, view, item));

        return view;
    }

    /**
     * @param itemView the view which was created for this ViewHolder. There is no need to setTag.
     * @param item the object from the object array for this section.
     * @return the new created item view.
     */
    protected abstract H createItemViewHolder(int sectionPosition, View itemView, V item);

    /**
     * @param sectionPosition is the position within the sections not including TYPE_ITEMs
     * @param itemPosition    is the within the item's section.
     * @param item      the object from the object array for this section.
     * @param itemViewHolder  the ViewHolder which should have data bound to. This maybe reused and have old data in it.
     * @param parent          the parent view. Typically a ListView.
     */
    protected abstract void bindItemViewHolder(int sectionPosition, int itemPosition, H itemViewHolder, ViewGroup parent, V item);



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
        int i = 0;
        int positionForSection = ListView.INVALID_POSITION;
        for (int listPosition : getSectionListPositions()) {
            if (i == sectionIndex) {
                positionForSection = listPosition;
                break;
            }
            i++;
        }

        return positionForSection != ListView.INVALID_POSITION
                ? positionForSection : getCount();
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
        int sectionIndex = getItemPosition(position);

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
        Collection<K> sectionsCollection = mSectionsMap.keySet();
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

