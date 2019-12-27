package com.hdpolover.ybbproject.hisTabProfile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hdpolover.ybbproject.MainActivity;
import com.hdpolover.ybbproject.R;

public class hisDashboardTab extends Fragment {


    TextView emailTv, phoneTv, bioTv, educationTv, interestTv, birthDateTv;

    String hisUid;

    public hisDashboardTab() {
        // Required empty public constructor
    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard_tab, container, false);

        bioTv = view.findViewById(R.id.bioTv);
        educationTv = view.findViewById(R.id.educationTv);
        emailTv = view.findViewById(R.id.emailTv);
        phoneTv = view.findViewById(R.id.phoneTv);
        interestTv = view.findViewById(R.id.interestTv);
        birthDateTv = view.findViewById(R.id.birthDateTv);

        //get myUid of clicked user
        Intent intent = getActivity().getIntent();
        hisUid = intent.getStringExtra("uid");
        //checkUserStatus();

        setUserData();

        return view;
    }

    private void setUserData() {
        //get current user info
        Query myRef = FirebaseDatabase.getInstance().getReference("Users");
        final Query query = myRef.orderByChild("uid").equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //get data
                    bioTv.setText("" + ds.child("bio").getValue());
                    educationTv.setText("" + ds.child("education").getValue());
                    emailTv.setText("" + ds.child("email").getValue());
                    phoneTv.setText("" + ds.child("phone").getValue());
                    interestTv.setText("" + ds.child("interest").getValue());
                    birthDateTv.setText("" + ds.child("birthDate").getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
//
//    private void checkUserStatus() {
//        //get current user
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            //user is signed in stay here
//            myUid = user.getUid();
//        } else {
//            startActivity(new Intent(getActivity(), MainActivity.class));
//        }
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        checkUserStatus();
//        setUserData();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        checkUserStatus();
//        setUserData();
//    }
}
