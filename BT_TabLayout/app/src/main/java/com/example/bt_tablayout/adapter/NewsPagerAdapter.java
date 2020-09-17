package com.example.bt_tablayout.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.bt_tablayout.fragment.CovidFragment;
import com.example.bt_tablayout.fragment.MusicFragment;
import com.example.bt_tablayout.fragment.NewsFragment;
import com.example.bt_tablayout.fragment.VideoFragment;

public class NewsPagerAdapter  extends FragmentPagerAdapter {
    private String[] titles = new String[]{"News","Music","Video","Covid"};

    public NewsPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new NewsFragment();
            case 1:
                return new MusicFragment();
            case 2:
                return new VideoFragment();
            case 3:
                return new CovidFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return titles.length;
    }
    public CharSequence getPageTitle(int position){
        return titles[position];
    }
}
