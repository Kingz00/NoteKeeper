package com.gads.notekeeper;


import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;

// a contract class for the NoteKeeper content provider
public final class NoteKeeperProviderContract {

    private NoteKeeperProviderContract(){}

    //NoteKeeper content provider Authority constant
    public static final String AUTHORITY = "com.gads.notekeeper.provider";
    //NoteKeeper content provider base URI constant
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);


    // protected interface for the courseId column
    protected interface CoursesIdColumns {
        public static final String COLUMN_COURSE_ID = "course_id";
    }


    // protected interface for courses columns
    protected interface CoursesColumns {
        //column constants
        public static final String COLUMN_COURSE_TITLE = "course_title";
    }

    // protected interface for notes columns
    protected interface NotesColumns {
        public static final String COLUMN_NOTE_TITLE = "note_title";
        public static final String COLUMN_NOTE_TEXT = "note_text";
    }



    public static final class Courses implements BaseColumns, CoursesColumns, CoursesIdColumns{
        // a path to expose the data for courses table
        public static final String PATH = "courses";
        /* content://com.gads.notekeeper.provider/courses
        NoteKeeper courses table URI constant
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }

    public static final class Notes implements BaseColumns, NotesColumns, CoursesIdColumns, CoursesColumns{
        // path for the notes table data
        public static final String PATH = "notes";
        // content://com.gads.notekeeper.provider/notes
        // notes table URI constant
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);

        // Uri constant to join the NoteInfo table with the CourseInfo table
        public static final String PATH_EXPANDED = "notes_expanded";
        public static final Uri CONTENT_EXPANDED_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH_EXPANDED);
    }
}
