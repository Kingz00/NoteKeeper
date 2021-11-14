package com.gads.notekeeper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NoteReminderReceiver extends BroadcastReceiver {

    public static final String EXTRA_NOTE_TITLE = "com.gads.notekeeper.extra.NOTE_TITLE";
    public static final String EXTRA_NOTE_TEXT = "com.gads.notekeeper.extra.NOTE_TEXT";
    public static final String EXTRA_NOTE_ID = "com.gads.notekeeper.extra.NOTE_ID";

    // receives the broadcast intent
    @Override
    public void onReceive(Context context, Intent intent) {
        // obtain the values for the reminder notification from the intent extras
        String noteTitle = intent.getStringExtra(EXTRA_NOTE_TITLE);
        String noteText = intent.getStringExtra(EXTRA_NOTE_TEXT);
        int noteId = intent.getIntExtra(EXTRA_NOTE_ID, 0);

        NoteReminderNotification.createNotificationChannel(context);
        NoteReminderNotification.notify(context, noteTitle, noteText, noteId);
    }
}