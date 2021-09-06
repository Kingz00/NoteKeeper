package com.gads.notekeeper;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Jim.
 */

//Class to add details to the rows in CourseInfo table and NoteInfo table
public class DatabaseDataWorker {
    private SQLiteDatabase mDb;

    // A constructor that accepts a reference to a SQL database
    public DatabaseDataWorker(SQLiteDatabase db) {
        mDb = db;
    }

    //CourseInfo values
    public void insertCourses() {
        insertCourse("android_intents", "Android Programming with Intents");
        insertCourse("android_async", "Android Async Programming and Services");
        insertCourse("java_lang", "Java Fundamentals: The Java Language");
        insertCourse("java_core", "Java Fundamentals: The Core Platform");
    }

    //NoteInfo values
    public void insertSampleNotes() {
        insertNote("android_intents", "Dynamic intent resolution", "Wow, intents allow components to be resolved at runtime");
        insertNote("android_intents", "Delegating intents", "PendingIntents are powerful; they delegate much more than just a component invocation");

        insertNote("android_async", "Service default threads", "Did you know that by default an Android Service will tie up the UI thread?");
        insertNote("android_async", "Long running operations", "Foreground Services can be tied to a notification icon");

        insertNote("java_lang", "Parameters", "Leverage variable-length parameter lists?");
        insertNote("java_lang", "Anonymous classes", "Anonymous classes simplify implementing one-use types");

        insertNote("java_core", "Compiler options", "The -jar option isn't compatible with with the -cp option");
        insertNote("java_core", "Serialization", "Remember to include SerialVersionUID to assure version compatibility");
    }

    //Method to insert rows to the course info table
    private void insertCourse(String courseId, String title) {
        ContentValues values = new ContentValues();
        values.put(NoteKeeperDatabaseContract.CourseInfoEntry.COLUMN_COURSE_ID, courseId);
        values.put(NoteKeeperDatabaseContract.CourseInfoEntry.COLUMN_COURSE_TITLE, title);

        long newRowId = mDb.insert(NoteKeeperDatabaseContract.CourseInfoEntry.TABLE_NAME, null, values);
    }

    //Method to insert rows to the note info table
     private void insertNote(String courseId, String title, String text) {
        ContentValues values = new ContentValues();
        values.put(NoteKeeperDatabaseContract.NoteInfoEntry.COLUMN_COURSE_ID, courseId);
        values.put(NoteKeeperDatabaseContract.NoteInfoEntry.COLUMN_NOTE_TITLE, title);
        values.put(NoteKeeperDatabaseContract.NoteInfoEntry.COLUMN_NOTE_TEXT, text);

        long newRowId = mDb.insert(NoteKeeperDatabaseContract.NoteInfoEntry.TABLE_NAME, null, values);
    }

}
