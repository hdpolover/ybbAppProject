package com.hdpolover.ybbproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.hdpolover.ybbproject.adapters.AdapterPost;
import com.hdpolover.ybbproject.adapters.AdapterSchedules;
import com.hdpolover.ybbproject.models.ModelPost;

import java.util.ArrayList;
import java.util.List;

public class SchedulesFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;
    AdapterSchedules adapterSchedules;

    List<ModelPost> eventList;
    AdapterPost adapterEvent;
    LinearLayout noEventSchedulesLayout;
    RecyclerView eventRecyclerView;
    ShimmerFrameLayout shimmerFrameLayoutEvent;

    FloatingActionButton fab_add_event;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedules, container, false);

        viewPager = view.findViewById(R.id.scheduleViewPager);

        fab_add_event = view.findViewById(R.id.fab_add_event);

        adapterSchedules = new AdapterSchedules(getFragmentManager());

        viewPager.setAdapter(adapterSchedules);
        tabLayout = view.findViewById(R.id.scheduleTabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabRippleColor(null);

        eventList = new ArrayList<>();

        fab_add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), "Add event coming soon...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), AddEventActivity.class));
            }
        });

        return view;
    }

    private void loadPosts() {
        //path of all Event
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events");
        //get all data from this ref
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);

                    eventList.add(modelPost);

                    //adapter
                    adapterEvent = new AdapterPost(getActivity(), eventList);

                    if (eventList.size() == 0) {
                        noEventSchedulesLayout.setVisibility(View.VISIBLE);
                    } else {
                        //set adapter recycler view
                        noEventSchedulesLayout.setVisibility(View.GONE);
                        eventRecyclerView.setAdapter(adapterEvent);
                        adapterEvent.notifyDataSetChanged();
                        shimmerFrameLayoutEvent.stopShimmer();
                        shimmerFrameLayoutEvent.setVisibility(View.GONE);
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
//                in case of error
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
