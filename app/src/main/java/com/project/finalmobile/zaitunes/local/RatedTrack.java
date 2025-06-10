package com.project.finalmobile.zaitunes.local;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// @Entity menandakan bahwa class ini adalah sebuah tabel database.
@Entity(tableName = "rated_tracks")
public class RatedTrack {

    // @PrimaryKey menandakan bahwa 'trackId' adalah kunci unik untuk setiap baris.
    @PrimaryKey
    private long trackId;

    // @ColumnInfo digunakan untuk memberi nama kolom di tabel.
    // Jika namanya sama dengan variabel, ini opsional tapi praktik yang baik.
    @ColumnInfo(name = "track_name")
    private String trackName;

    @ColumnInfo(name = "artist_name")
    private String artistName;

    @ColumnInfo(name = "artwork_url")
    private String artworkUrl100;

    @ColumnInfo(name = "rating")
    private float rating;

    @ColumnInfo(name = "collection_name")
    private String collectionName;

    @ColumnInfo(name = "genre")
    private String primaryGenreName;

    @ColumnInfo(name = "release_date")
    private String releaseDate;

    // Room membutuhkan constructor kosong
    public RatedTrack() {
    }

    // --- Getters and Setters ---
    // Anda bisa generate ini secara otomatis di Android Studio
    // dengan cara: Klik kanan -> Generate -> Getter and Setter -> Pilih semua

    public long getTrackId() {
        return trackId;
    }

    public void setTrackId(long trackId) {
        this.trackId = trackId;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtworkUrl100() {
        return artworkUrl100;
    }

    public void setArtworkUrl100(String artworkUrl100) {
        this.artworkUrl100 = artworkUrl100;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getCollectionName() { return collectionName; }
    public void setCollectionName(String collectionName) { this.collectionName = collectionName; }
    public String getPrimaryGenreName() { return primaryGenreName; }
    public void setPrimaryGenreName(String primaryGenreName) { this.primaryGenreName = primaryGenreName; }
    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
}