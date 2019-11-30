package com.hdpolover.ybbproject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class AddEventActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    String uid;

    EditText titleEt, descEt, dateEt;
    TextInputLayout eventDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        firebaseAuth = firebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create New Event");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        titleEt = findViewById(R.id.titleEt);
        descEt = findViewById(R.id.descEt);
        dateEt = findViewById(R.id.dateEt);
        eventDate = findViewById(R.id.eventDate);

        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDateButton();
            }
        });
    }

    private void checkUserStatus() {
        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
        } else {
            //startActivity(new Intent(SettingsActivity.this, MainActivity.class));
        }
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

                dateEt.setText(dateText);
            }
        }, YEAR, MONTH, DATE);

        datePickerDialog.show();




    }

    private void handleTimeButton() {
        Calendar calendar = Calendar.getInstance();
        int HOUR = calendar.get(Calendar.HOUR);
        int MINUTE = calendar.get(Calendar.MINUTE);
        boolean is24HourFormat = DateFormat.is24HourFormat(this);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                //Log.i(TAG, "onTimeSet: " + hour + minute);
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.HOUR, hour);
                calendar1.set(Calendar.MINUTE, minute);
                String dateText = DateFormat.format("h:mm a", calendar1).toString();
                //timeTextView.setText(dateText);
            }
        }, HOUR, MINUTE, is24HourFormat);

        timePickerDialog.show();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //get item id
        int id = item.getItemId();
        if (id == R.id.action_publish_post) {
//            //get data
////            String desc = descEt.getText().toString().trim();
////            if (TextUtils.isEmpty(desc)) {
////                Toast.makeText(AddPostActivity.this, "Write something here...", Toast.LENGTH_SHORT).show();
////                return false;
////            }
////
////            if (isUpdateKey.equals("editPost")) {
////                beginUpdate(desc, editPostId);
////            } else {
////                uploadData(desc);
////            }
            Toast.makeText(getApplicationContext(), "Published", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
