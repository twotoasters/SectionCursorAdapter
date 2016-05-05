package com.twotoasters.sectioncursoradapter.adapter;

import android.support.v7.widget.RecyclerView.ViewHolder;

import com.twotoasters.sectioncursoradapter.adapter.datahandler.ArrayDataHandler;

/**
 * Uses {@link ArrayDataHandler} as its DataHandler.
 *
 * @param <T> Data typo such as a String.
 * @param <VH> The ViewHolder that is being used.
 */
public abstract class ArrayDataAdapter<T, VH extends ViewHolder> extends SimpleDataAdapter<T, ArrayDataHandler<T>, VH> {

    public ArrayDataAdapter(ArrayDataHandler<T> dataHandler) {
        super(dataHandler);
        dataHandler.setAdapter(this);
    }
}
