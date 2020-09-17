package com.example.bt_walpaper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class RegistrationActivity extends AppCompatActivity {
    private Button btn_registration2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        btn_registration2 = findViewById(R.id.btn_registration);
    }

    public void registration2(View view) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        Toast.makeText(RegistrationActivity.this,"Đăng ký thành công ",Toast.LENGTH_SHORT).show();
    }
}