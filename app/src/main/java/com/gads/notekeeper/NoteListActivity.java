package com.gads.notekeeper;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteListActivity extends AppCompatActivity {

    private NoteRecyclerAdapter mNoteRecyclerAdapter;
//    private NotesOpenHelper mOpenHelper;
//    private CoursesOpenHelper mCoursesOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(NoteListActivity.this, NoteActivity.class));
            }
        });

        initializeDisplayContent();
//        database();

    }

    private void initializeDisplayContent() {

        //Setting up the RecyclerView and LayoutManager
        final RecyclerView recyclerNotes = (RecyclerView) findViewById(R.id.list_notes);
        final LinearLayoutManager notesLayoutManager = new LinearLayoutManager(this);
        recyclerNotes.setLayoutManager(notesLayoutManager);

        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        mNoteRecyclerAdapter = new NoteRecyclerAdapter(this, null);
        recyclerNotes.setAdapter(mNoteRecyclerAdapter);
    }
//    public void database() throws SQLException{
//        mOpenHelper = new NotesOpenHelper(this);
//        mCoursesOpenHelper = new CoursesOpenHelper(this);
//        SQLiteDatabase notesDb = mOpenHelper.getReadableDatabase();
//        SQLiteDatabase coursesDb = mCoursesOpenHelper.getReadableDatabase();
//    }

    @Override
    protected void onResume() {
        super.onResume();
        mNoteRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}