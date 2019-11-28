package com.hdpolover.ybbproject.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hdpolover.ybbproject.tabProfile.EventsTab;
import com.hdpolover.ybbproject.tabProfile.UpvotesTab;
import com.hdpolover.ybbproject.tabProfile.DashboardTab;
import com.hdpolover.ybbproject.tabProfile.PostTab;
import com.hdpolover.ybbproject.tabProfile.CommentTab;

public class AdapterProfile extends FragmentPagerAdapter {

    public AdapterProfile(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DashboardTab();
            case 1:
                return new PostTab();
            case 2:
                return new CommentTab();
            case 3:
                return new UpvotesTab();
            case 4:
                return new EventsTab();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Dashboard";
            case 1:
                return "Posts";
            case 2:
                return "Comments";
            case 3:
                return "Upvotes";
            case 4:
                return "Events";
            default:
                return null;
        }
    }
}
