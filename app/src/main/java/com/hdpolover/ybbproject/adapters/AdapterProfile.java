package com.hdpolover.ybbproject.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hdpolover.ybbproject.tabProfile.AboutTab;
import com.hdpolover.ybbproject.tabProfile.DashboardTab;
import com.hdpolover.ybbproject.tabProfile.PostTab;
import com.hdpolover.ybbproject.tabProfile.ScheduleTab;

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
                return new ScheduleTab();
            case 3:
                return new AboutTab();
            case 4:
                return new AboutTab();
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
                return "Post";
            case 2:
                return "Schedule";
            case 3:
                return "About";
            case 4:
                return "About";
            default:
                return null;
        }
    }
}
