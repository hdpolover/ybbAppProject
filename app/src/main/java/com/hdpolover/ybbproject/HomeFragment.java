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
import android.widget.LinearLayout;
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
import com.google.android.material.chip.Chip;
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
import com.hdpolover.ybbproject.models.ModelPost;
import com.hdpolover.ybbproject.models.ModelUser;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FloatingActionButton fab_add_post;
    TextView peopleTv;

    String myUid;

    RecyclerView postRecyclerView;
    List<ModelPost> postList;
    AdapterPost adapterPost;

    RecyclerView peopleSuggestionRecyclerView;
    List<ModelUser> peopleList;
    AdapterPeopleSuggestion adapterPeopleSuggestion;

    List<String> followedPeopleId;
    List<String> idList;

    NestedScrollView nestedScrollView;
    ShimmerFrameLayout shimmerFrameLayoutPeople;
    ShimmerFrameLayout shimmerFrameLayoutPost;
    LinearLayout noPostHomeLayout;

    Chip chip1, chip2, chip3, chip4, chip5, chip6, chip7;
    Chip activeChip;

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

        //init chips
        chip1 = view.findViewById(R.id.chip1);
        chip2 = view.findViewById(R.id.chip2);
        chip3 = view.findViewById(R.id.chip3);
        chip4 = view.findViewById(R.id.chip4);
        chip5 = view.findViewById(R.id.chip5);
        chip6 = view.findViewById(R.id.chip6);
        chip7 = view.findViewById(R.id.chip7);

        activeChip = chip1;
        activeChip.setChipBackgroundColor(getResources().getColorStateList(R.color.primaryLightColor));

        shimmerFrameLayoutPeople = view.findViewById(R.id.shimmerFrameLayoutPeople);
        shimmerFrameLayoutPost = view.findViewById(R.id.shimmerFrameLayoutPost);
        noPostHomeLayout = view.findViewById(R.id.noPostHomeLayout);

        checkUserStatus();

        //init post list
        postList = new ArrayList<>();
        peopleList = new ArrayList<>();
        followedPeopleId =  new ArrayList<>();
        idList = new ArrayList<>();

        //recycler view and its properties
        postRecyclerView = view.findViewById(R.id.postRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //show newest post first, for this load from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set layout to recycler view
        postRecyclerView.setLayoutManager(layoutManager);
        postRecyclerView.setHasFixedSize(true);
        adapterPost = new AdapterPost(getContext(), postList);
        postRecyclerView.setAdapter(adapterPost);
        postRecyclerView.setNestedScrollingEnabled(false);

        //recycler view and its properties
        peopleSuggestionRecyclerView = view.findViewById(R.id.peopleRecyclerView);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        //set layout to recycler view
        peopleSuggestionRecyclerView.setLayoutManager(layoutManager1);
        peopleSuggestionRecyclerView.setHasFixedSize(true);
        adapterPeopleSuggestion = new AdapterPeopleSuggestion(getContext(), peopleList);
        peopleSuggestionRecyclerView.setAdapter(adapterPeopleSuggestion);
        peopleSuggestionRecyclerView.setNestedScrollingEnabled(false);

        nestedScrollView = view.findViewById(R.id.nestedScrollViewHome);

        //get followed user id to be compared later
        //setFollowedPeopleId();

        loadPeople();
        loadPosts();
        getUnfollowedPeople();

        fab_add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddPostActivity.class));
            }
        });

        final ArrayList<Chip> chips = new ArrayList<>();
        chips.add(chip1);
        chips.add(chip2);
        chips.add(chip3);
        chips.add(chip4);
        chips.add(chip5);
        chips.add(chip6);
        chips.add(chip7);

        final String[] chipContents = {"All", "Followings", "Istanbul", "IYS2020", "Winter", "Museum", "Kota"};

        for (int i = 0; i < chips.size(); i++) {
            chips.get(i).setText(chipContents[i]);
            final int finalI = i;
            final int finalI1 = i;
            final int finalI2 = i;
            chips.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //chip1.setBackgroundColor(808080);
                    activeChip.setChipBackgroundColor(getResources().getColorStateList(R.color.placeholder_bg));
                    chips.get(finalI).setChipBackgroundColor(getResources().getColorStateList(R.color.primaryLightColor));
                    activeChip = chips.get(finalI1);

                    if (chipContents[finalI2].equals("All")) {
                        loadPosts();
                    } else if (chipContents[finalI2].equals("Followings")) {
                        Toast.makeText(getActivity(), "Coming soon", Toast.LENGTH_SHORT).show();
                    } else {
                        searchPostsOnChip(chipContents[finalI2]);
                    }
                }
            });
        }

        return view;
    }

    private void searchPostsOnChip(final String searchQuery) {
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

                    if (postList.size() == 0) {
                        noPostHomeLayout.setVisibility(View.VISIBLE);
                    } else {
                        //set adapter recycler view
                        noPostHomeLayout.setVisibility(View.GONE);
                        postRecyclerView.setAdapter(adapterPost);
                        adapterPost.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //in case of error
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUnfollowedPeople() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follows").child(myUid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        if (snapshot.child("Followings").exists()) {
                            idList.add(snapshot.child("Followings").getKey());
                            Log.e("id", snapshot.child("Followings").getKey());
                        } else {
                            Log.e("2", "noFoloowings");
                        }
                }
                Log.e("se", idList.size() + "");
                //showUsers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setFollowedPeopleId() {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follows")
                    .child(myUid).child("Followings");
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

//                if (dataSnapshot.child(myUid).exists()) {
//                    if (dataSnapshot.child(myUid).child("Followings").exists()) {
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
                    ModelUser modelUser = ds.getValue(ModelUser.class);

//                        if (followedPeopleId.size() != 0) {
//                            for (String followedUserId: followedPeopleId) {
//                                if (!modelPeopleSuggestion.getUid().equals(myUid)
//                                        && !modelPeopleSuggestion.getUid().equals(followedUserId)) {
//                                    peopleList.add(modelPeopleSuggestion);
//                                }
//                            }
//                    } else {
//                            if (!modelPeopleSuggestion.getUid().equals(myUid))
//                        {
//                        peopleList.add(modelPeopleSuggestion);
//                        }}

                    if (!modelUser.getUid().equals(myUid)) {
                        peopleList.add(modelUser);
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

                    if (postList.size() == 0) {
                        noPostHomeLayout.setVisibility(View.VISIBLE);
                    } else {
                        //set adapter recycler view
                        noPostHomeLayout.setVisibility(View.GONE);
                        postRecyclerView.setAdapter(adapterPost);
                        adapterPost.notifyDataSetChanged();
                        shimmerFrameLayoutPost.stopShimmer();
                        shimmerFrameLayoutPost.setVisibility(View.GONE);
                    }
//                    //set adapter recycler view
//                    postRecyclerView.setAdapter(adapterPost);
//                    adapterPost.notifyDataSetChanged();
//                    shimmerFrameLayoutPost.stopShimmer();
//                    shimmerFrameLayoutPost.setVisibility(View.GONE);
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
            myUid = user.getUid();
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
