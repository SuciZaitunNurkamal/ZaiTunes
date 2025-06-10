package com.project.finalmobile.zaitunes.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface RatedTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RatedTrack ratedTrack);

    @Query("SELECT * FROM rated_tracks ORDER BY track_name ASC")
    List<RatedTrack> getAllRatedTracks();

    @Query("SELECT * FROM rated_tracks WHERE trackId = :id")
    RatedTrack getTrackById(long id);

    // PASTIKAN METODE INI BENAR-BENAR ADA DI DALAM FILE ANDA
    @Query("SELECT * FROM rated_tracks ORDER BY trackId DESC LIMIT 5")
    List<RatedTrack> getRecentlyRated();

    // Ubah metode insert Anda menjadi seperti ini
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertIgnore(RatedTrack ratedTrack);

    // Anda mungkin juga perlu metode update biasa untuk saat rating diubah di halaman detail
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(RatedTrack ratedTrack);

    @Delete
    void delete(RatedTrack ratedTrack);
}