package com.hdpolover.ybbproject.tabProfile;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hdpolover.ybbproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutTab extends Fragment {


    public AboutTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about_tab, container, false);
    }

}
