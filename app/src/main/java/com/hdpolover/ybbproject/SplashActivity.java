package com.hdpolover.ybbproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hdpolover.ybbproject.models.ModelUser;

import java.net.InetAddress;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private int loadingTime = 1500;

    String myUid;
    List<ModelUser> userList;

    boolean isUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (isNetworkConnected() || isInternetAvailable()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        SharedPreferences sp = getSharedPreferences("StatusLogin",MODE_PRIVATE);
                        sp.getString("isNew","false");
                        isUser = true;

                    }catch (Exception e){
                        isUser = false;
                        Toast.makeText(SplashActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    if (isUser) {
                        //jump to dashboard activity after splash screen
                        //Log.e("is", isUser+"");
                        Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        //user not signed in
                        Log.e("is", isUser+"");
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }
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

    private boolean isAlreadyLoggedin() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //path of all posts
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //get all data from this ref
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ModelUser modelUser = ds.getValue(ModelUser.class);

                    if (modelUser.getUid().equals(user.getUid())) {
                        Log.e("us", user.getUid());
                        isUser = true;
                    } else {
                        isUser = false;
                        Log.e("us", "false");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return isUser;
    }
}
