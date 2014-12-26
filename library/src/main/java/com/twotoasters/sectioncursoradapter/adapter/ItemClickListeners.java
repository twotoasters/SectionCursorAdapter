package com.twotoasters.sectioncursoradapter.adapter;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;

import com.twotoasters.sectioncursoradapter.adapter.viewholder.SViewHolder;
import com.twotoasters.sectioncursoradapter.util.ListAdapterUtils;

public class ItemClickListeners {

    /**
     * This is a convenience class. onItemClick will hand through the item for the position clicked.
     * Note: this adjusts the position param and comes through with header positions removed.
     * As such this will not work to receive header clicks.
     */
    public abstract static class NonSectioningOnItemClickListener<T> implements OnItemClickListener {

        @Override
        @Deprecated
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListAdapter listAdapter = ListAdapterUtils.getWrappedAdapter(parent);
            int adjustedPosition = ListAdapterUtils.getHeaderAdjustedPosition(parent, position);

            if (listAdapter instanceof NonSectioningArrayAdapter && adjustedPosition >= 0) {
                NonSectioningArrayAdapter<T, SViewHolder> adapter = (NonSectioningArrayAdapter<T, SViewHolder>) listAdapter;

                T item = adapter.getItem(adjustedPosition);
                onItemClick(parent, view, adjustedPosition, item, id);
            } else if (adjustedPosition >= 0) {
                throw new IllegalArgumentException("This listener can only be used with the UscArrayAdapter.");
            }
        }

        protected abstract void onItemClick(AdapterView<?> parent, View view, int position, T item, long id);
    }

    /**
     * This class can only be used in conjunction with the SectionArrayAdapter. It will error otherwise.
     */
    public static abstract class SectionArrayOnItemClickListener<K, V> implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int listPosition, long id) {
            ListAdapter listAdapter = ListAdapterUtils.getWrappedAdapter(parent);
            int adjustedPosition = ListAdapterUtils.getHeaderAdjustedPosition(parent, listPosition);

            if (listAdapter instanceof SectionArrayAdapter) {
                SectionArrayAdapter<K, V, SViewHolder, SViewHolder> adapter = (SectionArrayAdapter<K, V, SViewHolder, SViewHolder>) listAdapter;

                int sectionPosition = adapter.getSectionPosition(adjustedPosition);
                int itemPosition = adapter.getItemPosition(adjustedPosition);

                if (adapter.isSection(adjustedPosition)) {
                    K section = adapter.getSection(sectionPosition);
                    onSectionClick(parent, view, sectionPosition, section, id);
                } else if (adjustedPosition >= 0) {
                    V item = adapter.getItem(sectionPosition, itemPosition);
                    onItemInSectionClick(parent, view, sectionPosition, itemPosition, item, id);
                }
            } else {
                throw new IllegalArgumentException("This listener can only be used with the SectionListAdapter.");
            }
        }

        /**
         * @param parent - The AdapterView where the click happened.
         * @param view - The view within the AdapterView that was clicked (this will be a view provided by the adapter)
         * @param sectionPosition This is the position within the full list.
         * @param section The section object which is associated with the view that was clicked.
         * @param id - The row id of the item that was clicked.
         */
        public abstract void onSectionClick(AdapterView<?> parent, View view, int sectionPosition, K section, long id);

        /**
         * @param parent - The AdapterView where the click happened.
         * @param view - The view within the AdapterView that was clicked (this will be a view provided by the adapter)
         * @param sectionPosition This is the position within the full list.
         * @param itemPosition - This is the position within the full list.
         * @param item The item object which is associated with the view that was clicked.
         * @param id - The row id of the item that was clicked.
         */
        public abstract void onItemInSectionClick(AdapterView<?> parent, View view, int sectionPosition, int itemPosition, V item, long id);
    }

    /**
     * This class can only be used in conjunction with the SectionCursorAdapter. It will error otherwise.
     */
    public static abstract class SectionCursorOnItemClickListener<T> implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int listPosition, long id) {
            ListAdapter listAdapter = ListAdapterUtils.getWrappedAdapter(parent);
            int adjustedPosition = ListAdapterUtils.getHeaderAdjustedPosition(parent, listPosition);

            if (listAdapter instanceof SectionCursorAdapter) {
                SectionCursorAdapter<T, SViewHolder, SViewHolder> adapter = (SectionCursorAdapter<T, SViewHolder, SViewHolder>) listAdapter;

                int cursorPosition = adapter.getCursorPositionWithoutSections(adjustedPosition);

                if (adapter.isSection(adjustedPosition)) {
                    T section = (T) adapter.getItem(adjustedPosition);
                    onSectionClick(parent, view, adjustedPosition, section, id);
                } else if (cursorPosition != SectionCursorAdapter.NO_CURSOR_POSITION) {
                    T section = (T) adapter.getItem(adapter.getSectionForPosition(adjustedPosition));
                    Object itemObject = adapter.getItem(adjustedPosition);
                    onItemInSectionClick(parent, view, cursorPosition, itemObject, section, id);
                }
            } else {
                throw new IllegalArgumentException("This listener can only be used with the SectionListAdapter.");
            }
        }

        /**
         * @param parent - The AdapterView where the click happened.
         * @param view - The view within the AdapterView that was clicked (this will be a view provided by the adapter)
         * @param listPosition This is the listPosition minus any header views.
         * @param section The section object which is associated with the view that was clicked.
         * @param id - The row id of the item that was clicked.
         */
        public abstract void onSectionClick(AdapterView<?> parent, View view, int listPosition, T section, long id);

        /**
         * @param parent - The AdapterView where the click happened.
         * @param view - The view within the AdapterView that was clicked (this will be a view provided by the adapter)
         * @param cursorPosition - This is the position within the full list.
         * @param itemObject is whatever getItem returns from the adapter.
         * @param section
         * @param id - The row id of the item that was clicked.
         */
        public abstract void onItemInSectionClick(AdapterView<?> parent, View view, int cursorPosition, Object itemObject, T section, long id);
    }
}
