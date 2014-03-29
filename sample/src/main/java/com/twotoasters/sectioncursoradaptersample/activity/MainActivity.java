package com.twotoasters.sectioncursoradaptersample.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.content.ContentProvider;
import com.twotoasters.sectioncursoradaptersample.R;
import com.twotoasters.sectioncursoradaptersample.adapter.ToastersAdapter;
import com.twotoasters.sectioncursoradaptersample.database.ToasterModel;


public class MainActivity extends ActionBarActivity implements LoaderCallbacks<Cursor> {

    ToastersAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(R.string.two_toasters_team);

        SQLiteDatabase db = ActiveAndroid.getDatabase();
        String orderBy = ToasterModel.SHORT_JOB + " ASC, " + ToasterModel.NAME + " ASC";
        Cursor cursor = db.query(ToasterModel.TABLE_NAME, null, null, null, null, null, orderBy);

        mAdapter = new ToastersAdapter(this, cursor);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(mAdapter);

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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ContentProvider.createUri(ToasterModel.class, null), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        mAdapter.swapCursor(null);
    }
}
