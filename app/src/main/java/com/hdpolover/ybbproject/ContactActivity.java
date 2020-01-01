package com.hdpolover.ybbproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hdpolover.ybbproject.adapters.AdapterPostUpvoter;
import com.hdpolover.ybbproject.adapters.AdapterUsers;
import com.hdpolover.ybbproject.models.ModelUser;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    List<ModelUser> userList;
    ImageButton backBtn;
    TextView countContactTv;

    String uid;

    //firebase auth
    FirebaseAuth firebaseAuth;

    List<String> idList;

    LinearLayout noFollows;
    NestedScrollView nestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();

        backBtn = findViewById(R.id.backBtn);
        countContactTv = findViewById(R.id.countContactTv);
        noFollows = findViewById(R.id.noFollows);
        nestedScrollView = findViewById(R.id.nestedScrollViewContact);

        //init recyclerview
        recyclerView = findViewById(R.id.user_recyclerView);
        //set it's propertis
        //recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        //init user list
        userList = new ArrayList<>();
        adapterUsers = new AdapterUsers(getApplicationContext(), userList);
        recyclerView.setAdapter(adapterUsers);
        nestedScrollView.setSmoothScrollingEnabled(false);

        idList = new ArrayList<>();

        checkUserStatus();

        setUserFollows(countContactTv);

        //getAll users
        getUserIds();

        //click button to back
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }


    private void setUserFollows(final TextView countContactTv) {
        DatabaseReference followersRef = FirebaseDatabase.getInstance().getReference().child("Follows")
                .child(uid).child("Followings");
        followersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    countContactTv.setText("0 Contact");
                } else {
                    countContactTv.setText(dataSnapshot.getChildrenCount() + " Contacts");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUserIds() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follows").child(uid).child("Followings");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                if (idList.size() != 0) {
                    showUsers();
                    noFollows.setVisibility(View.GONE);
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
        //DatabaseReference
        Query reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    for (String id : idList){
                        try {
                            if (modelUser.getUid() != null) {
                                if (modelUser.getUid().equals(id)) {
                                    userList.add(modelUser);
                                }
                            }
                        } catch (Exception e) {
                            Log.d("gagal", modelUser.getUid() + " != " + id);
                        }
                    }
                }
                adapterUsers.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    private void showUsers() {
//        //get current user
//        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
//        //get path of database name "Users" containing users info
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follows")
//                .child(myUid).child("Followings");
//
//        Query query = ref.orderByChild("myUid").equalTo(myUid);
//        //get all data from path
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                userList.clear();
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    ModelUser modelUser = ds.getValue(ModelUser.class);
//
//                    //get all users except currently signed in user
//                    if (modelUser.getUid() != null) {
//                        if (!modelUser.getUid().equals(fUser.getUid())) {
//                            userList.add(modelUser);
//                        }
//                    }
//
//                    //adapter
//                    adapterUsers = new AdapterUsers(getApplicationContext(), userList);
//                    //set adapter to recycler view
//                    recyclerView.setAdapter(adapterUsers);
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

    private void searchUsers(final String query) {
        //get current user
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of database name "Users" containing users info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //get all data from path
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelUser modelUser = ds.getValue(ModelUser.class);

                    //conditions to fulfil search
                    // 1. User not current user
                    // 2. The user name of email contains text entered in Searchview (case insersitive)

                    //get all searched users except currently signed in user

                    if (!modelUser.getUid().equals(fUser.getUid())) {
                        if (modelUser.getName().toLowerCase().contains(query.toLowerCase()) ||
                                modelUser.getEmail().toLowerCase().contains(query.toLowerCase())) {
                            userList.add(modelUser);
                        }
                    }

                    //adapter
                    adapterUsers = new AdapterUsers(getApplicationContext(), userList);
                    //refresh adapter
                    adapterUsers.notifyDataSetChanged();
                    //set adapter to recycler view
                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //searchView
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        //search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //called when user press search button from keyboard
                //if search query is not empty the search
                if (!TextUtils.isEmpty(s.trim())) {
                    //search text contains text, search it
                    searchUsers(s);
                } else {
                    //search text empty, get all users
                    getUserIds();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //called whenever user press any single letter
                //if search query is not empty the search
                if (!TextUtils.isEmpty(s.trim())) {
                    //search text contains text, search it
                    searchUsers(s);
                } else {
                    //search text empty, get all users
                    getUserIds();
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    private void checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }
    }
}
