package com.example.bt_retrofit.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SongClient {
    private static Retrofit INSTANCE;

    public static Retrofit getSongClient(String baseUrl) {
        if (INSTANCE == null) {
            INSTANCE = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return INSTANCE;
    }
}
