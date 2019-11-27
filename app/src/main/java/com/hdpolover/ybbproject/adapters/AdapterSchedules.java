package com.hdpolover.ybbproject.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hdpolover.ybbproject.schedulesTab.MyEventsFragment;
import com.hdpolover.ybbproject.schedulesTab.PastFragment;
import com.hdpolover.ybbproject.schedulesTab.UpcomingFragment;
import com.hdpolover.ybbproject.tabProfile.AboutTab;
import com.hdpolover.ybbproject.tabProfile.DashboardTab;
import com.hdpolover.ybbproject.tabProfile.PostTab;
import com.hdpolover.ybbproject.tabProfile.ScheduleTab;

public class AdapterSchedules extends FragmentPagerAdapter {

    public AdapterSchedules(@NonNull FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MyEventsFragment();
            case 1:
                return new UpcomingFragment();
            case 2:
                return new PastFragment();
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
                return "My Events";
            case 1:
                return "Upcoming";
            case 2:
                return "Past";
            default:
                return null;
        }
    }
}

