package com.twotoasters.sectioncursoradapter.scrolllistener;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.RecyclerView.RecycledViewPool;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.FrameLayout;

import com.twotoasters.sectioncursoradapter.adapter.datahandler.SectionDataWrapper;

/**
 * While attached to the RecyclerView all sections will stick to the top.
 * These "sticky" views will be built using the adapter on the RecyclerView.
 *
 * This currently only supports vertical scrolling. Please make a pull request if you would like to see it support more stuff.
 */
public class StickySectionScrollListener extends OnScrollListener {

    private SectionDataWrapper<?, ?, ?> mDataWrapper;
    private FrameLayout mStickyLayout;

    @Nullable private ViewHolder mCurrentViewHolder;
    @Nullable private ViewHolder mNextViewHolder;

    private int mCurrentPosition = RecyclerView.NO_POSITION;
    private int mNextPosition = RecyclerView.NO_POSITION;

    private Class<?> mAdapterClass;
    private RecyclerView mRecyclerView;

    public StickySectionScrollListener(FrameLayout stickyLayout, SectionDataWrapper<?, ?, ?> dataWrapper) {
        this.mDataWrapper = dataWrapper;
        mStickyLayout = stickyLayout;
    }

    public SectionDataWrapper<?, ?, ?> getDataWrapper() {
        return mDataWrapper;
    }

    /**
     * Calls {@link #clean()} before setting new {@link SectionDataWrapper}. This will remove any stuck header.
     */
    public void setDataWrapper(SectionDataWrapper<?, ?, ?> dataWrapper) {
        clean();
        this.mDataWrapper = dataWrapper;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        checkAdapter(recyclerView);
        checkRecyclerView(recyclerView);

        int sectionPosition = getCurrentSectionPosition(recyclerView);

        attachCurrentViewHolder(recyclerView, sectionPosition);
        attachNextViewHolder(recyclerView, sectionPosition + 1);

        translateSections(recyclerView);
    }

    private int getTopPosition(RecyclerView recyclerView) {
        View topView = recyclerView.getLayoutManager().getChildAt(0);
        return recyclerView.getChildAdapterPosition(topView);
    }

    private int getCurrentSectionPosition(RecyclerView recyclerView) {
        int listPosition = getTopPosition(recyclerView);
        return mDataWrapper.getSectionPosition(listPosition);
    }

    ////////////////////////
    // ViewHolder Binding //
    ////////////////////////

    private void attachCurrentViewHolder(RecyclerView recyclerView, int sectionPosition) {
        int listPosition = mDataWrapper.getPositionForSection(sectionPosition);

        if (listPosition == RecyclerView.NO_POSITION) {
            recycleViewHolder(recyclerView, mCurrentViewHolder);
            mCurrentViewHolder = null;
            mCurrentPosition = RecyclerView.NO_POSITION;
        } else if (mCurrentViewHolder == null || mCurrentPosition != listPosition) {
            recycleViewHolder(recyclerView, mCurrentViewHolder);
            mCurrentViewHolder = attachViewHolder(recyclerView, listPosition);
            mCurrentPosition = listPosition;
        }
    }

    private void attachNextViewHolder(RecyclerView recyclerView, int sectionPosition) {
        int listPosition = mDataWrapper.getPositionForSection(sectionPosition);

        if (listPosition == RecyclerView.NO_POSITION || mDataWrapper.getItemCount() == listPosition) {
            recycleViewHolder(recyclerView, mNextViewHolder);
            mNextViewHolder = null;
            mNextPosition = RecyclerView.NO_POSITION;
        } else if (mNextViewHolder == null || mNextPosition != listPosition) {
            recycleViewHolder(recyclerView, mNextViewHolder);
            mNextViewHolder = attachViewHolder(recyclerView, listPosition);
            mNextPosition = listPosition;
        }
    }

    private ViewHolder attachViewHolder(RecyclerView recyclerView, int listPosition) {
        ViewHolder viewHolder = getViewHolderForPosition(recyclerView, listPosition);
        mStickyLayout.addView(viewHolder.itemView);
        return viewHolder;
    }

    //////////////////////////
    // ViewHolder Animation //
    //////////////////////////

    private void translateSections(RecyclerView recyclerView) {
        if ((mCurrentViewHolder == null || mNextPosition -1 != getTopPosition(recyclerView)) && mNextViewHolder != null) {
            mNextViewHolder.itemView.setVisibility(View.INVISIBLE);
            if (mCurrentViewHolder != null && mCurrentViewHolder.itemView.getTranslationY() != 0)
                mCurrentViewHolder.itemView.setTranslationY(0);
        } else if (mCurrentViewHolder != null && mNextViewHolder != null) {
            View viewAboveNext = recyclerView.getLayoutManager().getChildAt(0);
            View currentView = mCurrentViewHolder.itemView;
            View nextView = mNextViewHolder.itemView;

            int overhang = viewAboveNext.getTop() + viewAboveNext.getHeight();
            if (overhang > mCurrentViewHolder.itemView.getHeight() || viewAboveNext == currentView) {
                if (currentView.getTranslationY() != 0) currentView.setTranslationY(0);
                nextView.setVisibility(View.INVISIBLE);
            } else {
                nextView.setVisibility(View.VISIBLE);

                currentView.setTranslationY(overhang - currentView.getHeight());
                nextView.setTranslationY(overhang);
            }
        } else if (mCurrentViewHolder != null) {
            if (mCurrentViewHolder != null && mCurrentViewHolder.itemView.getTranslationY() != 0)
                mCurrentViewHolder.itemView.setTranslationY(0);
        }
    }

    //////////////
    // Clean up //
    //////////////

    public void clean() {
        recycleViewHolder(mRecyclerView, mCurrentViewHolder);
        recycleViewHolder(mRecyclerView, mNextViewHolder);

        mCurrentViewHolder = null;
        mNextViewHolder = null;
        mAdapterClass = null;
        mRecyclerView = null;

        mCurrentPosition = RecyclerView.NO_POSITION;
        mNextPosition = RecyclerView.NO_POSITION;
    }

    @SuppressWarnings("unchecked")
    private void recycleViewHolder(RecyclerView recyclerView, @Nullable ViewHolder viewHolder) {
        if (viewHolder == null) return;
        viewHolder.itemView.setTranslationY(0);

        mStickyLayout.removeView(viewHolder.itemView);
        if (recyclerView != null) {
            recyclerView.getAdapter().onViewRecycled(viewHolder);
            recyclerView.getRecycledViewPool().putRecycledView(viewHolder);
        }
    }

    @SuppressWarnings("unchecked")
    private ViewHolder getViewHolderForPosition(RecyclerView recyclerView, int listPosition) {
        Adapter adapter = recyclerView.getAdapter();
        RecycledViewPool pool = recyclerView.getRecycledViewPool();

        int viewType = adapter.getItemViewType(listPosition);
        ViewHolder viewHolder = pool.getRecycledView(viewType);

        if (viewHolder == null) viewHolder = adapter.createViewHolder(recyclerView, viewType);
        adapter.bindViewHolder(viewHolder, listPosition);

        return viewHolder;
    }

    private void checkAdapter(RecyclerView recyclerView) {
        if (mAdapterClass == null) mAdapterClass = recyclerView.getAdapter().getClass();
        if (mAdapterClass != recyclerView.getAdapter().getClass())
            throw new IllegalStateException("You cannot use this listener with a different adapter without calling clean first");
    }

    private void checkRecyclerView(RecyclerView recyclerView) {
        if (mRecyclerView == null) mRecyclerView = recyclerView;
        if (mRecyclerView != recyclerView)
            throw new IllegalStateException("You cannot use this listener with a different recyclerview without calling clean first");
    }
}
