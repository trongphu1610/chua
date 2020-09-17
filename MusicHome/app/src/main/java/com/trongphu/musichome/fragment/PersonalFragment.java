package com.trongphu.musichome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.trongphu.musichome.R;

public class PersonalFragment extends Fragment {
    public static PersonalFragment INSTANCE;

    public PersonalFragment() {
    }
    public static PersonalFragment getInstance(){
        if (INSTANCE == null){
            INSTANCE = new PersonalFragment();
        }
        return INSTANCE;
    }  @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.fragment_personal, container, false);
        return view;
    }
}