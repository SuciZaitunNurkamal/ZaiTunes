package com.project.finalmobile.zaitunes.api;

import retrofit2.Call;
import com.project.finalmobile.zaitunes.model.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import com.project.finalmobile.zaitunes.model.RssResponse;

public interface ApiService {

    @GET("search")
    Call<Response> searchTrack(
            @Query("term") String searchTerm,
            @Query("media") String mediaType,
            @Query("entity") String entity,
            @Query("limit") int limit);

    @GET("search?media=music&entity=song")
    Call<Response> searchMusicSongs(
            @Query("term") String searchTerm,
            @Query("limit") int limit);

    // Add this method for popular songs
    @GET("search?media=music&entity=song&limit=10")
    Call<Response> getPopularSongs(@Query("term") String searchTerm);
}