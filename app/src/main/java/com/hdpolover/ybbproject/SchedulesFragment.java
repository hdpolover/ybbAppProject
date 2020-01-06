package com.hdpolover.ybbproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hdpolover.ybbproject.adapters.AdapterEvent;
import com.hdpolover.ybbproject.adapters.AdapterPost;
import com.hdpolover.ybbproject.adapters.AdapterSchedules;
import com.hdpolover.ybbproject.models.ModelEvent;
import com.hdpolover.ybbproject.models.ModelPost;

import java.util.ArrayList;
import java.util.List;

public class SchedulesFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;
    AdapterSchedules adapterSchedules;

    FloatingActionButton fab_add_event;

    List<ModelEvent> eventList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedules, container, false);

        fab_add_event = view.findViewById(R.id.fab_add_event);
        viewPager = view.findViewById(R.id.schedulesViewPager);

        adapterSchedules = new AdapterSchedules(getFragmentManager());

        viewPager.setAdapter(adapterSchedules);
        tabLayout = view.findViewById(R.id.scheduleTabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabRippleColor(null);

        fab_add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), "Add event coming soon...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), AddEventActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true); //to show menu option in fragment
        super.onCreate(savedInstanceState);
    }

//    private void searchPosts(final String searchQuery) {
//        //path of all posts
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
//        //get all data from this ref
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                postList.clear();
//                for (DataSnapshot ds: dataSnapshot.getChildren()) {
//                    ModelPost modelPost = ds.getValue(ModelPost.class);
//
//                    if (modelPost.getpDesc().toLowerCase().contains(searchQuery.toLowerCase())) {
//                        postList.add(modelPost);
//                    }
//
//                    //adapter
//                    adapterPost = new AdapterPost(getActivity(), postList);
//                    //set adapter recycler view
//                    postRecyclerView.setAdapter(adapterPost);
//                    adapterPost.notifyDataSetChanged();
//                    shimmerFrameLayoutPost.stopShimmer();
//                    shimmerFrameLayoutPost.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                //in case of error
//                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    //Inflate options menu

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflating menu
        inflater.inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_notif).setVisible(false);
        menu.findItem(R.id.action_more).setVisible(false);
        //search view to search post by post title/desc
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        //search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //called when user press search button
//                if (!TextUtils.isEmpty(query)) {
//                    searchPosts(query);
//                    peopleTv.setVisibility(View.GONE);
//                    peopleSuggestionRecyclerView.setVisibility(View.GONE);
//                    shimmerFrameLayoutPost.startShimmer();
//                } else {
//                    loadPosts();
//                    peopleTv.setVisibility(View.VISIBLE);
//                    peopleSuggestionRecyclerView.setVisibility(View.VISIBLE);
//                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //called as and when useer press any letter
//                if (!TextUtils.isEmpty(newText)) {
//                    searchPosts(newText);
//                    peopleTv.setVisibility(View.GONE);
//                    peopleSuggestionRecyclerView.setVisibility(View.GONE);
//                    shimmerFrameLayoutPost.startShimmer();
//                } else {
//                    loadPosts();
//                    peopleTv.setVisibility(View.VISIBLE);
//                    peopleSuggestionRecyclerView.setVisibility(View.VISIBLE);
//                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }
}
