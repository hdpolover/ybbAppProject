package com.hdpolover.ybbproject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hdpolover.ybbproject.adapters.AdapterFollows;
import com.hdpolover.ybbproject.adapters.AdapterPostUpvoter;
import com.hdpolover.ybbproject.models.ModelUser;

import java.util.ArrayList;
import java.util.List;

public class UserFollowersActivity extends AppCompatActivity {

    ActionBar actionBar;

    String myUid, hisUid;

    RecyclerView recyclerView;

    List<ModelUser> followsList;
    AdapterFollows adapterFollows;
    List<String> idList;

    LinearLayout noFollows;
    NestedScrollView nestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_followers);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Followers");
        //enable back button in action bar
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        noFollows = findViewById(R.id.noFollows);
        nestedScrollView = findViewById(R.id.nestedScrollViewFollows);


        recyclerView = findViewById(R.id.userFollowersRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        followsList = new ArrayList<>();
        adapterFollows = new AdapterFollows(getApplicationContext(), followsList);
        recyclerView.setAdapter(adapterFollows);
        nestedScrollView.setSmoothScrollingEnabled(false);

        idList = new ArrayList<>();

        checkUserStatus();

        //get myUid of clicked user
        Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisUid");

        if (hisUid.equals(myUid)) {
            getFollowers(myUid);
        } else {
            getFollowers(hisUid);
        }

    }

    private void getFollowers(String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follows").child(uid).child("Followers");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                if (idList.size() != 0) {
                    noFollows.setVisibility(View.GONE);
                    showUsers();
                } else {
                    noFollows.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                followsList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    for (String id : idList){
                        try {
                            if (modelUser.getUid() != null) {
                                if (modelUser.getUid().equals(id)) {
                                    followsList.add(modelUser);
                                }
                            }
                        } catch (Exception e) {
                            Log.d("gagal", modelUser.getUid() + " != " + id);
                        }
                    }
                }
                adapterFollows.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }

    private  void checkUserStatus() {
        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            myUid = user.getUid();
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // go to previous activity
        return super.onSupportNavigateUp();
    }

}