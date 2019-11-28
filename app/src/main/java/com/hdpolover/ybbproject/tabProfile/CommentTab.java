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
public class CommentTab extends Fragment {


    public CommentTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment_tab, container, false);
    }

}
