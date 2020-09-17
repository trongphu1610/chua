
package com.example.bt_retrofit.model;

import com.google.gson.annotations.SerializedName;
public class Song {

    @SerializedName("artistName")
    private String mArtistName;
    @SerializedName("id")
    private String mId;
    @SerializedName("linkImage")
    private String mLinkImage;
    @SerializedName("linkMusic")
    private Object mLinkMusic;
    @SerializedName("linkSong")
    private String mLinkSong;
    @SerializedName("songName")
    private String mSongName;

    public String getArtistName() {
        return mArtistName;
    }

    public void setArtistName(String artistName) {
        mArtistName = artistName;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getLinkImage() {
        return mLinkImage;
    }

    public void setLinkImage(String linkImage) {
        mLinkImage = linkImage;
    }

    public Object getLinkMusic() {
        return mLinkMusic;
    }

    public void setLinkMusic(Object linkMusic) {
        mLinkMusic = linkMusic;
    }

    public String getLinkSong() {
        return mLinkSong;
    }

    public void setLinkSong(String linkSong) {
        mLinkSong = linkSong;
    }

    public String getSongName() {
        return mSongName;
    }

    public void setSongName(String songName) {
        mSongName = songName;
    }

}
