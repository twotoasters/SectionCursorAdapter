package com.twotoasters.sectioncursoradapter.adapter.datahandler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * For full animation support {@link #setAdapter(Adapter)}.
 *
 * {@inheritDoc}
 */
@SuppressWarnings("unused")
public class ArrayDataHandler<T> implements DataHandler<T>, Filterable {
    @Nullable private Adapter<?> mAdapter;

    /**
     * Contains the list of objects that represent the data of this ArrayAdapter.
     * The content of this list is referred to as "the array" in the documentation.
     */
    @NonNull private List<T> mObjects = new ArrayList<>();
    /**
     * Lock used to modify the content of {@link #mObjects}. Any write operation
     * performed on the array should be synchronized on this lock. This lock is also
     * used by the filter (see {@link #getFilter()} to make a synchronized copy of
     * the original array of data.
     */
    private final Object mLock = new Object();
    // A copy of the original mObjects array, initialized from and then used instead as soon as
    // the mFilter ArrayFilter is used. mObjects will then only contain the filtered values.
    private ArrayList<T> mOriginalValues;
    private ArrayFilter mFilter;

    /**
     * Constructor
     */
    public ArrayDataHandler() {
        init(null, null);
    }

    /**
     * Constructor
     *
     * @param objects The objects to represent in the ListView. These will be copied so that outside modification is ignored.
     */
    public ArrayDataHandler(T[] objects) {
        init(Arrays.asList(objects), null);
    }
    /**
     * Constructor
     *
     * @param objects The objects to represent in the ListView. These will be copied so that outside modification is ignored.
     * @param adapter The adapter to notify of data changes.
     */
    public ArrayDataHandler(T[] objects, Adapter adapter) {
        init(Arrays.asList(objects), adapter);
    }
    /**
     * Constructor
     *
     * @param objects The objects to represent in the ListView. These will be copied so that outside modification is ignored.
     */
    public ArrayDataHandler(List<T> objects) {
        init(objects, null);
    }
    /**
     * Constructor
     *
     * @param objects The objects to represent in the ListView. These will be copied so that outside modification is ignored.
     * @param adapter The adapter to notify of data changes.
     */
    public ArrayDataHandler(List<T> objects, Adapter adapter) {
        init(objects, adapter);
    }

    private void init(@Nullable List<T> objects, Adapter adapter) {
        mAdapter = adapter;

        if (objects != null) {
            mObjects = new ArrayList<>(objects);
        }
    }

    @Override
    public T getItemAtPosition(int position) {
        return mObjects.get(position);
    }

    @Override
    public int getItemCount() {
        return mObjects.size();
    }

    @Override
    public long getItemId(int position) {
        return RecyclerView.NO_ID;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public void setAdapter(@Nullable Adapter<?> mAdapter) {
        this.mAdapter = mAdapter;
    }

    /**
     * Adds the specified object at the end of the array.
     *
     * @param object The object to add at the end of the array.
     */
    public void add(T object) {
        int lastPosition;
        synchronized (mLock) {
            lastPosition = mObjects.size();
            if (mOriginalValues != null) {
                mOriginalValues.add(object);
                runFilter();
            } else {
                mObjects.add(object);
            }
        }
        if (mAdapter != null) mAdapter.notifyItemInserted(lastPosition);
    }
    /**
     * Adds the specified Collection at the end of the array.
     *
     * @param collection The Collection to add at the end of the array.
     */
    public void addAll(Collection<? extends T> collection) {
        int lastPosition;
        synchronized (mLock) {
            lastPosition = mObjects.size();
            if (mOriginalValues != null) {
                mOriginalValues.addAll(collection);
                runFilter();
            } else {
                mObjects.addAll(collection);
            }
        }
        if (mAdapter != null) mAdapter.notifyItemRangeInserted(lastPosition, collection.size());
    }
    /**
     * Adds the specified items at the end of the array.
     *
     * @param items The items to add at the end of the array.
     */
    public void addAll(T... items) {
        addAll(Arrays.asList(items));
    }
    /**
     * Inserts the specified object at the specified index in the array.
     *
     * @param object The object to insert into the array.
     * @param index The index at which the object must be inserted.
     */
    public void insert(T object, int index) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.add(index, object);
                runFilter();
            } else {
                mObjects.add(index, object);
            }
        }
        if (mAdapter != null) mAdapter.notifyItemInserted(index);
    }
    /**
     * Inserts the specified object at the specified index in the array.
     *
     * @param collection The Collection to insert into the array.
     * @param index The index at which the object must be inserted.
     */
    public void insertAll(Collection<? extends T> collection, int index) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.addAll(index, collection);
                runFilter();
            } else {
                mObjects.addAll(index, collection);
            }
        }
        if (mAdapter != null) mAdapter.notifyItemRangeInserted(index, collection.size());
    }
    /**
     * Removes the specified object from the array.
     *
     * @param object The object to remove.
     */
    public void remove(T object) {
        int index;
        synchronized (mLock) {
            if (mOriginalValues != null) {
                index = mOriginalValues.indexOf(object);
                mOriginalValues.remove(object);
                runFilter();
            } else {
                index = mObjects.indexOf(object);
                mObjects.remove(object);
            }
        }
        if (mAdapter != null) mAdapter.notifyItemRemoved(index);
    }
    /**
     * Removes the specified object from the array.
     *
     * @param index The index to remove.
     */
    public void remove(int index) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.remove(index);
                runFilter();
            } else {
                mObjects.remove(index);
            }
        }
        if (mAdapter != null) mAdapter.notifyItemRemoved(index);
    }
    /**
     * Removes the specified object from the array.
     *
     * @param start first index to remove
     * @param end last index to remove non inclusive.
     */
    public void removeRange(int start, int end) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                removeRange(mOriginalValues, start, end);
                runFilter();
            } else {
                removeRange(mObjects, start, end);
            }
        }
        if (mAdapter != null) mAdapter.notifyItemRangeRemoved(start, end - start);
    }

    private void removeRange(List<T> list, int start, int end) {
        for (int i = end - 1; i >= start; i--) {
            list.remove(i);
        }
    }

    /**
     * Remove all elements from the list.
     */
    public void clear() {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.clear();
                runFilter();
            } else {
                mObjects.clear();
            }
        }
        if (mAdapter != null) mAdapter.notifyDataSetChanged();
    }

    /**
     * Sorts the content of this adapter using the specified comparator.
     *
     * @param comparator The comparator used to sort the objects contained
     *        in this adapter.
     */
    public void sort(Comparator<? super T> comparator) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                Collections.sort(mOriginalValues, comparator);
                runFilter();
            } else {
                Collections.sort(mObjects, comparator);
            }
        }
        if (mAdapter != null) mAdapter.notifyDataSetChanged();
    }

    /**
     * Returns the position of the specified item in the array.
     *
     * @param item The item to retrieve the position of.
     *
     * @return The position of the specified item.
     */
    public int getPosition(T item) {
        return mObjects.indexOf(item);
    }

    /**
     * @return a filter. After preforming a filter notify data set changed will need to be called.
     */
    @NonNull
    public ArrayFilter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }
    private void runFilter() {
        if (mFilter != null) {
            mFilter.performFiltering();
        }
    }
    /**
     * <p>An array filter constrains the content of the array adapter with
     * a prefix. Each item that does not start with the supplied prefix
     * is removed from the list.</p>
     */
    public class ArrayFilter extends Filter {
        private CharSequence mPrefix;

        private void performFiltering() {
            if (mPrefix != null && mPrefix.length() != 0) {
                publishResults(null, performFiltering(mPrefix));
            }
        }

        public final FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (prefix == null || prefix.length() == 0) {
                results.values = mOriginalValues;
                results.count = mOriginalValues.size();
                mPrefix = null;
            } else {

                if (mOriginalValues == null) {
                    synchronized (mLock) {
                        mOriginalValues = new ArrayList<>(mObjects);
                    }
                }

                String prefixString = prefix.toString().toLowerCase().trim();
                ArrayList<T> values;
                synchronized (mLock) {
                    values = new ArrayList<>(mOriginalValues);
                }
                final int count = values.size();
                final ArrayList<T> newValues = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    final T value = values.get(i);
                    final String valueText = value.toString().toLowerCase().trim();

                    if (valueText.startsWith(prefixString)) {
                        newValues.add(value);
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mObjects = (List<T>) results.values;
            if (mAdapter != null) mAdapter.notifyDataSetChanged();
            if (mObjects == mOriginalValues) mOriginalValues = null;
        }
    }
}
