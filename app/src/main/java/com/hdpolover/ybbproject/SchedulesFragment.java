package com.hdpolover.ybbproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.hdpolover.ybbproject.adapters.AdapterSchedules;

public class SchedulesFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;
    AdapterSchedules adapterSchedules;

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

        fab_add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Create new event clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), AddEventActivity.class));
            }
        });

        return view;
    }
}
