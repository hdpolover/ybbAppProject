package com.hdpolover.ybbproject.tabProfile;


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
import com.hdpolover.ybbproject.R;
import com.squareup.picasso.Picasso;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardTab extends Fragment {

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //storage
    StorageReference storageReference;
    TextView emailTv, phoneTv;

    public DashboardTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard_tab, container, false);

        firebaseAuth = firebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        storageReference = getInstance().getReference(); //firebase storage refence

//        emailTv = view.findViewById(R.id.emailTv);
//        phoneTv = view.findViewById(R.id.phoneTv);


        //we have to get info of currently signed in user
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //check until required data get
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //get data
                    String email = "" + ds.child("email").getValue();
                    String phone = "" + ds.child("phone").getValue();

                    //set data
//                    emailTv.setText(email);

//                    if (phone == "") {
//                        phoneTv.setText("086213253142");
//                    } else {
//                        phoneTv.setText(phone);
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

}
