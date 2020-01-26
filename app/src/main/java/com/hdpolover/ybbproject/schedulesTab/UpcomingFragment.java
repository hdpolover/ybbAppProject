package com.hdpolover.ybbproject.schedulesTab;

import android.os.Bundle;
import android.util.Log;
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

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hdpolover.ybbproject.R;
import com.hdpolover.ybbproject.adapters.AdapterEvent;
import com.hdpolover.ybbproject.models.ModelEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UpcomingFragment extends Fragment {

    View view;
    ShimmerFrameLayout shimmerFrameLayout;
    LinearLayout noUpcomingLayout;

    RecyclerView recyclerView;
    List<ModelEvent> eventList;
    AdapterEvent adapterEvent;

    String myUid;

    List<String> upcomingIdList;

    public UpcomingFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.schedules_upcoming_fragment, container, false);

        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayoutEventUpcoming);
        noUpcomingLayout = view.findViewById(R.id.noUpcomingLayout);

        noUpcomingLayout.setVisibility(View.GONE);

        //init event list
        eventList = new ArrayList<>();
        upcomingIdList = new ArrayList<>();

        //recycler view
        recyclerView = view.findViewById(R.id.eventUpcomingRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        //show event first, for this load from last
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        //set layout to recyclerView
        recyclerView.setLayoutManager(linearLayoutManager);
        //adapter
        adapterEvent = new AdapterEvent(getActivity(), eventList);
        //set adapter to recycle
        recyclerView.setAdapter(adapterEvent);

        loadEvents();
        getUserUpcomingEventIds();

        return view;
    }

    private void loadEvents() {
        //path of all event

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Events");
        //get all data from this ref
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if (ds.getKey().equals(myUid)) {
                        for (DataSnapshot ds1: ds.getChildren()) {
                            ModelEvent modelEvent = ds1.getValue(ModelEvent.class);

                            if (modelEvent.geteStatus().equals("upcoming")) {
                                    eventList.add(modelEvent);
                            }
                        }
                    } else {
                        if (eventList.size() == 0) {
                            noUpcomingLayout.setVisibility(View.VISIBLE);
                        }
                    }

                    //adapter
                    adapterEvent = new AdapterEvent(getActivity(), eventList);

                    if (eventList.size() == 0) {
                        noUpcomingLayout.setVisibility(View.VISIBLE);
                    } else {
                        noUpcomingLayout.setVisibility(View.GONE);
                        //set adapter to recycle
                        recyclerView.setAdapter(adapterEvent);
                        Collections.reverse(eventList);
                        adapterEvent.notifyDataSetChanged();
                    }

                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //in case of errors
                Toast.makeText(getActivity(),""+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getUserUpcomingEventIds() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("EventParticipants");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                upcomingIdList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    for (DataSnapshot ds1: ds.getChildren()) {
                        if (ds1.getKey().equals(myUid)) {
                            upcomingIdList.add(ds.getKey());
                        }
                    }
                }

                showUpcomingEvents();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showUpcomingEvents() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Events");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    for (DataSnapshot ds1: ds.getChildren()) {
                        ModelEvent modelEvent = ds1.getValue(ModelEvent.class);

                        boolean isJoined = false;
                        for (String id : upcomingIdList) {
                            if (modelEvent.geteId().equals(id) && modelEvent.geteStatus().equals("upcoming")) {
                                isJoined = true;
                            }
                        }
                        if (isJoined) {
                            eventList.add(modelEvent);
                        }
                    }

                    //adapter
                    adapterEvent = new AdapterEvent(getActivity(), eventList);

                    if (eventList.size() == 0) {
                        noUpcomingLayout.setVisibility(View.VISIBLE);
                    } else {
                        noUpcomingLayout.setVisibility(View.GONE);
                        //set adapter to recycle
                        recyclerView.setAdapter(adapterEvent);
                        Collections.reverse(eventList);
                        adapterEvent.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
    }

    @Override
    public void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmer();
    }
}
