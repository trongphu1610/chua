package com.trongphu.musichome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.trongphu.musichome.R;

import java.util.ArrayList;

public class AlbumFragment extends Fragment {
    public static AlbumFragment INSTANCE;

    public AlbumFragment() {
    }
    public static AlbumFragment getInstance(){
        if (INSTANCE == null){
            INSTANCE = new AlbumFragment();
        }
        return INSTANCE;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album,
                container,
                false);
        return view;
    }
}
