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

    RecyclerView recyclerView;
    List<ModelEvent> eventList;
    AdapterEvent adapterEvent;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedules, container, false);

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

        //recycler view
        recyclerView = view.findViewById(R.id.eventRecycleview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        //show event first, for this load from last
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        //set layout to recyclerView
         recyclerView.setLayoutManager(linearLayoutManager);


        //init event list
        eventList = new ArrayList<>();
        
        loadEvent();

        return view;
    }

    private void loadEvent() {
        //path of all event

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Events");
        //get all data from this ref
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelEvent modelEvent = ds.getValue(ModelEvent.class);

                    eventList.add(modelEvent);

                    //adapter
                    adapterEvent = new AdapterEvent(getActivity(), eventList);
                    //set adapter to recycle
                    recyclerView.setAdapter(adapterEvent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //in case of errors
                Toast.makeText(getActivity(),""+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
