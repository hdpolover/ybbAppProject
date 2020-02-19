package com.hdpolover.ybbproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hdpolover.ybbproject.adapters.AdapterPeople;
import com.hdpolover.ybbproject.adapters.AdapterPeopleSuggestion;
import com.hdpolover.ybbproject.adapters.AdapterPostUpvoter;
import com.hdpolover.ybbproject.models.ModelUser;

import java.util.ArrayList;
import java.util.List;

public class PeopleActivity extends AppCompatActivity {

    String myEmail, myUid;
    String postId;

    ImageView upvoterAvatarIv;
    TextView upvoterNameTv;
    Button followUpvoterBtn;

    RecyclerView recyclerView;

    List<ModelUser> peopleList;
    AdapterPeople adapterPeople;

    List<String> idList;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        //action bar and its propertoes
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("User Suggestion");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //init views
        upvoterAvatarIv = findViewById(R.id.upvoterAvatarIv);
        upvoterNameTv = findViewById(R.id.upvoterNameTv);
        followUpvoterBtn = findViewById(R.id.followUpvoterBtn);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        peopleList = new ArrayList<>();
        adapterPeople = new AdapterPeople(getApplicationContext(), peopleList);
        recyclerView.setAdapter(adapterPeople);

        idList = new ArrayList<>();

        checkUserStatus();

        getUnfollowedPeople(myUid);
    }

    private void getUnfollowedPeople(final String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follows").child(uid).child("Followings");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }

                showPeople();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showPeople() {
        //path of all posts
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //get all data from this ref
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                peopleList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    try {
                        ModelUser modelUser = ds.getValue(ModelUser.class);

                        if (!modelUser.getUid().equals(myUid)) {
                            if (idList.size() == 0) {
                                peopleList.add(modelUser);
                            } else {
                                boolean isFollowed = true;
                                for (String id : idList) {
                                    if (modelUser.getUid().equals(id)) {
                                        isFollowed = false;
                                    }
                                }
                                if (isFollowed) {
                                    peopleList.add(modelUser);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("invalid", "user not found");
                    }

                    //adapter
                    adapterPeople = new AdapterPeople(getApplicationContext(), peopleList);
                    //set adapter recycler view
                    recyclerView.setAdapter(adapterPeople);
                    adapterPeople.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //in case of error
//                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //user is signed in
            myEmail = user.getEmail();
            myUid = user.getUid();
        } else {
            //user not signed in
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
