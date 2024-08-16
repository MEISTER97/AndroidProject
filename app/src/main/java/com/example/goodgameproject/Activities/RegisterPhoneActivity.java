package com.example.goodgameproject.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import androidx.appcompat.app.AppCompatActivity;


import com.example.goodgameproject.R;
import com.google.android.material.button.MaterialButton;


public class RegisterPhoneActivity extends AppCompatActivity {

    MaterialButton loginOrRegister;
    MaterialButton emailRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone);
        findViews();
        initViews();
    }


    private void findViews()
    {
        loginOrRegister=findViewById(R.id.registerOrLoginPhone);
        emailRegister=findViewById(R.id.MoveToEmailRegister);

    }

    private void initViews() {

        loginOrRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),LoginPhoneActivity.class);
                startActivity(intent);
                finish();
            }
        });


        emailRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


}