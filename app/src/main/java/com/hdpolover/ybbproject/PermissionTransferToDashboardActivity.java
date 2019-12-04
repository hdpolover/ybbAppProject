package com.hdpolover.ybbproject;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import com.hdpolover.ybbproject.helper.RuntimePermission;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PermissionTransferToDashboardActivity extends RuntimePermission {

    private static final int REQUEST_PERMISSION = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        // In the below string array whatever permission you need just write it one after another.
        requestAppPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.WAKE_LOCK},R.string.give_permission,REQUEST_PERMISSION);
    }

    @Override
    public void onPermissionGranted(int requestCode) {
        startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
        finish();
    }
}