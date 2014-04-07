package com.twotoasters.sectioncursoradaptersample.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.squareup.picasso.Picasso;
import com.twotoasters.sectioncursoradapter.SectionCursorAdapter;
import com.twotoasters.sectioncursoradaptersample.R;
import com.twotoasters.sectioncursoradaptersample.database.ToasterModel;
import com.twotoasters.sectioncursoradaptersample.transformation.SquareTransformation;

import java.util.SortedMap;
import java.util.TreeMap;

public class ToastersAdapter extends SectionCursorAdapter {

    private final SquareTransformation mToasterTrans;
    private final SquareTransformation mHumanTrans;

    public ToastersAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);

        mToasterTrans = new SquareTransformation(true);
        mHumanTrans = new SquareTransformation(false);
    }

    @Override
    protected SortedMap<Integer, Object> buildSections(Cursor cursor) {
        TreeMap<Integer, Object> sections = new TreeMap<Integer, Object>();
        // It is safe to have just one model because we know we have data in every cell.
        final ToasterModel toaster = new ToasterModel();
        int cursorPosition = 0;
        while (cursor.moveToNext() == true) {
            toaster.loadFromCursor(cursor);
            if (!sections.containsValue(toaster.shortJob)) {
                sections.put(cursorPosition + sections.size(), toaster.shortJob);
            }
            cursorPosition++;
        }
        return sections;
    }

    @Override
    protected View newSectionView(Context context, Object item, ViewGroup parent) {
        return getLayoutInflater().inflate(R.layout.item_section, parent, false);
    }

    @Override
    protected void bindSectionView(View convertView, Context context, int position, Object item) {
        ((TextView) convertView).setText((String) item);
    }

    @Override
    protected View newItemView(Context context, Cursor cursor, ViewGroup parent) {
        View convertView = getLayoutInflater().inflate(R.layout.item_toaster, parent, false);
        ViewHolder holder = new ViewHolder(convertView);
        convertView.setTag(holder);

        return convertView;
    }

    @Override
    protected void bindItemView(View convertView, Context context, Cursor cursor) {
        final ToasterModel toaster = new ToasterModel();
        toaster.loadFromCursor(cursor);

        final ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.txtName.setText(toaster.name);
        holder.txtJob.setText(toaster.jobDescription);

        Picasso.with(context).load(toaster.imageUrl)
                .error(R.drawable.toaster_backup).transform(mToasterTrans)
                .into(holder.imgToaster);
        Picasso.with(context).load(toaster.imageUrl)
                .error(R.drawable.toaster_backup).transform(mHumanTrans)
                .into(holder.imgHuman);
        // Reseting the view our switcher is showing.
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
        };
    }

    private static class ViewHolder {
        TextView txtName;
        TextView txtJob;
        ImageView imgToaster;
        ImageView imgHuman;
        ViewSwitcher switcher;

        private ViewHolder(View convertView) {
            txtName = (TextView) convertView.findViewById(R.id.txtName);
            txtJob = (TextView) convertView.findViewById(R.id.txtJob);
            imgToaster = (ImageView) convertView.findViewById(R.id.imgToaster);
            imgHuman = (ImageView) convertView.findViewById(R.id.imgHuman);
            switcher = (ViewSwitcher) convertView.findViewById(R.id.switcherImg);
        }
    }

    @Override
    protected int getMaxIndexerLength() {
        return 1;
    }
}
