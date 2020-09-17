package com.example.bt_day5;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Bai1_Calculator extends AppCompatActivity implements View.OnClickListener {
    private Button btn_phepcong, btn_pheptru, btn_phepnhan, btn_phepchia;
    private TextView tv_name, tv_ketqua;
    private EditText edt_numberone, edt_numbertwo;
    private int result;
    private TextView info;
    private final char ADDITION = '+';
    private final char SUBTRACTION = '-';
    private final char MULTIPLICATION = '*';
    private final char DIVISION = '/';
    private final char EQU = 0;
    private double val1 = Double.NaN;
    private double val2;
    private char ACTION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bai1__calculator);
        btn_phepcong = findViewById(R.id.btn_add);
        btn_pheptru = findViewById(R.id.btn_subtraction);
        btn_phepnhan = findViewById(R.id.btn_multiplication);
        btn_phepchia = findViewById(R.id.btn_division);
        tv_name = findViewById(R.id.tv_name);
        tv_ketqua = findViewById(R.id.tv_contact);
        btn_phepcong.setOnClickListener(this);
        btn_pheptru.setOnClickListener(this);
        btn_phepnhan.setOnClickListener(this);
        btn_phepchia.setOnClickListener(this);
        edt_numberone = findViewById(R.id.edt_numberone);
        edt_numbertwo = findViewById(R.id.edt_numbertwo);


    }

    private int division(EditText edt_numberone, EditText edt_numbertwo) {
        return Integer.parseInt(this.edt_numberone.toString()) / Integer.parseInt(this.edt_numbertwo.toString());

    }

    private int multiplication(EditText edt_numberone, EditText edt_numbertwo) {
        return Integer.parseInt(this.edt_numberone.toString()) * Integer.parseInt(this.edt_numbertwo.toString());

    }

    private int add(EditText edt_numberone, EditText edt_numbertwo) {
        return Integer.parseInt(this.edt_numberone.toString()) + Integer.parseInt(this.edt_numbertwo.toString());

    }

    private int subtraction(EditText edt_numberone, EditText edt_numbertwo) {
        return Integer.parseInt(this.edt_numberone.toString()) - Integer.parseInt(this.edt_numbertwo.toString());


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                tv_ketqua.setText(add(edt_numberone,edt_numbertwo));
                break;
            case R.id.btn_subtraction:
                tv_ketqua.setText(subtraction(edt_numberone,edt_numbertwo));

                break;
            case R.id.btn_multiplication:
                tv_ketqua.setText(multiplication(edt_numberone,edt_numbertwo));

                break;
            case R.id.btn_division:
                tv_ketqua.setText(division(edt_numberone,edt_numbertwo));

                break;
        }
    }

}