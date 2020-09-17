package com.example.bt_retrofit.api;



import com.example.bt_retrofit.model.Song;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface SongService {
    @GET("api/searchSong")
    Call<List<Song>> getAllSong();
}
