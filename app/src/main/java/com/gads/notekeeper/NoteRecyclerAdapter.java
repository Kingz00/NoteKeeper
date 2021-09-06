package com.gads.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder>{

    private final Context mContext;
    private final List<NoteInfo> mNotes;
    private final LayoutInflater mLayoutInflater;

    public NoteRecyclerAdapter(Context context, List<NoteInfo> notes) {
        mContext = context;
        mNotes = notes;
        //To create views from the layout resource
        mLayoutInflater = LayoutInflater.from(mContext);
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
        //Getting to note that corresponds with the position
        NoteInfo note = mNotes.get(position);
        //Getting each of the textviews from the ViewHolder
        holder.mTextCourse.setText(note.getCourse().getTitle());
        holder.mTextTitle.setText(note.getTitle());
        //get the id associated with the note
        holder.mId = note.getId();
    }

    //Indicates the number of data items
    @Override
    public int getItemCount() {
        return mNotes.size();
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
