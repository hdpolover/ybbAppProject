package com.hdpolover.ybbproject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hdpolover.ybbproject.helpers.AppVersionCheck;

public class SettingsActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    AppVersionCheck appVersionCheck;

    String uid;

    MaterialCardView logoutBtn, aboutBtn, feedbackBtn;
    TextView ybbVersionTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        appVersionCheck = new AppVersionCheck();

        firebaseAuth = firebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Settings");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        checkUserStatus();

        //init views
        logoutBtn = findViewById(R.id.logoutBtn);
        feedbackBtn = findViewById(R.id.feedbackBtn);
        aboutBtn = findViewById(R.id.aboutBtn);
        ybbVersionTv = findViewById(R.id.ybbVersionTv);

        ybbVersionTv.setText(getResources().getString(R.string.app_name) + " " + appVersionCheck.getAppVersion());

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                //checkUserStatus();
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                finish();
            }
        });

        feedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, FeedbackActivity.class));
            }
        });

        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkUserStatus() {
        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
        } else {
            startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
