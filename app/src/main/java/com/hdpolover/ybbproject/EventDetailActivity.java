package com.hdpolover.ybbproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hdpolover.ybbproject.adapters.AdapterComment;
import com.hdpolover.ybbproject.models.ModelComment;
import com.hdpolover.ybbproject.models.ModelEvent;
import com.hdpolover.ybbproject.models.ModelUser;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EventDetailActivity  extends AppCompatActivity {

    ImageView imageViewEvent;
    EditText editTextTitleEvent;
    EditText editTextDateFromEvent;
    EditText editTextTimeFromEvent;
    EditText editTextDateToEvent;
    EditText editTextTimeToEvent;
    Spinner editTextSpinnerEvent;
    EditText editTextLoctEvent;
    EditText editTextDescEvent;
    FirebaseAuth auth;
    FirebaseDatabase getDatabase;
    DatabaseReference getReference;
    String uid, myEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        getSupportActionBar().setTitle("Detail Event");

        imageViewEvent = findViewById(R.id.eventImgdt);
        editTextTitleEvent = findViewById(R.id.titleEtdt);
        editTextDateFromEvent = findViewById(R.id.eventDateFromdt);
        editTextTimeFromEvent = findViewById(R.id.eventTimeFromdt);
        editTextDateToEvent = findViewById(R.id.eventDateTodt);
        editTextTimeToEvent = findViewById(R.id.eventTimeTodt);
        editTextSpinnerEvent = findViewById(R.id.eventCatSpindt);
        editTextLoctEvent = findViewById(R.id.eventLocEtdt);
        editTextDescEvent = findViewById(R.id.descEtdt);

        auth = FirebaseAuth.getInstance();
        // mendapatkan user id dari akun terautentififkasi
        FirebaseUser user = auth.getCurrentUser();
        uid = user.getUid();

        getDatabase = FirebaseDatabase.getInstance();
        getReference = getDatabase.getReference();

        getReference.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            ModelEvent event = dataSnapshot.getValue(ModelEvent.class);
                String imageEvent = event.getImgEvent();
                try {
                    Picasso.get().load(imageEvent)
                            .placeholder(R.drawable.ic_undraw_profile_pic)
                            .fit()
                            .centerCrop()
                            .into(imageViewEvent);
                } catch (Exception e) {

                }
                String TitleEvent = event.getTitleEvent();
                event.setTitleEvent(TitleEvent);
                String DateEventFrom = event.getDateEventFrom();
                String TimeEventFrom = event.getTimeEventFrom();
                String DateEventTo = event.getDateEventTo();
                String TimeEventTo = event.getTimeEventTo();
                String CatEvent = event.getCatEvent();
                String LoctEvent = event.getLoctEvent();
                String DescEvent = event.getDescEvent();
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
            myEmail = user.getEmail();
            uid = user.getUid();
        } else {
            //user not signed in
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

}
