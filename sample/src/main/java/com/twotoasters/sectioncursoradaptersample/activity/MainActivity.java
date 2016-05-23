package com.twotoasters.sectioncursoradaptersample.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.activeandroid.content.ContentProvider;
import com.twotoasters.sectioncursoradapter.adapter.SectionDataAdapter;
import com.twotoasters.sectioncursoradapter.adapter.datahandler.ArrayDataHandler;
import com.twotoasters.sectioncursoradapter.adapter.datahandler.CursorDataHandler;
import com.twotoasters.sectioncursoradapter.adapter.datahandler.SectionDataWrapper;
import com.twotoasters.sectioncursoradapter.scrolllistener.StickySectionScrollListener;
import com.twotoasters.sectioncursoradaptersample.R;
import com.twotoasters.sectioncursoradaptersample.adapter.ArrayAdapter;
import com.twotoasters.sectioncursoradaptersample.adapter.CursorAdapter;
import com.twotoasters.sectioncursoradaptersample.database.ToasterModel;


public class MainActivity extends ActionBarActivity implements LoaderCallbacks<Cursor> {
    private static final String KEY_ADAPTER_TYPE = "adapterType";
    private static final int ADAPTER_TYPE_CURSOR = 0;
    private static final int ADAPTER_TYPE_ARRAY = 1;

    private int mType = ADAPTER_TYPE_CURSOR;

    private RecyclerView recyclerView;
    private StickySectionScrollListener scrollListener;

    CursorAdapter mCursorAdapter;
    private CursorDataHandler mCursorHandler;

    private ArrayAdapter mArrayAdapter;
    private ArrayDataHandler<ToasterModel> mArrayHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(R.string.two_toasters_team);

        recyclerView = (RecyclerView) findViewById(R.id.listView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupDataHandlers();
        if (shouldSetupCursor(savedInstanceState)) {
            switchToCursorAdapter();
        } else {
            switchToArrayAdapter();
        }

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_ADAPTER_TYPE, mType);
    }

    private boolean shouldSetupCursor(@Nullable Bundle savedInstanceState) {
        mType = savedInstanceState != null
                ? savedInstanceState.getInt(KEY_ADAPTER_TYPE, ADAPTER_TYPE_CURSOR)
                : ADAPTER_TYPE_CURSOR;
        return mType == ADAPTER_TYPE_CURSOR;
    }

    private void setupDataHandlers() {
        mCursorHandler = new CursorDataHandler(null, 0);
        SectionDataWrapper<String, Cursor, CursorDataHandler> cursorWrapper = new SectionDataWrapper<>(mCursorHandler);

        mCursorAdapter = new CursorAdapter(cursorWrapper);
        mCursorHandler.setAdapter(mCursorAdapter);
        cursorWrapper.setSectionBuilder(mCursorAdapter);

        mArrayHandler = new ArrayDataHandler<>();
        SectionDataWrapper<String, ToasterModel, ArrayDataHandler<ToasterModel>> arrayWrapper = new SectionDataWrapper<>(mArrayHandler);

        mArrayAdapter = new ArrayAdapter(arrayWrapper);
        mArrayHandler.setAdapter(mArrayAdapter);
        arrayWrapper.setSectionBuilder(mArrayAdapter);
    }

    private void switchToCursorAdapter() {
        mType = ADAPTER_TYPE_CURSOR;
        recyclerView.setAdapter(mCursorAdapter);
        setupScrollListener(mCursorAdapter);
    }

    private void switchToArrayAdapter() {
        mType = ADAPTER_TYPE_ARRAY;
        recyclerView.setAdapter(mArrayAdapter);
        setupScrollListener(mArrayAdapter);
    }

    private void setupScrollListener(SectionDataAdapter<?, ?, ?> adapter) {
        if (scrollListener != null) {
            scrollListener.clean();
            recyclerView.removeOnScrollListener(scrollListener);
        }
        scrollListener = new StickySectionScrollListener((FrameLayout) recyclerView.getParent(), adapter.getDataHandler());
        recyclerView.addOnScrollListener(scrollListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mType == ADAPTER_TYPE_CURSOR) {
            menu.findItem(R.id.action_switch_to_cursor).setVisible(false);
            menu.findItem(R.id.action_switch_to_array).setVisible(true);
        } else {
            menu.findItem(R.id.action_switch_to_cursor).setVisible(true);
            menu.findItem(R.id.action_switch_to_array).setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_contact_us:
                ShareCompat.IntentBuilder.from(this)
                        .addEmailTo("general@twotoasters.com")
                        .startChooser();
                return true;

            case R.id.action_switch_to_cursor:
                switchToCursorAdapter();
                invalidateOptionsMenu();
                return true;

            case R.id.action_switch_to_array:
                switchToArrayAdapter();
                invalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String orderBy = ToasterModel.SHORT_JOB + " ASC, " + ToasterModel.NAME + " ASC";
        return new CursorLoader(this, ContentProvider.createUri(ToasterModel.class, null), null, null, null, orderBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorHandler.swapCursor(data);

        mArrayHandler.clear();
        int size = mCursorHandler.getItemCount();
        for (int i = 0; i < size; i++) {
            final ToasterModel toaster = new ToasterModel();
            toaster.loadFromCursor(mCursorHandler.getItemAtPosition(i));
            mArrayHandler.add(toaster);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorHandler.swapCursor(null);
        mArrayHandler.clear();
    }
}
