package com.project.finalmobile.zaitunes.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static com.project.finalmobile.zaitunes.local.DatabaseContract.TABLE_NAME;
import static com.project.finalmobile.zaitunes.local.DatabaseContract.TrackColumns;

public class RatedTrackHelper {

    private static final String DATABASE_TABLE = TABLE_NAME;
    private static DatabaseHelper dbHelper;
    private static RatedTrackHelper INSTANCE;
    private SQLiteDatabase database;
    private boolean isOpen = false;

    private RatedTrackHelper(Context context) {
        dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public static RatedTrackHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (RatedTrackHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RatedTrackHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    public synchronized void open() throws SQLException {
        if (!isOpen) {
            database = dbHelper.getWritableDatabase();
            isOpen = true;
            Log.d("RatedTrackHelper", "Database opened");
        }
    }

    public synchronized void close() {
        if (isOpen) {
            if (database != null && database.isOpen()) {
                database.close();
            }
            dbHelper.close();
            isOpen = false;
            Log.d("RatedTrackHelper", "Database closed");
        }
    }

    public Cursor queryAll() {
        if (!isOpen) {
            open();
        }
        Log.d("RatedTrackHelper", "Querying all tracks");
        return database.query(DATABASE_TABLE, null, null, null, null, null, TrackColumns._ID + " DESC");
    }

    public Cursor queryById(String trackId) {
        if (!isOpen) {
            open();
        }
        Log.d("RatedTrackHelper", "Querying track with ID: " + trackId);
        String selection = TrackColumns.TRACK_ID + " = ?";
        String[] selectionArgs = {trackId};
        try {
            Cursor cursor = database.query(DATABASE_TABLE, null, selection, selectionArgs, null, null, null);
            Log.d("RatedTrackHelper", "Found " + (cursor != null ? cursor.getCount() : 0) + " tracks");
            return cursor;
        } catch (Exception e) {
            Log.e("RatedTrackHelper", "Error querying track: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public long insert(ContentValues values) {
        if (!isOpen) {
            open();
        }
        Log.d("RatedTrackHelper", "Inserting track: " + values.toString());
        try {
            long id = database.insert(DATABASE_TABLE, null, values);
            Log.d("RatedTrackHelper", "Inserted track with ID: " + id);
            return id;
        } catch (Exception e) {
            Log.e("RatedTrackHelper", "Error inserting track: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    public int update(String trackId, ContentValues values) {
        if (!isOpen) {
            open();
        }
        Log.d("RatedTrackHelper", "Updating track with ID: " + trackId);
        Log.d("RatedTrackHelper", "New values: " + values.toString());
        String selection = TrackColumns.TRACK_ID + " = ?";
        String[] selectionArgs = {trackId};
        try {
            int count = database.update(DATABASE_TABLE, values, selection, selectionArgs);
            Log.d("RatedTrackHelper", "Updated " + count + " tracks");
            if (count == 0) {
                Log.e("RatedTrackHelper", "No tracks were updated. Track ID: " + trackId);
            }
            return count;
        } catch (Exception e) {
            Log.e("RatedTrackHelper", "Error updating track: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    public int delete(String trackId) {
        if (!isOpen) {
            open();
        }
        Log.d("RatedTrackHelper", "Deleting track with ID: " + trackId);
        String selection = TrackColumns.TRACK_ID + " = ?";
        String[] selectionArgs = {trackId};
        try {
            int count = database.delete(DATABASE_TABLE, selection, selectionArgs);
            Log.d("RatedTrackHelper", "Deleted " + count + " tracks");
            return count;
        } catch (Exception e) {
            Log.e("RatedTrackHelper", "Error deleting track: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
}