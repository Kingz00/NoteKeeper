package com.gads.notekeeper;

import android.provider.BaseColumns;

//Holds information about the SQL database
//Marked as final because it requires no inheritance by other classes
public final class NoteKeeperDatabaseContract {

    //Make a non-creatable instance with a private constructor
    private NoteKeeperDatabaseContract(){};

    //Nested class for the course info table
    //Implements the BaseColumns interface to make use of the PRIMARY KEY row identifier
    public static final class CourseInfoEntry implements BaseColumns {
        //Constant for the table name
        public static final String TABLE_NAME = "course_info";
        //Constant for the table columns
        public static final String COLUMN_COURSE_ID = "course_id";
        public static final String COLUMN_COURSE_TITLE = "course_title";

        //Constant for the SQL to create the Table
        // CREATE TABLE TABLE_NAME (Column1, Column2, ....)

        //Constant to create course info table
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY, " + COLUMN_COURSE_ID + " TEXT NOT NULL UNIQUE, " + COLUMN_COURSE_TITLE + " TEXT NOT NULL);";

        //NB: Column 1 has a storage class of type TEXT (because it is a text),
        // a constraint of NOT NULL(because it cannot be empty) and UNIQUE (because every course should be unique)

        //PRIMARY KEY provides unambiguous row identity and a table can only have one
    }


    //Nested class for the note info table
    public static final class NoteInfoEntry implements BaseColumns {
        //Constant for the table name
        public static final String TABLE_NAME = "note_info";
        //Constant for the table columns
        public static final String COLUMN_COURSE_ID = "course_id";
        public static final String COLUMN_NOTE_TITLE = "note_title";
        public static final String COLUMN_NOTE_TEXT = "note_text";

        //Constant to create the note info table
        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_NOTE_TITLE + " TEXT NOT NULL, " +
                        COLUMN_NOTE_TEXT + " TEXT, " +
                        COLUMN_COURSE_ID + " TEXT NOT NULL);";

    }
}
