package com.hdpolover.ybbproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;

public class NoConnectionActivity extends AppCompatActivity {

    MaterialButton tryAgainBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connection);

        tryAgainBtn = findViewById(R.id.tryAgainBtn);

        tryAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoConnectionActivity.this, SplashActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
