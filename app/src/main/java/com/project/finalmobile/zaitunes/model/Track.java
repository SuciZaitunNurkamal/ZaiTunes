package com.project.finalmobile.zaitunes.model;

import com.google.gson.annotations.SerializedName;

// Model class untuk data lagu yang disederhanakan
public class Track {

    @SerializedName("trackName")
    private String trackName;

    @SerializedName("artistName")
    private String artistName;

    @SerializedName("collectionName")
    private String collectionName;

    @SerializedName("artworkUrl100")
    private String artworkUrl100;

    @SerializedName("trackId")
    private long trackId;

    public String getTrackName() {
        return trackName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public String getArtworkUrl100() {
        return artworkUrl100;
    }

    public long getTrackId() {
        return trackId;
    }
}
