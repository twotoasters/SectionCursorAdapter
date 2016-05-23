package com.twotoasters.sectioncursoradaptersample.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.activeandroid.content.ContentProvider;
import com.twotoasters.sectioncursoradapter.adapter.datahandler.CursorDataHandler;
import com.twotoasters.sectioncursoradapter.adapter.datahandler.SectionDataWrapper;
import com.twotoasters.sectioncursoradapter.adapter.datahandler.SectionDataWrapper.SectionBuilder;
import com.twotoasters.sectioncursoradaptersample.R;
import com.twotoasters.sectioncursoradaptersample.adapter.ToasterAdapter;
import com.twotoasters.sectioncursoradaptersample.database.ToasterModel;

import java.util.SortedMap;


public class MainActivity extends ActionBarActivity implements LoaderCallbacks<Cursor>, SectionBuilder<String,Cursor,CursorDataHandler> {

    ToasterAdapter mAdapter;
    private CursorDataHandler mDataHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(R.string.two_toasters_team);

        mDataHandler = new CursorDataHandler(null, 0);
        SectionDataWrapper<String, Cursor, CursorDataHandler> dataWrapper = new SectionDataWrapper<>(mDataHandler);
        dataWrapper.setSectionBuilder(this);

        mAdapter = new ToasterAdapter(dataWrapper);
        mDataHandler.setAdapter(mAdapter);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.listView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        getSupportLoaderManager().initLoader(0, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_contact_us) {
            Uri uri = Uri.parse("mailto:general@twotoasters.com");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.getDataHandler().setSectionBuilder(null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String orderBy = ToasterModel.SHORT_JOB + " ASC, " + ToasterModel.NAME + " ASC";
        return new CursorLoader(this, ContentProvider.createUri(ToasterModel.class, null), null, null, null, orderBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mDataHandler.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDataHandler.swapCursor(null);
    }

    @Nullable
    @Override
    public SortedMap<Integer, String> buildSections(CursorDataHandler dataHandler) {
        return null; // Not going to give this a pre-built map so null is what we want to hand back.
    }

    @NonNull
    @Override
    public String getSectionFromItem(Cursor cursor) {
        final ToasterModel toaster = new ToasterModel();
        toaster.loadFromCursor(cursor);
        return toaster.shortJob;
    }
}
