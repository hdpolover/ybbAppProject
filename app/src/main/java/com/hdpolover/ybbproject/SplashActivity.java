package com.hdpolover.ybbproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;

import java.net.InetAddress;

public class SplashActivity extends AppCompatActivity {

    private int loadingTime = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (isNetworkConnected() || isInternetAvailable()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //jump to dashboard activity after splash screen
                    Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, loadingTime);
        } else {
            Intent intent = new Intent(SplashActivity.this, NoConnectionActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }
}
