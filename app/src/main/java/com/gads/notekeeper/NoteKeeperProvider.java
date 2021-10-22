package com.gads.notekeeper;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import com.gads.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.gads.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;
import com.gads.notekeeper.NoteKeeperProviderContract.Courses;
import com.gads.notekeeper.NoteKeeperProviderContract.CoursesIdColumns;
import com.gads.notekeeper.NoteKeeperProviderContract.Notes;

public class NoteKeeperProvider extends ContentProvider {

    private static final String MIME_VENDOR_TYPE = "vnd." + NoteKeeperProviderContract.AUTHORITY + ".";
    private NotesOpenHelper mDbOpenHelper;

    // creates an instance of the Uri Matcher class for interpreting Uris contained within the NoteKeeper content provider
    /* PS: NO_MATCH passed in indicates that any attempt to match a Uri that doesn't contain
           an Authority of a Path should return a value of NO_MATCH
     */
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final int COURSES = 0;

    public static final int NOTES = 1;

    public static final int NOTES_EXPANDED = 2;

    public static final int NOTES_ROW = 3;

    public static final int COURSES_ROW = 4;

    public static final int NOTES_EXPANDED_ROW = 5;

    //list of valid Uris
    static {
        // support for table Uris
        sUriMatcher.addURI(NoteKeeperProviderContract.AUTHORITY, Courses.PATH, COURSES);
        sUriMatcher.addURI(NoteKeeperProviderContract.AUTHORITY, Notes.PATH, NOTES);
        sUriMatcher.addURI(NoteKeeperProviderContract.AUTHORITY, Notes.PATH_EXPANDED, NOTES_EXPANDED);
        // support for row Uris
        sUriMatcher.addURI(NoteKeeperProviderContract.AUTHORITY, Notes.PATH + "/#", NOTES_ROW);
        sUriMatcher.addURI(NoteKeeperProviderContract.AUTHORITY, Courses.PATH + "/#", COURSES_ROW);
        sUriMatcher.addURI(NoteKeeperProviderContract.AUTHORITY, Notes.PATH_EXPANDED + "/#", NOTES_EXPANDED_ROW);
    }


    public NoteKeeperProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        long rowId = -1;
        // selection criteria for note to be deleted from the database
        String rowSelection = null;
        String[] rowSelectionArgs = null;

        int nRows = -1;

        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch){
            case NOTES:
                nRows = db.delete(NoteInfoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COURSES:
                nRows = db.delete(CourseInfoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case NOTES_EXPANDED:
                // throw exception saying this is a read-only table
                throw new UnsupportedOperationException("Read-only table");
            case NOTES_ROW:
                // extract the rowId from the uri
                rowId = ContentUris.parseId(uri);
                rowSelection = NoteInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[]{Long.toString(rowId)};

                nRows = db.delete(NoteInfoEntry.TABLE_NAME, rowSelection, rowSelectionArgs);
                break;
            case COURSES_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = CourseInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[]{Long.toString(rowId)};

                nRows = db.delete(CourseInfoEntry.TABLE_NAME, rowSelection, rowSelectionArgs);
                break;
            case NOTES_EXPANDED_ROW:
                // throw exception saying this is a read-only table
                throw new UnsupportedOperationException("Read-only table");
        }

        return nRows;
    }

    @Override
    public String getType(Uri uri) {
        String mimeType = null;

        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch){
            case COURSES:
                // mimetype for courses table uri that returns multiple rows
                // vnd.android.cursor.dir/vnd.com.gads.notekeeper.provider.courses
                mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                        MIME_VENDOR_TYPE + Courses.PATH;
                break;
            case NOTES:
                mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                        MIME_VENDOR_TYPE + Notes.PATH;
                break;
            case NOTES_EXPANDED:
                mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                        MIME_VENDOR_TYPE + Notes.PATH_EXPANDED;
                break;
            case COURSES_ROW:
                // mimetype for individual row uris in the courses table
                // vnd.android.cursor.item/vnd.com.gads.notekeeper.provider.courses
                mimeType = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                        MIME_VENDOR_TYPE + Courses.PATH;
                break;
            case NOTES_ROW:
                mimeType = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                        MIME_VENDOR_TYPE + Notes.PATH;
                break;
            case NOTES_EXPANDED_ROW:
                mimeType = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                        MIME_VENDOR_TYPE + Notes.PATH_EXPANDED;
                break;
        }

        return mimeType;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        // the SQLiteDatabase insert method returns a row Id that identifies the newly inserted row
        long rowId = -1;
        // the content provider insert method returns a uri that identifies the newly inserted row
        Uri rowUri = null;

        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case NOTES:
                rowId = db.insert(NoteInfoEntry.TABLE_NAME, null, values);
                // row Uri
                // content://com.gads.notekeeper.provider/notes/1
                rowUri = ContentUris.withAppendedId(Notes.CONTENT_URI, rowId);
                break;
            case COURSES:
                rowId = db.insert(CourseInfoEntry.TABLE_NAME, null, values);
                rowUri = ContentUris.withAppendedId(Courses.CONTENT_URI, rowId);
                break;
            case NOTES_EXPANDED:
                // throw exception saying this is a read-only table
                throw new UnsupportedOperationException("Read-only table");
        }

        return rowUri;

    }

    @Override
    public boolean onCreate() {
        // reference to SQLiteOpenHelper
        mDbOpenHelper = new NotesOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        long rowId = -1;
        String rowSelection = null;
        String[] rowSelectionArgs = null;
        // projection receives the array of column names from the SQLiteDatabase
        Cursor cursor = null;
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        // return a query of either the notes table or courses table based on
        // the Uri used to access the NoteKeeper content provider
        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch){
            case COURSES:
                cursor = db.query(CourseInfoEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case NOTES:
                cursor = db.query(NoteInfoEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case NOTES_EXPANDED:
                cursor = notesExpandedQuery(db, projection, selection, selectionArgs, sortOrder);
                break;
            case NOTES_ROW:
                // support for rowUris that reference rows with notes table
                // extracts the rowId from the rowUri
                rowId = ContentUris.parseId(uri);
                // build a selection criteria based on the rowId
                rowSelection = NoteInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[] {Long.toString(rowId)};
                cursor = db.query(NoteInfoEntry.TABLE_NAME, projection, rowSelection, rowSelectionArgs,
                        null, null, null);
                break;

            case COURSES_ROW:
                // extract rowId from the rowUri
                rowId = ContentUris.parseId(uri);
                // selection criteria for course rows
                rowSelection = CourseInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[]{Long.toString(rowId)};

                cursor = db.query(CourseInfoEntry.TABLE_NAME, projection, rowSelection, rowSelectionArgs,
                        null, null, null);
                break;
            case NOTES_EXPANDED_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = NoteInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[]{Long.toString(rowId)};

                cursor = notesExpandedQuery(db, projection, rowSelection, rowSelectionArgs, null);
                break;
        }


        return cursor;
    }

    private Cursor notesExpandedQuery(SQLiteDatabase db, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // code to table qualify columns that are part of both the notes table and the courses table
        String[] columns = new String[projection.length];
        for (int idx=0; idx< projection.length; idx++){
            columns[idx] = projection[idx].equals(BaseColumns._ID) || projection[idx].equals(CoursesIdColumns.COLUMN_COURSE_ID) ?
                    NoteInfoEntry.getQName(projection[idx]) : projection[idx];
        }


        //using a JOIN clause to get the Course Title from the CourseInfo Table that matches the CourseId in the NoteInfo Table
        //note_info JOIN course_info ON note_info.course_id = course_info.course_id
        String tablesWithJoin = NoteInfoEntry.TABLE_NAME + " JOIN " + CourseInfoEntry.TABLE_NAME + " ON " +
                NoteInfoEntry.getQName(NoteInfoEntry.COLUMN_COURSE_ID) + " = " +
                CourseInfoEntry.getQName(CourseInfoEntry.COLUMN_COURSE_ID);

        return db.query(tablesWithJoin, columns, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        long rowId = -1;
        //selection criteria to identify which note to update
        String rowSelection = null;
        String[] rowSelectionArgs = null;

        int nRows = -1;

        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch){
            case NOTES:
                nRows = db.update(NoteInfoEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case COURSES:
                nRows = db.update(CourseInfoEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case NOTES_EXPANDED:
                // throw exception saying this is a read-only table
                throw new UnsupportedOperationException("Read-only table");
            case NOTES_ROW:
                // extract the rowId for the uri
                rowId = ContentUris.parseId(uri);
                rowSelection = NoteInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[]{Long.toString(rowId)};
                nRows = db.update(NoteInfoEntry.TABLE_NAME, values, rowSelection, rowSelectionArgs);
                break;
            case COURSES_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = CourseInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[]{Long.toString(rowId)};
                nRows = db.update(CourseInfoEntry.TABLE_NAME, values, rowSelection, rowSelectionArgs);

                break;
            case NOTES_EXPANDED_ROW:
                // throw exception saying this is a read-only table
                throw new UnsupportedOperationException("Read-only table");
        }
        return nRows;
    }
}