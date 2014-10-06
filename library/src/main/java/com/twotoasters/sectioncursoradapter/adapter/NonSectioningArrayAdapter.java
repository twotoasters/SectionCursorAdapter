package com.twotoasters.sectioncursoradapter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.twotoasters.sectioncursoradapter.adapter.viewholder.ViewHolder;

import java.util.List;

/**
 * This adapter uses the ViewHolder class to completely handle view recycling for you.
 * This is similar to the new RecyclerViewAdapter.
 *
 * @param <T> the item type your array or list holds.
 * @param <H> the viewHolder type that the list should use.
 */
public abstract class NonSectioningArrayAdapter<T, H extends ViewHolder> extends ArrayAdapter<T> {

    private LayoutInflater mInflater;
    private int mResourceId;

    public NonSectioningArrayAdapter(Context context, int resource) {
        super(context, resource);
        init(context, resource);
    }

    public NonSectioningArrayAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
        init(context, resource);
    }

    public NonSectioningArrayAdapter(Context context, int resource, T[] objects) {
        super(context, resource, objects);
        init(context, resource);
    }

    private void init(Context context, int resource) {
        mResourceId = resource;
        mInflater = LayoutInflater.from(context);
    }

    /**
     * @return The layout mInflater which should be used for this adapter.
     */
    protected LayoutInflater getInflater() {
        return mInflater;
    }

    /**
     * @param id the item of your array item.
     * @return the array item which contains this id.
     */
    public int getItemPositionById(long id) {
        for (int i = 0; i < getCount(); i++) {
            if (getItemId(i) == id) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        View view;
        T item = getItem(position);

        if (convertView == null) {
            view = newView(position, parent, item);
        } else {
            view = convertView;
        }

        bindViewHolder(position, (H) view.getTag(), parent, getItem(position));
        return view;
    }

    /**
     * Override to manually create your views. MAKE SURE YOU TAG A ViewHolder TO THIS VIEW!
     * If you do not tag a ViewHolder, the bind methods will give you a null ViewHolder.
     */
    protected View newView(int position, ViewGroup parent, T item) {
        View view = getInflater().inflate(mResourceId, parent, false);
        view.setTag(createViewHolder(position, view, item));

        return view;
    }

    /**
     * @param position position for the view holder. Remember that this viewHolder will be reused.
     * @param view the view which was created for this ViewHolder. There is no need to setTag.
     * @param item the object from the array for this position.
     * @return the new created item view.
     */
    protected abstract H createViewHolder(int position, View view, T item);

    /**
     * @param position that this view is being bound for.
     * @param holder the ViewHolder which contains all of your views.
     * @param parent of the rootView of your ViewHolder.
     * @param item the object from the array for this position.
     */
    protected abstract void bindViewHolder(int position, H holder, ViewGroup parent, T item);
}
