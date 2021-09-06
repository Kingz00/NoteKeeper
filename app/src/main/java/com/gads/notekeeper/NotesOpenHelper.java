package com.gads.notekeeper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

//Provides methods to access the SQL database
public class NotesOpenHelper extends SQLiteOpenHelper {
    //SQLiteDatabase.CursorFactory factory customizes the behaviour of the database interaction
    //name refers to the filename that contains the database
    //version is the version number of the database

    //constant for the database filename
    public static final String DATABASE_NAME = "Notes.db";
    //integer constant for the database version
    public static final int DATABASE_VERSION = 1;
    public NotesOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Creates the SQL database if the database does not exist
    //Also creates the Database tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        //creating the tables
        db.execSQL(NoteKeeperDatabaseContract.CourseInfoEntry.SQL_CREATE_TABLE);
        db.execSQL(NoteKeeperDatabaseContract.NoteInfoEntry.SQL_CREATE_TABLE);

        //Adding data to the tables
        DatabaseDataWorker worker = new DatabaseDataWorker(db);
        worker.insertCourses();
        worker.insertSampleNotes();

    }

    //checks the database version and upgrades it if necessary
    //Preserves existing data in the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
