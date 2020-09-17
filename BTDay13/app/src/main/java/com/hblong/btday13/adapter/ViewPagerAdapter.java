package com.hblong.btday13.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hblong.btday13.fragment.MusicFragment;
import com.hblong.btday13.fragment.NewsFragment;
import com.hblong.btday13.fragment.VideoFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private String[] title = new String[]{"Tin Tức", "Nhạc", "Video"};

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new NewsFragment();
            case 1:
                return new MusicFragment();
            case 2:
                return new VideoFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}
