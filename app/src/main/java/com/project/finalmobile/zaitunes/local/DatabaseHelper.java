package com.project.finalmobile.zaitunes.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "zaitunes.db";
    private static final int DATABASE_VERSION = 6;

    private static final String SQL_CREATE_TABLE_RATED_TRACKS = String.format(
            "CREATE TABLE %s"
                    + " (%s INTEGER PRIMARY KEY AUTOINCREMENT," // _ID from BaseColumns
                    + " %s INTEGER NOT NULL," // track_id as INTEGER
                    + " %s TEXT NOT NULL,"
                    + " %s TEXT NOT NULL,"
                    + " %s TEXT NOT NULL,"
                    + " %s TEXT NOT NULL,"
                    + " %s TEXT NOT NULL,"
                    + " %s TEXT NOT NULL,"
                    + " %s TEXT NOT NULL,"
                    + " %s REAL NOT NULL DEFAULT 0)", // rating with default value 0
            DatabaseContract.TABLE_NAME,
            DatabaseContract.TrackColumns._ID,
            DatabaseContract.TrackColumns.TRACK_ID,
            DatabaseContract.TrackColumns.TRACK_NAME,
            DatabaseContract.TrackColumns.ARTIST_NAME,
            DatabaseContract.TrackColumns.COLLECTION_NAME,
            DatabaseContract.TrackColumns.GENRE,
            DatabaseContract.TrackColumns.RELEASE_DATE,
            DatabaseContract.TrackColumns.TRACK_VIEW_URL,
            DatabaseContract.TrackColumns.ARTWORK_URL,
            DatabaseContract.TrackColumns.RATING
    );

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "Creating database table: " + SQL_CREATE_TABLE_RATED_TRACKS);
        db.execSQL(SQL_CREATE_TABLE_RATED_TRACKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_NAME);
        onCreate(db);
    }
}