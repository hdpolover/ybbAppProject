package com.hdpolover.ybbproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hdpolover.ybbproject.models.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity {

    ActionBar actionBar;

    String myUid;

    //views
    //personal information
    EditText fullnameEt, birthDateEt, phoneNumberEt,
    cityLiveEt, countryLiveEt, cityFromEt, countryFromEt;
    ImageView infoDateHintIv;

    //details
    EditText bioEt;

    MaterialButton saveProfileBtn;

    ArrayList<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        checkUserStatus();

        actionBar = getSupportActionBar();
        actionBar.setTitle("Edit Profile");
        //enable back button in action bar
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //init personal info
        fullnameEt = findViewById(R.id.fullnameEt);
        birthDateEt = findViewById(R.id.birthDateEt);
        phoneNumberEt = findViewById(R.id.phoneNumberEt);
        cityLiveEt = findViewById(R.id.cityLiveEt);
        countryLiveEt = findViewById(R.id.countryLiveEt);
        cityFromEt = findViewById(R.id.cityFomEt);
        countryFromEt = findViewById(R.id.countryFromEt);
        infoDateHintIv = findViewById(R.id.infoDateHintIv);

        //init details info
        bioEt = findViewById(R.id.bioEt);

        saveProfileBtn = findViewById(R.id.saveProfilebtn);

        infoDateHintIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        setUserData();

        saveProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUserData() {
        //personal info
        fullnameEt.setText(data.get(0));
        birthDateEt.setText(data.get(1));
        phoneNumberEt.setText(data.get(2));
        cityLiveEt.setText(data.get(3));
        countryLiveEt.setText(data.get(4));
        cityFromEt.setText(data.get(5));
        cityFromEt.setText(data.get(6));
        countryFromEt.setText(data.get(7));

        //details
        bioEt.setText(data.get(8));
    }

    private void getCurrentUserData() {
        final ArrayList<String> data = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        Log.e("ref", reference.toString());
        //Query fQuery = reference.orderByChild("uid").equalTo(myUid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelUser user = dataSnapshot.getValue(ModelUser.class);

                data.add(user.getName());
                data.add(user.getName());
                data.add(user.getName());
                data.add(user.getName());
                data.add(user.getName());
                data.add(user.getName());
                data.add(user.getName());

//                for (DataSnapshot ds: dataSnapshot.getChildren()) {
//                    ModelUser user =
//                    //get data
//                    data.add(""+ds.child("name").getValue());
//                    data.add(""+ds.child("birthDate").getValue());
//                    data.add(""+ds.child("phone").getValue());
//                    data.add(""+ds.child("city").getValue());
//                    data.add(""+ds.child("country").getValue());
//                    data.add(""+ds.child("cityFrom").getValue());
//                    data.add(""+ds.child("countryFrom").getValue());
//                    data.add(""+ds.child("bio").getValue());
//                    data.add(""+ds.child("uid").getValue());
//                    Log.e("name", ""+ds.child("name").getValue());
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }

    private  void checkUserStatus() {
        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //user is signed in stay here
            myUid = user.getUid();
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // go to previous activity
        return super.onSupportNavigateUp();
    }

}
