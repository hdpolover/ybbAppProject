package com.hdpolover.ybbproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hdpolover.ybbproject.adapters.AdapterProfile;

public class NewsFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;
    AdapterProfile viewAdapterProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        /////*     initialize view   */////
        viewPager = view.findViewById(R.id.viewPager);

        /////*     initialize ViewPager   */////
        viewAdapterProfile = new AdapterProfile(getFragmentManager());

        /////*     add adapter to ViewPager  */////
        viewPager.setAdapter(viewAdapterProfile);
        tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabRippleColor(null);

        return view;
    }

}
