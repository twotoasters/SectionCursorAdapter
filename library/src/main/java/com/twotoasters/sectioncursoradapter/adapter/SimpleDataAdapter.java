package com.twotoasters.sectioncursoradapter.adapter;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;

import com.twotoasters.sectioncursoradapter.adapter.datahandler.DataHandler;

/**
 * Simple class that can take any DataHolder.
 * You con use {@link #getItemAtPosition(int)} to skip grabbing the {@link DataHandler} directly.
 * More advanced functions maybe missing.
 *
 * @param <T> Data type such as String or Cursor.
 * @param <D> The DataHandler that is being used.
 * @param <VH> The ViewHolder that is being used.
 */
public abstract class SimpleDataAdapter<T, D extends DataHandler<T>, VH extends ViewHolder> extends Adapter<VH> {
    private final D mDataHandler;

    public SimpleDataAdapter(D dataHandler) {
        this.mDataHandler = dataHandler;
        if (mDataHandler.hasStableIds()) setHasStableIds(true);
    }

    public D getDataHandler() {
        return mDataHandler;
    }

    public T getItemAtPosition(int position) {
        return getDataHandler().getItemAtPosition(position);
    }

    @Override
    public int getItemCount() {
        return mDataHandler.getItemCount();
    }

    @Override
    public long getItemId(int position) {
        return mDataHandler.getItemId(position);
    }
}
