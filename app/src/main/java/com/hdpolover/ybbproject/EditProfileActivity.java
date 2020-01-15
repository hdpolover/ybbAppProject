package com.hdpolover.ybbproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    ActionBar actionBar;

    String myUid, usernameInput;

    //views
    //personal information
    EditText fullnameEt, birthDateEt, phoneNumberEt, occupationEt, usernameEt,
            cityLiveEt, countryLiveEt, cityFromEt, countryFromEt, educationEt, interestEt;
    ImageView infoDateHintIv;

    ChipGroup chipInterestGroup;
    TextInputEditText textInputEditText;
    MaterialButton addInterestBtn;

    //details
    EditText bioEt;

    MaterialButton saveProfileBtn;

    String username;
    boolean check = false;
    ArrayList<String> data;
    List<String> usernameList = new ArrayList<>();

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        pd = new ProgressDialog(this);

        checkUserStatus();
        //getCurrentUserData(myUid, data);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Edit Profile");
        //enable back button in action bar
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //init personal info
        fullnameEt = findViewById(R.id.fullnameEt);
        usernameEt = findViewById(R.id.usernameEt);
        birthDateEt = findViewById(R.id.birthDateEt);
        phoneNumberEt = findViewById(R.id.phoneNumberEt);
        occupationEt = findViewById(R.id.occupationEt);
        cityLiveEt = findViewById(R.id.cityLiveEt);
        countryLiveEt = findViewById(R.id.countryLiveEt);
        cityFromEt = findViewById(R.id.cityFromEt);
        countryFromEt = findViewById(R.id.countryFromEt);
        infoDateHintIv = findViewById(R.id.infoDateHintIv);
        educationEt = findViewById(R.id.educationEt);
        interestEt = findViewById(R.id.interestEt);

        //init details info
        bioEt = findViewById(R.id.bioEt);
        usernameInput = usernameEt.getText().toString();
        saveProfileBtn = findViewById(R.id.saveProfilebtn);

        infoDateHintIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "You can choose not to show your birthdate", Toast.LENGTH_LONG).show();
            }
        });

        setUserData();

        setUsernameList();

        birthDateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDateButton();
            }
        });

        saveProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // if ()
                updateUserProfileInfo();
            }
        });


    }

    private void setUsernameList() {
        //get post using the id of the post
        Query ref = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usernameList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        usernameList.add("" + ds.child("username").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void handleDateButton() {
        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, year);
                calendar1.set(Calendar.MONTH, month);
                calendar1.set(Calendar.DATE, date);
                String dateText = DateFormat.format("EEEE, MMM d, yyyy", calendar1).toString();

                birthDateEt.setText(dateText);
            }
        }, YEAR, MONTH, DATE);

        datePickerDialog.show();
    }

    private void setUserData() {
        //get current user info
        Query myRef = FirebaseDatabase.getInstance().getReference("Users");
        final Query query = myRef.orderByChild("uid").equalTo(myUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //get data
                    fullnameEt.setText("" + ds.child("name").getValue());
                    usernameEt.setText("" + ds.child("username").getValue());
                    username = "" + ds.child("username").getValue().toString();
                    phoneNumberEt.setText("" + ds.child("phone").getValue());
                    occupationEt.setText("" + ds.child("job").getValue());
                    cityLiveEt.setText("" + ds.child("city").getValue());
                    countryLiveEt.setText("" + ds.child("country").getValue());
                    cityFromEt.setText("" + ds.child("cityFrom").getValue());
                    countryFromEt.setText("" + ds.child("countryFrom").getValue());
                    if(ds.child("birthDate").getValue().toString().equals("edit in your profile!")){
                        birthDateEt.setText("");
                        bioEt.setText("");
                        educationEt.setText("");
                        interestEt.setText("");
                    }else{
                        birthDateEt.setText("" + ds.child("birthDate").getValue());
                        bioEt.setText("" + ds.child("bio").getValue());
                        educationEt.setText(""+ds.child("education").getValue());
                        interestEt.setText(""+ds.child("interest").getValue());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUserProfileInfo() {
        pd.setMessage("Updating profile info...");
        pd.show();

        String job = "";
        try {
            job = occupationEt.getText().toString().substring(0, 1).toUpperCase()
                    + occupationEt.getText().toString().substring(1);
        } catch (Exception e) {
            job = occupationEt.getText().toString().trim();
        }

        //interest
        String[] item = interestEt.getText().toString().split(", |\\,");
        String strItem = "";
        for (int i = 0; i < item.length ; i++) {
            strItem += item[i] + ", ";
        }

        HashMap<String, Object> hashMap = new HashMap<>();
        //put post info
        hashMap.put("uid", myUid);
        hashMap.put("name", fullnameEt.getText().toString().trim());
        hashMap.put("username", usernameEt.getText().toString().trim());
        hashMap.put("birthDate", birthDateEt.getText().toString().trim());
        hashMap.put("phone", phoneNumberEt.getText().toString().trim());
        hashMap.put("job", job);
        hashMap.put("city", cityLiveEt.getText().toString().trim());
        hashMap.put("country", countryLiveEt.getText().toString().trim());
        hashMap.put("cityFrom", cityFromEt.getText().toString().trim());
        hashMap.put("countryFrom", countryFromEt.getText().toString().trim());
        hashMap.put("bio", bioEt.getText().toString().trim());
        hashMap.put("education", educationEt.getText().toString().trim());
        hashMap.put("interest", strItem);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(myUid)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        Toast.makeText(EditProfileActivity.this, "Profile info successfully updated...", Toast.LENGTH_SHORT).show();

                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(EditProfileActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void checkUserStatus() {
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
