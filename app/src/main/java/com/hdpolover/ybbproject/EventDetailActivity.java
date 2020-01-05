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

    List<String> joinedIdList;
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
        getEventQuotaLeft();
        Log.e("left", eQuotaLeft+"");
        Log.e("q", eQuota +"");

        if (uid.equals(myUid)) {
            joinBtn.setEnabled(false);
            joinBtn.setText("Quota left: " + eQuotaLeft);
        } else {
            joinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (eQuotaLeft < eQuota) {
                        if (checkCurrentlyJoined()) {
                            joinBtn.setText("JOINED");
                            joinBtn.setEnabled(false);
                        } else {
                            joinEvent();
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
            });
        }
    }

    private void getEventQuotaLeft() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("EventParticipants").child(eId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    eQuotaLeft = eQuota;
                } else {
                    int left = Integer.parseInt(dataSnapshot.getChildrenCount() + "");
                    eQuotaLeft = eQuota - left;
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
        //post without image
        HashMap<Object, String> hashMap = new HashMap<>();
        //put post info
        hashMap.put("uid", uid);

        //path to store post data
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("EventParticipants").child(eId);
        //put data in this ref
        ref.child(uid).setValue(hashMap)
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
                        if (uid.equals(myUid)) {
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}
