package com.hdpolover.ybbproject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hdpolover.ybbproject.adapters.AdapterNotifications;
import com.hdpolover.ybbproject.models.ModelNotification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    String myUid;

    LinearLayout noNotifLayout;
    RecyclerView recyclerView;

    AdapterNotifications adapterNotifications;
    List<ModelNotification> modelNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        noNotifLayout = findViewById(R.id.noNotifLayout);

        //action bar and its propertoes
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Notifications");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.notifRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);

        modelNotifications = new ArrayList<>();

        checkUserStatus();

        loadNotifications();
    }

    private void checkUserStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
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

    private void loadNotifications(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(myUid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                modelNotifications.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelNotification notification = snapshot.getValue(ModelNotification.class);
                    modelNotifications.add(notification);
                }

                adapterNotifications = new AdapterNotifications(getApplicationContext(), modelNotifications);

                if (modelNotifications.size() == 0) {
                    noNotifLayout.setVisibility(View.VISIBLE);
                } else {
                    noNotifLayout.setVisibility(View.GONE);
                    recyclerView.setAdapter(adapterNotifications);
                    Collections.reverse(modelNotifications);
                    adapterNotifications.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
