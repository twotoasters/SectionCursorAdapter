package com.twotoasters.sectioncursoradaptersample.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import com.twotoasters.sectioncursoradapter.adapter.viewholder.SViewHolder;

public class SectionViewHolder extends SViewHolder {
    public final TextView textView;

    public SectionViewHolder(View rootView) {
        super(rootView);
        textView = (TextView) rootView;
    }
}
