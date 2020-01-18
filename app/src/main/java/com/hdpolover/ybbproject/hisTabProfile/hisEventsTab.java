package com.hdpolover.ybbproject.hisTabProfile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import static android.content.Context.MODE_PRIVATE;

public class hisEventsTab extends Fragment {

    View view;
    ShimmerFrameLayout shimmerFrameLayout;
    LinearLayout noMyEventLayout;

    RecyclerView recyclerView;
    List<ModelEvent> eventList;
    AdapterEvent adapterEvent;

    String hisUid;
    TextView noEventTv;

    public hisEventsTab() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_events_tab, container, false);

        shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayoutEvent);
        noMyEventLayout = view.findViewById(R.id.noMyEventLayout);
        noEventTv = view.findViewById(R.id.noEventTv);

        noEventTv.setText("This user has no events");

        //get myUid of clicked user
        Intent intent = getActivity().getIntent();
        hisUid = intent.getStringExtra("uid");

        //init event list
        eventList = new ArrayList<>();

        //recycler view
        recyclerView = view.findViewById(R.id.myEventsRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        //show event first, for this load from last
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        //set layout to recyclerView
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterEvent = new AdapterEvent(getContext(), eventList);
        recyclerView.setAdapter(adapterEvent);

        noMyEventLayout.setVisibility(View.GONE);

        loadEvents();

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
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equals(hisUid)) {
                        for (DataSnapshot ds1 : ds.getChildren()) {
                            ModelEvent modelEvent = ds1.getValue(ModelEvent.class);

                            if (modelEvent.geteConfirmStatus().equals("approved")) {
                                eventList.add(modelEvent);
                            }

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
                        }
                    } else {
                        if (eventList.size() == 0) {
                            noMyEventLayout.setVisibility(View.VISIBLE);
                        }
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
