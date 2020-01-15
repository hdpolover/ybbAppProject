package com.hdpolover.ybbproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HelpCenterActivity extends AppCompatActivity {

    //permission constants
    private static final int STORAGE_REQUEST_CODE = 200;
    //image pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    //perrmission array
    String[] storagePermissions;

    //image picked will be the same in this uri
    Uri image_rui = null;

    ActionBar actionBar;

    String myUid, myEmail;

    EditText helpEt;
    MaterialButton sendHelpBtn;
    ImageView helpImageIv;
    Spinner helpCategorySpinner;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);

        pd = new ProgressDialog(this);

        checkUserStatus();

        //int permission array
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        actionBar = getSupportActionBar();
        actionBar.setTitle("Help Center");
        //enable back button in action bar
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        helpEt = findViewById(R.id.helpEt);
        sendHelpBtn = findViewById(R.id.sendHelpBtn);
        helpCategorySpinner = findViewById(R.id.helpCategorySpinner);
        helpImageIv = findViewById(R.id.helpImageIv);

        setHelpCategory();

        sendHelpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendHelp();
            }
        });

        helpImageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

    }

    private void setHelpCategory(){
        List<String> category = new ArrayList<String>();
        category.add("Bugs");
        category.add("Errors");
        category.add("Others");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(HelpCenterActivity.this,android.R.layout.simple_spinner_dropdown_item,category);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        helpCategorySpinner.setAdapter(adapter);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //get uri of image
                image_rui = data.getData();

                helpImageIv.setImageURI(image_rui);

                try {
                    Glide.with(getApplicationContext()).load(image_rui)
                            .centerCrop()
                            .into(helpImageIv);
                } catch (Exception e) {

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendHelp() {
        pd.setMessage("Sending problem report...");
        pd.show();

        //get data from comment edit text
        final String helpText = helpEt.getText().toString().trim();
        //validate
        if (TextUtils.isEmpty(helpText)) {
            Toast.makeText(this, "Field is empty...", Toast.LENGTH_SHORT).show();
            return;
        }

        //for post-image name, post-id, post-publish-time
        final String timeStamp = String.valueOf(System.currentTimeMillis());

        String filePathAndName = "HelpCenter/" + "help_ss_" + timeStamp;

        //try the post with image
        try {
            //get image from image view
            Bitmap bitmap = ((BitmapDrawable)helpImageIv.getDrawable()).getBitmap();
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
                                //put info in hashmap
                                hashMap.put("hId", timeStamp);
                                hashMap.put("helpText", helpText);
                                hashMap.put("timestamp", timeStamp);
                                hashMap.put("uid", myUid);
                                hashMap.put("email", myEmail);
                                hashMap.put("category", helpCategorySpinner.getSelectedItem().toString().trim());
                                hashMap.put("image", downloadUri);

                                //path to store post data
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("HelpCenter");
                                //put this data in db
                                ref.child(timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //added
                                                pd.dismiss();
                                                Toast.makeText(HelpCenterActivity.this, "Problem report sent...", Toast.LENGTH_SHORT).show();
                                                helpEt.setText("");
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //failed
                                                pd.dismiss();
                                               // Toast.makeText(HelpCenterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

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
                          //  Toast.makeText(HelpCenterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            //uri is receiveed upload post
            HashMap<Object, String> hashMap = new HashMap<>();
            //put info in hashmap
            hashMap.put("hId", timeStamp);
            hashMap.put("helpText", helpText);
            hashMap.put("timestamp", timeStamp);
            hashMap.put("uid", myUid);
            hashMap.put("email", myEmail);
            hashMap.put("category", helpCategorySpinner.getSelectedItem().toString().trim());
            hashMap.put("image", "noImage");

            //path to store post data
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("HelpCenter");
            //put this data in db
            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //added
                            pd.dismiss();
                            Toast.makeText(HelpCenterActivity.this, "Problem report sent...", Toast.LENGTH_SHORT).show();
                            helpEt.setText("");
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed
                            pd.dismiss();
                           // Toast.makeText(HelpCenterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
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

    private  void checkUserStatus() {
        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //user is signed in stay here
            myUid = user.getUid();
            myEmail = user.getEmail();
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
