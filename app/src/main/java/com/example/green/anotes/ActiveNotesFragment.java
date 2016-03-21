package com.example.green.anotes;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.green.anotes.data.NotesContract;
import com.example.green.anotes.data.NotesDBHelper;


public class ActiveNotesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private final String LOG_TAG = "ActiveNotesFragment";
    private final int LOADER_ID = 1;
    private ActiveNotesAdapter activeNotesAdapter;
    private NotesDBHelper dbHelper ;

    public ActiveNotesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dbHelper = new NotesDBHelper(getContext());
        ActiveNotesAdapter adapter = new ActiveNotesAdapter(getContext());
        View rootView = inflater.inflate(R.layout.fragment_active_notes, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.list_active_notes);
        listView.setAdapter(adapter);
        EditText editText = (EditText) rootView.findViewById(R.id.edit_text);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.v(LOG_TAG, "Action: " + actionId);
                return false;
            }
        });
        return rootView;
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new NotesLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private class NotesLoader extends CursorLoader {
        public NotesLoader(Context context) {
            super(context);
        }

        @Override
        public Cursor loadInBackground() {
            return dbHelper.getReadableDatabase().query(
                    NotesContract.NoteEntry.TABLE_NAME,
                    new String[]{NotesContract.NoteEntry._ID, NotesContract.NoteEntry.NOTE},
                    NotesContract.NoteEntry.REMOVED + " > 0 ",
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
