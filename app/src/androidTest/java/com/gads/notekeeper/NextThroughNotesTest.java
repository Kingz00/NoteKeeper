package com.gads.notekeeper;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static org.hamcrest.Matchers.*;
import androidx.test.espresso.contrib.DrawerActions;

import java.util.List;

import static org.junit.Assert.*;

public class NextThroughNotesTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void NextThroughNotes(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_notes));

        onView(withId(R.id.list_items)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        //For loop to loop through the notes obtained from the DataManager
        for(int index = 0; index < notes.size(); index++){
            NoteInfo note = notes.get(index);

            //Check if the values of the note obtained from the click event matches the note in the DataManager
            onView(withId(R.id.spinner_courses)).check(
                    matches(withSpinnerText(note.getCourse().getTitle()))
            );
            onView(withId(R.id.text_note_title)).check(
                    matches(withText(note.getTitle()))
            );
            onView(withId(R.id.text_note_text)).check(
                    matches(withText(note.getText()))
            );

            //test on the menu item action_next to move through the notes
            if (index < notes.size() - 1) //condition to ensure that the menu item is disabled on the last note
                onView(allOf(withId(R.id.action_next), isEnabled())).perform(click());
        }
        //test to see if the menu item action_next is enabled on the last note
        onView(withId(R.id.action_next)).check(matches(not(isEnabled())));
    }

}