package com.hdpolover.ybbproject.tabProfile;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.hdpolover.ybbproject.MainActivity;
import com.hdpolover.ybbproject.R;
import com.squareup.picasso.Picasso;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardTab extends Fragment {

    TextView emailTv, phoneTv, bioTv;

    String myUid;

    public DashboardTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard_tab, container, false);

        bioTv = view.findViewById(R.id.bioTv);

        checkUserStatus();

        setUserData();

        return view;
    }

    private void setUserData() {
        //get current user info
        Query myRef = FirebaseDatabase.getInstance().getReference("Users");
        final Query query = myRef.orderByChild("uid").equalTo(myUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    //get data
                    bioTv.setText("" + ds.child("bio").getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private  void checkUserStatus() {
        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //user is signed in stay here
            myUid = user.getUid();
        } else {
            startActivity(new Intent(getActivity(), MainActivity.class));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        checkUserStatus();
        setUserData();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkUserStatus();
        setUserData();
    }
}
