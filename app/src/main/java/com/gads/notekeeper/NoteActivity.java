package com.gads.notekeeper;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    //A constant for the putExtra gotten from NoteListActivity
    public static final String NOTE_POSITION = "com.gads.notekeeper.NOTE_POSITION";
    public static final int POSITION_NOT_SET = -1;
    private NoteInfo mNote;
    private boolean mIsNewNote;
    private Spinner mSpinnerCourses;
    private EditText mTextNoteTitle;
    private EditText mTextNoteText;
    private int mNotePosition;
    private boolean mIsCancelling;
    private NoteActivityViewModel mViewModel;

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

        //Get a list of courses from the DataManager
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        //Adapter to associate the list with the spinner
        ArrayAdapter<CourseInfo> adaperCourses = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        adaperCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCourses.setAdapter(adaperCourses);

        readDisplayStateValues();
        saveOriginalNoteValues();

        mTextNoteTitle = findViewById(R.id.text_note_title);
        mTextNoteText = findViewById(R.id.text_note_text);

        if (!mIsNewNote)
            displayNote(mSpinnerCourses, mTextNoteTitle, mTextNoteText);

    }

    private void saveOriginalNoteValues() {
        if (mIsNewNote)
            return;
        mViewModel.mOriginalNoteCourseId = mNote.getCourse().getCourseId();
        mViewModel.mOriginalNoteTitle = mNote.getTitle();
        mViewModel.mOriginalNoteText = mNote.getText();
    }

    private void displayNote(Spinner spinnerCourses, EditText textNoteTitle, EditText textNoteText) {
        //Get list of courses from the DataManager
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        //Get the index of Note's course from the list
        int courseIndex = courses.indexOf(mNote.getCourse());
        //Populating the spinner with the course
        spinnerCourses.setSelection(courseIndex);
        //Getting the note title and note text
        textNoteTitle.setText(mNote.getTitle());
        textNoteText.setText(mNote.getText());
    }

    private void readDisplayStateValues() {
        // Getting the note based on user selection from the NoteInfo class through Intent passed from the NoteListActivity
        Intent intent = getIntent();
        // To get the note that corresponds to the user selection without Parcelable
        int position = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);

        //Boolean Code for new Note
        mIsNewNote = position == POSITION_NOT_SET;

        if (mIsNewNote){
            createNewNote();
        }else {
            //Condition to get note based on its position in the DataManager
            mNote = DataManager.getInstance().getNotes().get(position);
        }
    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        mNotePosition = dm.createNewNote();
        mNote = dm.getNotes().get(mNotePosition);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsCancelling){
            if (mIsNewNote){
                DataManager.getInstance().removeNote(mNotePosition);
            } else {
                //Code to restore the original values on the note if the user is cancelling
                storePreviousNoteValues();
            }
        }else {
        savenote();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null)
            mViewModel.saveState(outState);
    }

    private void storePreviousNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(mViewModel.mOriginalNoteCourseId);
        mNote.setCourse(course);
        mNote.setTitle(mViewModel.mOriginalNoteTitle);
        mNote.setText(mViewModel.mOriginalNoteText);
    }

    private void savenote() {
        mNote.setCourse((CourseInfo) mSpinnerCourses.getSelectedItem());
        mNote.setTitle(mTextNoteTitle.getText().toString());
        mNote.setText(mTextNoteText.getText().toString());
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
        }

        return super.onOptionsItemSelected(item);
    }

    //Gets called only when the menu is initially displayed.
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_next);
        //get the index of the very last note on the list
        int lastNoteIndex = DataManager.getInstance().getNotes().size() - 1;
        //Disables the menu item for action_next  on the last note in the list
        item.setEnabled(mNotePosition < lastNoteIndex);

        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        //Save the changes made from the previous note
        savenote();
        //Increment mNote position to get the next note
        ++mNotePosition;
        //Get the note that corresponds to the position from the DataManager
        mNote = DataManager.getInstance().getNotes().get(mNotePosition);

        //saves the original values of the note in the case where the user cancels
        saveOriginalNoteValues();
        //Call displayNote to display the note obtained from the DataManager
        displayNote(mSpinnerCourses, mTextNoteTitle, mTextNoteText);
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
}
