package com.hdpolover.ybbproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hdpolover.ybbproject.notifications.Token;

public class DashboardActivity extends AppCompatActivity {

    public BottomNavigationView bottomNavigationView;

    final Fragment fragment1 = new HomeFragment();
    final Fragment fragment2 = new NewsFragment();
    final Fragment fragment3 = new MessagesFragment();
    final Fragment fragment4 = new SchedulesFragment();
    public final Fragment fragment5 = new ProfileFragment();

    public final FragmentManager fragmentManager = getSupportFragmentManager();

    public Fragment active = fragment1;

    //firebase auth
    FirebaseAuth firebaseAuth;
    boolean doubleBackToExit = false;

    ActionBar actionBar;
    Toolbar toolbar;

    String mUID;

    public Fragment getActiveFragment() {
        return active;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //action bar and its propertoes
        actionBar = getSupportActionBar();
        //actionBar.setIcon(R.drawable.ybb_white_cropped);
        actionBar.setDisplayShowHomeEnabled(true);
//        toolbar = findViewById(R.id.customToolbar);
//        setSupportActionBar(toolbar);

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();

        bottomNavigationView = findViewById(R.id.botton_navigation);

        fragmentManager.beginTransaction().add(R.id.fragment_container, fragment5, "5").hide(fragment5).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, fragment4, "4").hide(fragment4).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, fragment3, "3").hide(fragment3).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, fragment2, "2").hide(fragment2).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, fragment1, "1").commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        fragmentManager.beginTransaction().hide(active).show(fragment1).commit();
                        active = fragment1;
                        actionBar.setTitle("YBB");
                        return true;

                    case R.id.nav_news:
                        fragmentManager.beginTransaction().hide(active).show(fragment2).commit();
                        active = fragment2;
                        actionBar.setTitle("YBB News");
                        return true;

                    case R.id.nav_messages:
                        fragmentManager.beginTransaction().hide(active).show(fragment3).commit();
                        active = fragment3;
                        actionBar.setTitle("YBB Messages");
                        return true;

                    case R.id.nav_schedules:
                        fragmentManager.beginTransaction().hide(active).show(fragment4).commit();
                        active = fragment4;
                        actionBar.setTitle("YBB Schedules");
                        return true;

                    case R.id.nav_profile:
                        fragmentManager.beginTransaction().hide(active).show(fragment5).commit();
                        active = fragment5;
                        actionBar.setTitle("YBB Profile");
                        return true;
                }
                return false;
            }
        });

        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                if (active == fragment1) {
                    ((HomeFragment) fragment1).scrollUp();
                }
            }
        });

        checkUserStatus();
    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    public void updateToken(String token){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(mUID).setValue(mToken);
    }

    private void checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //user is signed in stay here
            //set users of logged in user
//            mMasukTv.setText(user.getEmail());
            mUID = user.getUid();

            //save myUid of currently signed in user in share preferences
            SharedPreferences sp = getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", mUID);
            editor.apply();

            //update token
            updateToken(FirebaseInstanceId.getInstance().getToken());

        } else {
            //user not signed in, go to welcome
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        //check on start of app
        checkUserStatus();
        super.onStart();
    }

    //mendisabled tombol back
    @Override
    public void onBackPressed() {
        if (doubleBackToExit) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExit = true;
        Toast.makeText(DashboardActivity.this, "Click back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExit = false;
            }
        }, 2000);
    }
}
