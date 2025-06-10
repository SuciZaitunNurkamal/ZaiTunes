package com.project.finalmobile.zaitunes.local;

import android.database.Cursor;
import android.util.Log;
import com.project.finalmobile.zaitunes.local.DatabaseContract.TrackColumns;
import java.util.ArrayList;

public class MappingHelper {

    public static ArrayList<RatedTrack> mapCursorToArrayList(Cursor cursor) {
        ArrayList<RatedTrack> list = new ArrayList<>();
        if (cursor == null) {
            Log.e("MappingHelper", "Cursor is null!");
            return list;
        }

        try {
            while (cursor.moveToNext()) {
                RatedTrack ratedTrack = new RatedTrack();
                
                // Get column indices
                int trackIdIndex = cursor.getColumnIndexOrThrow(TrackColumns.TRACK_ID);
                int trackNameIndex = cursor.getColumnIndexOrThrow(TrackColumns.TRACK_NAME);
                int artistNameIndex = cursor.getColumnIndexOrThrow(TrackColumns.ARTIST_NAME);
                int collectionNameIndex = cursor.getColumnIndexOrThrow(TrackColumns.COLLECTION_NAME);
                int genreIndex = cursor.getColumnIndexOrThrow(TrackColumns.GENRE);
                int releaseDateIndex = cursor.getColumnIndexOrThrow(TrackColumns.RELEASE_DATE);
                int trackViewUrlIndex = cursor.getColumnIndexOrThrow(TrackColumns.TRACK_VIEW_URL);
                int artworkUrlIndex = cursor.getColumnIndexOrThrow(TrackColumns.ARTWORK_URL);
                int ratingIndex = cursor.getColumnIndexOrThrow(TrackColumns.RATING);

                // Set values
                ratedTrack.setTrackId(cursor.getLong(trackIdIndex));
                ratedTrack.setTrackName(cursor.getString(trackNameIndex));
                ratedTrack.setArtistName(cursor.getString(artistNameIndex));
                ratedTrack.setCollectionName(cursor.getString(collectionNameIndex));
                ratedTrack.setPrimaryGenreName(cursor.getString(genreIndex));
                ratedTrack.setReleaseDate(cursor.getString(releaseDateIndex));
                ratedTrack.setTrackViewUrl(cursor.getString(trackViewUrlIndex));
                ratedTrack.setArtworkUrl100(cursor.getString(artworkUrlIndex));
                ratedTrack.setRating(cursor.getFloat(ratingIndex));

                // Debug logging
                Log.d("MappingHelper", "Mapped track: " + ratedTrack.getTrackName());
                Log.d("MappingHelper", "Artist: " + ratedTrack.getArtistName());
                Log.d("MappingHelper", "Album: " + ratedTrack.getCollectionName());
                Log.d("MappingHelper", "Genre: " + ratedTrack.getPrimaryGenreName());
                Log.d("MappingHelper", "Artwork URL: " + ratedTrack.getArtworkUrl100());
                Log.d("MappingHelper", "Track ID: " + ratedTrack.getTrackId());

                list.add(ratedTrack);
            }
        } catch (Exception e) {
            Log.e("MappingHelper", "Error mapping cursor: " + e.getMessage());
            e.printStackTrace();
        }

        Log.d("MappingHelper", "Mapped " + list.size() + " tracks");
        return list;
    }
}