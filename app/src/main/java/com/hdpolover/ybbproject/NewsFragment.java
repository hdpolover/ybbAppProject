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
import com.hdpolover.ybbproject.adapters.AdapterNews;
import com.hdpolover.ybbproject.adapters.AdapterProfile;

public class NewsFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;
    AdapterNews adapterNews;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        /////*     initialize view   */////
        viewPager = view.findViewById(R.id.newsViewPager);

        /////*     initialize ViewPager   */////
        adapterNews = new AdapterNews(getFragmentManager());

        /////*     add adapter to ViewPager  */////
        viewPager.setAdapter(adapterNews);
        tabLayout = view.findViewById(R.id.newsTabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabRippleColor(null);

        return view;
    }

}
