package com.gads.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gads.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;

import java.util.List;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder>{

    private final Context mContext;
    private Cursor mCursor;
    private final LayoutInflater mLayoutInflater;
    private int mCoursePos;
    private int mNoteTitlePos;
    private int mIdPos;

    public NoteRecyclerAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        //To create views from the layout resource
        mLayoutInflater = LayoutInflater.from(mContext);
        //Method to get the positions of the columns in the cursor
        populateColumnPositions();
    }

    private void populateColumnPositions() {
        if (mCursor == null)
            return;
        // Get column indexes from mCursor
        mCoursePos = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        mNoteTitlePos = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        mIdPos = mCursor.getColumnIndex(NoteInfoEntry._ID);
    }

    public void changeCursor(Cursor cursor) {
        //First close cursor if it is not null
        if (mCursor != null)
            mCursor.close();
        //Assign mCursor received to the cursor field
        mCursor = cursor;
        //call populateCursorPositions to return the columns in the same order as the previous cursor
        populateColumnPositions();
        //notify RecyclerView that the data has changed
        notifyDataSetChanged();

    }

    @NonNull

    //Responsible for creating view holder instances
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Creating a view
        View itemView = mLayoutInflater.inflate(R.layout.item_note_list, parent, false);
        return new ViewHolder(itemView);
    }

    //Responsible for associating data with the views in the ViewHolder class
    @Override
    public void onBindViewHolder(@NonNull NoteRecyclerAdapter.ViewHolder holder, int position) {
        //move the cursor to the position being requested
        mCursor.moveToPosition(position);
        //Getting the values for the note that corresponds with the position
        String course = mCursor.getString(mCoursePos);
        String noteTitle = mCursor.getString(mNoteTitlePos);
        int id = mCursor.getInt(mIdPos);

        //Getting each of the textviews from the ViewHolder
        holder.mTextCourse.setText(course);
        holder.mTextTitle.setText(noteTitle);
        //get the id associated with the note
        holder.mId = id;
    }

    //Indicates the number of data items
    @Override
    public int getItemCount() {
        //check to see if the cursor is null and pass 0 as its item count if it is
        return mCursor == null ? 0 : mCursor.getCount();
    }


    //Responsible for keeping references to views set at runtime for each item
    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTextCourse;
        public final TextView mTextTitle;
        public int mId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextCourse = (TextView) itemView.findViewById(R.id.text_course);
            mTextTitle = (TextView) itemView.findViewById(R.id.text_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, NoteActivity.class);
                    intent.putExtra(NoteActivity.NOTE_ID, mId);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
