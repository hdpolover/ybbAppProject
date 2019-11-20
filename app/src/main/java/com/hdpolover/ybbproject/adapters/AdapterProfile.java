package com.hdpolover.ybbproject.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hdpolover.ybbproject.tabProfile.AboutTab;
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
                return new PostTab();
            case 1:
                return new ScheduleTab();
            case 2:
                return new AboutTab();
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
                return "Post";
            case 1:
                return "Schedule";
            case 2:
                return "About";
            default:
                return null;
        }
    }
}
