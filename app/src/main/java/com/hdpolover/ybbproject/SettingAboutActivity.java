package com.hdpolover.ybbproject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.hdpolover.ybbproject.helpers.AppVersionCheck;

public class SettingAboutActivity extends AppCompatActivity {

    TextView appNameTv, appVersionTv;

    AppVersionCheck avc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_about);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("About");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        avc = new AppVersionCheck();

        appNameTv = findViewById(R.id.appNameTv);
        appVersionTv = findViewById(R.id.appVersionTv);

        appNameTv.setText("Youth Break the Boundaries (YBB)");
        appVersionTv.setText("Version " + avc.getAppVersion());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
