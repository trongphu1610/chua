package com.example.bt_day13.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt_day13.fragment.CovidFragment;
import com.example.bt_day13.fragment.MusicFragment;
import com.example.bt_day13.fragment.NewFragment;
import com.example.bt_day13.fragment.VideoFragment;

public class MusicPagerAdapter extends FragmentPagerAdapter {

    private String[] titles = new String[]{"Song", "Album", "Artist"};


    public MusicPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new NewFragment();
            case 1:
                return new MusicFragment();
            case 2:
                return new VideoFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return titles.length;// dem so luong pager
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];// hien thi chu ten tablayout
    }
}