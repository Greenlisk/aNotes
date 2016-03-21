package com.example.green.anotes;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.zip.Inflater;


public class ActiveNotesFragment extends Fragment {
    private final int LOADER_ID = 1;
    private ActiveNotesAdapter activeNotesAdapter;

    public ActiveNotesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_active_notes, container, false);
    }



    @Override
    public void onDetach() {
        super.onDetach();
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
