package com.hdpolover.ybbproject;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class EventDetailActivity  extends AppCompatActivity {

    ImageView imageViewEvent;
    TextView editTextTitleEvent;
    TextView editTextDateFromEvent;
    TextView editTextTimeFromEvent;
    TextView editTextDateToEvent;
    TextView editTextTimeToEvent;
    TextView editTextSpinnerEvent;
    TextView editTextLoctEvent;
    TextView editTextDescEvent, editTextEventQuot, editTextEventParticipants, editTextEventSpeaker;
    Button joinBtn;

    String myUid, eId, uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        //action bar and its propertoes
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Event Detail");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        imageViewEvent = findViewById(R.id.myeventImg);
        editTextTitleEvent = findViewById(R.id.myeventTit);
        editTextDateFromEvent = findViewById(R.id.myeventDate);
        editTextTimeFromEvent = findViewById(R.id.myeventTime);
//        editTextDateToEvent = findViewById(R.id.eventDateTo);
//        editTextTimeToEvent = findViewById(R.id.eventTimeTo);
        editTextSpinnerEvent = findViewById(R.id.myeventCategory);
        editTextLoctEvent = findViewById(R.id.myeventLocation);
        editTextDescEvent = findViewById(R.id.myeventDescrip);
        editTextEventQuot = findViewById(R.id.eventQuot);
        editTextEventSpeaker = findViewById(R.id.myeventSpeaker);
        joinBtn = findViewById(R.id.joinBtn);

        checkUserStatus();

        Intent intent = getIntent();
        eId = intent.getStringExtra("eId");
        uid = intent.getStringExtra("uid");
        Log.e("AD", eId + uid);

        showEventDetails();

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EventDetailActivity.this, "Join Click!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEventDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Events").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    if (ds.getKey().equals(eId)) {
                        //get data
                        String eTitle = ""+ds.child("eTitle").getValue();
                        String eDateFrom = ""+ds.child("eDateFrom").getValue();
                        String eImage = ""+ds.child("eImage").getValue();
                        String eDateTo = ""+ds.child("eDateTo").getValue();
                        String eDesc = ""+ds.child("eDesc").getValue();
                        String eLocation = ""+ds.child("eLocation").getValue();
                        String eQuota = ""+ds.child("eQuota").getValue();
                        String eParticipants = ""+ds.child("eParticipants").getValue();
                        String eSpeaker = ""+ds.child("eSpeaker").getValue();
                        String eCategory = ""+ds.child("eCategory").getValue();
                        String eTimeFrom = ""+ds.child("eTimeFrom").getValue();
                        String eTimeTo = ""+ds.child("eTimeTo").getValue();

                        //set data
                        editTextTitleEvent.setText(eTitle);

                        try {
                            Glide.with(getApplicationContext()).load(eImage).into(imageViewEvent);
                        } catch (Exception e) {

                        }
                        editTextDateFromEvent.setText(eDateFrom);
                        editTextTimeFromEvent.setText(eTimeFrom);
//                        editTextDateToEvent.setText(eDateTo);
//                        editTextTimeToEvent.setText(eTimeTo);
                        editTextLoctEvent.setText(eLocation);
//                        List<String> category = new ArrayList<String>();
//                        category.add(eCategory);
//
//                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EventDetailActivity.this,android.R.layout.simple_spinner_dropdown_item,category);
//                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        editTextSpinnerEvent.setAdapter(adapter);

                        editTextSpinnerEvent.setText(eCategory);
                        editTextEventSpeaker.setText(eSpeaker);
                        editTextEventQuot.setText(eQuota);
                        editTextDescEvent.setText(eDesc);

                        editTextTitleEvent.setEnabled(false);
                        editTextDateFromEvent.setEnabled(false);
//                        editTextDateToEvent.setEnabled(false);
//                        editTextTimeToEvent.setEnabled(false);
                        editTextTimeFromEvent.setEnabled(false);
                        editTextLoctEvent.setEnabled(false);
                        editTextEventSpeaker.setEnabled(false);
                        editTextEventQuot.setEnabled(false);
                        editTextDescEvent.setEnabled(false);
                        editTextSpinnerEvent.setEnabled(false);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkUserStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //user is signed in
            myUid = user.getUid();
        } else {
            //user not signed in
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}
