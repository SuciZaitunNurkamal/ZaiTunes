package com.project.finalmobile.zaitunes.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "zaitunes.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TABLE_RATED_TRACKS = String.format(
            "CREATE TABLE %s"
                    + " (%s INTEGER PRIMARY KEY," // _ID dari BaseColumns
                    + " %s TEXT NOT NULL,"
                    + " %s TEXT NOT NULL,"
                    + " %s TEXT,"
                    + " %s TEXT,"
                    + " %s TEXT,"
                    + " %s TEXT,"
                    + " %s REAL NOT NULL)",
            DatabaseContract.TABLE_NAME,
            DatabaseContract.TrackColumns._ID, // Kolom ID primary key
            DatabaseContract.TrackColumns.TRACK_NAME,
            DatabaseContract.TrackColumns.ARTIST_NAME,
            DatabaseContract.TrackColumns.COLLECTION_NAME,
            DatabaseContract.TrackColumns.GENRE,
            DatabaseContract.TrackColumns.RELEASE_DATE,
            DatabaseContract.TrackColumns.TRACK_VIEW_URL,
            DatabaseContract.TrackColumns.RATING
    );

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_RATED_TRACKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_NAME);
        onCreate(db);
    }
}