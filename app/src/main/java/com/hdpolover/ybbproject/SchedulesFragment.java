package com.hdpolover.ybbproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hdpolover.ybbproject.adapters.AdapterSchedules;

public class SchedulesFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;
    AdapterSchedules adapterSchedules;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedules, container, false);

        viewPager = view.findViewById(R.id.scheduleViewPager);

        adapterSchedules = new AdapterSchedules(getFragmentManager());

        viewPager.setAdapter(adapterSchedules);
        tabLayout = view.findViewById(R.id.scheduleTabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabRippleColor(null);

        return view;
    }
}
