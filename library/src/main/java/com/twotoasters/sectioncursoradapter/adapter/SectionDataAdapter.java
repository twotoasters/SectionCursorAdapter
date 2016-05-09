package com.twotoasters.sectioncursoradapter.adapter;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.twotoasters.sectioncursoradapter.adapter.datahandler.DataHandler;
import com.twotoasters.sectioncursoradapter.adapter.datahandler.SectionDataWrapper;

public abstract class SectionDataAdapter<S, T, D extends DataHandler<T>>
                                                                    extends SimpleDataAdapter<T, SectionDataWrapper<S, T, D>, ViewHolder> {
    private static final int VIEW_TYPE_SECTION = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    public SectionDataAdapter(SectionDataWrapper<S, T, D> dataHandler) {
        super(dataHandler);
        setupWrappedDataHandler(dataHandler.getWrapped());
    }

    @Override
    public int getItemViewType(int position) {
        return getDataHandler().isSection(position)
                ? VIEW_TYPE_SECTION
                : VIEW_TYPE_ITEM;
    }

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPE_SECTION:
                return onCreateSectionViewHolder(inflater, parent);
            case VIEW_TYPE_ITEM:
                return onCreateItemViewHolder(inflater, parent);
        }
        return null;
    }

    @Override
    public final void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_SECTION:
                S section = getDataHandler().getSectionFromListPosition(position);
                onBindSectionViewHolder(holder, section);
                break;
            case VIEW_TYPE_ITEM:
                T item = getItemAtPosition(position);
                onBindItemViewHolder(holder, item);
                break;
        }
    }

    abstract protected void setupWrappedDataHandler(D dataHandler);

    abstract protected ViewHolder onCreateSectionViewHolder(LayoutInflater inflater, ViewGroup parent);
    abstract protected ViewHolder onCreateItemViewHolder(LayoutInflater inflater, ViewGroup parent);

    abstract protected void onBindSectionViewHolder(ViewHolder holder, S section);
    abstract protected void onBindItemViewHolder(ViewHolder holder, T item);
}
