package com.hdpolover.ybbproject.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hdpolover.ybbproject.newsTab.BlogFragment;
import com.hdpolover.ybbproject.newsTab.EventsFragment;
import com.hdpolover.ybbproject.newsTab.InfoFragment;
import com.hdpolover.ybbproject.schedulesTab.MyEventsFragment;
import com.hdpolover.ybbproject.schedulesTab.PastFragment;
import com.hdpolover.ybbproject.schedulesTab.UpcomingFragment;

public class AdapterNews extends FragmentPagerAdapter {

    public AdapterNews(@NonNull FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new InfoFragment();
            case 1:
                return new BlogFragment();
            case 2:
                return new EventsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Info";
            case 1:
                return "Blog";
            case 2:
                return "Events";
            default:
                return null;
        }
    }
}

