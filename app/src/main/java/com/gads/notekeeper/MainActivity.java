package com.gads.notekeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;


import com.gads.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gads.notekeeper.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOADER_NOTES = 0;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private NoteRecyclerAdapter mNoteRecyclerAdapter;
    NavigationView mNavigationView;
    private RecyclerView mRecyclerItems;
    private LinearLayoutManager mNotesLayoutManager;
    private CourseRecyclerAdapter mCourseRecyclerAdapter;
    private GridLayoutManager mCoursesLayoutManager;
    private NotesOpenHelper mDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //instance of the SQLiteOpenHelper
        mDbOpenHelper = new NotesOpenHelper(this);

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.toolbar.setTitle(getString(R.string.app_name));
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NoteActivity.class));
            }
        });

        //Setting the default value for elements in the settings activity
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);

        DrawerLayout drawer = binding.drawerLayout;
        mNavigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_notes, R.id.nav_courses)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(mNavigationView, navController);

        initializeDisplayContent();

        navClickHandling();
    }

    private void navClickHandling() {
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NotNull MenuItem menuItem) {

                int id = menuItem.getItemId();

                if (id == R.id.nav_notes){
                    displayNotes();
                }
                else if (id == R.id.nav_courses){
                    displayCourses();
                }
                else if (id == R.id.nav_share){
                    //code to interact with the value for favorite social network
                    handleShare();
                }

                DrawerLayout drawer = binding.drawerLayout;
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void handleShare() {
        View view = findViewById(R.id.list_items);
        Snackbar.make(view,"Share to - " +
                PreferenceManager.getDefaultSharedPreferences(this).getString("user_favorite_social", ""),
                Snackbar.LENGTH_SHORT).show();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = binding.drawerLayout;
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void initializeDisplayContent() {
        //load data from SQL database called from the DataManager class
        DataManager.loadFromDatabase(mDbOpenHelper);

        //Setting up the RecyclerView and LayoutManager
        mRecyclerItems = (RecyclerView) findViewById(R.id.list_items);
        //Layout Manager for Notes
        mNotesLayoutManager = new LinearLayoutManager(this);
        mRecyclerItems.setLayoutManager(mNotesLayoutManager);
        //Layout Manager for Courses
        mCoursesLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.course_grid_span));

        //Populating the Notes RecyclerAdapter with the list of notes
        mNoteRecyclerAdapter = new NoteRecyclerAdapter(this, null);
        //Reference to the list of courses in the DataManager
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        //Populating the Course RecyclerAdapter with the list of courses
        mCourseRecyclerAdapter = new CourseRecyclerAdapter(this, courses);

        displayNotes();
    }

    private void displayNotes() {
        mRecyclerItems.setLayoutManager(mNotesLayoutManager);
        mRecyclerItems.setAdapter(mNoteRecyclerAdapter);
    }

    private void displayCourses() {
        mRecyclerItems.setLayoutManager(mCoursesLayoutManager);
        mRecyclerItems.setAdapter(mCourseRecyclerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //getting latest notes from the Database
        LoaderManager.getInstance(this).restartLoader(LOADER_NOTES, null, this);
        updateNavHeader();
    }

    private void loadNotes() {

    }

    private void updateNavHeader() {
        NavigationView navigationView = binding.navView;
        //Reference to NavigationView header
        View headerView = navigationView.getHeaderView(0);
        //Reference to the TextViews in the Navigation header
        TextView textUserName = headerView.findViewById(R.id.text_user_name);
        TextView textEmailAddress = headerView.findViewById(R.id.text_email_address);

        //Interacting with the Preference System in SettingsActivity
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = pref.getString("user_display_name", "");
        String emailAddress = pref.getString("user_email_address", "");

        textUserName.setText(userName);
        textEmailAddress.setText(emailAddress);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings){
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
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
        CursorLoader loader = null;
        if (id == LOADER_NOTES)
            loader = createNoteLoader();
        return loader;
    }

    private CursorLoader createNoteLoader() {
        return new CursorLoader(this){
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
                String noteOrderBy = NoteInfoEntry.COLUMN_COURSE_ID + "," + NoteInfoEntry.COLUMN_NOTE_TITLE;
                final String[] noteColumns = {NoteInfoEntry._ID,
                        NoteInfoEntry.COLUMN_COURSE_ID,
                        NoteInfoEntry.COLUMN_NOTE_TITLE};
                return db.query(NoteInfoEntry.TABLE_NAME, noteColumns,
                        null, null, null, null, noteOrderBy);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull @NotNull Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_NOTES)
            mNoteRecyclerAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull @NotNull Loader<Cursor> loader) {
        if (loader.getId() == LOADER_NOTES){
            mNoteRecyclerAdapter.changeCursor(null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //destroy the loader before leaving the activity
        LoaderManager.getInstance(this).destroyLoader(LOADER_NOTES);
    }
}