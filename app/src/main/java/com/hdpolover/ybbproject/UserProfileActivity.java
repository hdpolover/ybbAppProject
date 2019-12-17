package com.hdpolover.ybbproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hdpolover.ybbproject.adapters.AdapterHisProfile;
import com.hdpolover.ybbproject.adapters.AdapterPost;
import com.hdpolover.ybbproject.adapters.AdapterProfile;
import com.hdpolover.ybbproject.models.ModelPost;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;

    //views from xml
    ImageView profileIv, cameraIv;
    TextView nameTv, emailTv, phoneTv, usernameTv, jobTv, cityTv, countryTv;
    Button messageBtn, followBtn, profileBtn;
    RecyclerView postsRecyclerView;
    LinearLayout followsLayout;

    List<ModelPost> postList;
    AdapterPost adapterPost;

    String myUid, hisUid;

    TabLayout tabLayout;
    ViewPager viewPager;
    AdapterHisProfile viewAdapterProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //init views
        nameTv = findViewById(R.id.nameTv);
        emailTv = findViewById(R.id.emailTv);
        phoneTv = findViewById(R.id.phoneTv);
        profileIv = findViewById(R.id.profileIv);
        usernameTv = findViewById(R.id.usernameTv);
        jobTv = findViewById(R.id.jobTv);
        cityTv = findViewById(R.id.cityTv);
        countryTv = findViewById(R.id.countryTv);
        messageBtn = findViewById(R.id.messageBtn);
        followBtn = findViewById(R.id.followBtn);
        cameraIv = findViewById(R.id.cameraIv);
        followsLayout = findViewById(R.id.followsLayout);

        cameraIv.setVisibility(View.GONE);
        followsLayout.setVisibility(View.GONE);

        postsRecyclerView = findViewById(R.id.recyclerview_posts);

        /////*     initialize view   */////
        viewPager = findViewById(R.id.viewPager);

        /////*     initialize ViewPager   */////
        viewAdapterProfile = new AdapterHisProfile(getSupportFragmentManager());

        /////*     add adapter to ViewPager  */////
        viewPager.setAdapter(viewAdapterProfile);
        tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabRippleColor(null);
        firebaseAuth = firebaseAuth.getInstance();

        checkUserStatus();

        //get uid of clicked user
        Intent intent = getIntent();
        hisUid = intent.getStringExtra("uid");

        //we have to get info of currently signed in user
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //check until required data get
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //get data
                    String name = "" + ds.child("name").getValue();
//                    String email = "" + ds.child("email").getValue();
//                    String phone = "" + ds.child("phone").getValue();
                    String image = "" + ds.child("image").getValue();
                    String username = "@" + ds.child("username").getValue();
                    String job = "" + ds.child("job").getValue();
                    String city = "" + ds.child("city").getValue();
                    String country = "" + ds.child("country").getValue();

                    //set data
                    nameTv.setText(name);
//                    emailTv.setText(email);
//                    phoneTv.setText(phone);
                    usernameTv.setText(username);
                    jobTv.setText(job);
                    cityTv.setText(city);
                    countryTv.setText(country);

                    try {
                        //if image is received then set
                        Picasso.get().load(image).placeholder(R.drawable.ic_undraw_profile_pic).into(profileIv);
                    } catch (Exception e) {
                        //if there is any exception while getting image then set default
                        //Picasso.get().load(R.drawable.ic_default_img_white).into(avatarIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("hisUid", hisUid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        followBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (followBtn.getText().toString().equals("Follow")) {
                        FirebaseDatabase.getInstance().getReference().child("Follows").child(myUid)
                                .child("Followings").child(hisUid).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("Follows").child(hisUid)
                                .child("Followers").child(myUid).setValue(true);
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Follows").child(myUid)
                                .child("Followings").child(hisUid).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("Follows").child(hisUid)
                                .child("Followers").child(myUid).removeValue();
                    }
                }
            });

        isFollowing(hisUid, followBtn);

        //postList = new ArrayList<>();
        //loadHistPosts();
    }

    private void isFollowing(final String followerId, final Button followBtn) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follows").child(myUid).child("Followings");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(followerId).exists()) {
                    followBtn.setText("Following");
                } else {
                    followBtn.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadHistPosts() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //show newest post first
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set this layout to recyclerview
        postsRecyclerView.setLayoutManager(layoutManager);

        //init post list
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        //query to load posts
        Query query = ref.orderByChild("uid").equalTo(hisUid);
        //get all data
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ModelPost myPosts = ds.getValue(ModelPost.class);

                    //add to list
                    postList.add(myPosts);

                    //adapter
                    adapterPost = new AdapterPost(UserProfileActivity.this, postList);
                    //set this adapter to recyclerview
                    postsRecyclerView.setAdapter(adapterPost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserProfileActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchHistPosts(final String searchQuery) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(UserProfileActivity.this);
        //show newest post first
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set this layout to recyclerview
        postsRecyclerView.setLayoutManager(layoutManager);

        //init post list
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        //query to load posts
        Query query = ref.orderByChild("uid").equalTo(hisUid);
        //get all data
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ModelPost myPosts = ds.getValue(ModelPost.class);

                    if (myPosts.getpDesc().toLowerCase().contains(searchQuery.toLowerCase())) {
                        //add to list
                        postList.add(myPosts);
                    }

                    //adapter
                    adapterPost = new AdapterPost(UserProfileActivity.this, postList);
                    //set this adapter to recyclerview
                    postsRecyclerView.setAdapter(adapterPost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserProfileActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            myUid = user.getUid();
        } else {
            //user not signed in, go to welcome
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.action_notif).setVisible(false);
        menu.findItem(R.id.action_settings).setVisible(false);

        MenuItem item = menu.findItem(R.id.action_search);
        //searchview
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                //called when user press search button
//                if (!TextUtils.isEmpty(query)) {
//                    //search
//                    searchHistPosts(query);
//                } else {
//                    loadHistPosts();
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                //called when user press search button
//                if (!TextUtils.isEmpty(newText)) {
//                    //search
//                    searchHistPosts(newText);
//                } else {
//                    loadHistPosts();
//                }
//                return false;
//            }
//        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //get item id
        int id = item.getItemId();
//        if (id == R.id.action_logout) {
//            firebaseAuth.signOut();
//            checkUserStatus();
//        }

        return super.onOptionsItemSelected(item);
    }

}
