package com.example.bt_game;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bt_game.activity.PlayActivity;

public class MainActivity extends AppCompatActivity {
    private EditText edtName;
    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ánh xạ các thành phần giao diện
        initView();

        //Khởi chạy animation
        startAnimation();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                intent.putExtra("name", edtName.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.in_right, R.anim.out_left);
                finish();
            }
        });

    }

    private void startAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.scale);
        btnStart.startAnimation(animation);
    }

    private void initView() {
        edtName = findViewById(R.id.edt_name);
        btnStart = findViewById(R.id.btn_start);
    }
}