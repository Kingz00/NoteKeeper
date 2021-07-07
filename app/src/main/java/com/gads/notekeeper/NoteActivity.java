package com.gads.notekeeper;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    //A constant for the putExtra gotten from NoteListActivity
    public static final String NOTE_INFO = "com.gads.notekeeper.NOTE_INFO";
    private NoteInfo mNote;
    private boolean mIsNewNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner spinnerCourses = findViewById(R.id.spinner_courses);

        //Get a list of courses from the DataManager
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        //Adapter to associate the list with the spinner
        ArrayAdapter<CourseInfo> adaperCourses = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        adaperCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(adaperCourses);

        readDisplayStateValues();

        EditText textNoteTitle = findViewById(R.id.text_note_title);
        EditText textNoteText = findViewById(R.id.text_note_text);

        if (!mIsNewNote)
            displayNote(spinnerCourses, textNoteTitle, textNoteText);

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
        mNote = intent.getParcelableExtra(NOTE_INFO);

        //Boolean Code for new Note
        mIsNewNote = mNote == null;
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
