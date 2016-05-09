package com.twotoasters.sectioncursoradapter.adapter.datahandler;

public class DataWrapper<T, D extends DataHandler<T>> implements DataHandler<T> {
    private final D mWrapped;

    public D getWrapped() {
        return mWrapped;
    }

    public DataWrapper(D wrapped) {
        this.mWrapped = wrapped;
    }

    @Override
    public T getItemAtPosition(int position) {
        return mWrapped.getItemAtPosition(position);
    }

    @Override
    public int getItemCount() {
        return mWrapped.getItemCount();
    }

    @Override
    public long getItemId(int position) {
        return mWrapped.getItemId(position);
    }

    @Override
    public boolean hasStableIds() {
        return mWrapped.hasStableIds();
    }

    @Override
    public void registerObservable(DataChangeListener listener) {
        mWrapped.registerObservable(listener);
    }

    @Override
    public void unregisterObservable(DataChangeListener listener) {
        mWrapped.unregisterObservable(listener);
    }
}
