package com.gads.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class CourseRecyclerAdapter extends RecyclerView.Adapter<CourseRecyclerAdapter.ViewHolder>{

    private final Context mContext;
    private final List<CourseInfo> mCourses;
    private final LayoutInflater mLayoutInflater;

    public CourseRecyclerAdapter(Context context, List<CourseInfo> courses) {
        mContext = context;
        mCourses = courses;
        //To create views from the layout resource
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull

    //Responsible for creating view holder instances
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Creating a view instance associated with each course
        View itemView = mLayoutInflater.inflate(R.layout.item_course_list, parent, false);
        return new ViewHolder(itemView);
    }

    //Responsible for associating data with the views
    @Override
    public void onBindViewHolder(@NonNull CourseRecyclerAdapter.ViewHolder holder, int position) {
        //Getting to course that corresponds with the position
        CourseInfo course = mCourses.get(position);
        //Getting each of the textViews from the ViewHolder
        holder.mTextCourse.setText(course.getTitle());
        //To set the position each time the view is associated with different data
        holder.mCurrentPosition = position;
    }

    //Indicates the number of data items
    @Override
    public int getItemCount() {
        return mCourses.size();
    }


    //Responsible for keeping references to views set at runtime for each item
    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTextCourse;
        public int mCurrentPosition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextCourse = (TextView) itemView.findViewById(R.id.text_course);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, mCourses.get(mCurrentPosition).getTitle(), Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }
}
