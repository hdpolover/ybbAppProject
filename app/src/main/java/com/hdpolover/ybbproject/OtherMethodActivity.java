package com.hdpolover.ybbproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class OtherMethodActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    EditText mFullnameEt, mPhoneEt, mJobEt;
    Button mLoginBtn;
    TextView countryTv, cityTv, emailTv;
    CountryCodePicker countryCP;

    //    progressbar to display while registering user
    ProgressDialog progressDialog;

    //    Declare as instance of FirebaseAuth
    private FirebaseAuth mAuth;
    FirebaseUser user;
    String uid;

    private static final String TAG = "OtherMethodActivity";
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
        setContentView(R.layout.activity_other_method);

        mFullnameEt = findViewById(R.id.fullnameEt);
        mPhoneEt = findViewById(R.id.phoneEt);
        mJobEt = findViewById(R.id.jobEt);
        mLoginBtn = findViewById(R.id.loginBtn);
        countryTv = findViewById(R.id.countryTv);
        cityTv = findViewById(R.id.cityTv);
        emailTv = findViewById(R.id.emailTv);
        countryCP = findViewById(R.id.countryCP);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        emailTv.setText(user.getEmail());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");

        //location
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        checkLocation();

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String fullname = mFullnameEt.getText().toString().trim();
                final String phone = mPhoneEt.getText().toString().trim();
                final String job = mJobEt.getText().toString().trim();
                final String email = emailTv.getText().toString();
                //Validate
                if (fullname.equals("")) {
                    //set error
                    mFullnameEt.setError("Full name cannot be empty");
                    mFullnameEt.setFocusable(true);
                } else if (phone.equals("")) {
                    //set error
                    mPhoneEt.setError("Phone number cannot be empty");
                    mPhoneEt.setFocusable(true);
                } else if (job.equals("")) {
                    //set error
                    mJobEt.setError("Job cannot be empty");
                    mJobEt.setFocusable(true);
                } else {
                    registerUser(); //register user
                }
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


    private void registerUser() {
        //email and password pattern is valid
        progressDialog.show();

//        HashMap<String, Object> hashMap = new HashMap<>();
//        //put post info
//        hashMap.put("uid", uid);
//        hashMap.put("pDesc", desc);
//        hashMap.put("pImage", "noImage");
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        ref.child(uid)
//                .updateChildren(hashMap)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        pd.dismiss();
//                        Toast.makeText(AddPostActivity.this, "Post successfully updated...", Toast.LENGTH_SHORT).show();
//
//                        startActivity(new Intent(AddPostActivity.this, DashboardActivity.class));
//                        finish();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        pd.dismiss();
//                        Toast.makeText(AddPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });

        mAuth.updateCurrentUser(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
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
                            //capitalize the first letter
                            String job = mJobEt.getText().toString().substring(0, 1).toUpperCase()
                                    + mJobEt.getText().toString().substring(1);

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

                            //when user is registered store user info in firebase realtime database too
                            //using hashmap
                            HashMap<Object, String> hashMap = new HashMap<>();
                            //put info in hasmap
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("name", fullName); //will add later
                            hashMap.put("onlineStatus", "online"); //will add later
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

                            //firebase database instance
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            //path to store user data name Users
                            DatabaseReference reference = database.getReference("Users");
                            //put data within hashmap in database
                            reference.child(uid).setValue(hashMap);

                            Toast.makeText(OtherMethodActivity.this, "Login Success . . .\n" + user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(OtherMethodActivity.this, UploadProfileActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(OtherMethodActivity.this, "Login Failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

//        mAuth.updateCurrentUser(user)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    // Sign in success, dismiss dialog and start register activity
//                    progressDialog.dismiss();
//
//                    FirebaseUser user = mAuth.getCurrentUser();
//
//                    //Get user email and uid from auth
//                    String email = user.getEmail();
//                    String uid = user.getUid();
//
//                    //get username based on first 5 letters of uid
//                    String username = "ybb" + uid.substring(0, 5);
//
//                    //get Textview location
//                    String country = countryTv.getText().toString();
//                    String city = cityTv.getText().toString();
//
//                    String fullName = mFullnameEt.getText().toString();
//                    String phone = mPhoneEt.getText().toString();
//                    //capitalize the first letter
//                    String job = mJobEt.getText().toString().substring(0, 1).toUpperCase()
//                            + mJobEt.getText().toString().substring(1);
//
//                    //substring for get name
//                    //String subName = "@";
//                    //String name = email.substring(0, email.indexOf(subName));
//
//                    //when user is registered store user info in firebase realtime database too
//                    //using hashmap
//                    HashMap<Object, String> hashMap = new HashMap<>();
//                    //put info in hasmap
//                    hashMap.put("email", email);
//                    hashMap.put("uid", uid);
//                    hashMap.put("name", fullName); //will add later
//                    hashMap.put("onlineStatus", "online"); //will add later
//                    hashMap.put("typingTo", "noOne"); //will add later
//                    hashMap.put("phone", phone); //will add later
//                    hashMap.put("image", ""); //will add later
//                    hashMap.put("country", country);
//                    hashMap.put("city", city);
//                    hashMap.put("username", username);
//                    hashMap.put("job", job);
//
//                    //firebase database instance
//                    FirebaseDatabase database = FirebaseDatabase.getInstance();
//                    //path to store user data name Users
//                    DatabaseReference reference = database.getReference("Users");
//                    //put data within hashmap in database
//                    reference.child(uid).setValue(hashMap);
//
//                    Toast.makeText(OtherMethodActivity.this, "Login Success . . .\n" + user.getEmail(), Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(OtherMethodActivity.this, UploadProfileActivity.class));
//                    finish();
//                } else {
//                    // If sign in fails, display a message to the user.
//                    progressDialog.dismiss();
//                    Toast.makeText(OtherMethodActivity.this, "Login Failed.",
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });

//        startActivity(new Intent(OtherMethodActivity.this, DashboardActivity.class));
//        finish();
    }
}
