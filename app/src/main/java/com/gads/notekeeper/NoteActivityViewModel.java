package com.gads.notekeeper;

import android.os.Bundle;

import androidx.lifecycle.ViewModel;

//Using the ViewModel to manage activity InstanceState
public class NoteActivityViewModel extends ViewModel {
    public static final String ORIGINAL_NOTE_COURSE_ID = "com.gads.notekeeper.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.gads.notekeeper.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.gads.notekeeper.ORIGINAL_NOTE_TEXT";

    public String mOriginalNoteCourseId;
    public String mOriginalNoteTitle;
    public String mOriginalNoteText;
    //Field to tell if the ViewModel is a brand new instance
    public boolean mIsNewlyCreated = true;

    public void saveState(Bundle outState) {
        outState.putString(ORIGINAL_NOTE_COURSE_ID, mOriginalNoteCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE, mOriginalNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT, mOriginalNoteText);
    }

    public void restoreState(Bundle instate){
        mOriginalNoteCourseId = instate.getString(ORIGINAL_NOTE_COURSE_ID);
        mOriginalNoteTitle = instate.getString(ORIGINAL_NOTE_TITLE);
        mOriginalNoteText = instate.getString(ORIGINAL_NOTE_TEXT);
    }
}
