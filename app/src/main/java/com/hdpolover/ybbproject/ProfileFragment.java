package com.hdpolover.ybbproject;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hdpolover.ybbproject.adapters.AdapterPost;
import com.hdpolover.ybbproject.adapters.AdapterProfile;
import com.hdpolover.ybbproject.models.ModelPost;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class ProfileFragment extends Fragment {

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //storage
    StorageReference storageReference;

    //views from xml
    ImageView profileIv;
    TextView nameTv, emailTv, phoneTv, usernameTv, jobTv, cityTv, countryTv, followersTv, followingsTv;
    RecyclerView postsRecyclerView;
    Button messageBtn, followBtn, profileBtn;

    //progress dialog
    ProgressDialog pd;

    //permission constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    //image pick constants
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    //arrays of permissions to be requsted
    String cameraPermissions[];
    String storagePermissions[];

    List<ModelPost> postList;
    AdapterPost adapterPost;
    String uid, email;

    //image picked will be the same in this uri
    Uri image_rui = null;

    //for checking profile of cover photo
    String profilePhoto;

    TabLayout tabLayout;
    ViewPager viewPager;
    AdapterProfile viewAdapterProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = firebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        storageReference = getInstance().getReference(); //firebase storage refence

        //init array of permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //init views
        profileIv = view.findViewById(R.id.profileIv);
        nameTv = view.findViewById(R.id.nameTv);
        usernameTv = view.findViewById(R.id.usernameTv);
        jobTv = view.findViewById(R.id.jobTv);
        cityTv = view.findViewById(R.id.cityTv);
        countryTv = view.findViewById(R.id.countryTv);
        followersTv = view.findViewById(R.id.followersTv);
        followingsTv = view.findViewById(R.id.followingsTv);
        messageBtn = view.findViewById(R.id.messageBtn);
        followBtn = view.findViewById(R.id.followBtn);
        profileBtn = view.findViewById(R.id.profileBtn);

        postsRecyclerView = view.findViewById(R.id.recyclerview_posts);
        pd = new ProgressDialog(getActivity());

        /////*     initialize view   */////
        viewPager = view.findViewById(R.id.viewPager);

        /////*     initialize ViewPager   */////
        viewAdapterProfile = new AdapterProfile(getFragmentManager());

        /////*     add adapter to ViewPager  */////
        viewPager.setAdapter(viewAdapterProfile);
        tabLayout = view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabRippleColor(null);

        followBtn.setVisibility(View.GONE);
        messageBtn.setVisibility(View.GONE);
        profileBtn.setVisibility(View.VISIBLE);

        checkUserStatus();
        setUserFollows(followersTv, followingsTv);

        //we have to get info of currently signed in user
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //check until required data get
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //get data
                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
                    String phone = "" + ds.child("phone").getValue();
                    String image = "" + ds.child("image").getValue();
                    String username = "@" + ds.child("username").getValue();
                    String job = "" + ds.child("job").getValue();
                    String city = "" + ds.child("city").getValue();
                    String coutry = "" + ds.child("country").getValue();

                    //set data
                    //set data
                    nameTv.setText(name);
//                    emailTv.setText(email);
//                    phoneTv.setText(phone);
                    usernameTv.setText(username);
                    jobTv.setText(job);
                    cityTv.setText(city);
                    countryTv.setText(coutry);

                    try {
                        //if image is received then set
                        Picasso.get().load(image).placeholder(R.drawable.ic_undraw_profile_pic).into(profileIv);
                    } catch (Exception e) {
                        //if there is any exception while getting image then set default
                        //Picasso.get().load(R.drawable.ic_default_img_white).into(avatarIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //profile on click
        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog();
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });

        followersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UserFollowersActivity.class));
            }
        });

        followingsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UserFollowingsActivity.class));
            }
        });

        //postList = new ArrayList<>();

        //loadMyPosts();

        return view;
    }

    private void setUserFollows(final TextView followersTv, final TextView followingsTv){
        DatabaseReference followersRef = FirebaseDatabase.getInstance().getReference().child("Follows")
                .child(uid).child("Followers");
        followersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    followersTv.setText("0 Followers");
                } else {
                    followersTv.setText(dataSnapshot.getChildrenCount() + " Followers");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference followingsRef = FirebaseDatabase.getInstance().getReference().child("Follows")
                .child(uid).child("Followings");
        followingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    followingsTv.setText("0 Followings");
                } else {
                    followingsTv.setText(dataSnapshot.getChildrenCount() + " Followings");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //image new
    private void showImagePickDialog() {
        //option camera/gallery to show in dialog
        String[] options = {"Camera", "Gallery"};

        //dialog
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Choose Image from");
        //set options to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //camera clicked
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }
                }
                if (which == 1) {
                    //gallery clicked
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
            }
        });
        //create and show dialog
        builder.create().show();
    }

    private void pickFromCamera() {
        //intent to pick image from camera
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Temp desc");
        image_rui = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_rui);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermission() {
        //check if storage permission is enabled
        boolean result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        //request runtime storage permission
        ActivityCompat.requestPermissions(getActivity(), storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        //check if camera permission is enabled
        boolean result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPermission() {
        //request runtime camera permission
        ActivityCompat.requestPermissions(getActivity(), cameraPermissions, CAMERA_REQUEST_CODE);
    }

    //handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(getContext(), "Camera and storage permissions are necessary..", Toast.LENGTH_SHORT).show();
                    }
                } else {

                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(getContext(), "Storage permissions are necessary..", Toast.LENGTH_SHORT).show();
                    }
                } else {

                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //get uri of image
                image_rui = data.getData();

                profileIv.setImageURI(image_rui);

                updateProfilePic();
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                profileIv.setImageURI(image_rui);

                updateProfilePic();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateProfilePic() {
        pd.setMessage("Updating profile picture...");
        pd.show();

        //for post-image name, post-id, post-publish-time
        final String timeStamp = String.valueOf(System.currentTimeMillis());

        String filePathAndName = "Users/" + uid + "/" + "profile_pic_" + timeStamp;

        //try the post with image
        try {
            //get image from image view
            Bitmap bitmap = ((BitmapDrawable) profileIv.getDrawable()).getBitmap();
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
                            while (!uriTask.isSuccessful()) ;

                            String downloadUri = uriTask.getResult().toString();

                            if (uriTask.isSuccessful()) {
                                //uri is receiveed upload post
                                HashMap<String, Object> hashMap = new HashMap<>();
                                //put user profile image info
                                hashMap.put("image", downloadUri);

                                //firebase database instance
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                //path to store user data name Users
                                DatabaseReference reference = database.getReference("Users");
                                //put data within hashmap in database
                                reference.child(uid)
                                        .updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                pd.dismiss();

                                                Toast.makeText(getContext(), "Profile picture successfully updated...", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //failed addding post
                                                pd.dismiss();
                                                Toast.makeText(getActivity(), "Profile picture update failed...", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getActivity(), "Profile picture update failed..." + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            pd.dismiss();
            Toast.makeText(getActivity(), "An error occured... " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMyPosts() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //show newest post first
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set this layout to recyclerview
        postsRecyclerView.setLayoutManager(layoutManager);

        //init post list
        DatabaseReference ref = firebaseDatabase.getInstance().getReference("Posts");
        //query to load posts
        Query query = ref.orderByChild("uid").equalTo(uid);
        //get all data
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelPost myPosts = ds.getValue(ModelPost.class);

                    //add to list
                    postList.add(myPosts);

                    //adapter
                    adapterPost = new AdapterPost(getActivity(), postList);
                    //set this adapter to recyclerview
                    postsRecyclerView.setAdapter(adapterPost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(getActivity(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchMyPosts(final String searchQuery) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //show newest post first
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set this layout to recyclerview
        postsRecyclerView.setLayoutManager(layoutManager);

        //init post list
        DatabaseReference ref = firebaseDatabase.getInstance().getReference("Posts");
        //query to load posts
        Query query = ref.orderByChild("myUid").equalTo(uid);
        //get all data
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelPost myPosts = ds.getValue(ModelPost.class);

                    if (myPosts.getpDesc().toLowerCase().contains(searchQuery.toLowerCase())) {
                        //add to list
                        postList.add(myPosts);
                    }

                    //adapter
                    adapterPost = new AdapterPost(getActivity(), postList);
                    //set this adapter to recyclerview
                    postsRecyclerView.setAdapter(adapterPost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(getActivity(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
            email = user.getEmail();
        } else {
            //user not signed in, go to welcome
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true); //to show menu option in fragment
        super.onCreate(savedInstanceState);
    }

    //Inflate options menu

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflating menu
        inflater.inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_notif).setVisible(false);
        menu.findItem(R.id.action_more).setVisible(false);
        //searchview
        //SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                //called when user press search button
//                if (!TextUtils.isEmpty(query)) {
//                    //search
//                    searchMyPosts(query);
//                } else {
//                    loadMyPosts();
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                //called when user press search button
//                if (!TextUtils.isEmpty(newText)) {
//                    //search
//                    searchMyPosts(newText);
//                } else {
//                    loadMyPosts();
//                }
//                return false;
//            }
//        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    //handle menu item click

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //get item id
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }


}
