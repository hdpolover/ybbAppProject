package com.hdpolover.ybbproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingMyAccountActivity extends AppCompatActivity {

    String myUid;
    MaterialCardView changePassBtn, deleteAccBtn, changeEmailBtn;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_my_account);

        progressDialog = new ProgressDialog(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("My Account Settings");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        checkUserStatus();

        changePassBtn = findViewById(R.id.changePassBtn);
        changeEmailBtn = findViewById(R.id.changeEmailBtn);
        deleteAccBtn = findViewById(R.id.deleteAccBtn);

        changePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingMyAccountActivity.this, SettingChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        deleteAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });
    }

    private void deleteAccount() {
        //alertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Account");
        builder.setMessage("This action cannot be undone. Please consider carefully.");

        //set layout linear layout
        LinearLayout linearLayout = new LinearLayout(this);
        //views to set in dialog
        final EditText currentPasswordEt = new EditText(this);
        currentPasswordEt.setHint("Enter your current password");
        currentPasswordEt.setWidth(200);
        currentPasswordEt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        /*set the min width of a Edit view to fit a text of n 'M' letter regardless of the actual text
        extension and text size*/
        currentPasswordEt.setMinEms(20);

        linearLayout.addView(currentPasswordEt);
        linearLayout.setPadding(10, 10, 10, 10);

        builder.setView(linearLayout);

        //button recover
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, final int i) {
                String password = currentPasswordEt.getText().toString().trim();

                if(password == null || password.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Current password must not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.setMessage("Deleting account...");
                progressDialog.show();

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Account successfully changed", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(SettingMyAccountActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                progressDialog.dismiss();
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
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        //show dialog
        builder.create().show();
    }

    private void checkUserStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            myUid = user.getUid();
        } else {
            startActivity(new Intent(SettingMyAccountActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
