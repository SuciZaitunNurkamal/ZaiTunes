package com.project.finalmobile.zaitunes.api;

import retrofit2.Call;
//import retrofit2.Response;
import com.project.finalmobile.zaitunes.model.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;
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

    @GET
    Call<RssResponse> getTopSongs(@Url String url);
}
