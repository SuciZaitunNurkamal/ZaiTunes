package com.project.finalmobile.zaitunes.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import static com.project.finalmobile.zaitunes.local.DatabaseContract.TABLE_NAME;
import static com.project.finalmobile.zaitunes.local.DatabaseContract.TrackColumns;

public class RatedTrackHelper {

    private static final String DATABASE_TABLE = TABLE_NAME;
    private static DatabaseHelper dbHelper;
    private static RatedTrackHelper INSTANCE;
    private SQLiteDatabase database;

    private RatedTrackHelper(Context context) {
        dbHelper = new DatabaseHelper(context);
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

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

    public Cursor queryAll() {
        return database.query(DATABASE_TABLE, null, null, null, null, null, TrackColumns._ID + " DESC");
    }

    public Cursor queryById(String trackId) {
        return database.query(DATABASE_TABLE, null, TrackColumns.TRACK_ID + " = ?", new String[]{trackId}, null, null, null);
    }

    public long insert(ContentValues values) {
        return database.insert(DATABASE_TABLE, null, values);
    }

    public int update(String trackId, ContentValues values) {
        return database.update(DATABASE_TABLE, values, TrackColumns.TRACK_ID + " = ?", new String[]{trackId});
    }

    public int delete(String trackId) {
        return database.delete(DATABASE_TABLE, TrackColumns.TRACK_ID + " = ?", new String[]{trackId});
    }
}