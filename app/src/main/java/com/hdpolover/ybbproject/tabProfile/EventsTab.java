package com.hdpolover.ybbproject.tabProfile;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hdpolover.ybbproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsTab extends Fragment {


    public EventsTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events_tab, container, false);


        return view;
    }

}
