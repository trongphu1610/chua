package com.example.constraintlayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Placeholder;

import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private Placeholder placeholder;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        placeholder = findViewById(R.id.placeholder);
        constraintLayout = findViewById(R.id.cl_root);
    }

    public void showView(View v) {
        TransitionManager.beginDelayedTransition(constraintLayout);
        placeholder.setContentId(v.getId());
    }
}
