package com.example.day8drawable;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private ImageView imgLevel, imgLed;
    private int level = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgLevel = findViewById(R.id.img_level);
        imgLed = findViewById(R.id.img_led);
        imgLed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionDrawable drawable = (TransitionDrawable) imgLed.getDrawable();
                drawable.startTransition(2000);
            }
        });
        imgLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                level += 26;
                if (level > 101) {
                    level = 0;
                }
                imgLevel.setImageLevel(level);
            }
        });
    }
}
