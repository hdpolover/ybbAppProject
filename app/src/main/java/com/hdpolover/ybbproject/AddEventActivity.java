package com.hdpolover.ybbproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.logging.SimpleFormatter;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class AddEventActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    //permission constants
    private static final int STORAGE_REQUEST_CODE = 200;
    //image pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    //perrmission array
    String[] storagePermissions;

    //image picked will be the same in this uri
    Uri image_rui = null;

    String uid;

    ProgressDialog pd;
    String editImage;

    private DatePickerDialog datePickerDialog1 , datePickerDialog2;
    private SimpleDateFormat dateFormatter;

    private TimePickerDialog timePickerDialog1, timePickerDialog2;
//    private SimpleTimeFormat simpleTimeFormat;

    EditText titleEt, descEt, dateEtFrom, dateEtTo, timeEtFrom, timeEtTo, eventLocEt, eventSpekEt, eventQuotaEt;
    ImageView imgEt;
    Spinner categoryEt;

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

        //int permission array
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        pd = new ProgressDialog(this);

        checkUserStatus();

        titleEt = findViewById(R.id.titleEt);
        descEt = findViewById(R.id.descEt);

        //get image from camera/gallery on click
        imgEt = findViewById(R.id.eventImg);
        imgEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

        //DatePicker
        dateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        dateEtFrom = findViewById(R.id.eventDateFrom);
        dateEtFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDateButton1();
            }
        });

//        simpleTimeFormat = new SimpleTimeFormat();
        timeEtFrom = findViewById(R.id.eventTimeFrom);
        timeEtFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleTimeButton1();
            }
        });

        dateEtTo = findViewById(R.id.eventDateTo);
        dateEtTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDateButton2();
            }
        });
        timeEtTo = findViewById(R.id.eventTimeTo);
        timeEtTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleTimeButton2();
            }
        });

        categoryEt = findViewById(R.id.eventCatSpin);
        eventSpekEt = findViewById(R.id.eventSpek);
        eventLocEt = findViewById(R.id.eventLocEt);
        categoryLoation();

        eventQuotaEt = findViewById(R.id.eventQuot);
    }

    private void checkUserStatus() {
        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
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

        String filePathAndName = "Events/" + uid + "/event_banner_" + timeStamp;

        //try the post with image
        try {
            //get image from image view
            Bitmap bitmap = ((BitmapDrawable)imgEt.getDrawable()).getBitmap();
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

                            if (uriTask.isSuccessful()) {
                                //uri is receiveed upload post
                                HashMap<Object, String> hashMap = new HashMap<>();
                                //put post info
                                hashMap.put("myUid", uid);
                                hashMap.put("eId", timeStamp);
                                hashMap.put("eTitle", titleEt.getText().toString());
                                hashMap.put("eDesc", descEt.getText().toString());
                                hashMap.put("eImage", downloadUri);
                                hashMap.put("eLocation", eventLocEt.getText().toString());
                                hashMap.put("eDateFrom", dateEtFrom.getText().toString());
                                hashMap.put("eTimeFrom", timeEtFrom.getText().toString());
                                hashMap.put("eDateTo", dateEtTo.getText().toString());
                                hashMap.put("eTimeTo", timeEtTo.getText().toString());
                                hashMap.put("eCategory", categoryEt.getSelectedItem().toString());
                                hashMap.put("eQuota", eventQuotaEt.getText().toString());
                                hashMap.put("eParticipants", "0");
                                hashMap.put("eCreatedOn", timeStamp);
                                hashMap.put("eSpeaker", eventSpekEt.getText().toString());
                                hashMap.put("eConfirmStatus", "pending");
                                hashMap.put("eStatus", "upcoming");

                                //path to store post data
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events");
                                //put data in this ref
                                ref.child(uid).child(timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                pd.dismiss();
                                                Toast.makeText(AddEventActivity.this, "Event successfully created...", Toast.LENGTH_SHORT).show();

                                                //reset views
                                                titleEt.setText("");
                                                dateEtFrom.setText("");
                                                timeEtFrom.setText("");
                                                descEt.setText("");
                                                imgEt.setImageURI(null);
                                                image_rui = null;

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

                imgEt.setImageURI(image_rui);

                try {
                    Picasso.get().load(image_rui)
                            .fit()
                            .centerCrop()
                            .into(imgEt);
                } catch (Exception e) {

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleDateButton1() {

         Calendar calendar = Calendar.getInstance();
         datePickerDialog1 = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
             @Override
             public void onDateSet(DatePicker datePicker, int year1, int month1, int day1) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year1, month1, day1);

                dateEtFrom.setText(dateFormatter.format(newDate.getTime()));
             }
         },calendar.get(YEAR), calendar.get(MONTH), calendar.get(DAY_OF_MONTH));
         datePickerDialog1.show();

    }

    private void handleTimeButton1() {
        Calendar calendar = Calendar.getInstance();

        timePickerDialog1 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour1, int minute1) {
                timeEtFrom.setText(hour1+"."+minute1);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(this));
        timePickerDialog1.show();
    }

    private void handleDateButton2() {
        Calendar calendar = Calendar.getInstance();

        datePickerDialog2 = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year2, int month2, int day2) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year2, month2, day2);

                dateEtTo.setText(dateFormatter.format(newDate.getTime()));
            }
        },calendar.get(YEAR), calendar.get(MONTH), calendar.get(DAY_OF_MONTH));
        datePickerDialog2.show();

    }

    private void handleTimeButton2() {
        Calendar calendar = Calendar.getInstance();

        timePickerDialog2 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour2, int minute2) {
                timeEtTo.setText(hour2+"."+minute2);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(this));
        timePickerDialog2.show();
    }

    private void categoryLoation(){
        List<String> category = new ArrayList<String>();
        category.add("Seminar");
        category.add("Discussion");
        category.add("Online Sharing");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddEventActivity.this,android.R.layout.simple_spinner_dropdown_item,category);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryEt.setAdapter(adapter);
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

        if (!TextUtils.isEmpty(titleEt.getText().toString()) && image_rui != null) {
            createEvent();
        } else {
            Toast.makeText(getApplicationContext(), "Some fields are empty...", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
