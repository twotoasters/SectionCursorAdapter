package com.twotoasters.sectioncursoradapter.adapter.datahandler;

/**
 * Simple data handler. The following methods should be called by an {@link android.support.v7.widget.RecyclerView.Adapter};
 * {@link #getItemCount()}, {@link #getItemId(int)}.
 * The adapter's setHasStableIds should be set by {@link #hasStableIds()}.
 * <br /><br />
 * Check out {@link com.twotoasters.sectioncursoradapter.adapter.SimpleDataAdapter} for an example implementation.
 *
 * @param <T> Data type such as String or Cursor.
 */
public interface DataHandler<T> {
    T getItemAtPosition(int position);
    int getItemCount();

    /**
     * @return {@link android.support.v7.widget.RecyclerView#NO_ID}
     */
    long getItemId(int position);
    boolean hasStableIds();
    void registerObservable(DataChangeListener listener);
    void unregisterObservable(DataChangeListener listener);

    interface DataChangeListener {
        void onDataChanged();
    }
}
