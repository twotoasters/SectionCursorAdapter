package com.twotoasters.sectioncursoradaptersample.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ViewSwitcher;

import com.squareup.picasso.Picasso;
import com.twotoasters.sectioncursoradapter.adapter.SimpleDataAdapter;
import com.twotoasters.sectioncursoradapter.adapter.datahandler.CursorDataHandler;
import com.twotoasters.sectioncursoradapter.adapter.datahandler.SectionDataWrapper;
import com.twotoasters.sectioncursoradapter.adapter.datahandler.SectionDataWrapper.SectionBuilder;
import com.twotoasters.sectioncursoradaptersample.R;
import com.twotoasters.sectioncursoradaptersample.adapter.viewholder.ItemViewHolder;
import com.twotoasters.sectioncursoradaptersample.adapter.viewholder.SectionViewHolder;
import com.twotoasters.sectioncursoradaptersample.database.ToasterModel;
import com.twotoasters.sectioncursoradaptersample.transformation.SquareTransformation;

import java.util.SortedMap;

public class ToasterAdapter extends SimpleDataAdapter<Cursor, SectionDataWrapper<String, Cursor, CursorDataHandler>, ViewHolder>
                                                                        implements SectionBuilder<String,Cursor,CursorDataHandler> {
    private static final int VIEW_TYPE_SECTION = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private final SquareTransformation mToasterTrans;
    private final SquareTransformation mHumanTrans;

    public ToasterAdapter(SectionDataWrapper<String, Cursor, CursorDataHandler> dataHandler) {
        super(dataHandler);
        dataHandler.getWrapped().setAdapter(this);

        mToasterTrans = new SquareTransformation(true);
        mHumanTrans = new SquareTransformation(false);
    }

    @Override
    public int getItemViewType(int position) {
        return getDataHandler().isSection(position)
                ? VIEW_TYPE_SECTION
                : VIEW_TYPE_ITEM;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPE_SECTION:
                return new SectionViewHolder(inflater.inflate(R.layout.item_section, parent, false));
            case VIEW_TYPE_ITEM:
                return new ItemViewHolder(inflater.inflate(R.layout.item_toaster, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_SECTION:
                String section = getDataHandler().getSectionFromListPosition(position);
                onBindSectionViewHolder((SectionViewHolder) holder, section);
                break;
            case VIEW_TYPE_ITEM:
                Cursor cursor = getItemAtPosition(position);
                onBindItemViewHolder((ItemViewHolder) holder, cursor);
                break;
        }
    }

    protected void onBindSectionViewHolder(SectionViewHolder holder, String section) {
        holder.textView.setText(section);
    }

    protected void onBindItemViewHolder(ItemViewHolder holder, Cursor cursor) {
        final ToasterModel toaster = new ToasterModel();
        toaster.loadFromCursor(cursor);

        holder.txtName.setText(toaster.name);
        holder.txtJob.setText(toaster.jobDescription);

        Context context = holder.itemView.getContext();
        Picasso.with(context).load(toaster.imageUrl)
                .error(R.drawable.toaster_backup).transform(mToasterTrans)
                .into(holder.imgToaster);
        Picasso.with(context).load(toaster.imageUrl)
                .error(R.drawable.toaster_backup).transform(mHumanTrans)
                .into(holder.imgHuman);
        // Resetting the view our switcher is showing.
        switchWithoutAnimation(holder.switcher);
        holder.switcher.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewSwitcher) v).showNext();
            }
        });
    }

    private void switchWithoutAnimation(ViewSwitcher switcher) {
        if (switcher.getDisplayedChild() != 0) {
            Animation in = switcher.getInAnimation();
            Animation out = switcher.getOutAnimation();

            switcher.setInAnimation(null);
            switcher.setOutAnimation(null);

            switcher.setDisplayedChild(0);
            switcher.setInAnimation(in);
            switcher.setOutAnimation(out);
        }
    }

    @Nullable
    @Override
    public SortedMap<Integer, String> buildSections(CursorDataHandler dataHandler) {
        return null;
    }

    @NonNull
    @Override
    public String getSectionFromItem(Cursor cursor) {
        final ToasterModel toaster = new ToasterModel();
        toaster.loadFromCursor(cursor);
        return toaster.shortJob;
    }
}
