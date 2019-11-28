package com.hdpolover.ybbproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class UploadProfileActivity extends AppCompatActivity {

    ImageView profileIv;
    Button skipBtn, finishBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile);

        profileIv = findViewById(R.id.profileIv);
        skipBtn = findViewById(R.id.skipBtn);
        finishBtn = findViewById(R.id.finishBtn);

        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UploadProfileActivity.this, DashboardActivity.class));
                finish();
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UploadProfileActivity.this, DashboardActivity.class));
                finish();
            }
        });
    }
}
