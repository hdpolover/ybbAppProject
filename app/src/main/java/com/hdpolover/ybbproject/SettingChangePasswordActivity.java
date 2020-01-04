package com.hdpolover.ybbproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingChangePasswordActivity extends AppCompatActivity {

    String myUid;

    EditText currentPasswordEt, newPasswordEt, confirmNewPasswordEt;
    Button saveNewPassBtn;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_change_password);

        progressDialog = new ProgressDialog(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Change Password");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        currentPasswordEt = findViewById(R.id.currentPasswordEt);
        newPasswordEt = findViewById(R.id.newPasswordEt);
        confirmNewPasswordEt = findViewById(R.id.confirmNewPasswordEt);
        saveNewPassBtn = findViewById(R.id.saveNewPassBtn);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String providerId = user.getProviderId();

        checkUserStatus();

        saveNewPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String currentPassword = currentPasswordEt.getText().toString().trim();
                final String newPassword = newPasswordEt.getText().toString().trim();
                final String confirmNewPassword = confirmNewPasswordEt.getText().toString().trim();

                if(currentPassword == null || currentPassword.isEmpty()){
                    currentPasswordEt.setError("Current password must not be empty");
                    currentPasswordEt.setFocusable(true);
                    return;
                }

                if(newPassword == null || newPassword.isEmpty()){
                    newPasswordEt.setError("New password must not be empty");
                    newPasswordEt.setFocusable(true);
                    return;
                }

                if(confirmNewPassword == null || confirmNewPassword.isEmpty()){
                    confirmNewPasswordEt.setError("Confirm password must not be empty");
                    confirmNewPasswordEt.setFocusable(true);
                    return;
                }

                if(newPassword.length() < 6){
                    newPasswordEt.setError("Password must not be at least 6 characters");
                    newPasswordEt.setFocusable(true);
                    return;
                }

                if(!confirmNewPassword.equals(newPassword)){
                    confirmNewPasswordEt.setError("Passwords do not match");
                    confirmNewPasswordEt.setFocusable(true);
                    return;
                }

                if(!newPassword.equals(currentPassword)){
                    newPasswordEt.setError("New password must not be the same with the current one");
                    newPasswordEt.setFocusable(true);
                    return;
                }

                updateOldPassword(currentPassword, newPassword);
            }
        });

    }

    private void updateOldPassword(String currentPassword, final String newPassword) {
        progressDialog.setMessage("Updating password...");
        progressDialog.show();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
        
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Log.d("success", "Password updated");
                                        Toast.makeText(getApplicationContext(), "Password successfully changed", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        progressDialog.dismiss();
                                        Log.d("failed", "Error password not updated");
                                        Toast.makeText(getApplicationContext(), "An error occured", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Log.d("failed", "Error auth failed");
                            currentPasswordEt.setError("Current password is wrong");
                            currentPasswordEt.setFocusable(true);
                        }
                    }
                });
    }

    private void checkUserStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            myUid = user.getUid();
        } else {
            startActivity(new Intent(SettingChangePasswordActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
