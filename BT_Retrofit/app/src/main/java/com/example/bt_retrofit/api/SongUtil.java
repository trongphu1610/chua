package com.example.bt_retrofit.api;

public class SongUtil {
    private static final String BASE_URL = "https://songserver.herokuapp.com/";

    public static SongService getSongService() {
        return SongClient.getSongClient(BASE_URL).create(SongService.class);
    }
}
