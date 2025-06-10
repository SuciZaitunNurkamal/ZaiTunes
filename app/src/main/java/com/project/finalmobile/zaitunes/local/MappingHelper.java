package com.project.finalmobile.zaitunes.local;

import android.database.Cursor;
import com.project.finalmobile.zaitunes.local.DatabaseContract.TrackColumns;
import java.util.ArrayList;

public class MappingHelper {

    public static ArrayList<RatedTrack> mapCursorToArrayList(Cursor cursor) {
        ArrayList<RatedTrack> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            RatedTrack ratedTrack = new RatedTrack();
            ratedTrack.setTrackId(cursor.getLong(cursor.getColumnIndexOrThrow(TrackColumns._ID)));
            ratedTrack.setTrackName(cursor.getString(cursor.getColumnIndexOrThrow(TrackColumns.TRACK_NAME)));
            ratedTrack.setArtistName(cursor.getString(cursor.getColumnIndexOrThrow(TrackColumns.ARTIST_NAME)));
            // ... tambahkan mapping untuk kolom lain (collectionName, genre, dll.) ...
            ratedTrack.setRating(cursor.getFloat(cursor.getColumnIndexOrThrow(TrackColumns.RATING)));
            list.add(ratedTrack);
        }
        return list;
    }
}