package com.trongphu.service;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.trongphu.service.service.Service;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnStart, btnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStart = findViewById(R.id.btnstart);
        btnStop = findViewById(R.id.btnstop);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, Service.class);
        switch (view.getId()) {
            case R.id.btnstart:
                startService(intent);
                break;
            case R.id.btnstop:

                break;
        }
    }
}