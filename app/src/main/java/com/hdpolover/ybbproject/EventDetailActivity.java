package com.hdpolover.ybbproject;

import android.content.DialogInterface;
import android.content.Intent;
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

    String myUid, eId, uid;

    int eQuotaLeft;
    int eQuota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

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

        showEventDetails();

        Log.e("uid", uid);
        Log.e("my", myUid);

        if (uid.equals(myUid)) {
            getEventQuotaLeft();
        } else {
            if (eQuotaLeft < eQuota) {
                if (checkCurrentlyJoined()) {
                    joinBtn.setText("JOINED");
                    joinBtn.setEnabled(false);
                } else {
                    joinBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            joinEvent();
                        }
                    });
                }
            } else {
                if (checkCurrentlyJoined()) {
                    joinBtn.setText("QUOTA FULL");
                    joinBtn.setEnabled(false);
                } else {
                    joinBtn.setText("JOINED");
                    joinBtn.setEnabled(false);
                }
            }

        }
    }

    private void getEventQuotaLeft() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("EventParticipants").child(eId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    eQuotaLeft = eQuota;

                    joinBtn.setEnabled(false);
                    joinBtn.setText("Quota left: " + eQuotaLeft);
                } else {
                    int left = Integer.parseInt(dataSnapshot.getChildrenCount() + "");
                    eQuotaLeft = eQuota - left;

                    joinBtn.setEnabled(false);
                    joinBtn.setText("Quota left: " + eQuotaLeft);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean checkCurrentlyJoined() {
        final boolean[] isJoined = {false};
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("EventParticipants").child(eId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    if (ds.getKey().equals(myUid)) {
                        isJoined[0] = true;
                    } else {
                        isJoined[0] = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return isJoined[0];
    }

    private void joinEvent(){

        //path to store post data
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("EventParticipants").child(eId).child(myUid);
        //put data in this ref
        ref.setValue(true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //added
                        Toast.makeText(getApplicationContext(), "Event successfully joined", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed
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
                            Glide.with(getApplicationContext()).load(eImage).into(eImageIv);
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
