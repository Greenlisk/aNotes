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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.green.anotes.data.NotesContract;
import com.example.green.anotes.data.NotesDBHelper;



public class ActiveNotesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private final String LOG_TAG = "ActiveNotesFragment";
    private final int LOADER_ID = 1;
    private ActiveNotesAdapter activeNotesAdapter;
    private NotesDBHelper dbHelper;
    ListView listView;
    EditText editText;
    public ActiveNotesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dbHelper = new NotesDBHelper(getContext());
        activeNotesAdapter = new ActiveNotesAdapter(getContext());
        final View rootView = inflater.inflate(R.layout.fragment_active_notes, container, false);
        listView = (ListView) rootView.findViewById(R.id.list_active_notes);
        listView.setAdapter(activeNotesAdapter);
        ImageButton imageButton = (ImageButton)rootView.findViewById(R.id.save_button);
        imageButton.setOnClickListener(new OnSave());
        editText = (EditText)rootView.findViewById(R.id.edit_text);
        getLoaderManager().initLoader(LOADER_ID, null, this);
        return rootView;
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
                String text = editText.getText().toString();
                View view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_active_notes, listView, false);
                TextView textView = (TextView)view.findViewById(R.id.note_field);
                textView.setText(text);
                listView.addFooterView(view);
                editText.setText("");
                listView.setSelection(listView.getCount());
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
        activeNotesAdapter.changeCursor(data);
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

        public ViewHolder(View parent) {
            textView = (TextView)parent.findViewById(R.id.note_field);
        }
    }

    private class ActiveNotesAdapter extends CursorAdapter{

        public ActiveNotesAdapter(Context context) {
            super(context, null, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {

            View view = LayoutInflater.from(context).inflate(
                    R.layout.list_item_active_notes, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder viewHolder =(ViewHolder)view.getTag();

            viewHolder.textView.setText(cursor.getString(1));

        }
    }


}
