package com.twotoasters.sectioncursoradaptersample.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ViewSwitcher;

import com.squareup.picasso.Picasso;
import com.twotoasters.sectioncursoradapter.adapter.SectionCursorAdapter;
import com.twotoasters.sectioncursoradaptersample.R;
import com.twotoasters.sectioncursoradaptersample.adapter.viewholder.ItemViewHolder;
import com.twotoasters.sectioncursoradaptersample.adapter.viewholder.SectionViewHolder;
import com.twotoasters.sectioncursoradaptersample.database.ToasterModel;
import com.twotoasters.sectioncursoradaptersample.transformation.SquareTransformation;

public class ToasterAdapter extends SectionCursorAdapter<String, SectionViewHolder, ItemViewHolder> {

    private final SquareTransformation mToasterTrans;
    private final SquareTransformation mHumanTrans;

    public ToasterAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0, R.layout.item_section, R.layout.item_toaster);

        mToasterTrans = new SquareTransformation(true);
        mHumanTrans = new SquareTransformation(false);
    }

    @Override
    protected String getSectionFromCursor(Cursor cursor) {
        final ToasterModel toaster = new ToasterModel();
        toaster.loadFromCursor(cursor);
        return toaster.shortJob;
    }

    @Override
    protected SectionViewHolder createSectionViewHolder(View sectionView, String section) {
        return new SectionViewHolder(sectionView);
    }

    @Override
    protected void bindSectionViewHolder(int position, SectionViewHolder sectionViewHolder, ViewGroup parent, String section) {
        sectionViewHolder.textView.setText(section);
    }

    @Override
    protected ItemViewHolder createItemViewHolder(Cursor cursor, View itemView) {
        return new ItemViewHolder(itemView);
    }

    @Override
    protected void bindItemViewHolder(ItemViewHolder itemViewHolder, Cursor cursor, ViewGroup parent) {
        final ToasterModel toaster = new ToasterModel();
        toaster.loadFromCursor(cursor);

        itemViewHolder.txtName.setText(toaster.name);
        itemViewHolder.txtJob.setText(toaster.jobDescription);

        Context context = itemViewHolder.itemView.getContext();
        Picasso.with(context).load(toaster.imageUrl)
                .error(R.drawable.toaster_backup).transform(mToasterTrans)
                .into(itemViewHolder.imgToaster);
        Picasso.with(context).load(toaster.imageUrl)
                .error(R.drawable.toaster_backup).transform(mHumanTrans)
                .into(itemViewHolder.imgHuman);
        // Resetting the view our switcher is showing.
        switchWithoutAnimation(itemViewHolder.switcher);
        itemViewHolder.switcher.setOnClickListener(new OnClickListener() {
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
        };
    }

    @Override
    protected int getMaxIndexerLength() {
        return 1;
    }
}
