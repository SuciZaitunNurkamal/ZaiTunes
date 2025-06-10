package com.project.finalmobile.zaitunes.model;

import com.google.gson.annotations.SerializedName;

public class Track {

    // Anotasi @SerializedName digunakan untuk mencocokkan nama variabel Java
    // dengan nama key di dalam JSON. Ini sangat berguna jika nama key di JSON
    // berbeda dengan nama variabel yang Anda inginkan.

    @SerializedName("trackName")
    private String trackName;

    @SerializedName("artistName")
    private String artistName;

    @SerializedName("collectionName")
    private String collectionName;

    @SerializedName("artworkUrl100")
    private String artworkUrl100;

    // Kita juga butuh trackId untuk fitur rating nanti
    @SerializedName("trackId")
    private long trackId;

    // --- GENERATE GETTERS ---
    // Anda bisa generate ini secara otomatis di Android Studio
    // dengan cara: Klik kanan -> Generate -> Getter

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
