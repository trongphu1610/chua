package com.example.bt_game.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.example.bt_game.R;

import java.util.Random;

public class PlayActivity extends AppCompatActivity implements Runnable, View.OnClickListener {
    private TextView tvName;
    private TextView tvScore;
    private ProgressBar proBar;
    private Button btnOk;
    private Button btnX;
    private TextView tvQues;
    private long time = 5000;
    private int a, b, c, score = 0;
    private AlertDialog alertDialog;
    private Random random = new Random();
    private Thread thread, thread1;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == 1) {
                a = random.nextInt(10);
                b = random.nextInt(10);
                c = random.nextInt(20);
                if (random.nextInt(2) == 0) {
                    c = a + b;
                }
                tvQues.setText(a + " + " + b + " = " + c);
                tvScore.setText(score + "");
            } else if (message.what == 2) {
                showDialog();
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        initView();
        thread = new Thread(PlayActivity.this);
        thread.start();
        if (thread.isAlive()) {
            Log.e("phu", "a0");
        }
    }

    private void initView() {
        tvName = findViewById(R.id.tv_name);
        tvScore = findViewById(R.id.tv_score);
        proBar = findViewById(R.id.pro_bar);
        btnOk = findViewById(R.id.btn_ok);
        btnX = findViewById(R.id.btn_x);
        tvQues = findViewById(R.id.tv_ques);

        proBar.setProgress(100);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.btn_ok_scale);
        btnOk.startAnimation(animation);
        btnX.startAnimation(animation);

        btnX.setOnClickListener(this);
        btnOk.setOnClickListener(this);

        //Set Text
        tvName.setText("Hi, " + getIntent().getStringExtra("name"));
        tvScore.setText("0");
    }

    @Override
    public void run() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        while (time >= 100) {
            if (time == 5000) {
                handler.sendEmptyMessage(1);
            }
            time -= 100;
            try {
                Thread.sleep(100);
                proBar.post(new Runnable() {
                    @Override
                    public void run() {
                        proBar.setProgress((int) (time * 100 / 5000));
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        handler.sendEmptyMessage(2);
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PlayActivity.this);
        View view = LayoutInflater.from(PlayActivity.this).inflate(R.layout.dialog_alert, null);
        TextView tvScore;
        Button btnPlay;

        tvScore = view.findViewById(R.id.tv_score);
        btnPlay = view.findViewById(R.id.btn_play);

        tvScore.setText("Your score: " + score);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                score = 0;
                time = 5000;
                thread1 = new Thread(PlayActivity.this);
                thread1.start();
            }
        });
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
        builder.setCancelable(false);
    }

    @Override
    public void onClick(View view) {
        boolean ckeckResult = a + b == c ? true : false;
        switch (view.getId()) {
            case R.id.btn_ok:
                if (ckeckResult) {
                    score++;
                    time = 5000;
                } else {
                    showDialog();
                }
                break;
            case R.id.btn_x:
                if (ckeckResult == false) {
                    score++;
                    time = 5000;
                } else {
                    showDialog();
                }
                break;
        }
    }
}