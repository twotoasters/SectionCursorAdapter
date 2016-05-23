package com.twotoasters.sectioncursoradapter.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView.ViewHolder;

import com.twotoasters.sectioncursoradapter.adapter.datahandler.CursorDataHandler;

/**
 * Uses {@link CursorDataHandler} as its DataHandler.
 *
 * @param <VH> The ViewHolder that is being used.
 */
public abstract class CursorDataAdapter<VH extends ViewHolder> extends SimpleDataAdapter<Cursor, CursorDataHandler, VH> {

    public CursorDataAdapter(CursorDataHandler dataHandler) {
        super(dataHandler);
        dataHandler.setAdapter(this);
    }
}
