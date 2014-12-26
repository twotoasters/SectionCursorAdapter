package com.twotoasters.sectioncursoradapter.adapter.viewholder;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class TextViewHolder extends SViewHolder {
    public final TextView text;

    /**
     * @param textViewResId if 0 or View.NO_ID it is assumed that itemView is a TextView.
     *                      If that is not the case an IllegalStateException will be thrown.
     */
    public TextViewHolder(View itemView, int textViewResId) {
        super(itemView);

        try {
            if (textViewResId == 0 || textViewResId == View.NO_ID) {
                //  If no custom field is assigned, assume the whole resource is a TextView
                text = (TextView) itemView;
            } else {
                //  Otherwise, find the TextView field within the layout
                text = findWidgetById(textViewResId);
            }
        } catch (ClassCastException e) {
            Log.e(getClass().getSimpleName(), "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "RecyclerArrayAdapter requires the resource ID to be a TextView", e);
        }
    }
}
