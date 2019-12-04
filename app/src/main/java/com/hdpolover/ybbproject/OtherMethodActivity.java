package com.hdpolover.ybbproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OtherMethodActivity extends AppCompatActivity {

    EditText mFullnameEt, mPhoneEt, mJobEt;
    Button mLoginBtn;

    //    progressbar to display while registering user
    ProgressDialog progressDialog;

    //    Declare as instance of FirebaseAuth
    private FirebaseAuth mAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_method);

        mFullnameEt = findViewById(R.id.fullnameEt);
        mPhoneEt = findViewById(R.id.phoneEt);
        mJobEt = findViewById(R.id.jobEt);
        mLoginBtn = findViewById(R.id.loginBtn);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String fullname = mFullnameEt.getText().toString().trim();
                final String phone = mPhoneEt.getText().toString().trim();
                final String job = mJobEt.getText().toString().trim();
                //Validate
                if (fullname.equals("")) {
                    //set error
                    mFullnameEt.setError("Full name cannot be empty");
                    mFullnameEt.setFocusable(true);
                } else if (phone.equals("")) {
                    //set error
                    mPhoneEt.setError("Phone number cannot be empty");
                    mPhoneEt.setFocusable(true);
                } else if (job.equals("")) {
                    //set error
                    mJobEt.setError("Job cannot be empty");
                    mJobEt.setFocusable(true);
                } else {
                    registerUser(); //register user
                }
            }
        });
    }

    private void registerUser() {
        //email and password pattern is valid
        progressDialog.show();

        startActivity(new Intent(OtherMethodActivity.this, DashboardActivity.class));
        finish();
    }
}
