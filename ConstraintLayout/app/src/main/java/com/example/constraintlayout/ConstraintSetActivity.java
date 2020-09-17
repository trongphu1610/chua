package com.example.constraintlayout;

import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.transition.ChangeBounds;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

public class ConstraintSetActivity extends AppCompatActivity {

    private ConstraintLayout layout;
    private ConstraintSet constraintSetOld = new ConstraintSet();
    private ConstraintSet constraintSetNew = new ConstraintSet();
    private boolean altLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constraint_set);

        layout = findViewById(R.id.layout);
        constraintSetOld.clone(layout);
        constraintSetNew.clone(this, R.layout.activity_constraint_set_alt);
    }

    public void swapView(View v) {
        Transition changeBounds = new ChangeBounds();
        changeBounds.setInterpolator(new OvershootInterpolator());
        TransitionManager.beginDelayedTransition(layout, changeBounds);
        if (!altLayout) {
            constraintSetNew.applyTo(layout);
            altLayout = true;
        } else {
            constraintSetOld.applyTo(layout);
            altLayout = false;
        }
    }
}
