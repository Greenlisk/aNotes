package com.example.green.anotes.data;

import android.provider.BaseColumns;

/**
 * Created by green on 3/21/16.
 */
public class NotesContract {
    public static final class NoteEntry implements BaseColumns {
        public static final String TABLE_NAME = "notes";
        public static final String DATE = "date";
        public static final String NOTE = "note";
        public static final String REMOVED = "removed";
    }
}
