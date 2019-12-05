package com.hdpolover.ybbproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hdpolover.ybbproject.adapters.AdapterPeopleSuggestion;
import com.hdpolover.ybbproject.adapters.AdapterPost;
import com.hdpolover.ybbproject.models.JSONDataModel;
import com.hdpolover.ybbproject.models.ModelPeopleSuggestion;
import com.hdpolover.ybbproject.models.ModelPost;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FloatingActionButton fab_add_post;
    TextView peopleTv;

    String uid;

    RecyclerView postRecyclerView;
    List<ModelPost> postList;
    AdapterPost adapterPost;

    RecyclerView peopleSuggestionRecyclerView;
    List<ModelPeopleSuggestion> peopleList;
    AdapterPeopleSuggestion adapterPeopleSuggestion;

    List<String> followedPeopleId;

    NestedScrollView nestedScrollView;
    ShimmerFrameLayout shimmerFrameLayoutPeople;
    ShimmerFrameLayout shimmerFrameLayoutPost;

    public HomeFragment() {
        //required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        fab_add_post = view.findViewById(R.id.fab_add_post);
        peopleTv = view.findViewById(R.id.peopleTv);


        shimmerFrameLayoutPeople = view.findViewById(R.id.shimmerFrameLayoutPeople);
        shimmerFrameLayoutPost = view.findViewById(R.id.shimmerFrameLayoutPost);

        //recycler view and its properties
        postRecyclerView = view.findViewById(R.id.postRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //show newest post first, for this load from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set layout to recycler view
        postRecyclerView.setLayoutManager(layoutManager);
        postRecyclerView.setNestedScrollingEnabled(false);

        //recycler view and its properties
        peopleSuggestionRecyclerView = view.findViewById(R.id.peopleRecyclerView);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        //set layout to recycler view
        peopleSuggestionRecyclerView.setLayoutManager(layoutManager1);
        peopleSuggestionRecyclerView.setNestedScrollingEnabled(false);

        nestedScrollView = view.findViewById(R.id.nestedScrollViewHome);

        checkUserStatus();

        //init post list
        postList = new ArrayList<>();
        peopleList = new ArrayList<>();
        followedPeopleId =  new ArrayList<>();

//        tryData = new ArrayList<>();
//        tryData = SplashActivity.infoJsonData;
////
//        Log.e("data", tryData.size() + "");

        //get followed user id to be compared later
        //setFollowedPeopleId();

        loadPeople();
        loadPosts();

        fab_add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddPostActivity.class));
            }
        });

        return view;
    }

    private void setFollowedPeopleId() {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follows")
                    .child(uid).child("Followings");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //clear the list first before loading
                    followedPeopleId.clear();

                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        Log.e("value", ""+ ds.getValue());
                        followedPeopleId.add(ds.getKey());
                        Log.e("id", "" + ds.getKey());
                        Log.e("list size", "" + followedPeopleId.size());
                    }

//                if (dataSnapshot.child(uid).exists()) {
//                    if (dataSnapshot.child(uid).child("Followings").exists()) {
//                            for (DataSnapshot ds: dataSnapshot.getChildren()) {
//                                Log.e("value", ""+ ds.getValue());
//                                followedPeopleId.add(ds.getKey());
//                                Log.e("id", "" + ds.getKey());
//                                Log.e("list size", "" + followedPeopleId.size());
//                            }
//                    }
//                } else {
//                    Log.i("none", "user hasn't done anything in follows");
//                }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    private void loadPeople() {
        //path of all posts
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //get all data from this ref
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                peopleList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ModelPeopleSuggestion modelPeopleSuggestion = ds.getValue(ModelPeopleSuggestion.class);

//                        if (followedPeopleId.size() != 0) {
//                            for (String followedUserId: followedPeopleId) {
//                                if (!modelPeopleSuggestion.getUid().equals(uid)
//                                        && !modelPeopleSuggestion.getUid().equals(followedUserId)) {
//                                    peopleList.add(modelPeopleSuggestion);
//                                }
//                            }
//                    } else {
//                            if (!modelPeopleSuggestion.getUid().equals(uid))
//                        {
//                        peopleList.add(modelPeopleSuggestion);
//                        }}

                    if (!modelPeopleSuggestion.getUid().equals(uid)) {
                        peopleList.add(modelPeopleSuggestion);
                    }

                    //adapter
                    adapterPeopleSuggestion = new AdapterPeopleSuggestion(getActivity(), peopleList);
                    //set adapter recycler view
                    peopleSuggestionRecyclerView.setAdapter(adapterPeopleSuggestion);
                    adapterPeopleSuggestion.notifyDataSetChanged();
                    shimmerFrameLayoutPeople.stopShimmer();
                    shimmerFrameLayoutPeople.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //in case of error
//                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPosts() {
        //path of all posts
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        //get all data from this ref
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);

                    postList.add(modelPost);

                    //adapter
                    adapterPost = new AdapterPost(getActivity(), postList);
                    //set adapter recycler view
                    postRecyclerView.setAdapter(adapterPost);
                    adapterPost.notifyDataSetChanged();
                    shimmerFrameLayoutPost.stopShimmer();
                    shimmerFrameLayoutPost.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //in case of error
//                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchPosts(final String searchQuery) {
        //path of all posts
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        //get all data from this ref
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);

                    if (modelPost.getpDesc().toLowerCase().contains(searchQuery.toLowerCase())) {
                        postList.add(modelPost);
                    }

                    //adapter
                    adapterPost = new AdapterPost(getActivity(), postList);
                    //set adapter recycler view
                    postRecyclerView.setAdapter(adapterPost);
                    adapterPost.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //in case of error
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        } else {
            //user not signed in, go to welcome
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    public void scrollUp(){
        nestedScrollView.smoothScrollTo(0,0);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true); //to show menu option in fragment
        super.onCreate(savedInstanceState);
    }

    //Inflate options menu

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflating menu
        inflater.inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.action_settings).setVisible(false);
        //search view to search post by post title/desc
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)MenuItemCompat.getActionView(item);

        //search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //called when user press search button
                if (!TextUtils.isEmpty(query)) {
                    searchPosts(query);
                    peopleTv.setVisibility(View.GONE);
                    peopleSuggestionRecyclerView.setVisibility(View.GONE);
                    shimmerFrameLayoutPost.startShimmer();
                } else {
                    loadPosts();
                    peopleTv.setVisibility(View.VISIBLE);
                    peopleSuggestionRecyclerView.setVisibility(View.VISIBLE);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //called as and when useer press any letter
                if (!TextUtils.isEmpty(newText)) {
                    searchPosts(newText);
                    peopleTv.setVisibility(View.GONE);
                    peopleSuggestionRecyclerView.setVisibility(View.GONE);
                    shimmerFrameLayoutPost.startShimmer();
                } else {
                    loadPosts();
                    peopleTv.setVisibility(View.VISIBLE);
                    peopleSuggestionRecyclerView.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        shimmerFrameLayoutPost.startShimmer();
        shimmerFrameLayoutPeople.startShimmer();
        checkUserStatus();
    }

    @Override
    public void onPause() {
        super.onPause();
        shimmerFrameLayoutPeople.stopShimmer();
        shimmerFrameLayoutPost.stopShimmer();
        checkUserStatus();
    }

    @Override
    public void onStop() {
        super.onStop();
        checkUserStatus();
    }

    //handle menu item click

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //get item id
        int id = item.getItemId();
//        if (id == R.id.action_logout) {
//            firebaseAuth.signOut();
//            checkUserStatus();
//        }
        if (id == R.id.action_notif) {
            startActivity(new Intent(getActivity(), NotificationActivity.class));
        }
//        if (id == R.id.action_add_post) {
//            startActivity(new Intent(getActivity(), AddPostActivity.class));
//        }


        return super.onOptionsItemSelected(item);
    }

}
