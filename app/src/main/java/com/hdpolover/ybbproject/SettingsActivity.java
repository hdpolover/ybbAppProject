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

    MaterialCardView logoutBtn, aboutBtn, feedbackBtn, helpBtn, accountBtn, tncBtn, ppBtn;
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
        helpBtn = findViewById(R.id.helpBtn);
        accountBtn = findViewById(R.id.accountBtn);
        tncBtn = findViewById(R.id.tncBtn);
        ppBtn = findViewById(R.id.ppBtn);
        ybbVersionTv = findViewById(R.id.ybbVersionTv);

        ybbVersionTv.setText(getResources().getString(R.string.app_name) + " v " + appVersionCheck.getAppVersion());

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
                startActivity(new Intent(SettingsActivity.this, SettingAboutActivity.class));
            }
        });

        accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, SettingMyAccountActivity.class);
                startActivity(intent);
            }
        });

        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, HelpCenterActivity.class);
                startActivity(intent);
            }
        });

        tncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, WebviewSettingActivity.class);
                intent.putExtra("type", "tnc");
                startActivity(intent);
            }
        });

        ppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, WebviewSettingActivity.class);
                intent.putExtra("type", "pp");
                startActivity(intent);
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
