package com.hdpolover.ybbproject.tabProfile;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsTab extends Fragment {

    View view;
    ShimmerFrameLayout shimmerFrameLayout;
    LinearLayout noMyEventLayout;
    LinearLayout placeholders;

    RecyclerView recyclerView;
    List<ModelEvent> eventList;
    AdapterEvent adapterEvent;

    String myUid, hisUid;

    public EventsTab() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_events_tab, container, false);

        SharedPreferences sp = getContext().getSharedPreferences("OtherUserID",MODE_PRIVATE);
        hisUid = sp.getString("hisUid", "");

        //shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayoutEvent);
        noMyEventLayout = view.findViewById(R.id.noMyEventLayout);
        shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayoutEvent);

        noMyEventLayout.setVisibility(View.GONE);

        //recycler view
        recyclerView = view.findViewById(R.id.myEventsRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        //show event first, for this load from last
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        //init event list
        eventList = new ArrayList<>();

        //set layout to recyclerView
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterEvent = new AdapterEvent(getContext(), eventList);
        recyclerView.setAdapter(adapterEvent);

        checkUserStatus();

        loadEvents(myUid);

        return view;
    }

    private void loadEvents(String uid) {
        //path of all event
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Events").child(uid);
        //get all data from this ref
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelEvent modelEvent = ds.getValue(ModelEvent.class);

                    eventList.add(modelEvent);

                    //adapter
                    adapterEvent = new AdapterEvent(getActivity(), eventList);

                    if (eventList.size() == 0) {
                        noMyEventLayout.setVisibility(View.VISIBLE);
                    } else {
                        noMyEventLayout.setVisibility(View.GONE);
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
               // Toast.makeText(getActivity(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void checkUserStatus() {
        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //user is signed in stay here
            myUid = user.getUid();
        }
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
