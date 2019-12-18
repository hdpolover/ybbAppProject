package com.hdpolover.ybbproject.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hdpolover.ybbproject.hisTabProfile.hisCommentTab;
import com.hdpolover.ybbproject.hisTabProfile.hisDashboardTab;
import com.hdpolover.ybbproject.hisTabProfile.hisEventsTab;
import com.hdpolover.ybbproject.hisTabProfile.hisPostTab;
import com.hdpolover.ybbproject.hisTabProfile.hisUpvotesTab;


public class AdapterHisProfile extends FragmentPagerAdapter {

    public AdapterHisProfile(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new hisDashboardTab();
            case 1:
                return new hisPostTab();
            case 2:
                return new hisCommentTab();
            case 3:
                return new hisUpvotesTab();
//            case 4:
//                return new hisEventsTab();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Dashboard";
            case 1:
                return " Posts ";
            case 2:
                return " Comments ";
            case 3:
                return " Upvotes ";
//            case 4:
//                return " Events ";
            default:
                return null;
        }
    }
}
