package com.hdpolover.ybbproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hdpolover.ybbproject.adapters.AdapterArticle;
import com.hdpolover.ybbproject.adapters.AdapterNews;
import com.hdpolover.ybbproject.adapters.AdapterProfile;
import com.hdpolover.ybbproject.api.InitRetrofit;
import com.hdpolover.ybbproject.models.WordPressPostModel;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true); //to show menu option in fragment
        super.onCreate(savedInstanceState);
    }

    //Inflate options menu

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflating menu
        inflater.inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_notif).setVisible(false);
        menu.findItem(R.id.action_more).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        //search view to search post by post title/desc
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        //search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //called when user press search button
//                if (!TextUtils.isEmpty(query)) {
//                    searchPosts(query);
//                    peopleTv.setVisibility(View.GONE);
//                    peopleSuggestionRecyclerView.setVisibility(View.GONE);
//                    shimmerFrameLayoutPost.startShimmer();
//                } else {
//                    loadPosts();
//                    peopleTv.setVisibility(View.VISIBLE);
//                    peopleSuggestionRecyclerView.setVisibility(View.VISIBLE);
//                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //called as and when useer press any letter
//                if (!TextUtils.isEmpty(newText)) {
//                    searchPosts(newText);
//                    peopleTv.setVisibility(View.GONE);
//                    peopleSuggestionRecyclerView.setVisibility(View.GONE);
//                    shimmerFrameLayoutPost.startShimmer();
//                } else {
//                    loadPosts();
//                    peopleTv.setVisibility(View.VISIBLE);
//                    peopleSuggestionRecyclerView.setVisibility(View.VISIBLE);
//                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

}
