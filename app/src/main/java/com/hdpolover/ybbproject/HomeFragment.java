package com.hdpolover.ybbproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
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
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hdpolover.ybbproject.adapters.AdapterPeopleSuggestion;
import com.hdpolover.ybbproject.adapters.AdapterPost;
import com.hdpolover.ybbproject.models.ModelPost;
import com.hdpolover.ybbproject.models.ModelUser;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    FloatingActionButton fab_add_post;
    TextView peopleTv;

    String myUid;

    RecyclerView postRecyclerView;
    List<ModelPost> postList;
    List<ModelPost> followedPostList;
    AdapterPost adapterPost;

    RecyclerView peopleSuggestionRecyclerView;
    List<ModelUser> peopleList;
    List<ModelUser> searchedPeopleList;
    AdapterPeopleSuggestion adapterPeopleSuggestion;

    List<String> followedPeopleId;
    List<String> idList;

    NestedScrollView nestedScrollView;
    ShimmerFrameLayout shimmerFrameLayoutPeople;
    ShimmerFrameLayout shimmerFrameLayoutPost;
    LinearLayout noPostHomeLayout;
    LinearLayout verifiedAccountLayout;

    Chip chip1, chip2, chip3, chip4, chip5, chip6, chip7;
    Chip activeChip;

    SwipeRefreshLayout swipeRefreshLayout;

    public HomeFragment() {
        //required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        fab_add_post = view.findViewById(R.id.fab_add_post);
        peopleTv = view.findViewById(R.id.peopleTv);
        verifiedAccountLayout = view.findViewById(R.id.verifiedAccountLayout);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshHome);

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

        //hide layouts on create
        noPostHomeLayout.setVisibility(View.GONE);

        //init post list
        postList = new ArrayList<>();
        followedPostList = new ArrayList<>();
        peopleList = new ArrayList<>();
        followedPeopleId =  new ArrayList<>();
        idList = new ArrayList<>();

        searchedPeopleList = new ArrayList<>();

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

        loadContents();

        swipeRefreshLayout.setColorSchemeResources(R.color.primaryColor,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadContents();

                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2500);
            }
        });

        fab_add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddPostActivity.class));
            }
        });

        return view;
    }

    private void loadContents() {
        shimmerFrameLayoutPost.startShimmer();
        shimmerFrameLayoutPeople.startShimmer();

        checkUserStatus();
        checkVerifiedAccount();
        getUnfollowedPeople(myUid);
        loadPosts();
        manageChips();
    }

    private void manageChips() {

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
                        loadFollowedPeoplePosts();
                    } else {
                        searchPostsOnChip(chipContents[finalI2]);
                    }
                }
            });
        }
    }

    private void checkVerifiedAccount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified()) {
            Log.e("e", "verified");
            verifiedAccountLayout.setVisibility(View.GONE);
        } else {
            Log.e("e", "not verified");
            verifiedAccountLayout.setVisibility(View.VISIBLE);
        }
    }

    private void loadFollowedPeoplePosts() {
            //path of all posts
            Query ref = FirebaseDatabase.getInstance().getReference("Posts");
            //get all data from this ref
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    followedPostList.clear();
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        ModelPost modelPost = ds.getValue(ModelPost.class);

                        boolean isFollowed = false;
                        for (String id : idList) {
                            if (modelPost.getUid().equals(id)) {
                                isFollowed = true;
                            }
                        }
                        if (isFollowed) {
                            followedPostList.add(modelPost);
                        }

                        //adapter
                        adapterPost = new AdapterPost(getActivity(), followedPostList);

                        if (followedPostList.size() == 0) {
                            noPostHomeLayout.setVisibility(View.VISIBLE);
                            postRecyclerView.setVisibility(View.GONE);
                        } else {
                            //set adapter recycler view
                            noPostHomeLayout.setVisibility(View.GONE);
                            postRecyclerView.setAdapter(adapterPost);
                            adapterPost.notifyDataSetChanged();
                        }

                        shimmerFrameLayoutPost.stopShimmer();
                        shimmerFrameLayoutPost.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //in case of error
                    Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

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
                        postRecyclerView.setVisibility(View.VISIBLE);
                        //set adapter recycler view
                        noPostHomeLayout.setVisibility(View.GONE);
                        postRecyclerView.setAdapter(adapterPost);
                        adapterPost.notifyDataSetChanged();
                    }

                    shimmerFrameLayoutPost.stopShimmer();
                    shimmerFrameLayoutPost.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //in case of error
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                        postRecyclerView.setVisibility(View.VISIBLE);
                        //set adapter recycler view
                        noPostHomeLayout.setVisibility(View.GONE);
                        postRecyclerView.setAdapter(adapterPost);
                        adapterPost.notifyDataSetChanged();
                    }

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
                    shimmerFrameLayoutPost.stopShimmer();
                    shimmerFrameLayoutPost.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //in case of error
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchPeople(final String searchQuery) {
        //path of all posts
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //get all data from this ref
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchedPeopleList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ModelUser modelUser = ds.getValue(ModelUser.class);

                    if (modelUser.getUsername().toLowerCase().contains(searchQuery.toLowerCase())) {
                        searchedPeopleList.add(modelUser);
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
        menu.findItem(R.id.action_more).setVisible(false);
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
        checkVerifiedAccount();
    }

    @Override
    public void onPause() {
        super.onPause();
        shimmerFrameLayoutPeople.stopShimmer();
        shimmerFrameLayoutPost.stopShimmer();
        checkUserStatus();
        checkVerifiedAccount();
    }

    @Override
    public void onStop() {
        super.onStop();
        checkUserStatus();
        checkVerifiedAccount();
    }

    //handle menu item click

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //get item id
        int id = item.getItemId();

        if (id == R.id.action_notif) {
            startActivity(new Intent(getActivity(), NotificationActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

}
