package com.example.green.anotes;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.green.anotes.data.NotesContract;
import com.example.green.anotes.data.NotesDBHelper;

import java.util.ArrayList;



public class DeletionFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private final String LOG_TAG = "DeletionFragment";
    private final int LOADER_ID = 2;
    private NotesDBHelper dbHelper;
    private DeletingNotesAdapter deletingNotesAdapter;
    private ListView listView;
    private int positionToDel;

    public DeletionFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_deletion, container, false);
        dbHelper = new NotesDBHelper(getContext());
        deletingNotesAdapter = new DeletingNotesAdapter(getContext());
        listView = (ListView)rootView.findViewById(R.id.deletion_list);
        listView.setAdapter(deletingNotesAdapter);
        listView.setOnItemClickListener(new NoteListener());
        getLoaderManager().initLoader(LOADER_ID, null, this);
        return rootView;
    }

    private class NoteListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            positionToDel = position;
            Log.v(LOG_TAG, "Item clicked");
            new DBWriter().execute(parent.getAdapter().getItemId(position));
        }
    }

    private class DBWriter extends AsyncTask<Long, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Long... params) {
            ContentValues cv = new ContentValues();
            cv.put(NotesContract.NoteEntry.REMOVED, 1);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            return 0 < db.update(NotesContract.NoteEntry.TABLE_NAME,
                    cv,
                    NotesContract.NoteEntry._ID + " == ? ",
                    new String[]{Long.toString(params[0])});

        }

        @Override
        protected void onPostExecute(Boolean res) {
            if(res) {
                listView.removeViewAt(positionToDel);
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

    static class ViewHolder {
        public TextView textView;
        public ImageView imageButton;

        public ViewHolder(View parent) {
            textView = (TextView)parent.findViewById(R.id.note_field);
            imageButton = (ImageView)parent.findViewById(R.id.delete_button);
        }
    }

    private class DeletingNotesAdapter extends CursorAdapter {
        ArrayList<Integer> notesIds = new ArrayList<>();
        public DeletingNotesAdapter(Context context) {
            super(context, null, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {

            View view = LayoutInflater.from(context).inflate(
                    R.layout.list_item_notes, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder viewHolder =(ViewHolder)view.getTag();

            viewHolder.textView.setText(cursor.getString(1));
            viewHolder.imageButton.setVisibility(View.VISIBLE);

            notesIds.add(cursor.getInt(0));

        }

    }



}
