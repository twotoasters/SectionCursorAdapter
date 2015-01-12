package com.twotoasters.sectioncursoradaptersample.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.twotoasters.sectioncursoradapter.adapter.viewholder.SViewHolder;
import com.twotoasters.sectioncursoradaptersample.R;

public class ItemViewHolder extends SViewHolder {
    public final TextView txtName;
    public final TextView txtJob;
    public final ImageView imgToaster;
    public final ImageView imgHuman;
    public final ViewSwitcher switcher;

    public ItemViewHolder(View rootView) {
        super(rootView);
        txtName = findWidgetById(R.id.txtName);
        txtJob = findWidgetById(R.id.txtJob);
        imgToaster = findWidgetById(R.id.imgToaster);
        imgHuman = findWidgetById(R.id.imgHuman);
        switcher = findWidgetById(R.id.switcherImg);
    }
}
