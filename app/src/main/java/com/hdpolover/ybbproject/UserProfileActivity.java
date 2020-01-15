package com.hdpolover.ybbproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.hdpolover.ybbproject.models.ModelUser;
import com.hdpolover.ybbproject.notifications.APIService;
import com.hdpolover.ybbproject.notifications.Client;
import com.hdpolover.ybbproject.notifications.Data;
import com.hdpolover.ybbproject.notifications.Response;
import com.hdpolover.ybbproject.notifications.Sender;
import com.hdpolover.ybbproject.notifications.Token;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class UserProfileActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;

    //views from xml
    ImageView profileIv, cameraIv;
    TextView nameTv, emailTv, phoneTv, usernameTv, jobTv, cityTv, countryTv;
    Button messageBtn, followBtn;
    RecyclerView postsRecyclerView;

    String myUid, hisUid, publisherId;

    APIService apiService;
    ModelUser modelUser;
    String myName;

    TabLayout tabLayout;
    ViewPager viewPager;
    AdapterHisProfile viewAdapterProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile_user);

        //create api service
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);

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

        cameraIv.setVisibility(View.GONE);

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

        setCurrentUserName(myUid);

        //get myUid of clicked user
        Intent intent = getIntent();
        hisUid = intent.getStringExtra("uid");

        SharedPreferences sp = getSharedPreferences("OtherUserID", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("hisUid", hisUid);
        editor.apply();

        //we have to get info of currently signed in user
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //check until required data get
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //get data
                    String name = "" + ds.child("name").getValue();
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
                        Glide.with(getApplicationContext()).load(image).placeholder(R.drawable.ic_undraw_profile_pic).into(profileIv);
                    } catch (Exception e) {
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

                        publisherId = hisUid;
                        addNotification(hisUid, "");
                        myName = modelUser.getName();
                        sendNotification(hisUid,  myName," started following you");
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

    private void setCurrentUserName(String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelUser = dataSnapshot.getValue(ModelUser.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //for notification
    private void sendNotification(final String hisUid, final String name, final String message) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data("2", "", myUid, name + "" + message, "New notification", hisUid, R.drawable.ic_calendar);


                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addNotification(String userid, String postid){
        final String timeStamp = String.valueOf(System.currentTimeMillis());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", myUid);
        hashMap.put("publisherid", publisherId);
        hashMap.put("text", "started following you");
        hashMap.put("postid", postid);
        hashMap.put("timestamp", timeStamp);

        reference.push().setValue(hashMap);
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

    private void reportUser(final String hisUid) {
        //option camera/gallery to show in dialog
        String[] options = {"Spam", "Inappropiate"};

        //dialog
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Why are you reporting this user?");
        //set options to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    reportCurrentUser(hisUid, 0);
                }
                if (which == 1) {
                    reportCurrentUser(hisUid, 1);
                }
            }
        });
        //create and show dialog
        builder.create().show();
    }

    private void reportCurrentUser(String hisUid, int type) {
        String reportType = "";
        if (type == 0) {
            reportType = "spam";
        } else {
            reportType = "inappropiate";
        }

        String timeStamp = String.valueOf(System.currentTimeMillis());

        //each post will have a child "comments" taht willc ontain comments of that post
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Reports").child("Users");

        HashMap<String, Object> hashMap = new HashMap<>();
        //put info in hashmap
        hashMap.put("rId", timeStamp);
        hashMap.put("uid", hisUid);
        hashMap.put("timestamp", timeStamp);
        hashMap.put("reporterId", myUid);
        hashMap.put("violation", reportType);

        //put this data in db
        ref.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //added
                        Toast.makeText(getApplication(), "Thank you for reporting this user. We will review it and take further actions.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed
                        Toast.makeText(getApplication(), "Report Error", Toast.LENGTH_SHORT).show();

                    }
                });
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
        menu.findItem(R.id.action_search).setVisible(false);
        //searchview
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
        if (id == R.id.action_more) {
            reportUser(hisUid);
        }

        return super.onOptionsItemSelected(item);
    }

}
