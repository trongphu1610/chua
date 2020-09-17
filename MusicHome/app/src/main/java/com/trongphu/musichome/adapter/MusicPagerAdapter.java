package com.trongphu.musichome.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.trongphu.musichome.fragment.AlbumFragment;
import com.trongphu.musichome.fragment.ArtisrtFragment;
import com.trongphu.musichome.fragment.FavoriteFragment;
import com.trongphu.musichome.fragment.PersonalFragment;
import com.trongphu.musichome.fragment.SongFragment;

public class MusicPagerAdapter extends FragmentPagerAdapter {
    private String[] titles = new String[]{"ALbum", "Favorite", "Artist", "song", "Personal"};


    public MusicPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return AlbumFragment.getInstance();
            case 1:
                return ArtisrtFragment.getInstance();
            case 2:
                return FavoriteFragment.getInstance();
            case 3:
                return SongFragment.getInstance();
            case 4:
                return PersonalFragment.getInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return titles.length;
    }
}
