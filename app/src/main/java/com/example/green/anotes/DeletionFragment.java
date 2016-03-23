package com.example.green.anotes;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;

import android.widget.ListView;
import android.widget.TextView;

import com.example.green.anotes.data.NotesContract;
import com.example.green.anotes.data.NotesDBHelper;
import java.util.HashMap;



public class DeletionFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private final String LOG_TAG = "DeletionFragment";
    private final int LOADER_ID = 2;
    private NotesDBHelper dbHelper;
    private DeletingNotesAdapter deletingNotesAdapter;
    HashMap<Integer, Boolean> notesIds = new HashMap<>();

    public DeletionFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_deletion, container, false);
        dbHelper = new NotesDBHelper(getContext());
        deletingNotesAdapter = new DeletingNotesAdapter(getContext());
        ListView listView = (ListView)rootView.findViewById(R.id.deletion_list);
        listView.setAdapter(deletingNotesAdapter);
        getLoaderManager().initLoader(LOADER_ID, null, this);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_deletion,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            new DBWriter().execute();
        }
        return true;
    }

    private class DBWriter extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = true;
            ContentValues cv = new ContentValues();
            cv.put(NotesContract.NoteEntry.REMOVED, 1);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            for (HashMap.Entry<Integer,Boolean> entry : notesIds.entrySet()){
                Log.v(LOG_TAG, "Key: " + entry.getKey() + " Value: " + entry.getValue());
                if(entry.getValue()) {
                    Log.v(LOG_TAG, "Entry:" + entry.getValue() + " " + entry.getKey());
                    result = 1 == db.update(NotesContract.NoteEntry.TABLE_NAME,
                            cv,
                            NotesContract.NoteEntry._ID + " == ? ",
                            new String[]{entry.getKey().toString()});
                }
            }

            return result;

        }

        @Override
        protected void onPostExecute(Boolean res) {
            if(res) {
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        }
    }




    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new NotesLoader(getContext(), dbHelper);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        deletingNotesAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        deletingNotesAdapter.changeCursor(null);
    }

    private static class NotesLoader extends CursorLoader {
        NotesDBHelper dbHelper;
        public NotesLoader(Context context, NotesDBHelper dbHelper) {
            super(context);
            this.dbHelper = dbHelper;
        }

        @Override
        public Cursor loadInBackground() {
            return dbHelper.getReadableDatabase().query(
                    NotesContract.NoteEntry.TABLE_NAME,
                    new String[]{NotesContract.NoteEntry._ID, NotesContract.NoteEntry.NOTE},
                    NotesContract.NoteEntry.REMOVED + " == 0 ",
                    null,
                    null,
                    null,
                    null
            );
        }


    }



    private class ViewHolder {
        public TextView textView;
        public CheckBox checkBox;
        public Integer id;

        public ViewHolder(View parent) {
            textView = (TextView)parent.findViewById(R.id.note_field);

            checkBox = (CheckBox)parent.findViewById(R.id.delete_check_box);

        }
    }

    private class DeletingNotesAdapter extends CursorAdapter {

        public DeletingNotesAdapter(Context context) {
            super(context, null, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            Log.v(LOG_TAG, "New View.....");
            View view = LayoutInflater.from(context).inflate(
                    R.layout.list_item_delete, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Log.v(LOG_TAG, "Bind View.....");
            ViewHolder viewHolder =(ViewHolder)view.getTag();

            viewHolder.textView.setText(cursor.getString(1));
            Log.v(LOG_TAG, "ID: " + cursor.getInt(0));
            viewHolder.id = cursor.getInt(0);
            Boolean id = notesIds.get(cursor.getInt(0));
            if( id != null){
                Log.v(LOG_TAG, "Setting " + cursor.getInt(0) + " as " + id);
                viewHolder.checkBox.setChecked(id);

            } else {
                viewHolder.checkBox.setChecked(false);
                notesIds.put(viewHolder.id, false);
            }
            viewHolder.checkBox.setOnClickListener(new CheckDeleteListener());


        }
    }

    private class CheckDeleteListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            View frame = (View)v.getParent();
            View item = (View)frame.getParent();
            ViewHolder viewHolder = (ViewHolder)item.getTag();

            Log.v(LOG_TAG, "ChosenID: " + viewHolder.id);
            Boolean check = notesIds.get(viewHolder.id);
            if(check) {
                notesIds.put(viewHolder.id, false);
            } else {
                notesIds.put(viewHolder.id, true);
            }
        }
    }



}
