package com.twotoasters.sectioncursoradapter.adapter.viewholder;

import android.view.View;

public abstract class ViewHolder {
    public final View rootView;

    public ViewHolder(View rootView) {
        this.rootView = rootView;
    }

    /**
     * @return Auto-Magicly infers your return type. No casting necessary.
     */
    protected final <T extends View> T findWidgetById(int resId) {
        return (T) rootView.findViewById(resId);
    }
}
