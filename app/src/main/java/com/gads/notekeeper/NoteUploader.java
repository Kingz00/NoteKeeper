package com.gads.notekeeper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.gads.notekeeper.NoteKeeperProviderContract.Notes;

public class NoteUploader {
    private final String TAG = getClass().getSimpleName();

    private final Context mContext;
    private boolean mCanceled;

    public NoteUploader(Context context) {
        mContext = context;
    }

    public boolean isCanceled() {
        return mCanceled;
    }

    public void cancel() {
        Log.i(TAG, ">>>*** UPLOAD CANCELED ***<<<");
        mCanceled = true;
    }

    public void doUpload(Uri dataUri) {
        String[] columns = {
                Notes.COLUMN_COURSE_ID,
                Notes.COLUMN_NOTE_TITLE,
                Notes.COLUMN_NOTE_TEXT
        };

        Cursor cursor = mContext.getContentResolver().query(dataUri, columns, null, null, null);
        int courseIdPos = cursor.getColumnIndex(Notes.COLUMN_COURSE_ID);
        int noteTitlePos = cursor.getColumnIndex(Notes.COLUMN_NOTE_TITLE);
        int noteTextPos = cursor.getColumnIndex(Notes.COLUMN_NOTE_TEXT);


        Log.i(TAG, ">>>*** UPLOAD START - " + dataUri + " ***<<<");
        mCanceled = false;
        while (!mCanceled && cursor.moveToNext()){
            String courseId = cursor.getString(courseIdPos);
            String noteTitle = cursor.getString(noteTitlePos);
            String noteText = cursor.getString(noteTextPos);

            if (!noteTitle.equals("")){
                Log.i(TAG, ">>>>Uploading Note<<<< " + courseId + "|" + noteTitle + "|" + noteText);
                simulateLongRunningWork();
            }
        }
        Log.i(TAG, ">>>*** UPLOAD FINISHED ***<<<");
        cursor.close();

    }

    private static void simulateLongRunningWork() {
        try {
            Thread.sleep(1000);
        }catch (Exception ex){}
    }
}
