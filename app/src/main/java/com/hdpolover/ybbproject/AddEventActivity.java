package com.hdpolover.ybbproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.TimeFormat;
import org.ocpsoft.prettytime.format.SimpleTimeFormat;

import java.io.ByteArrayOutputStream;
import java.nio.FloatBuffer;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.logging.SimpleFormatter;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class AddEventActivity extends AppCompatActivity {

    //permission constants
    private static final int STORAGE_REQUEST_CODE = 200;
    //image pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    //perrmission array
    String[] storagePermissions;

    //image picked will be the same in this uri
    Uri image_rui = null;

    String myUid;
    String finalEventStart, finalEventEnd;

    ProgressDialog pd;

    EditText eTitleEt, eDescEt, eStartEt, eEndEt, eLocationEt, eSpekearEt, eQuotaEt;
    ImageView eImageIv;
    Spinner eCategorySpinner;

    public static final String DATE_TIME_FORMAT = "EEE dd/MM/yyy hh:mm a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create New Event");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //int permission array
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        pd = new ProgressDialog(this);

        checkUserStatus();

        eTitleEt = findViewById(R.id.eTitleEt);
        eDescEt = findViewById(R.id.eDescEt);
        eStartEt = findViewById(R.id.eStartEt);
        eEndEt = findViewById(R.id.eEndEt);
        eCategorySpinner = findViewById(R.id.eCategorySpinner);
        eSpekearEt = findViewById(R.id.eSpekearEt);
        eQuotaEt = findViewById(R.id.eQuotaEt);
        eLocationEt = findViewById(R.id.eLocationEt);
        eImageIv = findViewById(R.id.eImageIv);

        setEventCategoryList();

        //get image from camera/gallery on click
        eImageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

        //DatePicker
        eStartEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleStartDateTime();
            }
        });

        eEndEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleEndDateTime();
            }
        });
    }

    private void checkUserStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            myUid = user.getUid();
        } else {
            startActivity(new Intent(AddEventActivity.this, MainActivity.class));
        }
    }

    private  void showImagePickDialog() {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
    }

    private void pickFromGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermission() {
        //check if storage permission is enabled
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        //request runtime storage permission
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    //handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Storage permissions are necessary..", Toast.LENGTH_SHORT).show();
                    }
                } else {

                }
            }
        }
    }

    private void createEvent() {
        pd.setMessage("Creating Event...");
        pd.show();

        //for post-image name, post-id, post-publish-time
        final String timeStamp = String.valueOf(System.currentTimeMillis());

        String filePathAndName = "Events/" + myUid + "/event_image_" + timeStamp;

        //try the post with image
        try {
            //get image from image view
            Bitmap bitmap = ((BitmapDrawable)eImageIv.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //image compress
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] data = baos.toByteArray();

            //post with image
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //image is uploaded to firebase
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());

                            String downloadUri = uriTask.getResult().toString();
                            String title = eTitleEt.getText().toString().trim();
                            String desc = eDescEt.getText().toString().trim();
                            String speaker = eSpekearEt.getText().toString().trim();
                            String quota = eQuotaEt.getText().toString().trim();
                            String location = eLocationEt.getText().toString().trim();
                            String category = eCategorySpinner.getSelectedItem().toString().trim();

                            if (uriTask.isSuccessful()) {
                                //uri is receiveed upload post
                                HashMap<Object, String> hashMap = new HashMap<>();
                                //put post info
                                hashMap.put("uid", myUid);
                                hashMap.put("eId", timeStamp);
                                hashMap.put("eTitle", title);
                                hashMap.put("eDesc", desc);
                                hashMap.put("eImage", downloadUri);
                                hashMap.put("eLocation", location);
                                hashMap.put("eStart", finalEventStart);
                                hashMap.put("eEnd", finalEventEnd);
                                hashMap.put("eCategory", category);
                                hashMap.put("eQuota", quota);
                                hashMap.put("eCreatedOn", timeStamp);
                                hashMap.put("eSpeaker", speaker);
                                hashMap.put("eConfirmStatus", "pending");
                                hashMap.put("eStatus", "upcoming");

                                //path to store post data
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events");
                                //put data in this ref
                                ref.child(myUid).child(timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                pd.dismiss();
                                                Toast.makeText(AddEventActivity.this, "Event successfully created", Toast.LENGTH_SHORT).show();

                                                //reset views
                                                eTitleEt.setText("");
                                                eDescEt.setText("");
                                                eLocationEt.setText("");
                                                eQuotaEt.setText("");
                                                eImageIv.setImageURI(null);
                                                image_rui = null;
                                                eStartEt.setText("");
                                                eEndEt.setText("");
                                                eSpekearEt.setText("");

                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //failed addding post
                                                pd.dismiss();
                                                Toast.makeText(AddEventActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed upload
                            pd.dismiss();
                            Toast.makeText(AddEventActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //get uri of image
                image_rui = data.getData();

                eImageIv.setImageURI(image_rui);

                try {
                    Glide.with(getApplicationContext()).load(image_rui)
                            .centerCrop()
                            .into(eImageIv);
                } catch (Exception e) {

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleStartDateTime() {
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

                final String startDate = DateFormat.format("EEE dd/MM/yyy", calendar1).toString();

                final Calendar calendar = Calendar.getInstance();
                int HOUR = calendar.get(Calendar.HOUR);
                int MINUTE = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        Calendar calendar2 = Calendar.getInstance();
                        calendar2.set(Calendar.HOUR, hour);
                        calendar2.set(Calendar.MINUTE, minute);

                        String startTime = DateFormat.format("hh:mm aa", calendar2).toString();

                        String startDateTime = startDate + " " + startTime;
                        String prettyDateTime = startDateTime.substring(0, 3) + ", " + startDateTime.substring(4, 14) +
                                " at" + startDateTime.substring(14);
                        eStartEt.setText(prettyDateTime);

                        //convert start date time to milis
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT, Locale.getDefault());
                        LocalDateTime localDate = LocalDateTime.parse(startDateTime, formatter);
                        long timeInMilliseconds = localDate.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli();
                        finalEventStart = timeInMilliseconds + "";
                    }
                }, HOUR, MINUTE, DateFormat.is24HourFormat(AddEventActivity.this));
                timePickerDialog.show();
            }
        }, YEAR, MONTH, DATE);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialog.show();
    }

    private void handleEndDateTime() {
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

                final String endDate = DateFormat.format("EEE dd/MM/yyy", calendar1).toString();

                final Calendar calendar = Calendar.getInstance();
                int HOUR = calendar.get(Calendar.HOUR);
                int MINUTE = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        Calendar calendar2 = Calendar.getInstance();
                        calendar2.set(Calendar.HOUR, hour);
                        calendar2.set(Calendar.MINUTE, minute);

                        String endTime = DateFormat.format("hh:mm aa", calendar2).toString();
                        String endDateTime = endDate + " " + endTime;

                        //convert start date time to milis
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT, Locale.getDefault());
                        LocalDateTime localDate = LocalDateTime.parse(endDateTime, formatter);
                        long timeInMilliseconds = localDate.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli();
                        finalEventEnd = timeInMilliseconds + "";

                        try {
                            if (Long.parseLong(finalEventEnd) < Long.parseLong(finalEventStart)) {
                                Toast.makeText(getApplicationContext(), "Invalid Event End date. Try again.", Toast.LENGTH_LONG).show();
                                return;
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Event Start date must not be empty.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        String prettyDateTime = endDateTime.substring(0, 3) + ", " + endDateTime.substring(4, 14) +
                                " at" + endDateTime.substring(14);
                        eEndEt.setText(prettyDateTime);
                    }
                }, HOUR, MINUTE, DateFormat.is24HourFormat(AddEventActivity.this));
                timePickerDialog.show();
            }
        }, YEAR, MONTH, DATE);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialog.show();
    }

    private void setEventCategoryList(){
        List<String> category = new ArrayList<String>();
        category.add("Seminar");
        category.add("Discussion");
        category.add("Online Sharing");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddEventActivity.this,android.R.layout.simple_spinner_dropdown_item,category);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eCategorySpinner.setAdapter(adapter);
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

        if (!TextUtils.isEmpty(eTitleEt.getText().toString()) && image_rui != null) {
            createEvent();
        } else {
            Toast.makeText(getApplicationContext(), "Some fields are empty...", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
