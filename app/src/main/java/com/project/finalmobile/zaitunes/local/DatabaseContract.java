package com.project.finalmobile.zaitunes.local;

import android.provider.BaseColumns;
import com.project.finalmobile.zaitunes.local.DatabaseContract.TrackColumns;

// Prevent instantiation and inheritance
public final class DatabaseContract {
    public static final String TABLE_NAME = "rated_tracks";

    private DatabaseContract() {
        // Private constructor to prevent instantiation
    }

    public static final class TrackColumns implements BaseColumns {
        public static final String TRACK_ID = "track_id";
        public static final String TRACK_NAME = "track_name";
        public static final String ARTIST_NAME = "artist_name";
        public static final String COLLECTION_NAME = "collection_name";
        public static final String GENRE = "genre";
        public static final String RELEASE_DATE = "release_date";
        public static final String TRACK_VIEW_URL = "track_view_url";
        public static final String RATING = "rating";

        private TrackColumns() {
            // Private constructor to prevent instantiation
        }
    }
}