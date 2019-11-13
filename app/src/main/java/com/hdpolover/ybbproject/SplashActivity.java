package com.hdpolover.ybbproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;


public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //jump to dashboard activity after splash screen
        startActivity(new Intent(this, DashboardActivity.class));
        finish();
    }

}
