package com.trongphu.musichome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.trongphu.musichome.R;

public class SongFragment extends Fragment {
    public static SongFragment INSTANCE;

    public SongFragment() {
    }
    public static SongFragment getInstance(){
        if (INSTANCE == null){
            INSTANCE = new SongFragment();
        }
        return INSTANCE;
    }   @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song, container, false);
        return view;
    }
}
