package com.gads.notekeeper;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.gads.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.gads.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;
import com.gads.notekeeper.NoteKeeperProviderContract.Courses;
import com.gads.notekeeper.NoteKeeperProviderContract.Notes;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NoteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    //A constant for the putExtra gotten from NoteListActivity
    public static final String NOTE_ID = "com.gads.notekeeper.NOTE_ID";
    public static final String NOTE_SIZE = "com.gads.notekeeper.NOTE_SIZE";
    public static final int ID_NOT_SET = -1;
    public static final int LOADER_NOTES = 0;
    public static final int LOADER_COURSES = 1;
    private NoteInfo mNote;
    private boolean mIsNewNote;
    private Spinner mSpinnerCourses;
    private EditText mTextNoteTitle;
    private EditText mTextNoteText;
    private int mNoteId;
    private boolean mIsCancelling;
    private NoteActivityViewModel mViewModel;
    private NotesOpenHelper mDbOpenHelper;
    private Cursor mNoteCursor;
    private int mCourseIdPos;
    private int mNoteTitlePos;
    private int mNoteTextPos;
    private SQLiteDatabase mDb;
    private SimpleCursorAdapter mAdaperCourses;
    private boolean mCoursesQueryFinished;
    private boolean mNotesQueryFinished;
    private int mNoteSize;
    private Uri mNoteUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Code to get a reference to the ViewModelProvider
        ViewModelProvider viewModelProvider = new ViewModelProvider(getViewModelStore(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()));
        mViewModel = viewModelProvider.get(NoteActivityViewModel.class);

        if (mViewModel.mIsNewlyCreated && savedInstanceState != null)
            mViewModel.restoreState(savedInstanceState);

        mViewModel.mIsNewlyCreated = false;

        mSpinnerCourses = findViewById(R.id.spinner_courses);

        //instance of the SQLiteOpenHelper
        mDbOpenHelper = new NotesOpenHelper(this);


        //Adapter to associate the list with the spinner
        mAdaperCourses = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, null,
                new String[] {CourseInfoEntry.COLUMN_COURSE_TITLE},
                new int[] {android.R.id.text1}, 0);
        mAdaperCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCourses.setAdapter(mAdaperCourses);

        //method to load data into the SimpleCursorAdapter done in the background thread
        LoaderManager.getInstance(this).initLoader(LOADER_COURSES, null, this);
//        getLoaderManager().initLoader(LOADER_COURSES, null,  this);

        readDisplayStateValues();

        mTextNoteTitle = findViewById(R.id.text_note_title);
        mTextNoteText = findViewById(R.id.text_note_text);

        if (!mIsNewNote) {
            //load data for the note in the database from the LoaderManager in the Background thread
            LoaderManager.getInstance(this).initLoader(LOADER_NOTES, null, this);
        }
//            getLoaderManager().initLoader(LOADER_NOTES, null,  this);

    }

    //method for list of courses in the spinner
    private void loadCourseData() {

    }

    private void readDisplayStateValues() {
        // Getting the note based on user selection from the NoteInfo class through Intent passed from the NoteListActivity
        Intent intent = getIntent();
        // To get the note that corresponds to the user selection
        mNoteId = intent.getIntExtra(NOTE_ID, ID_NOT_SET);

        //Boolean Code for new Note
        mIsNewNote = mNoteId == ID_NOT_SET;

        if (mIsNewNote){
            createNewNote();
        }


        //Line below obsolete since notes are being read from the database
//        mNote = DataManager.getInstance().getNotes().get(mNoteId);
    }

    private void saveOriginalNoteValues() {
        if (mIsNewNote)
            return;
        mViewModel.mOriginalNoteCourseId = selectedCourseId();
        mViewModel.mOriginalNoteTitle = mTextNoteTitle.getText().toString();
        mViewModel.mOriginalNoteText = mTextNoteText.getText().toString();
//        mViewModel.mOriginalNoteCourseId = mNote.getCourse().getCourseId();
//        mViewModel.mOriginalNoteTitle = mNote.getTitle();
//        mViewModel.mOriginalNoteText = mNote.getText();
    }

    private void loadNoteData() {

    }

    private void displayNote() {
        //get the column values and pass to a String
        String courseId = mNoteCursor.getString(mCourseIdPos);
        String noteTitle = mNoteCursor.getString(mNoteTitlePos);
        String noteText = mNoteCursor.getString(mNoteTextPos);

        //getting the index of the CourseId for the selected note from the spinner's cursor
        int courseIndex = getIndexOfCourseId(courseId);
        //Populating the spinner with the course
        mSpinnerCourses.setSelection(courseIndex);
        //Getting the note title and note text
        mTextNoteTitle.setText(noteTitle);
        mTextNoteText.setText(noteText);
    }

    private int getIndexOfCourseId(String courseId) {
        //reference to the cursor used to populate the spinner
        Cursor cursor = mAdaperCourses.getCursor();
        //getting the column's position in the cursor that holds the CourseId
        int courseIdPos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID);
        //getting the course that corresponds with the note selected using the row index
        int courseRowIndex = 0;

        //iterating through the courses until the exact courseId is found
        boolean more = cursor.moveToFirst();
        while (more){
            String cursorCourseId = cursor.getString(courseIdPos);
            if (courseId.equals(cursorCourseId))
                break;
            courseRowIndex++;
            more = cursor.moveToNext();
        }

        return courseRowIndex;
    }

    private void createNewNote() {
        ContentValues values = new ContentValues();
        values.put(Notes.COLUMN_COURSE_ID, "");
        values.put(Notes.COLUMN_NOTE_TITLE, "");
        values.put(Notes.COLUMN_NOTE_TEXT, "");

        //using Async task to carryout the database interaction in the background
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
//                SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
//                mNoteId = (int) db.insert(NoteInfoEntry.TABLE_NAME, null, values);
//                return mNoteId;

                // using Content Provider to insert new rows to the database
                mNoteUri = getContentResolver().insert(Notes.CONTENT_URI, values);
                return mNoteUri;
            }
        };
        task.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsCancelling){
            if (mIsNewNote){
                //if user is cancelling remove created note from the database
                deleteNoteFromDatabase();
            } else {
                //Code to restore the original values on the note if the user is cancelling
                storePreviousNoteValues();
            }
        }else {
            if (TextUtils.isEmpty(mTextNoteTitle.getText()) && TextUtils.isEmpty(mTextNoteText.getText()))
                deleteNoteFromDatabase();
            saveNote();
        }
        LoaderManager.getInstance(this).destroyLoader(LOADER_NOTES);
        LoaderManager.getInstance(this).destroyLoader(LOADER_COURSES);
    }

    private void deleteNoteFromDatabase() {

        // deleting note from the SQLiteDatabase using the NoteKeeper Content Provider
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                return getContentResolver().delete(mNoteUri, null, null);
            }
        };
        task.execute();

//        final String selection = NoteInfoEntry._ID + " = ?";
//        final String[] selectionArgs = {Integer.toString(mNoteId)};
//
//        //using Async task to carryout the database interaction in the background
//        AsyncTask task = new AsyncTask() {
//            @Override
//            protected Object doInBackground(Object[] objects) {
//                SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
//                return db.delete(NoteInfoEntry.TABLE_NAME, selection, selectionArgs);
//            }
//        };
//        task.execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null)
            mViewModel.saveState(outState);
    }

    private void storePreviousNoteValues() {

        // Notes Table content Uri
        Uri uri = Notes.CONTENT_URI;

        final String selection = Notes._ID + " = ?";
        final String[] selectionArgs = {Integer.toString(mNoteId)};

        //getting the original note values from the ViewModel
        ContentValues values = new ContentValues();
        values.put(Notes.COLUMN_COURSE_ID, mViewModel.mOriginalNoteCourseId);
        values.put(Notes.COLUMN_NOTE_TITLE, mViewModel.mOriginalNoteTitle);
        values.put(Notes.COLUMN_NOTE_TEXT, mViewModel.mOriginalNoteText);

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                return getContentResolver().update(uri, values, selection, selectionArgs);
            }
        };
        task.execute();

//        CourseInfo course = DataManager.getInstance().getCourse(mViewModel.mOriginalNoteCourseId);
//        mNote.setCourse(course);
//        mNote.setTitle(mViewModel.mOriginalNoteTitle);
//        mNote.setText(mViewModel.mOriginalNoteText);
    }

    private void saveNote() {
        String courseId = selectedCourseId();
        String noteTitle = mTextNoteTitle.getText().toString();
        String noteText = mTextNoteText.getText().toString();

        saveNoteToDatabase(courseId, noteTitle, noteText);
    }

    private String selectedCourseId() {
        int selectedPosition = mSpinnerCourses.getSelectedItemPosition();
        Cursor cursor = mAdaperCourses.getCursor();
        cursor.moveToPosition(selectedPosition);

        //index of column that contains the courseId
        int courseIdPos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID);
        //pass the data at the index to a String
        String courseId = cursor.getString(courseIdPos);

        return courseId;
    }

    private void saveNoteToDatabase (String courseId, String noteTitle, String noteText) {

        //identifies the columns and their values
        ContentValues values = new ContentValues();
        values.put(Notes.COLUMN_COURSE_ID, courseId);
        values.put(Notes.COLUMN_NOTE_TITLE, noteTitle);
        values.put(Notes.COLUMN_NOTE_TEXT, noteText);


        // updating the SQLiteDatabase using the NoteKeeper Content Provider
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                return getContentResolver().update(mNoteUri, values, null, null);
            }
        };
        task.execute();


//        //selection criteria to identify which note to update
//        final String selection = NoteInfoEntry._ID + " = ?";
//        final String[] selectionArgs = {Integer.toString(mNoteId)};
//
//        //identifies the columns and their values
//        ContentValues values = new ContentValues();
//        values.put(NoteInfoEntry.COLUMN_COURSE_ID, courseId);
//        values.put(NoteInfoEntry.COLUMN_NOTE_TITLE, noteTitle);
//        values.put(NoteInfoEntry.COLUMN_NOTE_TEXT, noteText);
//
//        //using Async task to carryout the database interaction in the background
//        AsyncTask task = new AsyncTask() {
//            @Override
//            protected Object doInBackground(Object[] objects) {
//                SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
//                //code to update the note in the database
//                return db.update(NoteInfoEntry.TABLE_NAME, values, selection, selectionArgs);
//            }
//        };
//        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            sendEmail();
            return true;
        }else if (id == R.id.action_cancel){
            mIsCancelling = true;
            finish();
        } else if(id == R.id.action_next){
            moveNext();
        } else if (id == R.id.action_set_reminder){
            showReminderNotification();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showReminderNotification() {
        String noteTitle = mTextNoteTitle.getText().toString();
        String noteText = mTextNoteText.getText().toString();

        // extract the noteId from the rowUri
        int noteId = (int) ContentUris.parseId(mNoteUri);

        NoteReminderNotification.createNotificationChannel(this);
        NoteReminderNotification.notify(this, noteTitle, noteText, noteId);
    }

    //Gets called only when the menu is initially displayed.
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_next);
        Intent intent = getIntent();
        mNoteSize = intent.getIntExtra(NOTE_SIZE, 0);
        //get the index of the very last note on the list
        int lastNoteIndex = mNoteSize - 1;

//        int lastNoteIndex = DataManager.getInstance().getNotes().size() - 1;

            //Disables the menu item for action_next  on the last note in the list
        item.setEnabled(mNoteId < lastNoteIndex);

        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        //Save the changes made to the note
        saveNote();
        //Increment the note id
        ++mNoteId;

        LoaderManager.getInstance(this).restartLoader(LOADER_COURSES, null,  this);
        LoaderManager.getInstance(this).restartLoader(LOADER_NOTES, null, this);

        //allows the onPrepareOptionsMenu to get called whenever the next note is displayed
        invalidateOptionsMenu();
    }

    private void sendEmail() {
        CourseInfo course = (CourseInfo) mSpinnerCourses.getSelectedItem();
        String subject = mTextNoteTitle.getText().toString();
        String text = "Checkout what I learned in the pluralsight course \"" +
                course.getTitle() + "\"\n" + mTextNoteText.getText();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    @NonNull
    @NotNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable @org.jetbrains.annotations.Nullable Bundle args) {
        //querying the SQLite database using the CursorLoader
        CursorLoader loader = null;
        //checks the id passed in the onCreate method for LoaderManager
        if (id == LOADER_NOTES)
            loader = createLoaderNotes();
        else if (id == LOADER_COURSES)
            loader = createLoaderCourses();
        return loader;
    }
    //querying SQLite database for list of courses on the background
    private CursorLoader createLoaderCourses() {
        //create a boolean value to check if the courses in the spinner have been queried
        mCoursesQueryFinished = false;

        //uri for the courses class in the NoteKeeper content provider
        Uri uri = Courses.CONTENT_URI;
        // change the columns to use the columns from the NoteKeeper content provider Contract class
        String[] courseColumns = {
                Courses.COLUMN_COURSE_TITLE,
                Courses.COLUMN_COURSE_ID,
                Courses._ID};
        return  new CursorLoader(this, uri, courseColumns, null, null,
                Courses.COLUMN_COURSE_TITLE);
    }

    //querying SQLite database for list of notes on the background
    private CursorLoader createLoaderNotes() {
        //create a boolean value to check if the notes have been queried
        mNotesQueryFinished = false;

        // using the rowUri to request the note from the NoteKeeper Content Provider
        String[] noteColumns = {
                Notes.COLUMN_COURSE_ID,
                Notes.COLUMN_NOTE_TITLE,
                Notes.COLUMN_NOTE_TEXT};

        mNoteUri = ContentUris.withAppendedId(Notes.CONTENT_URI, mNoteId);
        return new CursorLoader(this, mNoteUri, noteColumns, null, null, null);

//        // query the SQLiteDatabase from the NoteKeeper Content Provider
//        String[] noteColumns = {
//                Notes.COLUMN_COURSE_ID,
//                Notes.COLUMN_NOTE_TITLE,
//                Notes.COLUMN_NOTE_TEXT};
//
//        String selection = NoteInfoEntry._ID + " = ?";
//        String[] selectionArgs = {Integer.toString(mNoteId)};
//
//        return new CursorLoader(this, Notes.CONTENT_URI, noteColumns, selection, selectionArgs, null);

//        return new CursorLoader(this){
//            @Override
//            public Cursor loadInBackground() {
//                //Read note data from the database
//                mDb = mDbOpenHelper.getReadableDatabase();
//                //selection criteria to return a subset of the rows in the table
//                String selection = NoteInfoEntry._ID + " = ?";
//                String[] selectionArgs = {Integer.toString(mNoteId)};
//
//                String[] noteColumns = {NoteInfoEntry.COLUMN_COURSE_ID, NoteInfoEntry.COLUMN_NOTE_TITLE, NoteInfoEntry.COLUMN_NOTE_TEXT};
//
//                return mDb.query(NoteInfoEntry.TABLE_NAME, noteColumns, selection, selectionArgs,
//                        null, null, null);
//            }
//        };
    }

    //load note data
    @Override
    public void onLoadFinished(@NonNull @NotNull Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_NOTES) {
            mNotesQueryFinished = true;
            loadFinishedNotes(data);
        }
        else if (loader.getId() == LOADER_COURSES) {
            mAdaperCourses.changeCursor(data);
            mCoursesQueryFinished = true;
            //method to display the note after the courses in the spinner and the notes has been queried
            displayNoteWhenQueriesFinished();
        }
    }

    private void loadFinishedNotes(Cursor data) {
        mNoteCursor = data;
        //get the positions of the courseId, noteTitle and noteText columns in the cursor
        mCourseIdPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        mNoteTitlePos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        mNoteTextPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);

        //moveToNext is called to move to the first row in the table
        mNoteCursor.moveToNext();

        //method to display the note after the courses in the spinner and the notes has been queried
        displayNoteWhenQueriesFinished();
    }

    private void displayNoteWhenQueriesFinished() {
        if (mNotesQueryFinished && mCoursesQueryFinished) {
            displayNote();
            saveOriginalNoteValues();
        }
    }

    @Override
    public void onLoaderReset(@NonNull @NotNull Loader<Cursor> loader) {
        if (loader.getId() == LOADER_NOTES) {
            if (mNoteCursor != null)
                mNoteCursor.close();
        }
        else if (loader.getId() == LOADER_COURSES) {
            //close cursor for the  spinner
            mAdaperCourses.changeCursor(null);
        }
    }
}
