package com.twotoasters.sectioncursoradapter.adapter.viewholder;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;

public abstract class SViewHolder extends ViewHolder {

    public SViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * @return Auto-Magicly infers your return type. No casting necessary.
     */
    protected final <T extends View> T findWidgetById(int resId) {
        return (T) itemView.findViewById(resId);
    }
}
