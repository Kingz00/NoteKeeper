package com.gads.notekeeper;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;

public class NoteUploaderJobService extends JobService {

    public static final String EXTRA_DATA_URI = "com.gads.notekeeper.extras.DATA_URI";
    private NoteUploader mNoteUploader;

    public NoteUploaderJobService() {
    }

    // indicates that the work should begin
    @Override
    public boolean onStartJob(JobParameters params) {

        // instance of the NoteUploader class
        mNoteUploader = new NoteUploader(this);

        // use AsyncTask to run background work since JobScheduler runs on the Main Thread
        AsyncTask<JobParameters, Void, Void> task = new AsyncTask<JobParameters, Void, Void>() {
            @Override
            protected Void doInBackground(JobParameters... backgroundParams) {

                /** JobParameters contains a variety of configuration and identification
                 * data about the job. Thus includes the PersistableBundle associated with
                 * the JobInfo extras when scheduling the Job
                 */
                JobParameters jobParams = backgroundParams[0];

                String stringDataUri = jobParams.getExtras().getString(EXTRA_DATA_URI);
                Uri dataUri = Uri.parse(stringDataUri);
                mNoteUploader.doUpload(dataUri);

                // code that tells the JobScheduler that background work has finished
                // NB: the if statement added ensures that the jobFinished method is not called
                // when the work is manually stopped
                if (!mNoteUploader.isCanceled())
                    jobFinished(jobParams, false);

                return null;
            }
        };

        task.execute(params);
        /** The return statement of true tells the JobScheduler that the process needs to be allowed
         * to keep running until the background work is finished
         */
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // stopping the note upload work by calling the cancel method in the onStopJob
        mNoteUploader.cancel();
        /** The return statement of true tells the JobScheduler that the work been done needs to
         * be rescheduled
         */
        return true;
    }

}