package com.gads.notekeeper;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.gads.notekeeper.NoteKeeperProviderContract.Notes;

public class NoteBackup {
    public static final String ALL_COURSES = "ALL_COURSES";
    private static final String TAG = NoteBackup.class.getSimpleName();

    // static method for the backup operation
    public static void doBackup(Context context, String backupCourseId) {

        // Columns of the notes to be backed up from the Content Provider
        String[] columns = {
                Notes.COLUMN_COURSE_ID,
                Notes.COLUMN_NOTE_TITLE,
                Notes.COLUMN_NOTE_TEXT};

        // Selection criteria for either all notes to be backed up or a single note based on the
        // courseId passed in
        String selection = null;
        String[] selectionArgs = null;
        if (!backupCourseId.equals(ALL_COURSES)) {
            selection = Notes.COLUMN_COURSE_ID + " = ?";
            selectionArgs = new String[] {backupCourseId};
        }

        // Query of the notes from the Content Provider
        Cursor cursor = context.getContentResolver().query(Notes.CONTENT_URI, columns, selection, selectionArgs, null);
        int courseIdPos = cursor.getColumnIndex(Notes.COLUMN_COURSE_ID);
        int noteTitlePos = cursor.getColumnIndex(Notes.COLUMN_NOTE_TITLE);
        int noteTextPos = cursor.getColumnIndex(Notes.COLUMN_NOTE_TEXT);

        Log.i(TAG, ">>>*** BACKUP START - Thread: " + Thread.currentThread().getId() + "  ***<<<");

        // moving through all the returned rows from the query and writing them to LogCat
        while (cursor.moveToNext()){
            String courseId = cursor.getString(courseIdPos);
            String noteTitle = cursor.getString(noteTitlePos);
            String noteText = cursor.getString(noteTextPos);

            if (!noteTitle.equals("")) {
                Log.i(TAG, ">>>Backing Up Note<<< " + courseId + "|" + noteTitle + "|" + noteText);
                simulateLongRunningWork();
            }
        }
        Log.i(TAG, ">>>***   BACKUP COMPLETE   ***<<<");
        cursor.close();
    }

    private static void simulateLongRunningWork() {
        try {
            Thread.sleep(1000);
        }catch (Exception ex){}
    }
}
