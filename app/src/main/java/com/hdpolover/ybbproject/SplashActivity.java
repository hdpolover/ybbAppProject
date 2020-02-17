package com.hdpolover.ybbproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
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
            //forceUpdate();

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

    public void forceUpdate(){
        PackageManager packageManager = this.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo =packageManager.getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String currentVersion = packageInfo.versionName;
        new ForceUpdateAsync(currentVersion,SplashActivity.this).execute();
    }

    public class ForceUpdateAsync extends AsyncTask<String, String, JSONObject> {

        private String latestVersion;
        private String currentVersion;
        private Context context;

        public ForceUpdateAsync(String currentVersion, Context context){
            this.currentVersion = currentVersion;
            this.context = context;
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            try {
                latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + context.getPackageName()+ "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div.hAyfc:nth-child(3) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                        .first()
                        .ownText();
                Log.e("latestversion","---"+latestVersion);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(latestVersion!=null){
                if(!currentVersion.equalsIgnoreCase(latestVersion)){
                    // Toast.makeText(context,"update is available.",Toast.LENGTH_LONG).show();
                    if(!(context instanceof SplashActivity)) {
                        if(!((Activity)context).isFinishing()){
                            showForceUpdateDialog();
                        }
                    }
                }
            }
            super.onPostExecute(jsonObject);
        }

        public void showForceUpdateDialog(){

            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
        }

    }
}
