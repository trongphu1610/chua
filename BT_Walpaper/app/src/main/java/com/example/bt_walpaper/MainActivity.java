package com.example.bt_walpaper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button btnLogin , btnRegistration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation alphaAnimation = AnimationUtils.loadAnimation(
                        MainActivity.this,
                        R.anim.login);
                alphaAnimation.setDuration(1000);
                alphaAnimation.setInterpolator(new AccelerateInterpolator());
                btnLogin.startAnimation(alphaAnimation);
               ClickLogin();
            }
        });
        btnRegistration = findViewById(R.id.btn_registration);


    }

    private void ClickLogin(){
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
        Toast.makeText(MainActivity.this,"Đăng nhập thành công ",Toast.LENGTH_SHORT).show();
    }

    public void registration2(View view) {
        Intent intent = new Intent(this,RegistrationActivity.class);
        startActivity(intent);
        Toast.makeText(MainActivity.this,"Mời bạn đăng ký tài khoản ",Toast.LENGTH_SHORT).show();
    }
}