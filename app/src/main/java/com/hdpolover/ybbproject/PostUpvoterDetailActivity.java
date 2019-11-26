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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hdpolover.ybbproject.adapters.AdapterPostUpvoter;
import com.hdpolover.ybbproject.adapters.AdapterUsers;
import com.hdpolover.ybbproject.models.ModelPostUptover;
import com.hdpolover.ybbproject.models.ModelUser;

import java.util.ArrayList;
import java.util.List;

public class PostUpvoterDetailActivity extends AppCompatActivity {

    String myEmail, myUid;
    String postId;

    ImageView upvoterAvatarIv;
    TextView upvoterNameTv;
    MaterialButton followUpvoterBtn;

    RecyclerView recyclerView;

    List<ModelUser> postUptoversList;
    AdapterPostUpvoter adapterPostUpvoter;

    List<String> idList;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_upvoter_detail);

        //action bar and its propertoes
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Upvoters");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //get id of post using intent
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //init views
        upvoterAvatarIv = findViewById(R.id.upvoterAvatarIv);
        upvoterNameTv = findViewById(R.id.upvoterNameTv);
        followUpvoterBtn = findViewById(R.id.followUpvoterBtn);

        recyclerView = findViewById(R.id.postUpvoterRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postUptoversList = new ArrayList<>();
        adapterPostUpvoter = new AdapterPostUpvoter(getApplicationContext(), postUptoversList);
        recyclerView.setAdapter(adapterPostUpvoter);

        idList = new ArrayList<>();

        checkUserStatus();

        //loadUpvoters();

        getLikes();

//        followUpvoterBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Followed", Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    private void getLikes() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PostUpvotes").child(postId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                showUsers();
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
                postUptoversList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    for (String id : idList){
                        try {
                            if (modelUser.getUid() != null) {
                                if (modelUser.getUid().equals(id)) {
                                    postUptoversList.add(modelUser);
                                }
                            }
                        } catch (Exception e) {
                            Log.d("gagal", modelUser.getUid() + " != " + id);
                        }
                    }
                }
                adapterPostUpvoter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                postUptoversList.clear();
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    ModelUser user = ds.getValue(ModelUser.class);
//
//                    for (String id : idList){
//                        if (user.getUid().equals(id.toString())){
//                            postUptoversList.add(user);
//                        }
//                    }
//
//                    //adapter
//                    adapterPostUpvoter = new AdapterPostUpvoter(getApplicationContext(), postUptoversList);
//                    //set adapter to recycler view
//                    recyclerView.setAdapter(adapterPostUpvoter);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

//    private void loadUpvoters() {
//        //layout linear for recyclerview
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
//        //set layout to recyclerview
//        recyclerView.setLayoutManager(layoutManager);
//
//        //init comment list
//        upvoterList = new ArrayList<>();
//
//        //path of the post, to get its comments
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Upvoters");
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                upvoterList.clear();
//                for (DataSnapshot ds: dataSnapshot.getChildren()) {
//                    ModelPostUptover modelPostUptover = ds.getValue(ModelPostUptover.class);
//
//                    upvoterList.add(modelPostUptover);
//
//                    //pass myUid
//
//                    //setup adapter
//                    adapterPostUpvoter = new AdapterPostUpvoter(getApplicationContext(), upvoterList, myUid, postId);
//                    //set adapter
//                    recyclerView.setAdapter(adapterPostUpvoter);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }

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
