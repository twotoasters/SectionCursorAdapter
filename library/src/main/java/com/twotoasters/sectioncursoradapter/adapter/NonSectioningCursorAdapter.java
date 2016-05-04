package com.twotoasters.sectioncursoradapter.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class NonSectioningCursorAdapter<VH extends ViewHolder> extends RecyclerCursorAdapter<VH> {

    private int mLayoutResId;
    private LayoutInflater mInflater;

    @Deprecated
    public NonSectioningCursorAdapter(Context context, Cursor c, int layoutResId) {
        super(context, c);
        init(context, layoutResId);
    }

    public NonSectioningCursorAdapter(Context context, Cursor c, boolean autoRequery, int layoutResId) {
        super(context, c, autoRequery);
        init(context, layoutResId);
    }

    public NonSectioningCursorAdapter(Context context, Cursor c, int flags, int layoutResId) {
        super(context, c, flags);
        init(context, layoutResId);
    }

    private void init(Context context, int layoutResId) {
        mLayoutResId = layoutResId;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    /**
     * {@inheritDoc}
     *
     * This method is finalized. Override onNewView and the other onCreateViewHolder instead of this method.
     */
    public final VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = onNewView(parent, viewType);
        return onCreateViewHolder(view, parent, viewType);
    }

    /**
     * Override to manually create your views.
     */
    protected View onNewView(ViewGroup parent, int viewType) {
        return getInflater().inflate(mLayoutResId, parent, false);
    }

    /**
     * @param view which was inflated according to the given resource id in the constructor.
     * @param parent, The ViewGroup into which the new View will be added after it is bound to an adapter position.
     *                More then likely a RecyclerView
     * @param viewType, The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    abstract protected VH onCreateViewHolder(View view, ViewGroup parent, int viewType);


    /**
     * @return The layout mInflater which should be used for this adapter.
     */
    protected LayoutInflater getInflater() {
        return mInflater;
    }
}
