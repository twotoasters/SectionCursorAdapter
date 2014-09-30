package com.twotoasters.sectioncursoradaptersample.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import com.twotoasters.sectioncursoradapter.adapter.viewholder.ViewHolder;

public class SectionViewHolder extends ViewHolder {
    public final TextView textView;

    public SectionViewHolder(View rootView) {
        super(rootView);
        textView = (TextView) rootView;
    }
}
