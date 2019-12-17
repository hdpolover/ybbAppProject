package com.hdpolover.ybbproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    //    View
    EditText mEmailEt, mPasswordEt, mFullnameEt, mPhoneEt, mJobEt;
    Button mRegisterBtn;
    TextView mHaveAccountTv, countryTv, cityTv;
    ImageButton backBtn;
    CountryCodePicker countryCP;

    //    progressbar to display while registering user
    ProgressDialog progressDialog;

    //    Declare as instance of FirebaseAuth
    private FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //location
    private static final String TAG = "RegisterActivity";
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000; //10 SECOND
    private long FASTEST_INTERVAL = 2000; //2 SECOND
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        changeStatusBarColor();

//    Init
        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        mFullnameEt = findViewById(R.id.fullnameEt);
        mPhoneEt = findViewById(R.id.phoneEt);
        mJobEt = findViewById(R.id.jobEt);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mHaveAccountTv = findViewById(R.id.have_account);
        backBtn = findViewById(R.id.backBtn);
        countryTv = findViewById(R.id.countryTv);
        cityTv = findViewById(R.id.cityTv);
        countryCP = findViewById(R.id.countryCP);

        //In the onCreate() method, initialize the FirebaseAuth instance.
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering user...");

        //location
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        checkLocation();

//        handle resgister btn click
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String fullname = mFullnameEt.getText().toString().trim();
                final String email = mEmailEt.getText().toString().trim();
                final String password = mPasswordEt.getText().toString().trim();
                final String phone = mPhoneEt.getText().toString().trim();
                final String job = mJobEt.getText().toString().trim();
                //Validate
                if (fullname.equals("")) {
                    //set error
                    mFullnameEt.setError("Full name cannot be empty");
                    mFullnameEt.setFocusable(true);
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    //set error
                    mEmailEt.setError("Invalid email!");
                    mEmailEt.setFocusable(true);
                } else if (password.length() < 6) {
                    //set error
                    mPasswordEt.setError("Password length at least 6 characters");
                    mPasswordEt.setFocusable(true);
                } else if (phone.equals("")) {
                    //set error
                    mPhoneEt.setError("Phone number cannot be empty");
                    mPhoneEt.setFocusable(true);
                } else if (job.equals("")) {
                    //set error
                    mJobEt.setError("Job cannot be empty");
                    mJobEt.setFocusable(true);
                } else {
                    registerUser(email, password); //register user
//                    //check if email same
//                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Users");
//
//                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                                if (ds.child(username).exists()) {
//                                    //set error
//                                    mEmailEt.setError("Username already exists!");
//                                    mEmailEt.setFocusable(true);
//                                }else if(ds.child(email).exists()){
//                                    //set error
//                                    mEmailEt.setError("Email already exists!");
//                                    mEmailEt.setFocusable(true);
//                                }else{
//                                    registerUser(email, password); //register user
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
                }
            }
        });
        //handle login textview click
        mHaveAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            }
        });

        //click button to back
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    //location
    public static ArrayList<String> getCountryName(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            // 41.015137, 28.979530
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address result;
            ArrayList<String> location = new ArrayList<>();
            if (addresses != null && !addresses.isEmpty()) {
                location.add(addresses.get(0).getCountryName());
                location.add(addresses.get(0).getSubAdminArea());
                return (location);
            }
            return null;
        } catch (IOException ignored) {
            //do something
        }
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            return;
        }

        startLocationUpdates();
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation == null) {
            startLocationUpdates();
        }
        if (mLocation != null) {
            countryTv.setText(getCountryName(this, mLocation.getLatitude(), mLocation.getLongitude()).get(0));
            cityTv.setText(getCountryName(this, mLocation.getLatitude(), mLocation.getLongitude()).get(1));
        } else {
            Toast.makeText(this, "Location not Detected. Please Enable Your Location", Toast.LENGTH_SHORT).show();
        }
    }

    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error : " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void startLocationUpdates() {
        //Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        //Request location update
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.d("reque", "--->");
    }

    public void onLocationChanged(Location location) {
        countryTv.setText(getCountryName(this, mLocation.getLatitude(), mLocation.getLongitude()).get(0));
        cityTv.setText(getCountryName(this, mLocation.getLatitude(), mLocation.getLongitude()).get(1));

        //create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'. \nPlease Enable Location to use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }

    private void registerUser(String email, String password) {
        //email and password pattern is valid
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, dismiss dialog and start register activity
                            progressDialog.dismiss();

                            FirebaseUser user = mAuth.getCurrentUser();

                            //Get user email and uid from auth
                            String email = user.getEmail();
                            String uid = user.getUid();

                            //get username based on first 5 letters of uid
                            String username = "ybb" + uid.substring(0, 5);

                            //get Textview location
                            String country = countryTv.getText().toString();
                            String city = cityTv.getText().toString();

                            String fullName = mFullnameEt.getText().toString();

                            String phone;
                            String codeNumber = countryCP.getSelectedCountryCodeWithPlus();
                            String phoneNumber = mPhoneEt.getText().toString().substring(0,1);
                            int subPhone = phoneNumber.indexOf(phoneNumber);

                            String phoneFull = mPhoneEt.getText().toString();
                            String phoneLast = mPhoneEt.getText().toString().substring(1);

                            if(subPhone == 0){
                                phone = codeNumber + phoneLast;
                            }else{
                                phone = codeNumber + phoneFull;
                            }

                            //convert timestamp to dd/mm/yyyy hh:mm am/pm
                            Date currentTime = Calendar.getInstance().getTime();
                            String time = DateFormat.format("dd/MM/yyyy hh:mm aa", currentTime).toString();

                            //capitalize the first letter
                            String job = mJobEt.getText().toString().substring(0, 1).toUpperCase()
                                    + mJobEt.getText().toString().substring(1);

                            //substring for get name
                            //String subName = "@";
                            //String name = email.substring(0, email.indexOf(subName));

                            //when user is registered store user info in firebase realtime database too
                            //using hashmap
                            HashMap<Object, String> hashMap = new HashMap<>();
                            //put info in hasmap
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("name", fullName); //will add later
                            hashMap.put("onlineStatus", time); //will add later
                            hashMap.put("typingTo", "noOne"); //will add later
                            hashMap.put("phone", phone); //will add later
                            hashMap.put("image", ""); //will add later
                            hashMap.put("country", country);
                            hashMap.put("city", city);
                            hashMap.put("username", username);
                            hashMap.put("job", job);
                            hashMap.put("cityFrom", "");
                            hashMap.put("countryFrom", "");
                            hashMap.put("birthDate", "");
                            hashMap.put("bio", "--");
                            hashMap.put("education", "--");
                            hashMap.put("interest", "");

                            //firebase database instance
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            //path to store user data name Users
                            DatabaseReference reference = database.getReference("Users");
                            //put data within hashmap in database
                            reference.child(uid).setValue(hashMap);

                            Toast.makeText(RegisterActivity.this, "Registered . . .\n" + user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, UploadProfileActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //error, dismiss progress dialog
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private String getRandomUsername() {
//        String username = "";
//        String salt = "";
//
//        Random rnd = new Random();
//        int numLetters = 5;
//
//        String randomLetters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
//
//        for (int n=0; n<numLetters; n++) {
//            salt += randomLetters.charAt(rnd.nextInt(randomLetters.length()));
//        }
//
//        username = "ybb" + salt;
//        return username;
//    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //go previous activity
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        finish();
    }
}
