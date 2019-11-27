package com.hdpolover.ybbproject.schedulesTab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hdpolover.ybbproject.R;

public class MyEventsFragment extends Fragment {

    View view;

    public MyEventsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.schedules_my_events_fragment, container, false);

        return view;
    }
}
