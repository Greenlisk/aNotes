package com.example.green.anotes;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.green.anotes.adapter.RecyclerCursorAdapter;
import com.example.green.anotes.data.NotesContract;
import com.example.green.anotes.data.NotesDBHelper;



public class ActiveNotesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private final static String LOG_TAG = "ActiveNotesFragment";
    private final int LOADER_ID = 1;
    private RecyclerCursorAdapter activeNotesAdapter;
    private NotesDBHelper dbHelper;
    RecyclerView listView;
    EditText editText;
    ActiveNotesFragment fragment = this;

    public ActiveNotesFragment() {

        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dbHelper = new NotesDBHelper(getContext());
        final View rootView = inflater.inflate(R.layout.fragment_active_notes, container, false);
        listView = (RecyclerView) rootView.findViewById(R.id.list_active_notes);
        activeNotesAdapter = new RecyclerCursorAdapter();

        listView.setAdapter(activeNotesAdapter);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerView.ItemAnimator animator = new DefaultItemAnimator();
        listView.setItemAnimator(animator);

        ImageButton imageButton = (ImageButton)rootView.findViewById(R.id.save_button);
        imageButton.setOnClickListener(new OnSave());
        editText = (EditText)rootView.findViewById(R.id.edit_text);
        getLoaderManager().initLoader(LOADER_ID, null, this);
        setHasOptionsMenu(true);
        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            Log.v(LOG_TAG, "Activity Resulted!!!");
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_archive, menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_archive) {
            Intent intent = new Intent(getContext(), ArchiveActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getContext(), DeletionActivity.class);
            startActivityForResult(intent, 0);
        }
        return true;
    }

    private class OnSave implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            String text = editText.getText().toString();
            if (!text.isEmpty()) {
                new DBWriter().execute(text);
            }

        }
    }


    private class DBWriter extends AsyncTask<String, Void, Boolean>{
        @Override
        protected Boolean doInBackground(String... params) {
            ContentValues cv = new ContentValues();
            cv.put(NotesContract.NoteEntry.NOTE, params[0]);
            cv.put(NotesContract.NoteEntry.REMOVED, 0);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            return -1 != db.insert(NotesContract.NoteEntry.TABLE_NAME,null, cv);

        }

        @Override
        protected void onPostExecute(Boolean res) {
            if(res){
                editText.setText("");
                getLoaderManager().restartLoader(LOADER_ID, null, fragment);
            }
        }
    }




    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "Creating loader...");
        return new NotesLoader(getContext(), dbHelper);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "Load finished...");
        activeNotesAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "Reseting Loader...");
        activeNotesAdapter.swapCursor(null);
    }

    private static class NotesLoader extends CursorLoader {
        NotesDBHelper dbHelper;
        public NotesLoader(Context context, NotesDBHelper dbHelper) {
            super(context);
            this.dbHelper = dbHelper;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor = dbHelper.getReadableDatabase().query(
                    NotesContract.NoteEntry.TABLE_NAME,
                    new String[]{NotesContract.NoteEntry._ID, NotesContract.NoteEntry.NOTE, NotesContract.NoteEntry.DATE, NotesContract.NoteEntry.REMOVED},
                    NotesContract.NoteEntry.REMOVED + " == 0 ",
                    null,
                    null,
                    null,
                    null
            );

            if(cursor.moveToFirst()) {
                do {
                    Log.v(LOG_TAG, cursor.getString(0) + " " + cursor.getString(1) + " " + cursor.getString(2));

                } while (cursor.moveToNext());
            }
            return cursor;
        }


    }



}
