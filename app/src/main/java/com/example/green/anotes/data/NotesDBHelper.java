package com.example.green.anotes.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by green on 3/21/16.
 */
public class NotesDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    public NotesDBHelper(Context context) {
        super(context, NotesContract.NoteEntry.TABLE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + NotesContract.NoteEntry.TABLE_NAME +
                " ( " + NotesContract.NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NotesContract.NoteEntry.NOTE + " TEXT NOT NULL, " +
                NotesContract.NoteEntry.REMOVED + " INTEGER DEFAULT 0 )";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + NotesContract.NoteEntry.TABLE_NAME;
        db.execSQL(query);

    }
}
