package com.hdpolover.ybbproject.schedulesTab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hdpolover.ybbproject.R;

public class UpcomingFragment extends Fragment {

    View view;

    public UpcomingFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.schedules_upcoming_fragment, container, false);

        return view;
    }
}