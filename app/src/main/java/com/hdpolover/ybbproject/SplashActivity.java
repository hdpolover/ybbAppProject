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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hdpolover.ybbproject.landingPages.LandingPageActivity;
import com.hdpolover.ybbproject.models.ModelEvent;
import com.hdpolover.ybbproject.models.ModelUser;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private int loadingTime = 2500;

    boolean isUser;
    String firstTime;

    ImageView ybbLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ybbLogo = findViewById(R.id.ybbSplashLogo);

        //load logo with glide
        Glide.with(getApplicationContext()).load(R.drawable.ybb_white_full).into(ybbLogo);

        //add animation
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splashanim);
        ybbLogo.startAnimation(animation);

        checkEventStatus();

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
                    }

                    try {
                        SharedPreferences sp = getSharedPreferences("StatusLogin",MODE_PRIVATE);
                        firstTime = sp.getString("firstTime","true");

                    }catch (Exception e){
                        firstTime = "true";
                        //Toast.makeText(SplashActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    if (firstTime.equals("true")) {
                        startActivity(new Intent(SplashActivity.this, LandingPageActivity.class));
                        finish();
                    } else {
                        if (isUser) {
                            //jump to dashboard activity after splash screen
                            //Log.e("is", isUser+"");
                            Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            //user not signed in
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            finish();
                        }
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

    public void checkEventStatus() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Events");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    for (DataSnapshot ds1: ds.getChildren()) {
                        ModelEvent modelEvent = ds1.getValue(ModelEvent.class);

                        long eventStartDate = Long.parseLong(modelEvent.geteStart());
                        long timeStamp = Long.parseLong(String.valueOf(System.currentTimeMillis()));

                        if (timeStamp > eventStartDate) {
                            updateEventStatus(modelEvent.geteId(), ds.getKey());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateEventStatus(String eId, String uid) {
        HashMap<String, Object> hashMap = new HashMap<>();
        //put post info
        hashMap.put("eStatus", "past");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events");
        ref.child(uid).child(eId).updateChildren(hashMap);
    }
}
