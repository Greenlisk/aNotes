package com.example.green.anotes.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.green.anotes.R;


/**
 * Created by green on 4/26/16.
 */
public class RecyclerCursorAdapter extends RecyclerView.Adapter<RecyclerCursorAdapter.ResViewHolder> {
    private final String LOG_TAG = "ResCursorAdapter";
    Cursor cursor;

    public class ResViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView note;

        public ResViewHolder(View view){
            super(view);
            date = (TextView)view.findViewById(R.id.note_date);
            note = (TextView)view.findViewById(R.id.note_text);
        }
    }

    @Override
    public ResViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ResViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notes, parent, false));
    }

    @Override
    public void onBindViewHolder(ResViewHolder holder, int position) {
        cursor.moveToPosition(position);
        Log.v(LOG_TAG, cursor.getString(0) + " " + cursor.getString(1) + " " + cursor.getString(2));
        holder.date.setText(cursor.getString(2));
        holder.note.setText(cursor.getString(1));
    }

    @Override
    public int getItemCount() {
        if (cursor != null) {
            return cursor.getCount();
        }
        return 0;
    }

    public void swapCursor(Cursor cursor){
        this.cursor = cursor;
        notifyDataSetChanged();
    }


}
