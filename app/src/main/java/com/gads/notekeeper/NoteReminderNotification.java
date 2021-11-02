package com.gads.notekeeper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.lang.annotation.Target;

public class NoteReminderNotification {

    // The unique identifier for this type of notification
    private static final String NOTIFICATION_TAG = "NoteReminder";
    private static final String CHANNEL_ID = "com.gads.notekeeper_CHANNEL_ID";

    /* Shows the notification, or updates a previously shown notification of this type,
       with the given parameters
     */

    public static void notify(final Context context, final String noteTitle,
                              final String  noteText, int noteId) {

        final Resources res = context.getResources();

        // This image is used as the notification's large icon (thumbnail).
        final Bitmap picture = BitmapFactory.decodeResource(res, R.drawable.logo);

        // Intent for handling click event on the notification
        Intent noteActivityIntent = new Intent(context, NoteActivity.class);
        noteActivityIntent.putExtra(NoteActivity.NOTE_ID, noteId);

        // Intent for backing up notes from the notification
        Intent backupServiceIntent = new Intent(context, NoteBackupService.class);
        backupServiceIntent.putExtra(NoteBackupService.EXTRA_COURSE_ID, NoteBackup.ALL_COURSES);

        // Set the notification content
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                // Set appropriate defaults for the notification light, sound and vibration.
                .setDefaults(Notification.DEFAULT_ALL)
                // Set required fields, including the small icon, the notification title and text.
                .setSmallIcon(R.drawable.notification_small_icon)
                .setContentTitle("Review note")
                .setContentText(noteText)

                // All fields below this line are optional.
                // Use a default priority (recognized on devices running Android
                // 4.1 or later)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                // Provide a large icon, shown with the notification in the
                // notification drawer on devices running Android 3.0 or later
                .setLargeIcon(picture)

                // Used by tools like screen readers
                .setTicker("Review note")

                // Using BigTextStyle
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(noteText).setBigContentTitle(noteTitle)
                        .setSummaryText("Review note"))

                // Show a number. This is useful when stacking notification of a single type
                // .setNumber(number)


                /** If this notification relates to a past or upcoming event, you
                * should set the relevant time information using the setWhen
                * method below. If this call is omitted, the notification's
                 * timestamp will be set to the time at which it was shown.
                * The sole argument to this method should be the notification
                * timestamp in milliseconds.
                */
                //.setWhen(...)

                // Set the pending intent to be initiated when the user touches
                // the notification.
                .setContentIntent(
                        PendingIntent.getActivity(context,
                                0,
                                noteActivityIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT)
                )

                // Code to add additional actions to the Notification
                .addAction(0,
                        "View all notes",
                        PendingIntent.getActivity(context,
                                0,
                                new Intent(context, MainActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT))
                .addAction(0,
                        "Backup notes",
                        PendingIntent.getService(context,
                                0,
                                backupServiceIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT))

                // Automatically dismiss the notification when it is touched
                .setAutoCancel(true);

        notify(context, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR){
            nm.notify(NOTIFICATION_TAG, 0, notification);
        }else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }

    // Cancels any notifications of this type previously shown
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR){
            nm.cancel(NOTIFICATION_TAG, 0);
        }else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }

    public static void createNotificationChannel(final Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name); // "channel_name"
            String description = context.getString(R.string.channel_description); // "channel_description"
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviours after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
