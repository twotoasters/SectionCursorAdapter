package com.twotoasters.sectioncursoradaptersample.adapter;

import android.content.Context;
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
import com.twotoasters.sectioncursoradapter.adapter.SectionDataAdapter;
import com.twotoasters.sectioncursoradapter.adapter.datahandler.ArrayDataHandler;
import com.twotoasters.sectioncursoradapter.adapter.datahandler.SectionDataWrapper;
import com.twotoasters.sectioncursoradapter.adapter.datahandler.SectionDataWrapper.SectionBuilder;
import com.twotoasters.sectioncursoradaptersample.R;
import com.twotoasters.sectioncursoradaptersample.adapter.viewholder.ItemViewHolder;
import com.twotoasters.sectioncursoradaptersample.adapter.viewholder.SectionViewHolder;
import com.twotoasters.sectioncursoradaptersample.database.ToasterModel;
import com.twotoasters.sectioncursoradaptersample.transformation.SquareTransformation;

import java.util.SortedMap;

public class ArrayAdapter extends SectionDataAdapter<String, ToasterModel, ArrayDataHandler<ToasterModel>>
        implements SectionBuilder<String, ToasterModel, ArrayDataHandler<ToasterModel>> {

    private final SquareTransformation mToasterTrans;
    private final SquareTransformation mHumanTrans;

    public ArrayAdapter(SectionDataWrapper<String, ToasterModel, ArrayDataHandler<ToasterModel>> dataHandler) {
        super(dataHandler);

        mToasterTrans = new SquareTransformation(true);
        mHumanTrans = new SquareTransformation(false);
    }

    @Override
    protected void setupWrappedDataHandler(ArrayDataHandler<ToasterModel> dataHandler) {
        dataHandler.setAdapter(this);
    }

    @Override
    protected ViewHolder onCreateSectionViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new SectionViewHolder(inflater.inflate(R.layout.item_section, parent, false));
    }

    @Override
    protected ViewHolder onCreateItemViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new ItemViewHolder(inflater.inflate(R.layout.item_toaster, parent, false));
    }

    @Override
    protected void onBindSectionViewHolder(ViewHolder holder, String section) {
        ((SectionViewHolder) holder).textView.setText(section);
    }

    @Override
    protected void onBindItemViewHolder(ViewHolder viewHolder, final ToasterModel toaster) {

        ItemViewHolder holder = (ItemViewHolder) viewHolder;

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
    public SortedMap<Integer, String> buildSections(ArrayDataHandler<ToasterModel> dataHandler) {
        return null; // Not going to give this a pre-built map so null is what we want to hand back.
    }

    @NonNull
    @Override
    public String getSectionFromItem(ToasterModel item) {
        return item.shortJob;
    }
}
