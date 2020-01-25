package com.hdpolover.ybbproject;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hdpolover.ybbproject.helpers.SocialTimeConverter;
import com.hdpolover.ybbproject.models.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class EventDetailActivity  extends AppCompatActivity {

    ImageView eImageIv, eCreatorImageIv;
    TextView eTitleTv, eDescTv, eStartTv, eEndTv, eLocationTv, eSpeakerTv,
            eQuotaTv, eCategoryTv, eConfirmStatusTv, eCreatorNameTv, eCreatedOnTv;
    LinearLayout confirmStatusDetailLayout;
    Button joinBtn;

    String myUid, eId, uid, joinStat;

    int eQuotaLeft = 0;
    int eQuota;

    boolean isJoined = false;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        progressDialog = new ProgressDialog(this);

        //action bar and its propertoes
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Event Detail");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        eImageIv = findViewById(R.id.eImageIv);
        eTitleTv = findViewById(R.id.eTitleTv);
        eDescTv = findViewById(R.id.eDescTv);
        eLocationTv = findViewById(R.id.eLocationTv);
        eStartTv = findViewById(R.id.eStartTv);
        eEndTv = findViewById(R.id.eEndTv);
        eSpeakerTv = findViewById(R.id.eSpeakerTv);
        eQuotaTv = findViewById(R.id.eQuotaTv);
        eCategoryTv = findViewById(R.id.eCategoryTv);
        eConfirmStatusTv = findViewById(R.id.eConfirmStatusTv);
        confirmStatusDetailLayout = findViewById(R.id.confirmStatusDetailLayout);
        eCreatedOnTv = findViewById(R.id.eCreatedOnTv);
        eCreatorImageIv = findViewById(R.id.eCreatorImageIv);
        eCreatorNameTv = findViewById(R.id.eCreatorNameTv);
        joinBtn = findViewById(R.id.joinBtn);

        checkUserStatus();

        Intent intent = getIntent();
        eId = intent.getStringExtra("eId");
        uid = intent.getStringExtra("uid");
        joinStat = intent.getStringExtra("isJoined");

        showEventDetails();

        if (uid.equals(myUid)) {
            joinBtn.setEnabled(false);
        } else {
            if (joinStat.equals("true")) {
                isJoined = true;
                joinBtn.setText("JOINED");
                joinBtn.setEnabled(false);
            } else {
                joinBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        joinEvent();
                        isJoined = true;
                        joinBtn.setEnabled(false);
                    }
                });
            }
        }
    }

    private void getEventQuotaLeft(final Button join) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("EventParticipants");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    if (ds.getKey().equals(eId)) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            int left = Integer.parseInt(ds.getChildrenCount() + "");
                            eQuotaLeft = eQuota - left;

                            join.setText("Quota left: " + eQuotaLeft);
                        } else {
                            eQuotaLeft = eQuota;

                            join.setText("Quota left: " + eQuotaLeft);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean checkCurrentlyJoined() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("EventParticipants").child(eId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    if (ds.getKey().equals(myUid)) {
                        isJoined = true;
                    } else {
                        isJoined = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return isJoined;
    }

    private void joinEvent(){
        progressDialog.setMessage("Joining...");
        progressDialog.show();

        //path to store post data
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("EventParticipants").child(eId).child(myUid);
        //put data in this ref
        ref.setValue(true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //added
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Event successfully joined", Toast.LENGTH_SHORT).show();
                        joinBtn.setText("JOINED");
                        joinBtn.setBackgroundColor(getResources().getColor(R.color.colorGray));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getUserData(final ImageView userImage, final TextView username, String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelUser user = dataSnapshot.getValue(ModelUser.class);

                //get user data
                String image = user.getImage();
                try {
                    Glide.with(getApplicationContext()).load(image)
                            .placeholder(R.drawable.ic_undraw_profile_pic)
                            .centerCrop()
                            .into(userImage);
                } catch (Exception e) {

                }

                String name = user.getName();
                username.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                        String eImage = ""+ds.child("eImage").getValue();
                        String eStart = ""+ds.child("eStart").getValue();
                        String eEnd = ""+ds.child("eEnd").getValue();
                        String eDesc = ""+ds.child("eDesc").getValue();
                        String eLocation = ""+ds.child("eLocation").getValue();
                        String quota = ""+ds.child("eQuota").getValue();
                        String eSpeaker = ""+ds.child("eSpeaker").getValue();
                        String eCategory = ""+ds.child("eCategory").getValue();
                        String eConfirmStatus = ""+ds.child("eConfirmStatus").getValue();
                        String uid = ""+ds.child("uid").getValue();
                        String eCreatedOn = ""+ds.child("eCreatedOn").getValue();

                        //set data
                        eTitleTv.setText(eTitle);

                        try {
                            Glide.with(getApplicationContext()).load(eImage).placeholder(R.drawable.placeholder_ybb_news).into(eImageIv);
                        } catch (Exception e) {

                        }

                        eDescTv.setText(eDesc);
                        eLocationTv.setText(eLocation);
                        eSpeakerTv.setText(eSpeaker);
                        eCategoryTv.setText(eCategory);

                        //set confirm status
                        if (!uid.equals(myUid)) {
                            confirmStatusDetailLayout.setVisibility(View.GONE);
                        } else {
                            eConfirmStatusTv.setText(eConfirmStatus);
                        }

                        //set Start and end date
                        SocialTimeConverter stc = new SocialTimeConverter();
                        eStartTv.setText(stc.getEventDateDetail(Long.parseLong(eStart)));
                        eEndTv.setText(stc.getEventDateDetail(Long.parseLong(eEnd)));

                        //set Quota
                        eQuota = Integer.parseInt(quota);
                        eQuotaLeft = eQuota;
                        eQuotaTv.setText(quota);

                        //get creator data
                        getUserData(eCreatorImageIv, eCreatorNameTv, uid);
                        eCreatedOnTv.setText(stc.getSocialTimeFormat(eCreatedOn));

                        if (uid.equals(myUid)) {
                            getEventQuotaLeft(joinBtn);
                        } else {
                            if (eQuotaLeft <= eQuota) {
                                if (joinStat.equals("true")) {
                                    joinBtn.setText("JOINED");
                                } else {
                                    joinBtn.setText("JOIN");
                                }
                            } else {
                                if (joinStat.equals("true")) {
                                    joinBtn.setText("JOINED");
                                } else {
                                    joinBtn.setText("QUOTA FULL");
                                }
                            }
                        }
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

    private void reportEvent(final String eventId) {
        //option camera/gallery to show in dialog
        String[] options = {"Spam", "Inappropiate"};

        //dialog
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Why are you reporting this event?");
        //set options to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    reportCurrentEvent(eventId, 0);
                }
                if (which == 1) {
                    reportCurrentEvent(eventId, 1);
                }
            }
        });
        //create and show dialog
        builder.create().show();
    }

    private void reportCurrentEvent(String eId, int type) {
        String reportType = "";
        if (type == 0) {
            reportType = "spam";
        } else {
            reportType = "inappropiate";
        }

        String timeStamp = String.valueOf(System.currentTimeMillis());

        //each post will have a child "comments" taht willc ontain comments of that post
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Reports").child("Events");

        HashMap<String, Object> hashMap = new HashMap<>();
        //put info in hashmap
        hashMap.put("rId", timeStamp);
        hashMap.put("eId", eId);
        hashMap.put("timestamp", timeStamp);
        hashMap.put("reporterId", myUid);

        //put this data in db
        ref.child(reportType).child(eId).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //added
                        Toast.makeText(getApplication(), "Thank you for reporting this event. We will review it and take further actions.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed
                        Toast.makeText(getApplication(), "Report Error", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.action_notif).setVisible(false);
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        if (uid.equals(myUid)) {
            menu.findItem(R.id.action_more).setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //get item id
        int id = item.getItemId();
        if (id == R.id.action_more) {
            reportEvent(eId);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}
